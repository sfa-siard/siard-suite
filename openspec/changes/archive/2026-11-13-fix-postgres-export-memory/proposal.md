# Proposal

## Why

Archiving a PostgreSQL database dominated by `bytea` payloads requires heap sizes several times the data size (SiardGui issues #60 and #61: ~11 GB heap for a 3.5 GB SIARD export, progressive slowdowns on a 90 GB database). The root cause is in `jdbc-postgres`: PgJDBC only uses a server-side cursor when `fetchSize > 0`, the result set is forward-only, autocommit is off **and** the result set is not holdable — but `PrimaryDataTransfer.openTable()` always requests `HOLD_CURSORS_OVER_COMMIT` and no fetch size is ever set, so the driver buffers the entire result set in heap. Secondary leaks (per-cell `pg_catalog` queries in `PostgresResultSetMetaData`, unbounded retention in `MimeTypeHandler`, unclosed streams passed to Tika) compound the problem.

## What Changes

- Enable server-side cursor streaming for Postgres SELECTs used by the archive export path: statements created through `PostgresConnection`/`PostgresStatement`/`PostgresPreparedStatement` get a default `fetchSize > 0`, and `HOLD_CURSORS_OVER_COMMIT` is neutralized (mapped to `CLOSE_CURSORS_AT_COMMIT`) so PgJDBC's `QUERY_FORWARD_CURSOR` condition can be met. No commits happen mid-export, so close-at-commit semantics are unchanged in practice.
- Cache column type/type-name lookups in `PostgresResultSetMetaData` (currently a `pg_catalog` query per call, hit per cell for `OTHER`/`STRUCT`/interval columns).
- Fix `MimeTypeHandler.individualMimeTypes` retaining every binary cell's `Cell` object for the whole table — entries are write-once-read-once and must not accumulate.
- Ensure the `InputStream`/`Reader` handed to `Tika.detect()` for `Blob`/`Clob` values is closed or bounded, so the underlying `LargeObject` is released instead of leaking a server-side descriptor per cell.
- Keep `PrimaryDataTransfer.openTable()` semantics unchanged for other databases.

## Capabilities

### New Capabilities

- `postgres-archive-export`: Bounded-memory, streaming behavior of SIARD Suite when exporting primary data from PostgreSQL — result sets are fetched incrementally, per-cell metadata lookups are cached, and binary/clob mime-type detection does not retain cell objects or leak large objects.

### Modified Capabilities

(none — no existing spec covers JDBC/Postgres export behavior)

## Impact

- **`jdbc-postgres`**: `PostgresConnection` (holdability mapping), `PostgresStatement`/`PostgresPreparedStatement` (default fetch size), `PostgresResultSetMetaData` (per-column caching)
- **`siard-cmd`**: `MimeTypeHandler` (transient mime-type tracking), blob/clob detection stream handling in `PrimaryDataFromDb`
- **Tests**: new integration test `PostgresResultSetMemoryIT` (testcontainers Postgres, `bytea`-heavy table, heap-delta assertions) already reproduces the bug — `exportPathConfiguration_doesNotBufferEntireResultSet` fails today and must pass after the fix
- **Risk**: fetch-size value must be conservative (rows can be 100 MB+); interleaved catalog/large-object calls on the same connection while a portal is suspended must be verified; updatable (`CONCUR_UPDATABLE`) result sets are unaffected on the read path
- **Out of scope**: the import/upload path (`PrimaryDataToDb` updatable result set accumulating inserted rows) — tracked in the separate change `fix-postgres-import-memory`
