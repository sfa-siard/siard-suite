# Tasks

## 1. Backend: DBMS model port

- [x] 1.1 Port the connection model into `ch.admin.bar.siardsuite.localwebapp.database` (`Dbms`, `DbmsConnectionProperties`, `ServerBasedDbms(ConnectionProperties)`, `FileBasedDbms(ConnectionProperties)`, `DbmsConnectionData`, `DbmsRegistry`) in modern style — plain classes/records, no Lombok, no `CastHelper`/`OptionalHelper`, all six DBMS entries with their JDBC URL encoders/decoders and example values. Verify: `./gradlew :siard-web:local:backend:compileJava` succeeds.
- [x] 1.2 Add `DbmsRegistryTest` covering: six registered DBMS, five server-based, PostgreSQL encoder produces `jdbc:postgresql://host:port/db?options`, MS Access entry uses `jdbc:access:` encoding. Verify: `./gradlew :siard-web:local:backend:test --tests DbmsRegistryTest` passes.

## 2. Backend: SQLite store

- [x] 2.1 Add `sqlite-jdbc` (Apache-2.0, pinned version in `gradle/libs.versions.toml`, published >7 days) to `siard-web/local/backend/build.gradle.kts`. Verify: `./gradlew :siard-web:local:backend:dependencies --configuration runtimeClasspath` lists `org.xerial:sqlite-jdbc`.
- [x] 2.2 Implement the database-path resolver: `siard.connections.db` config override, else per-OS app-data dir (`%APPDATA%` on Windows, `~/Library/Application Support` on macOS, `${XDG_DATA_HOME:-~/.local/share}` on Linux) under `siard-suite-web/`, directory created user-only. Unit test the resolver per OS branch. Verify: `./gradlew :siard-web:local:backend:test --tests '*Path*'` passes.
- [x] 2.3 Implement `SavedConnection` record (no password field), `ConnectionStore` interface (`list`, `create`, `delete`, `existsByName`) and `SqliteConnectionStore` with schema creation on init, `UNIQUE` name constraint mapped to a duplicate-name exception, `file` column nullable. Unit tests with a temp-dir database cover CRUD, ordering by name, duplicate rejection. Verify: `./gradlew :siard-web:local:backend:test --tests '*ConnectionStore*'` passes.
- [x] 2.4 Register the store as a CDI bean and initialize the schema at startup. Verify: a `@QuarkusTest` boots the app and `GET /api/connections` returns 200 `[]` on a fresh profile.

## 3. Backend: REST API

- [x] 3.1 Implement `GET /api/dbms` returning the five server-based catalog entries (`id`, `name`, `kind`, `fields`, example values as placeholders). `@QuarkusTest` covers: 200 with exactly 5 entries, 401 without session, no file-based DBMS. Verify: `./gradlew :siard-web:local:backend:test --tests '*DbmsResource*'` passes.
- [x] 3.2 Implement `GET /api/connections`, `POST /api/connections` (201; 400 on missing name/unknown DBMS/non-numeric port; 409 on duplicate name) and `DELETE /api/connections/{id}` (204; 404 unknown id), all returning problem-details on error and never containing a password. `@QuarkusTest` covers every spec scenario including session rejection. Verify: `./gradlew :siard-web:local:backend:test --tests '*ConnectionsResource*'` passes.
- [x] 3.3 Run `./gradlew :siard-web:local:backend:openApiUpdate`, review the `openapi.yaml` diff, and verify `./gradlew :siard-web:local:backend:openApiCheck` passes.

## 4. Frontend: API client, i18n, views

