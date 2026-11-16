# Tasks

## 1. Core change: row-free write-preparation SELECT

- [ ] 1.1 In `PrimaryDataTransfer.openTable`, append a false predicate (`WHERE 1=0`) to the SELECT when `sm != null` (upload path only); verify the generated SQL ends with the predicate and the download path (`sm == null`) is byte-identical to before
- [ ] 1.2 Add/adjust a unit test asserting the upload-path SELECT returns column metadata (names, types, ordinal positions) identical to the unfiltered SELECT while returning zero rows; verify it passes

## 2. Per-driver updatable-insert verification

- [ ] 2.1 Postgres: verify `rs.getMetaData()` on the empty updatable result set yields identical column metadata and the prepared-statement insert path is unaffected; extend `PostgresResultSetMemoryIT` (or add a case) so peak heap is sampled across the whole `uploadTable` call including `openTable`, not only inside the row loop; verify the test fails conceptually on the old path (documented) and passes now
- [ ] 2.2 MSSQL: verify `moveToInsertRow`/`updateObject`/`insertRow` succeed on an empty updatable result set via an integration test or targeted testcontainers check; verify a small restore round-trips
- [ ] 2.3 Oracle: same verification as 2.2 for the Oracle driver
- [ ] 2.4 DB2: same verification, additionally covering `Db2ResultSet.insertRow`'s special-character column-name workaround
- [ ] 2.5 MySQL/MariaDB: verify with the runtime driver (mariadb-java-client `UpdatableResult`), including `MySqlStatement.addPrimaryColumn`'s SELECT-list rewrite on tables without primary keys
- [ ] 2.6 MS Access: verify `insertRow` on an empty result set (jackcess cursor path)

## 3. Verification

- [ ] 3.1 `./gradlew build -x integrationTest`; unit tests pass
- [ ] 3.2 Run available per-DBMS integration suites (`integrationTestPostgres`, MySQL/MariaDB, MSSQL, Oracle, DB2, Access as configured); restore round-trips stay green
- [ ] 3.3 Self-review: change is confined to the upload branch of `openTable` (+ tests); no download-path or unrelated edits; sequence/merge note for `fix-postgres-import-memory` overlap in `siard-cmd`
