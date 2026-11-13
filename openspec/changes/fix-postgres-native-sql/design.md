# Design

## Context

See `proposal.md` for motivation. Verified mechanism in pgjdbc 42.7.11 (`Parser.parseJdbcSql`, `CachedQueryCreateAction`, `QueryExecutorBase`):

- `PgConnection.nativeSQL(sql)` → `createQuery(sql, escapeProcessing=false, isParameterized=true)`. Because `isParameterized=true` collapses the cache key to a plain `String`, escape processing runs (`Parser.replaceProcessing`) and `parseJdbcSql` rewrites every `?` to `$1, $2, …` (`withParameters=true`, `splitStatements=true`). It returns `NativeQuery.nativeSql` — the server-native form.
- Feeding that string back into `prepareStatement`/`executeQuery`/`prepareCall` makes pgjdbc parse it a **second** time. `$n` are not `?` markers, so the second parse finds zero bind parameters; any `setObject(1, …)` throws "column index out of range". A literal `?` in caller SQL (e.g. the jsonb `?` operator) is likewise corrupted to `$1` and fails server-side.
- pgjdbc already performs the full job internally on every affected path:
  - `PgStatement.executeQuery/execute` → `createQuery(sql, escapeProcessing=true, isParameterized=false)` → escapes processed, `?` kept literal (correct: `Statement` has no bind parameters).
  - `PgPreparedStatement` → `borrowQuery(sql)` → String key → escapes processed, `?`→`$n`, parsed query cached in `statementCache`.
  - `PgCallableStatement` → `borrowCallableQuery` → `CallableQueryKey` → `Parser.modifyJdbcCall` translates `{call …}`/`{? = call …}`.
- `fix-postgres-import-memory` already removed `nativeSQL` from all six `PostgresConnection.prepareStatement` overloads and pins the contract in `PostgresPreparedSqlTest`. This change removes the remaining eight call sites.
- Other drivers are unaffected: their `nativeSQL` overrides use the SIARD `SqlParser` for dialect translation and carry load-bearing side effects (MySQL no-PK workaround, MSSQL catalog/schema resolution and `DROP … CASCADE` tracking) that pgjdbc provides natively for Postgres.

## Goals / Non-Goals

**Goals:**

- Pass caller SQL unchanged to pgjdbc in `PostgresStatement.executeQuery(String)`, the four `PostgresStatement.execute(String, …)` overloads, and the three `PostgresConnection.prepareCall` overloads (8 sites total).
- Preserve JDBC escape syntax and literal `?` end-to-end; pin the pass-through contract with unit tests mirroring `PostgresPreparedSqlTest`.

**Non-Goals:**

- `PostgresStatement.executeUpdate` variants — they use `nativeSqlUpdate()` (SIARD sql-parser DELETE→large-object-unlink), a different mechanism that must stay.
- The public `Connection.nativeSQL()` method — stays delegated to pgjdbc; it is the correct JDBC implementation of "native form".
- `PostgresPreparedStatement` parameter binding internals — owned by `fix-postgres-import-memory`.
- Non-Postgres drivers.

## Decisions

### D1: Pass `sql` unchanged at all eight sites

Rationale: pgjdbc's own parse (`replaceProcessing` + `parseJdbcSql`/`modifyJdbcCall`) is a superset of what `nativeSQL` produced, executed once instead of twice, and additionally caches prepared queries. Alternatives considered:

- **Keep `nativeSQL` for escapes only** — impossible to separate; `nativeSQL` always parameterizes, so keeping it preserves the `?`→`$n` corruption.
- **Override `PostgresConnection.nativeSQL` to return `sql`** — changes public API semantics (callers of `nativeSQL()` expect the driver's native form) and does not fix the call sites, which would still double-handle the string.
- **Route through the SIARD `SqlParser` like `nativeSqlUpdate`** — wrong tool: that parser targets standard SQL:2008 dialect rendering and LOB unlinking, not pgjdbc escape/parameter processing.

### D2: Keep `nativeSQL()` itself untouched

`BaseConnection.nativeSQL` → `PgConnection.nativeSQL` is spec-correct: it returns the statement as the driver would send it. Only the *call sites* were wrong to pre-convert.

### D3: `PostgresPreparedStatement` inherits the changed `execute*(String)` methods harmlessly

`PostgresPreparedStatement` inherits `executeQuery(String)`/`execute(String)` from `PostgresStatement`. JDBC forbids String-argument query methods on `PreparedStatement`; pgjdbc throws `WRONG_OBJECT_TYPE` when reached — identical effective behavior before and after (the wrapper's `nativeSQL` call was dead work on that path anyway).

### D4: Tests assert the boundary, not pgjdbc internals

Unit tests verify the wrapper hands the exact SQL string to the wrapped driver and never calls `nativeSQL` — mirroring `PostgresPreparedSqlTest`. Escape expansion, `?`→`$n`, `??`→`?`, and jsonb-operator behavior are pgjdbc's contract, verified in its sources and covered by pgjdbc's own test suite; duplicating them here would test the driver, not the wrapper. One testcontainers integration test for a literal-`?` query is added if the existing Postgres IT fixture makes it cheap.

## Risks / Trade-offs

- [pgjdbc changes escape/parameter handling in a future upgrade] → pinned at 42.7.x; the unit tests pin the wrapper boundary so a driver change surfaces as a driver issue, not silent wrapper corruption; escape/param semantics are stable, spec-defined JDBC behavior
- [`{call …}` handling differs after dropping pre-conversion] → no `prepareCall` callers exist for Postgres in the codebase; pgjdbc's `CallableQueryKey`/`modifyJdbcCall` path is the driver's own implementation — verified by inspection, noted in tasks
- [Multi-statement SQL splitting differs: `nativeSQL` always split, `PgStatement` splits only when `preferQueryMode >= EXTENDED`] → observable only for semicolon-joined strings, which JDBC drivers handle variably anyway; acceptable
- [`nativeSqlUpdate` (`executeUpdate`) may still mangle literal `?` via the SIARD sql-parser] → pre-existing, out of scope; recorded here so it is not mistaken for a regression of this change

## Migration Plan

No migration. Rollback = revert. No archive format or API changes; the only observable difference is that previously-failing queries containing literal `?` now succeed.
