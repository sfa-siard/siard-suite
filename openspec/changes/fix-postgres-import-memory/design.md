# Design

## Context

See `proposal.md` for motivation. Verified mechanism in pgjdbc 42.7.11 `PgResultSet.insertRow()`:

```java
insertStatement = connection.prepareStatement(insertSQL, RETURN_GENERATED_KEYS);
...
castNonNull(rows).add(rowBuffer);   // every inserted row retained until rs.close()
```

`PrimaryDataToDb` writes archive rows through `openTable(..., CONCUR_UPDATABLE)` → `moveToInsertRow()` → `updateObject(...)` per cell → `insertRow()`. The driver keeps every inserted row — full `byte[]` payloads included — in the result set's row list, so heap grows to ≈ the largest table's size. `fetchSize` cannot help: this is not fetch buffering, it is insert-side accumulation. Additionally, `PostgresResultSet.updateObject` calls `getMetaData().getColumnType()` per cell, which issues a `pg_catalog.getColumns` query per non-null cell until `fix-postgres-export-memory`'s caching lands.

The updatable-RS insert exists partly because `PostgresResultSet.updateObject` contains the type translation the SIARD types need (`PostgresBlob`/`PostgresClob` → large-object OID, `PostgresArray`, temporal types, `OTHER`/`STRUCT`). A `PreparedStatement` path must reproduce that translation in `PostgresPreparedStatement.setObject`, which today just delegates and carries a `TODO` for BLOB/CLOB.

## Goals / Non-Goals

**Goals:**

- Bound heap during Postgres restore to O(batch) instead of O(table size)
- Reduce per-cell catalog queries to per-table column metadata resolution
- Preserve exact round-trip fidelity for every SIARD column type
- Reduce round-trips vs. the current one-statement-per-row updatable path via batching

**Non-Goals:**

- Changing transaction boundaries, table creation order, or SIARD file reading (`PrimaryDataFromDb`-side already streams)
- Altering updatable-RS behavior for other database drivers (their drivers do not exhibit this accumulation; changing shared code would be unverified risk)
- Rewriting `siard-api`'s `Value` model

## Decisions

### D1: Postgres-scoped insert path, selected in `siard-cmd`

Add a small insert-writer abstraction in `PrimaryDataToDb`'s write loop: the existing updatable-RS writer remains the default; a `PreparedStatement`-based writer is used when the connection is a `PostgresConnection` (or reports Postgres product name — decide at implementation; `instanceof` on the wrapper is simplest since `siard-cmd` already depends on `jdbc-postgres` for tests, but product-name detection avoids a hard module dependency if layering requires it — verify module deps first).

Alternatives considered:

- **Fix inside `PostgresResultSet.insertRow()`** (e.g., prevent row retention): impossible cleanly — `rows` is private in `PgResultSet` and `insertRow` is a single complex method (INSERT generation + `RETURN_GENERATED_KEYS`); reimplementing it in the wrapper would fork driver internals and break on upgrades.
- **Switch all drivers to prepared statements**: rejected — updatable-RS `insertRow` may be load-bearing on other databases (identity columns, triggers, vendor `getGeneratedKeys` semantics); unverified blast radius for a Postgres-specific defect.

### D2: Reproduce `updateObject` type translation in `PostgresPreparedStatement.setObject`

Inventory `PostgresResultSet.updateObject`'s per-type handling during implementation and mirror it: `PostgresBlob`/`PostgresClob` → create large object, stream contents, bind OID; `PostgresArray` → `java.sql.Array`; temporal/interval/`OTHER`/`STRUCT` → same conversions. Where logic is shareable, extract a helper rather than duplicating. This is the main correctness risk — parity is enforced by the existing round-trip integration test (`PostgresUploadDownloadSiardProjectIT`) plus a dedicated type-matrix test if coverage gaps appear.

### D3: Byte-aware batching, not just row-count batching

`PreparedStatement.executeBatch()` accumulates parameter values until flush, so a row-count-only batch still buffers `batchSize × rowBytes`. Flush when either a row-count cap (e.g., 100) **or** an estimated byte budget (e.g., 16 MiB, summed from value sizes known before binding) is reached. This bounds memory to O(budget) while keeping round-trips low for small-row tables. For oid-domain LOBs the parameter is an OID (small), but the LO write itself must stream — see D4.

### D4: Stream LOB writes, enlarge the chunk buffer

Today each oid-domain `BLOB`/`CLOB` cell costs `createLO` + `GRANT` + open + writes in 8 KiB fastpath chunks (~12,800 round-trips per 100 MB). On the prepared-statement path: obtain the value as a stream (`Value.getBinaryStream()`/`getAsciiStream()` where available instead of `Value.getBytes()` which materializes the whole cell), create the large object, and copy with a larger buffer (e.g., 256 KiB–1 MiB). `bytea` cells bind via `setBinaryStream`/`setBytes` directly — pgjdbc sends them without whole-result retention; per-statement parameter memory is transient.

### D5: Resolve column types once per table

The prepared-insert path needs target column types for `setObject` translation. Fetch them once per table (via `DatabaseMetaData.getColumns` or the existing metadata path, cached per `fix-postgres-export-memory`) instead of per cell — reducing catalog queries from O(cells) to O(tables).

## Risks / Trade-offs

- [Type-translation parity gap between `updateObject` and `setObject` → silent data corruption on restore] → Mitigation: inventory every branch of `updateObject`; round-trip integration test across the full SIARD type matrix; add cases for `BLOB`/`CLOB`/`ARRAY`/temporal/`OTHER` if existing tests lack them
- [Generated-key/identity handling differs between `insertRow` (`RETURN_GENERATED_KEYS`) and plain `executeBatch`] → SIARD restore writes explicit key values from the archive into empty tables, so generated keys are not consumed; verify no trigger/identity side-effect is required by checking how `PrimaryDataToDb` creates tables
- [`instanceof PostgresConnection` check couples `siard-cmd` to `jdbc-postgres`] → acceptable: `siard-cmd` already integrates all drivers; if layering forbids it, fall back to `getDatabaseProductName()` detection
- [Byte-budget estimation underestimates `bytea` hex expansion] → pgjdbc sends `bytea` params efficiently; estimate conservatively (count raw bytes) and rely on the row-count cap as backstop
- [Two insert code paths to maintain] → contained: Postgres path is additive; other drivers untouched; revisit unifying once parity is proven

## Migration Plan

No migration. Rollback = revert. Archives produced/restored are format-identical; only the write mechanism changes. Sequence after `fix-postgres-export-memory` merges to avoid concurrent edits in `jdbc-postgres` (overlap is limited to `PostgresPreparedStatement`, but sequencing keeps it clean).
