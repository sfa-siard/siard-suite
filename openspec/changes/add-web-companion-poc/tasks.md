# Tasks

## 1. Module scaffolding

- [ ] 1.1 Register `siard-web-backend` and `siard-web-frontend` in `settings.gradle.kts` and verify `./gradlew projects` lists both modules
- [ ] 1.2 Add Quarkus plugin + platform BOM and the node-gradle plugin to `gradle/libs.versions.toml` (pin the latest Quarkus 3.x LTS; resolves design open question) and verify the version catalog resolves via `./gradlew dependencies`
- [ ] 1.3 Create `siard-web-backend/build.gradle.kts` (application plugin with `mainClass = "io.quarkus.bootstrap.runner.QuarkusEntryPoint"`, `quarkus.package.jar.type=uber-jar`, deps: quarkus-rest/jackson + `project(":siard-cmd")` — transitively shipping siard-api, sql-parser, siard-utilities, jdbc-base and all jdbc-* drivers per design D5) and verify `./gradlew :siard-web-backend:build` produces a runnable uber-jar under `build/libs` containing JDBC driver classes and merged `META-INF/services` entries
- [ ] 1.4 Create `siard-web-frontend/build.gradle.kts` (node plugin tasks `npm ci` + `vite build`; jar packs `dist/` into `META-INF/resources`) and verify `./gradlew :siard-web-frontend:jar` produces a jar containing `META-INF/resources/index.html`
- [ ] 1.5 Update the `AGENTS.md` monorepo module table with both new modules and verify the table matches `settings.gradle.kts` includes

## 2. Backend lifecycle and session token

- [ ] 2.1 Implement session-token generation at startup and a `ContainerRequestFilter` requiring the token (bearer or `?token=`) on `/api/*` while exempting static resources and health; verify via `@QuarkusTest` that missing and invalid tokens return 401 and a valid token passes
- [ ] 2.2 Implement launch behavior: bind 127.0.0.1 on a random free port, open the default browser at the token URL on startup, and log the URL when no browser is available; verify a manual launch opens the browser (or logs the URL) and `/q/health` returns 200 without a token
- [ ] 2.3 Implement `POST /api/system/shutdown` returning 204 and terminating the process via `Quarkus.asyncExit()`; verify via `@QuarkusTest` that a valid token returns 204 and invalid/missing tokens return 401 (process exit verified manually in task 5.2)
- [ ] 2.4 At startup enumerate `DriverManager.getDrivers()` and log the registered JDBC drivers; verify a packaged/dev run lists all vendor drivers (postgres, mysql, mssql, oracle, db2, access/ucanaccess), proving the full chain loads inside the uber-jar

## 3. Archive browsing API

- [ ] 3.1 Implement `POST /api/archives` accepting `{path}`, opening the file via `ArchiveImpl`, registering it under a generated id, and returning 201 with `{id, name}`; verify via `@QuarkusTest` that `master.siard` returns 201, a nonexistent path returns 400, a non-SIARD file returns 400, and a token-less request returns 401
- [ ] 3.2 Implement `GET /api/archives/{id}/tree` returning `schemas[].name`, `schemas[].tables[]` (`name`, `rowCount`, `columns[]` with `name`/`type`); verify via `@QuarkusTest` that the `master.siard` tree matches its actual metadata, an unknown id returns 404, and a token-less request returns 401

## 4. Frontend

- [ ] 4.1 Scaffold the React + TypeScript + Vite app in `siard-web-frontend` (npm via the Gradle node plugin) with a dev proxy mapping `/api` to `localhost:8080`; verify `npm run dev` serves the page and proxying reaches a running backend
- [ ] 4.2 Implement the single screen: path input + open action + schema/table/column tree rendering, token captured from the launch URL and sent on every `/api/*` call, and an error message display for rejected paths; verify manually that opening `master.siard` renders its tree and a bad path shows an error without rendering
- [ ] 4.3 Add a Vitest smoke test covering token capture and API-call construction; verify `npm test` passes inside the Gradle build
- [ ] 4.4 Wire `siard-web-backend` to serve the frontend jar (`implementation(project(":siard-web-frontend"))`) and verify `GET /` on the packaged backend returns the built `index.html`

## 5. Platform packaging

- [ ] 5.1 Configure beryx `runtime` + `jpackage` for `siard-web-backend`: launcher `jvmArgs` with the `xercesSaxParserFactory` property and `--add-opens` for `java.xml` xerces/xalan and `java.base/java.lang` (no `javafx.graphics` opens); module list including `java.desktop` (required for `Desktop.browse`); verify `./gradlew :siard-web-backend:jpackage` produces the app image/installer on a Windows machine or `windows-latest` runner
- [ ] 5.2 Install and launch the packaged agent on Windows and verify the browser opens at the token URL, archive browsing works end-to-end, `POST /api/system/shutdown` stops the process, and the bundled runtime contains no `javafx.*` modules (`java --list-modules` on the bundled runtime)
- [ ] 5.3 Build the `.deb` on the dev machine (`./gradlew :siard-web-backend:jpackage` on Linux with `dpkg` tooling available), install it locally, and verify the same end-to-end behavior as 5.2 — browser opens at the token URL, archive browsing works, shutdown stops the process, no `javafx.*` modules in the bundled runtime

## Workflow follow-up

- Extend `deliverables.yml` to publish `siard-web-backend` artifacts once the PoC is accepted (deferred per design open question).
- Archive the change after the project's review requirements are satisfied.
