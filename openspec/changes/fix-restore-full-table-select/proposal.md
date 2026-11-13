# Proposal

## Why

`PrimaryDataToDb`'s upload path obtains its insert vehicle via `openTable`, which issues `SELECT <all mapped columns> FROM <table>` — a full-table scan whose rows are **never consumed** (`rs.next()` is never called; the result set is used only for column metadata and, on non-Postgres drivers, as the updatable insert vehicle). On drivers that cannot stream updatable result sets — pgjdbc cannot use a server-side cursor for `CONCUR_UPDATABLE` — the entire table, including LOB payloads, is materialized in heap at `executeQuery` time. Peak memory during restore therefore still scales with table size even after `fix-postgres-import-memory` removed insert-side accumulation, and on every driver the restore performs a pointless full table scan per table.

## What Changes

- On the upload path (`SchemaMapping != null`), make the `openTable` SELECT return zero rows (e.g., append `WHERE 1=0`) so no row data is transferred or buffered, while column metadata stays identical and `moveToInsertRow`/`updateObject`/`insertRow` continue to work.
- Keep the download path (`SchemaMapping == null`, rows consumed via `rs.next()`) unchanged.
- Verify per database driver that an updatable result set over an empty result supports the insert calls used by `putTableRecord` (Postgres additionally only needs the metadata, its inserts go through prepared statements).

## Capabilities

### New Capabilities

- `siard-archive-restore`: Behavior of SIARD Suite when restoring SIARD archives into relational databases — row writes must not require reading existing table data, and memory usage during restore must stay bounded independently of table size.

### Modified Capabilities

(none — no existing spec covers the restore/upload path; `postgres-archive-import` is still a pending delta of `fix-postgres-import-memory`)

## Impact

- **`siard-cmd`**: `PrimaryDataTransfer.openTable` (upload branch only), possibly `PrimaryDataToDb` if the metadata/insert-vehicle split needs adjustment
- **`jdbc-*` drivers**: behavioral verification only — Postgres (prepared-statement path needs metadata only), MSSQL/Oracle/DB2/MySQL-MariaDB/Access (updatable-RS insert vehicle must accept `insertRow` on an empty result set)
- **Tests**: unit test asserting the upload SELECT carries a false predicate / returns no rows; extend or mirror the Postgres memory IT to cover the `openTable` window; per-DBMS integration tests where available
- **Depends on**: `fix-postgres-import-memory` (same files in `siard-cmd`; sequencing avoids conflicts)
- **Risk**: a driver rejecting `insertRow`/`moveToInsertRow` on an empty updatable result set would break restore for that DBMS — mitigated by per-driver verification tasks before rollout; alternative per-driver metadata queries remain possible
