# Tasks

## 1. Streaming result sets

- [x] 1.1 In `PostgresConnection`, map `ResultSet.HOLD_CURSORS_OVER_COMMIT` to `ResultSet.CLOSE_CURSORS_AT_COMMIT` in `createStatement(3-arg)` and in `setHoldability` (store the downgraded value as connection default); add Javadoc noting the downgrade and why; verify `PostgresConnectionTest`-style unit coverage or a compile + targeted check that the wrapped statement receives `CLOSE_CURSORS_AT_COMMIT`
- [x] 1.2 Add a default `fetchSize` constant (start with 100) applied via `setFetchSize` on the wrapped statement in `PostgresStatement` and `PostgresPreparedStatement` construction (or first execution), preserving caller overrides; verify by inspection and unit test that `getFetchSize`/`setFetchSize` round-trip still works
- [x] 1.3 Run `./gradlew :siard-cmd:integrationTestPostgres --tests "*PostgresResultSetMemoryIT*"` and verify `exportPathConfiguration_doesNotBufferEntireResultSet` passes (heap delta below threshold) while the three mechanism tests still pass
- [x] 1.4 Run the full `./gradlew :siard-cmd:integrationTestPostgres` suite and verify existing Postgres export tests (`PostgresUploadDownloadSiardProjectIT` etc.) still pass — confirming interleaved catalog/large-object calls do not break cursor fetching

## 2. Metadata caching

- [x] 2.1 Cache resolved column type/type-name per column index in `PostgresResultSetMetaData` (lazily filled arrays sized to column count); verify unit test or new coverage that repeated `getColumnType`/`getColumnTypeName` calls on the same column issue at most one `getColumns` catalog query and different columns resolve independently

## 3. Mime-type handler leaks

- [x] 3.1 Add `MimeTypeHandler.clearIndividualMimeTypes()` and call it in `PrimaryDataFromDb`'s table loop after each record is put (D4); verify the map is empty after each record and archive output (mime types incl. array elements) is unchanged
- [x] 3.2 Ensure the `InputStream`/`Reader` passed to `Tika.detect` for `Blob`/`Clob` values is closed after detection (try-with-resources or finally at the call site in `PrimaryDataFromDb`/`MimeTypeHandler`); verify via code inspection plus an integration check that no large-object descriptors accumulate across cells

## 4. Verification

- [x] 4.1 Run `./gradlew build -x integrationTest` for affected modules (`jdbc-postgres`, `siard-cmd`) and unit tests; verify all pass
- [x] 4.2 Re-run `./gradlew :siard-cmd:integrationTestPostgres` in full; verify green including the new regression test
- [x] 4.3 Self-review the diff: changes confined to `jdbc-postgres` and `siard-cmd` export path, no behavior change for other drivers, no unrelated edits
- [x] 4.4 Add `PostgresExportStreamingIT` covering "export survives interleaved per-cell queries" (all 350 rows intact across fetch-size boundaries incl. oid-domain blob/clob, interval, int[] cells) and "no large-object descriptors remain open" (fd probe via Progress callback, max fd < 10; verified meaningful by failing with leaked fds 8..696 when stream closing was reverted)
