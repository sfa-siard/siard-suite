# Tasks

## 1. Streaming result sets

- [ ] 1.1 In `PostgresConnection`, map `ResultSet.HOLD_CURSORS_OVER_COMMIT` to `ResultSet.CLOSE_CURSORS_AT_COMMIT` in `createStatement(3-arg)` and in `setHoldability` (store the downgraded value as connection default); add Javadoc noting the downgrade and why; verify `PostgresConnectionTest`-style unit coverage or a compile + targeted check that the wrapped statement receives `CLOSE_CURSORS_AT_COMMIT`
- [ ] 1.2 Add a default `fetchSize` constant (start with 100) applied via `setFetchSize` on the wrapped statement in `PostgresStatement` and `PostgresPreparedStatement` construction (or first execution), preserving caller overrides; verify by inspection and unit test that `getFetchSize`/`setFetchSize` round-trip still works
- [ ] 1.3 Run `./gradlew :siard-cmd:integrationTestPostgres --tests "*PostgresResultSetMemoryIT*"` and verify `exportPathConfiguration_doesNotBufferEntireResultSet` passes (heap delta below threshold) while the three mechanism tests still pass
- [ ] 1.4 Run the full `./gradlew :siard-cmd:integrationTestPostgres` suite and verify existing Postgres export tests (`PostgresUploadDownloadSiardProjectIT` etc.) still pass — confirming interleaved catalog/large-object calls do not break cursor fetching

## 2. Metadata caching

- [ ] 2.1 Cache resolved column type/type-name per column index in `PostgresResultSetMetaData` (lazily filled arrays sized to column count); verify unit test or new coverage that repeated `getColumnType`/`getColumnTypeName` calls on the same column issue at most one `getColumns` catalog query and different columns resolve independently

## 3. Mime-type handler leaks

- [ ] 3.1 Remove per-cell entries from `MimeTypeHandler.individualMimeTypes` once `applyMimeType` consumes them (or restructure to avoid retention); verify the map is empty/unreachable after each cell's mime type is applied
- [ ] 3.2 Ensure the `InputStream`/`Reader` passed to `Tika.detect` for `Blob`/`Clob` values is closed after detection (try-with-resources or finally at the call site in `PrimaryDataFromDb`/`MimeTypeHandler`); verify via code inspection plus an integration check that no large-object descriptors accumulate across cells

## 4. Verification

- [ ] 4.1 Run `./gradlew build -x integrationTest` for affected modules (`jdbc-postgres`, `siard-cmd`) and unit tests; verify all pass
- [ ] 4.2 Re-run `./gradlew :siard-cmd:integrationTestPostgres` in full; verify green including the new regression test
- [ ] 4.3 Self-review the diff: changes confined to `jdbc-postgres` and `siard-cmd` export path, no behavior change for other drivers, no unrelated edits
