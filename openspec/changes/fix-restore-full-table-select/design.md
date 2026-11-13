# Design

## Context

See `proposal.md` for motivation. Verified facts from investigation:

- `PrimaryDataTransfer.openTable(table, sm)` (`siard-cmd/.../PrimaryDataTransfer.java:66-122`) builds `SELECT <all mapped columns> FROM <qiTable>` and executes it via `_conn.createStatement(TYPE_FORWARD_ONLY, CONCUR_UPDATABLE, HOLD_CURSORS_OVER_COMMIT)`.
- On the upload path (`sm != null`, called only from `PrimaryDataToDb.uploadTable`), `rs.next()` is **never called**: the loop iterates `TableRecordDispenser` (SIARD file rows), writes via `moveToInsertRow`/`updateObject`/`insertRow` (non-Postgres) or the prepared-statement path (Postgres, which uses `rs` only for `getMetaData()` at `PrimaryDataToDb.java:478` and closes it immediately).
- pgjdbc cannot use a server-side cursor for `CONCUR_UPDATABLE` (see `PostgresStatement.java:58-64`), so the updatable SELECT materializes the entire table — payloads included — in heap at `executeQuery` time.
- Other drivers' updatable result sets fetch in blocks (MSSQL: server-side dynamic cursor per MS docs; Oracle: row prefetch; DB2: JCC block fetch; MariaDB: `UpdatableResult` in mariadb-java-client — mysql-connector-j throws `NotUpdatable` and is not the updatable-capable driver at runtime), so they stream but still perform a pointless full scan.
- `openTable(table, null)` from `PrimaryDataFromDb` (download) consumes rows and must stay unchanged.

## Goals / Non-Goals

**Goals:**

- Eliminate row-data transfer and heap buffering on the restore write-preparation path for all drivers
- Remove the per-table full scan cost on every restore
- Keep column metadata (names, types, type names, ordinal positions) identical so insert binding is unaffected

**Non-Goals:**

- Changing the download/export path
- Reworking the per-driver insert mechanisms (updatable RS for non-Postgres, prepared statements for Postgres)
- Changing transaction boundaries, table creation, or SIARD file reading

## Decisions

### D1: Empty-result SELECT for the upload path, not per-driver metadata queries

When `sm != null`, append a false predicate (`WHERE 1=0`) to the generated SELECT. The updatable result set then carries identical column metadata with zero rows: no driver buffers row data, no rows cross the wire, and `moveToInsertRow`/`updateObject`/`insertRow` semantics are unchanged because insert mode never depends on fetched rows.

Alternatives considered:

- **`DatabaseMetaData.getColumns` for metadata**: works for Postgres (which only needs metadata) but does not produce a `ResultSet` usable as the updatable insert vehicle for the other drivers — they would still need the SELECT, so this solves only half the problem and forks the code path.
- **Per-driver fixes** (e.g., `LIMIT 0` for Postgres only): leaves the full scan and any driver-side buffering in place for MSSQL/Oracle/DB2/MariaDB/Access; the shared `WHERE 1=0` fixes all drivers uniformly.
- **Prepared-statement inserts for all drivers**: rejected — same blast-radius reasoning as `fix-postgres-import-memory` D1; updatable-RS insert may be load-bearing on other databases.

### D2: Keep `openTable`'s signature; branch on `sm != null` inside

`sm == null` already distinguishes download from upload inside `openTable` (it also selects `CONCUR_READ_ONLY` vs `CONCUR_UPDATABLE`). Adding the predicate in the same place keeps the change to one method and makes the intent obvious: upload prepares writes, download reads rows.

### D3: Verify updatable insert on empty result sets per driver before relying on it

The spec scenario "Insert works on a row-free write context" pins the requirement, but each driver's updatable RS must be confirmed to accept `moveToInsertRow`/`updateObject`/`insertRow` when zero rows were fetched. JDBC does not forbid it (insert mode is independent of the current row), but driver quirks are possible — e.g., drivers that lazily initialize insert state on first `next()`, or MySQL's `addPrimaryColumn` SELECT-list rewrite in `MySqlStatement.executeQuery` (unaffected by a WHERE clause, but must be re-verified end-to-end).

## Risks / Trade-offs

- [A driver rejects `insertRow`/`moveToInsertRow` on an empty updatable result set → restore breaks for that DBMS] → Mitigation: per-driver verification tasks (unit-level where possible, integration tests where available) before merge; the change is one predicate — rollback is trivial
- [`WHERE 1=0` interacts with driver-specific SELECT rewriting (MySQL `addPrimaryColumn`, DB2 column-name workaround in `Db2ResultSet.insertRow`)] → Mitigation: verification tasks cover each rewriting driver; the predicate is appended after the FROM clause and does not alter the select list
- [Some driver ignores the predicate and still fetches (e.g., eager drivers)] → pgjdbc is the known eager case and honors it (zero rows returned); other drivers stream anyway — worst case is status quo, not regression
- [Two changes touch `PrimaryDataToDb`/`PrimaryDataTransfer`] → Sequence after `fix-postgres-import-memory` merges; overlap is small (the postgres branch vs. the shared SELECT builder)

## Migration Plan

No migration. Rollback = revert. Archives and restored databases are identical; only the write-preparation query changes. Sequence after `fix-postgres-import-memory` to avoid concurrent edits in `siard-cmd`.
