# Tasks

## 1. Backend: DBMS model port

- [ ] 1.1 Port the connection model into `ch.admin.bar.siardsuite.localwebapp.database` (`Dbms`, `DbmsConnectionProperties`, `ServerBasedDbms(ConnectionProperties)`, `FileBasedDbms(ConnectionProperties)`, `DbmsConnectionData`, `DbmsRegistry`) in modern style — plain classes/records, no Lombok, no `CastHelper`/`OptionalHelper`, all six DBMS entries with their JDBC URL encoders/decoders and example values. Verify: `./gradlew :siard-web:local:backend:compileJava` succeeds.
- [ ] 1.2 Add `DbmsRegistryTest` covering: six registered DBMS, five server-based, PostgreSQL encoder produces `jdbc:postgresql://host:port/db?options`, MS Access entry uses `jdbc:access:` encoding. Verify: `./gradlew :siard-web:local:backend:test --tests DbmsRegistryTest` passes.

## 2. Backend: SQLite store

- [ ] 2.1 Add `sqlite-jdbc` (Apache-2.0, pinned version in `gradle/libs.versions.toml`, published >7 days) to `siard-web/local/backend/build.gradle.kts`. Verify: `./gradlew :siard-web:local:backend:dependencies --configuration runtimeClasspath` lists `org.xerial:sqlite-jdbc`.
- [ ] 2.2 Implement the database-path resolver: `siard.connections.db` config override, else per-OS app-data dir (`%APPDATA%` on Windows, `~/Library/Application Support` on macOS, `${XDG_DATA_HOME:-~/.local/share}` on Linux) under `siard-suite-web/`, directory created user-only. Unit test the resolver per OS branch. Verify: `./gradlew :siard-web:local:backend:test --tests '*Path*'` passes.
- [ ] 2.3 Implement `SavedConnection` record (no password field), `ConnectionStore` interface (`list`, `create`, `delete`, `existsByName`) and `SqliteConnectionStore` with schema creation on init, `UNIQUE` name constraint mapped to a duplicate-name exception, `file` column nullable. Unit tests with a temp-dir database cover CRUD, ordering by name, duplicate rejection. Verify: `./gradlew :siard-web:local:backend:test --tests '*ConnectionStore*'` passes.
- [ ] 2.4 Register the store as a CDI bean and initialize the schema at startup. Verify: a `@QuarkusTest` boots the app and `GET /api/connections` returns 200 `[]` on a fresh profile.

## 3. Backend: REST API

- [ ] 3.1 Implement `GET /api/dbms` returning the five server-based catalog entries (`id`, `name`, `kind`, `fields`, example values as placeholders). `@QuarkusTest` covers: 200 with exactly 5 entries, 401 without session, no file-based DBMS. Verify: `./gradlew :siard-web:local:backend:test --tests '*DbmsResource*'` passes.
- [ ] 3.2 Implement `GET /api/connections`, `POST /api/connections` (201; 400 on missing name/unknown DBMS/non-numeric port; 409 on duplicate name) and `DELETE /api/connections/{id}` (204; 404 unknown id), all returning problem-details on error and never containing a password. `@QuarkusTest` covers every spec scenario including session rejection. Verify: `./gradlew :siard-web:local:backend:test --tests '*ConnectionsResource*'` passes.
- [ ] 3.3 Run `./gradlew :siard-web:local:backend:openApiUpdate`, review the `openapi.yaml` diff, and verify `./gradlew :siard-web:local:backend:openApiCheck` passes.

## 4. Frontend: API client, i18n, views

- [ ] 4.1 Regenerate the typed API client with `cd siard-web/local/frontend && npm run gen:api` (writes `src/api/schema.d.ts` from `../openapi.yaml`). Verify: the generated types include `/api/dbms` and `/api/connections` and `npm run typecheck` passes.
- [ ] 4.2 Add `archive` and `connections` i18n keys to `frontend-shared/src/i18n/{de,fr,it,en}.json` covering picker, details form, delete confirmation, empty and error states. Verify: no missing-key warnings in the frontend tests.
- [ ] 4.3 Implement `ArchiveView` at `/archive`: saved-connections list with name + target summary, delete `IconButton` with MUI confirm `Dialog`, DBMS grid from the catalog, loading/error/empty states. RTL tests cover: both sections render, selection navigates with correct query param, delete confirm/cancel, empty state, untrusted query param not rendered, API error state. Verify: `cd siard-web/local/frontend && npm test` passes for the new spec.
- [ ] 4.4 Implement `ConnectionDetailsView` at `/archive/connection`: descriptor-driven form, prefill for `?connection=` (password empty, DBMS locked), placeholders for `?dbms=`, inline validation, optional-name save gating (`POST` only when named), password held client-side and never sent, return to `/archive` on submit, unknown-id error state. RTL tests cover the same spec scenarios. Verify: `cd siard-web/local/frontend && npm test` passes.
- [ ] 4.5 Wire routes in `App.tsx` and make the Start page Archive button navigate to `/archive` (Open/Upload/Export stay inert). Update `StartView` tests for the new Archive behavior. Verify: `App.test.tsx`/`StartView.test.tsx` pass.

## 5. Integration verification

- [ ] 5.1 Run `./gradlew :siard-web:check` — backend tests, `verifyCoreBoundary`, `openApiCheck`, frontend lint/typecheck/tests all pass.
- [ ] 5.2 Manual smoke on this machine: start the backend (`quarkusDev` or packaged run), open the launch URL, exercise picker → details → save → delete, restart the agent and confirm the profile persists. Report the result.

## Workflow follow-up

- CI runs the test suites on Windows, macOS and Linux — if the pipeline is green on all three, the cross-platform requirement is verified; the agent does not mark that done without the user's report.
- Archive the change after review requirements are satisfied.
