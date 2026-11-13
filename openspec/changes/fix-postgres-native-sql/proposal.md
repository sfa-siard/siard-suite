# Proposal

## Why

`jdbc-postgres` still routes `Statement.executeQuery`/`execute` and all `Connection.prepareCall` overloads through `nativeSQL()` before handing SQL to pgjdbc. For Postgres that method is pgjdbc's own `PgConnection.nativeSQL()`, which returns the fully parsed native query — JDBC escapes expanded **and every `?` rewritten to `$1, $2, …`**. The driver then parses the result a second time, so a literal `?` (e.g. the jsonb `?` existence operator) is corrupted into `$1` and fails server-side, and every statement is parsed twice. `prepareStatement` was already fixed the same way in `fix-postgres-import-memory`; this change removes the remaining occurrences.

## What Changes

- `PostgresStatement.executeQuery(String)` and the four `execute(String, …)` overloads pass the caller's SQL unchanged to the wrapped statement instead of pre-converting via `nativeSQL()`.
- `PostgresConnection.prepareCall` (all three overloads) passes SQL unchanged to the wrapped connection.
- JDBC escape syntax (`{fn …}`, `{d …}`, `{ts …}`, `{oj …}`, `{call …}`) keeps working: pgjdbc performs escape processing itself inside `PgStatement`/`PgPreparedStatement`/`PgCallableStatement`.
- Literal `?` characters in statement SQL now reach the server intact, fixing queries that use the PostgreSQL `?` jsonb operator (previously rewritten to `$1` and rejected).
- Unchanged: `PostgresStatement.executeUpdate` variants keep using `nativeSqlUpdate()` (the SIARD sql-parser DELETE→large-object-unlink path); the public `Connection.nativeSQL()` method keeps delegating to pgjdbc; other database drivers are untouched.

## Capabilities

### New Capabilities

- `postgres-jdbc-sql-handling`: The contract by which the PostgreSQL JDBC wrapper treats SQL text — statements and calls reach pgjdbc without wrapper-level `nativeSQL` pre-conversion, preserving JDBC parameter markers, literal `?` characters, and JDBC escape syntax.

### Modified Capabilities

(none — `postgres-archive-export`, `postgres-archive-import`, `siard-cli`, and `siard-cli-distribution` do not cover JDBC-wrapper SQL handling)

## Impact

- **`jdbc-postgres`**: `PostgresStatement` (5 call sites), `PostgresConnection` (3 call sites)
- **Behavioral fix**: statement SQL containing a literal `?` (jsonb operator, or any non-parameter use) now executes instead of failing with a server-side parameter error; parameterized `PreparedStatement` usage is unaffected (already fixed); escape syntax unchanged
- **Performance**: removes one full SQL parse per `Statement`/`CallableStatement` execution
- **Tests**: unit tests asserting the wrapped driver receives the SQL string unchanged (mirroring `PostgresPreparedSqlTest`); integration coverage for a literal-`?` query if a Postgres test fixture is available
- **Risk**: low — pgjdbc re-parses and performs escape/parameter processing internally on every path affected; `prepareCall` has no callers for Postgres in the codebase, and `{call …}` handling lives in pgjdbc's `modifyJdbcCall`
- **Out of scope**: `nativeSqlUpdate()` (LOB unlink on DELETE), `Connection.nativeSQL()` itself, non-Postgres drivers, `PostgresPreparedStatement` parameter binding
