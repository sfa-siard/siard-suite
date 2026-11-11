# Tasks

## 1. Inventory and prepared-statement type translation

- [ ] 1.1 Read `PostgresResultSet.updateObject` end-to-end and list every type-specific branch (`PostgresBlob`, `PostgresClob`, `PostgresArray`, temporal/interval, `OTHER`/`STRUCT`, null handling); record the mapping in the PR/commit notes for parity review
- [ ] 1.2 Implement equivalent `setObject` translation in `PostgresPreparedStatement` (extract shared helper if practical); verify with a unit test per type branch that `setObject` produces the same bound value as `updateObject`
- [ ] 1.3 Verify module layering: confirm `siard-cmd` may reference `jdbc-postgres` types; if not, switch driver detection to `DatabaseMetaData.getDatabaseProductName()`; verify compile

## 2. Prepared-statement insert path

- [ ] 2.1 Add an insert-writer abstraction (or equivalent branch) in `PrimaryDataToDb`'s row-write loop: default = existing updatable-RS writer; Postgres = prepared `INSERT INTO <table> (<cols>) VALUES (?,…)` statement per table
- [ ] 2.2 Implement byte-aware batching: flush `executeBatch` at row-count cap (~100) or estimated byte budget (~16 MiB), whichever first; verify batch flush logic with a unit test using size-tracking values
- [ ] 2.3 Bind values per row via the new `setObject` path, using streams (`getBinaryStream`/`getAsciiStream`) instead of `getBytes()` for large binary/clob cells where the `Value` API supports it; verify no whole-cell `byte[]` is required for large LOBs
- [ ] 2.4 Resolve target column types once per table (cached metadata) and pass them to the binding logic; verify catalog queries scale with tables/columns, not cells
- [ ] 2.5 oid-domain `BLOB`/`CLOB`: create large object, stream contents with enlarged buffer (~256 KiB–1 MiB vs current 8 KiB), bind OID; verify LO descriptor count stays bounded across many cells

## 3. Tests

- [ ] 3.1 Add/extend a testcontainers Postgres integration test asserting bounded heap during restore of a `bytea`-heavy archive (mirror `PostgresResultSetMemoryIT` approach); verify it fails on the old path conceptually (documented) and passes with the new path
- [ ] 3.2 Extend or verify round-trip coverage for the full SIARD type matrix incl. `BLOB`/`CLOB`/`ARRAY`/temporal/`OTHER`; verify `PostgresUploadDownloadSiardProjectIT` passes unchanged
- [ ] 3.3 Verify transaction semantics unchanged: mid-restore failure rolls back the table as before

## 4. Verification

- [ ] 4.1 `./gradlew build -x integrationTest` for `jdbc-postgres` and `siard-cmd`; unit tests pass
- [ ] 4.2 `./gradlew :siard-cmd:integrationTestPostgres` full suite green, including new memory test and existing round-trip tests
- [ ] 4.3 Self-review: Postgres-scoped only, no changes to other drivers' insert path, no unrelated edits