- [x] 4.1 Regenerate the typed API client with `cd siard-web/local/frontend && npm run gen:api` (writes `src/api/schema.d.ts` from `../openapi.yaml`). Verify: the generated types include `/api/dbms` and `/api/connections` and `npm run typecheck` passes.
- [x] 4.2 Add `archive` and `connections` i18n keys to `frontend-shared/src/i18n/{de,fr,it,en}.json` covering picker, details form, delete confirmation, empty and error states. Verify: no missing-key warnings in the frontend tests.
- [x] 4.3 Implement `ArchiveView` at `/archive`: saved-connections list with name + target summary, delete `IconButton` with MUI confirm `Dialog`, DBMS grid from the catalog, loading/error/empty states. RTL tests cover: both sections render, selection navigates with correct query param, delete confirm/cancel, empty state, untrusted query param not rendered, API error state. Verify: `cd siard-web/local/frontend && npm test` passes for the new spec.
- [x] 4.4 Implement `ConnectionDetailsView` at `/archive/connection`: descriptor-driven form, prefill for `?connection=` (password empty, DBMS locked), placeholders for `?dbms=`, inline validation, optional-name save gating (`POST` only when named), password held client-side and never sent, return to `/archive` on submit, unknown-id error state. RTL tests cover the same spec scenarios. Verify: `cd siard-web/local/frontend && npm test` passes.
- [x] 4.5 Wire routes in `App.tsx` and make the Start page Archive button navigate to `/archive` (Open/Upload/Export stay inert). Update `StartView` tests for the new Archive behavior. Verify: `App.test.tsx`/`StartView.test.tsx` pass.

## 5. Integration verification

- [x] 5.1 Run `./gradlew :siard-web:check` — backend tests, `verifyCoreBoundary`, `openApiCheck`, frontend lint/typecheck/tests all pass.
- [x] 5.2 Manual smoke on this machine: start the backend (`quarkusDev` or packaged run), open the launch URL, exercise picker → details → save → delete, restart the agent and confirm the profile persists. Report the result.

## 6. Connection details form refinement

The completed tasks above record the original implementation; the following tasks carry the revised form requirements into code.

- [x] 6.1 Add failing `ConnectionDetailsView.test.tsx` cases for all five DBMS default ports and `localhost` as actual editable values, other fields initially empty, edited values retained after rerender, saved custom values and empty optional values preserved, password empty on reopen, no DBMS input or save toggle, optional name last, and password exclusion from requests. Retain validation and unknown-identifier coverage. Verify: `cd siard-web/local/frontend && npm test -- ConnectionDetailsView.test.tsx` passes after 6.2 and 6.3.
- [x] 6.2 Initialize new forms with `localhost` and the selected DBMS's existing catalog port value (`5432`, `3306`, `1433`, `1521`, `50000` respectively for PostgreSQL, MySQL, Microsoft SQL Server, Oracle, DB/2); do not prefill other fields or overwrite saved values/user edits. Remove the disabled DBMS input, retain its identity in page state and the heading, and preserve optional-name save gating without a toggle. Verify: the targeted tests from 6.1 pass for initialization, saved profiles and credential handling.
- [ ] 6.3 Implement the desktop-inspired responsive layout: server plus compact port upper left, database name upper right, username below server, password below database name, existing additional fields next, optional connection name last and full-width. Stack fields in the specified order on narrow screens; retain localized labels and the existing wizard action bar. Do not add schema or connection-URL functionality. Verify: targeted tests pass and manually inspect the form at 1280 by 800 and 360 by 800 CSS pixels, confirming field positions, order, reachability and no horizontal overflow.
- [ ] 6.4 Run `./gradlew :siard-web:check` and the mandatory architecture, QM-Guide, spec-to-test and security reviews (security last) for the form refinement. Record results and resolve blocking findings before archiving. Verify the new defaults can be edited and saved, saved custom values survive reopening, passwords remain absent from API payloads, and unnamed submission still does not save.

## 7. Code review fixes

- [x] 7.1 Saving an opened saved profile updates it via new `PUT /api/connections/{id}` (`ConnectionStore.update`) instead of re-creating it; `existsByName` removed as unused.
- [x] 7.2 409 shown as a name-field error; save error cleared on resubmit; form remounts when `?dbms=`/`?connection=` changes; delete error shown inside the confirm dialog.
- [x] 7.3 Ports validated as 1–65535 (backend and frontend); duplicate names detected via `SQLITE_CONSTRAINT_UNIQUE` result code.
- [x] 7.4 Store open is atomic (connection kept only after schema and permissions succeed); an existing database directory keeps its permissions.

## Workflow follow-up

- CI runs the test suites on Windows, macOS and Linux — if the pipeline is green on all three, the cross-platform requirement is verified; the agent does not mark that done without the user's report.
- Archive the change after review requirements are satisfied.
