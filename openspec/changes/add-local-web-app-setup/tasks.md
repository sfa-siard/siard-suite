# Tasks

## 1. Verification first

- [ ] 1.1 Check the event typing variants in the order of design D6 (discriminatorMapping only → `@SchemaProperty` + shared constant + drift test → `OASFilter`) on a minimal backend with the `AgentEvent` sealed interface and `ConnectedEvent`, including confirming that SmallRye ignores `@JsonTypeInfo`/`@JsonSubTypes`; verify that the generated `openapi.yaml` gives `openapi-typescript` a discriminated union on which `switch (e.type)` narrows in a compiling TS snippet, then record the chosen variant and reason in design.md D6
- [ ] 1.2 Determine how a bean obtains the actual HTTP port via `io.quarkus.vertx.http.HttpServer` after `HttpServerStart` with `quarkus.http.port=0`; verify with a `@QuarkusTest` that the read port matches the port the test client connects to, and note the mechanism in design.md D3
- [ ] 1.3 Check whether `quarkus.smallrye-openapi.store-schema-directory` writes the schema during `quarkusDev` or only on build; verify by running both, and note the result in design.md D11

## 2. Module scaffolding and build

- [ ] 2.1 Add the `siard-suite-local-web-app:backend` and `:frontend` includes to `settings.gradle.kts` and set `archivesName` in both; verify `./gradlew projects` lists both nested projects
- [ ] 2.2 Add the Quarkus platform/plugin and the node-gradle plugin to `gradle/libs.versions.toml`, and create the backend build (Quarkus REST, Jackson, SmallRye Health, SmallRye OpenAPI, `siard-cmd` without logback, the PoC's JAXB/activation `strictly()` pins, test jvmArgs with the xerces property and `--add-opens`); verify `./gradlew :siard-suite-local-web-app:backend:build` succeeds
- [ ] 2.3 Create the frontend project (React, TS strict, Vite, MUI, React Router, TanStack Query, openapi-fetch/openapi-react-query, react-i18next, ESLint, Prettier, Vitest, RTL) with node-gradle tasks for install, build, lint, typecheck and test wired into `check`, and the jar packing `dist/` into `META-INF/resources`; verify `./gradlew :siard-suite-local-web-app:frontend:jar` contains `META-INF/resources/index.html`
- [ ] 2.4 Add a `@QuarkusTest` that opens `master.siard` via `ArchiveImpl` (no endpoint); verify it passes under `./gradlew :siard-suite-local-web-app:backend:test`
- [ ] 2.5 Update the `AGENTS.md` module table with the new module; verify it matches `settings.gradle.kts`

## 3. Agent core: binding, launch modes, session, access control

- [ ] 3.1 Configure `quarkus.http.host=127.0.0.1` (no profile prefix) and `quarkus.http.port=0` with `%dev` port 8080, and log the launch URL `http://127.0.0.1:<port>/launch?token=…` using the port from 1.2; verify with tests for the spec scenarios "Agent starts and logs its launch URL", "Port already in use is avoided" and "Not reachable from other network interfaces"
- [ ] 3.2 Implement launch-mode-based credentials (random single-use token and session secret; fixed `%dev` values only under `LaunchMode.DEVELOPMENT`; never configurable otherwise) and the `siard.agent.open-browser` / `siard.agent.idle-exit-grace` flags with per-mode defaults; verify with tests for all three "Fixed development credentials only in development mode" scenarios, including the packaged jar started with `-Dquarkus.profile=dev`
- [ ] 3.3 Implement `GET /launch` (constant-time check, invalidate on use, 303 with raw `Location: /`, `siard_session_<port>` HttpOnly SameSite=Strict cookie, `Cache-Control: no-store`, `Referrer-Policy: no-referrer`); verify with tests for the three "Session established by single-use launch token" scenarios
- [ ] 3.4 Implement the `/api/*` filter (session cookie + exact `Host: 127.0.0.1:<port>`) and the central problem-details `ExceptionMapper`; verify with tests for the "API access requires session and loopback host" and "Error responses" scenarios

## 4. Event stream and presence

- [ ] 4.1 Implement `GET /api/events` (SSE, `AgentEvent` with the variant from 1.1, `connected` first, 15 s keep-alive comments, close detection); verify with tests for the three "Event stream" scenarios
- [ ] 4.2 Implement counting of authorized streams and the idle exit timer (cancelled by a new stream, disabled per D8 defaults), with shutdown injected behind a bean so tests can replace it; verify with tests for the three "Exit when no window is connected" scenarios

## 5. Browser launch and instances

- [ ] 5.1 Implement browser launch per design D9 (Edge/Chrome install paths on Windows, executables on Linux/macOS, `--app`, then `Desktop.browse`, then log only), honouring `siard.agent.open-browser`; verify with unit tests for candidate resolution and the fallback order and with tests for the "Browser is opened at launch" scenarios that can run headless
- [ ] 5.2 Register the macOS `AppReopenedListener` that mints a new single-use token and opens a window (no-op on other OSes); verify with a unit test of the reopen action (new token differs, old one rejected); the Dock behaviour stays a manual check (Workflow follow-up)
- [ ] 5.3 Verify independent instances: a test starting two packaged-jar agents checks different ports, both healthy, one session rejected by the other agent, and only the agent whose stream closes exits ("Independent instances" scenarios)

## 6. System info and OpenAPI pipeline

- [ ] 6.1 Implement `GET /api/system/info` (`appVersion`, `javaVersion`, `jdbcDrivers`); verify with tests for the three "System information endpoint" scenarios
- [ ] 6.2 Store the generated schema as the committed `siard-suite-local-web-app/openapi.yaml`, add `npm run gen:api` (openapi-typescript) and a Gradle check that fails when the regenerated schema differs from the committed one; verify the check fails after changing a DTO field without regenerating and passes after regenerating

## 7. Frontend shell and About view

- [ ] 7.1 Add the MUI theme (colours from `siard-suite-app` `root.css`, Roboto), the app shell, React Router routes `/` and `/about`, and the backend SPA fallback for non-API GET paths; verify with tests for the "Frontend delivery" scenarios and a Vitest/RTL test rendering both routes
- [ ] 7.2 Add the one-time `.properties` → JSON conversion script and migrate the shell and About texts for de/fr/it/en, set up `react-i18next` with typed keys and browser-language detection with English fallback, without persistence; verify with Vitest tests for the three "Interface language" scenarios and a `tsc` failure on an unknown key
- [ ] 7.3 Implement the About view with the typed query on `/api/system/info`, the `error-message` and `session-required` states, and an `EventSource` connection to `/api/events` opened by the app shell; verify with Vitest/RTL tests for the three "About view" scenarios

## 8. End-to-end and packaging

- [ ] 8.1 Add the Playwright `e2eTest` Gradle task (Chromium) that starts the built backend jar with `open-browser=false` and a short grace period, reads the launch URL from the log, opens it, asserts the clean URL and the session cookie, checks that `/about` shows `SIARD Suite <version>`, closes the page and asserts the agent exits; verify `./gradlew :siard-suite-local-web-app:e2eTest` passes
- [ ] 8.2 Configure beryx `runtime`/`jpackage` with the PoC's launcher jvmArgs, jlink modules (incl. `java.desktop`, `jdk.zipfs`), fast-jar `installDist` staging and `patchAppImageCfg`, plus macOS `.dmg` options; verify `./gradlew :siard-suite-local-web-app:backend:jpackage` produces a `.deb` locally and that `java --list-modules` of the bundled runtime lists no `javafx` module

## 9. CI and documentation

- [ ] 9.1 Add the `local-web-app` job to `.github/workflows/build.yml` running `check` and `e2eTest` with a cached Playwright browser; verify the job passes on the PR
- [ ] 9.2 Add the installer workflow (matrix windows/ubuntu/macos, `pull_request` + `push: main` with `paths-ignore` for `siard-suite-app/**`, `docs/**`, `**/*.md`, plus `workflow_dispatch`, actions `@v6`, failing on a missing installer); verify the PR run uploads `.exe`/`.msi`, `.deb` and `.dmg`
- [ ] 9.3 Write `siard-suite-local-web-app/README.md`: dev workflow (quarkusDev + Vite, dev token, `127.0.0.1` URLs, Swagger UI, `gen:api`), the CI section stating that PRs trigger installer builds and why, and how to narrow the trigger later; verify every command in it runs as written

## Workflow follow-up

- Test an installer from the PR workflow on a BIT Windows machine: Edge availability and app mode, Edge policies, sleeping tabs vs SSE, grace-period value.
- Test the `.dmg` on a Mac: install, launch, browser window, Dock reopen (design D7); update D7 from "unverified" accordingly.
- Run `openspec-verify-change` and archive the change after review.
