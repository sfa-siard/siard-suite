# Proposal

## Why

Restoring a SIARD archive into PostgreSQL exhibits the same abnormal heap growth reported in SiardGui issue #60 (~13 GB heap while uploading a 3.5 GB archive). The cause is different from export: `PrimaryDataToDb` inserts rows through a `CONCUR_UPDATABLE` result set (`moveToInsertRow`/`updateObject`/`insertRow`), and pgjdbc's `PgResultSet.insertRow()` appends every inserted row — including full `byte[]` payloads — to the result set's internal row list, which is retained until `close()`. Heap therefore grows to ≈ the size of the largest table regardless of any fetch-size setting, and the per-cell `updateObject` path also triggers a `pg_catalog` query per non-null cell via `PostgresResultSetMetaData`.

## What Changes

- Replace the updatable-result-set insert mechanism for PostgreSQL with batched `INSERT` via `PreparedStatement` (bounded memory, far fewer round-trips than row-at-a-time updatable-RS inserts).
- Preserve exact type semantics for SIARD value types on the insert path — including oid-domain `BLOB`/`CLOB` (large objects), `ARRAY`, `OTHER`/`STRUCT`, dates/intervals — which today rely on `PostgresResultSet.updateObject`'s type-specific translation; the prepared-statement path needs equivalent handling in `PostgresPreparedStatement.setObject` (currently a `TODO` that delegates raw).
- Leverage the per-column metadata caching delivered by `fix-postgres-export-memory` where the insert path still needs column types.
- Keep `PrimaryDataToDb`'s table/row iteration and transaction boundaries unchanged; only the per-row write mechanism changes.

## Capabilities

### New Capabilities

- `postgres-archive-import`: Bounded-memory behavior of SIARD Suite when restoring SIARD archives into PostgreSQL — inserted rows are not accumulated in heap, large binary/clob values are written without materializing whole cells where avoidable, and all SIARD column types round-trip correctly.

### Modified Capabilities

(none — no existing spec covers JDBC/Postgres import behavior)

## Impact

- **`siard-cmd`**: `PrimaryDataToDb` insert path (updatable RS → batched prepared statements), possibly a Postgres-specific insert strategy alongside the shared path
- **`jdbc-postgres`**: `PostgresPreparedStatement.setObject` type translation for `PostgresBlob`/`PostgresClob`/`PostgresArray` and other SIARD types; `PostgresConnection`/`PostgresStatement` if insert batching hooks are needed
- **`siard-api`**: possibly `Value.getBytes()`/`getBinaryStream()` usage on the read side of archive cells (currently materializes whole LOB into `byte[]` before `updateBytes`)
- **Depends on**: `fix-postgres-export-memory` for metadata caching (shared `jdbc-postgres` files; sequencing avoids conflicts)
- **Tests**: extend `PostgresResultSetMemoryIT` or add an import-side testcontainers test asserting bounded heap during restore of a `bytea`-heavy archive; existing `PostgresUploadDownloadSiardProjectIT` must keep passing (round-trip correctness)
- **Risk**: type-translation parity between `updateObject` and `setObject` is the main correctness risk — every SIARD type the archive can contain must map identically; oid-domain LOB writes currently go through `createLO`+8 KiB chunks and should keep working or improve
- **Out of scope**: export/download path (covered by `fix-postgres-export-memory`); non-Postgres drivers
