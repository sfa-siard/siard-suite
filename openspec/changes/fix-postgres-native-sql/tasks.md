# Tasks

## 1. Remove wrapper-level nativeSQL conversion

- [ ] 1.1 In `PostgresStatement.executeQuery(String)`, pass `sql` unchanged to `super.executeQuery` instead of `getConnection().nativeSQL(sql)`; remove the now-dead `_lParse`/`_lExecute` fields and their assignments (private, write-only, only ever measured the removed call); verify the method compiles and delegates the exact SQL string
- [ ] 1.2 In `PostgresStatement`, apply the same pass-through to the four `execute(String, …)` overloads (`execute(String)`, `execute(String, int)`, `execute(String, int[])`, `execute(String, String[])`); do NOT touch the `executeUpdate` overloads — they use `nativeSqlUpdate()` for DELETE large-object unlinking; verify each overload forwards the caller's SQL verbatim, including the `SQLFeatureNotSupportedException` mapping on SQLState `0A000`
- [ ] 1.3 In `PostgresConnection`, pass `sql` unchanged in all three `prepareCall` overloads instead of `nativeSQL(sql)`; verify `prepareCall` still returns the driver's `CallableStatement` and that `downgradeHoldability` still applies on the 4-arg overload
- [ ] 1.4 Verify no `nativeSQL(` call sites remain under `jdbc-postgres/src/main` (`grep -rn "nativeSQL(" jdbc-postgres/src/main` returns nothing) while `Connection.nativeSQL()` in `jdbc-base` and the other drivers' implementations are untouched

## 2. Tests

- [ ] 2.1 Add a unit test (mirroring `PostgresPreparedSqlTest`) asserting `PostgresStatement.executeQuery(String)` and the four `execute(String, …)` overloads hand the exact SQL string to the wrapped `Statement` and never call `nativeSQL`; verify it fails if `nativeSQL` is reintroduced
- [ ] 2.2 Add a unit test asserting all three `PostgresConnection.prepareCall` overloads pass the exact SQL string to the wrapped connection and never call `nativeSQL`; verify it covers the holdability-downgrade overload too
- [ ] 2.3 Check whether the existing Postgres testcontainers fixture makes a literal-`?` integration test cheap (e.g. `SELECT … WHERE jdata ? 'key'` against a jsonb column); if so add it and verify it returns the matching row — if not cheap, document the decision and rely on 2.1/2.2 plus pgjdbc's own coverage

## 3. Verification

- [ ] 3.1 Run `./gradlew :jdbc-postgres:test` (plus `build -x integrationTest` for `jdbc-postgres` if the module's test task differs) and verify all unit tests pass
- [ ] 3.2 If 2.3 added an integration test, run `./gradlew :siard-cmd:integrationTestPostgres --tests "*<NewTest>*"` (or the module's equivalent) and verify it passes; otherwise run the existing Postgres unit suite only
- [ ] 3.3 Self-review the diff: changes confined to `PostgresStatement`/`PostgresConnection` in `jdbc-postgres`, no edits to `executeUpdate`/`nativeSqlUpdate`, no changes to other drivers, no unrelated edits
