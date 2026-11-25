# Tasks

## 1. JDBC URL codec

- [ ] 1.1 Add `siard-web/local/frontend/src/views/jdbcUrl.ts` with `encodeJdbcUrl` and `decodeJdbcUrl` for `postgresql`, `mysql`, `db2`, `oracle`, `sqlserver` (design D1, D2); verify with `jdbcUrl.test.ts`: encode and round-trip decode of each format with and without options, and `undefined` for a wrong DBMS prefix, a missing port, a missing database / `databaseName=` part and a non-JDBC string (`npm test` in `siard-web/local/frontend`)

## 2. Connection details form

- [ ] 2.1 Add the i18n keys `connections.fields.jdbcUrl` and `connections.invalidJdbcUrl` to `siard-web/frontend-shared/src/i18n/{de,fr,it,en}.json`; verify all four files carry both keys and `npm test` in `siard-web/frontend-shared` passes
- [ ] 2.2 Add the JDBC URL input and two-way sync to `ConnectionDetailsView.tsx` (design D3, D4): derived on load, re-encoded on host/port/dbName/options edits, decoded on URL edits, invalid URL shows the error and blocks submit, URL never in the request body
- [ ] 2.3 Extend `ConnectionDetailsView.test.tsx` with the scenarios of "JDBC URL kept in sync with the connection inputs": derived URL for each DBMS, input edits update URL, URL edits update inputs (user/password/name untouched), saved MS SQL connection shows its URL, invalid URL error + no POST + rebuilt on field edit, payload without URL/password, no URL input on 401; verify `npm test` passes
- [ ] 2.4 Update `siard-web/local/frontend/e2e/archive-wizard.spec.ts` for the modified layout scenarios (desktop: URL row after options and before the connection name; 360 px: stack order includes JDBC URL, no horizontal overflow); verify `npm run e2e` passes locally
- [ ] 2.5 Run `npm run lint` and `npm run typecheck` in `siard-web/local/frontend` and `./gradlew :siard-web:check`; verify all succeed

## 3. Cross-platform check (user step)

- [ ] 3.1 User: push the branch and check that the siard-web browser-test CI jobs for Linux, macOS and Windows are green (in particular `archive-wizard.spec.ts`). The agent does not mark this done without the user's report.
