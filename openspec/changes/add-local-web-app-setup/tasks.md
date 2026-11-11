# Tasks

Test scope: see design.md D15. One test per spec scenario, nothing beyond; security scenarios tested thoroughly; ask before adding any other test.

## 1. Backend skeleton

- [x] 1.1 Add the `siard-suite-local-web-app:backend` and `:frontend` includes to `settings.gradle.kts` and set `archivesName` in both; verify `./gradlew projects` lists both nested projects
- [x] 1.2 Add the Quarkus platform/plugin and the node-gradle plugin to `gradle/libs.versions.toml`, and create the backend build (Quarkus REST, Jackson, SmallRye Health, SmallRye OpenAPI, `siard-cmd` without logback, the PoC's JAXB/activation `strictly()` pins, test jvmArgs with the xerces property and `--add-opens`); verify `./gradlew :siard-suite-local-web-app:backend:build` succeeds

## 2. Verification first

- [x] 2.1 Check the event typing variants in the order of design D6 (discriminatorMapping only → `@SchemaProperty` + shared constant + drift test → hand-written TypeScript event types) on a minimal backend with the `AgentEvent` sealed interface and `ConnectedEvent`, including confirming that SmallRye ignores `@JsonTypeInfo`/`@JsonSubTypes`; verify that the generated `openapi.yaml` gives `openapi-typescript` a discriminated union on which `switch (e.type)` narrows in a compiling TS snippet, then record the chosen variant and reason in design.md D6
- [x] 2.2 Determine how a bean obtains the actual HTTP port via `io.quarkus.vertx.http.HttpServer` after `HttpServerStart` with `quarkus.http.port=0`; verify by starting the backend once and comparing the read port with the port it actually listens on (no permanent test; covered later by the "Agent starts and logs its launch URL" test), and note the mechanism in design.md D3
- [x] 2.3 Check whether `quarkus.smallrye-openapi.store-schema-directory` writes the schema during `quarkusDev` or only on build; verify by running both, and note the result in design.md D11

## 3. Frontend project and remaining scaffolding

- [x] 3.1 Create the frontend project (React, TS strict, Vite, MUI, React Router, TanStack Query, openapi-fetch/openapi-react-query, react-i18next, ESLint, Prettier, Vitest, RTL) with node-gradle tasks for install, build, lint, typecheck and test wired into `check`, and the jar packing `dist/` into `META-INF/resources`, plus the backend's `implementation(project(":siard-suite-local-web-app:frontend"))` dependency so the built frontend ends up in the backend's `META-INF/resources`; verify `./gradlew :siard-suite-local-web-app:frontend:jar` contains `META-INF/resources/index.html` and that `GET /` on the backend started with `quarkusRun` (or in a `@QuarkusTest`) returns the built `index.html`
- [x] 3.2 Add a `@QuarkusTest` that opens the tracked fixture `siard-api/src/test/resources/testfiles/sample.siard` via `ArchiveImpl` (no endpoint); verify it passes under `./gradlew :siard-suite-local-web-app:backend:test`
- [x] 3.3 Update the `AGENTS.md` module table with the new module; verify it matches `settings.gradle.kts`

## 4. Agent core: binding, launch modes, session, access control

- [x] 4.1 Configure `quarkus.http.host=127.0.0.1` (no profile prefix) and `quarkus.http.port=0` with `%dev` port 8080, and log the launch URL `http://127.0.0.1:<port>/launch?token=…` using the port from 2.2; verify with tests for the spec scenarios "Agent starts and logs its launch URL" and "Not reachable from other network interfaces"
- [x] 4.2 Implement launch-mode-based credentials (random single-use token and session secret; fixed `%dev` values only under `LaunchMode.DEVELOPMENT`; never configurable otherwise) and the `siard.agent.open-browser` / `siard.agent.idle-exit-grace` flags with per-mode defaults; verify with tests for all three "Fixed development credentials only in development mode" scenarios, including the packaged jar started with `-Dquarkus.profile=dev`
- [x] 4.3 Implement `GET /launch` (constant-time check, 2-minute expiry with an injectable clock, invalidate on use, 303 with raw `Location: /`, failures uniformly 303 to `/launch-failed` without cookie, `siard_session_<port>` HttpOnly SameSite=Strict cookie, `Cache-Control: no-store`, `Referrer-Policy: no-referrer`); verify with tests for the "Session established by launch token" and "Rejected launch link" scenarios
- [x] 4.4 Implement the Host filter on every request (exact `Host: 127.0.0.1:<port>`, 403 problem details pointing to `http://127.0.0.1:<port>/`), the `/api/*` session-cookie filter and the central problem-details `ExceptionMapper`, with test base URI `127.0.0.1`; verify with tests for the "Loopback Host header required", "API access requires session" and "Error responses" scenarios

## 5. Event stream and presence

- [x] 5.1 Implement `GET /api/events` (SSE, `AgentEvent` with the variant from 2.1, `connected` first, 15 s keep-alive comments, close detection); verify with tests for the three "Event stream" scenarios
- [x] 5.2 Implement counting of authorized streams and the idle exit timer (cancelled by a new stream, disabled per D8 defaults), with shutdown injected behind a bean so tests can replace it; exit when no stream has connected by token expiry plus the grace period; verify with tests for the four "Exit when no window is connected" scenarios

## 6. Browser launch and instances

- [x] 6.1 Implement browser launch per design D9 (Edge/Chrome install paths on Windows, executables on Linux/macOS, `--app`, then `Desktop.browse`, then log only), honouring `siard.agent.open-browser`; verify with tests for the "Browser is opened at launch" scenarios that can run headless
- [x] 6.2 Verify independent instances: a test starting two packaged-jar agents checks different ports, both healthy, one session rejected by the other agent, and only the agent whose stream closes exits ("Independent instances" scenarios)

## 7. System info and OpenAPI pipeline

- [x] 7.1 Implement `GET /api/system/info` (`appVersion`, `javaVersion`, `jdbcDrivers`); verify with tests for the three "System information endpoint" scenarios
- [x] 7.2 Store the generated schema in the build directory and add `openApiUpdate`, which copies it to the committed `siard-suite-local-web-app/openapi.yaml` (with a constant `info.version` instead of the axion version, which contains the branch name, and with the auto-generated `servers` entries switched off, which differ between build and dev; candidate properties `quarkus.smallrye-openapi.info-version` and `quarkus.smallrye-openapi.auto-add-server`, to be confirmed), add `npm run gen:api` (openapi-typescript) and the Gradle check `openApiCheck` that fails when the generated schema differs from the committed one; verify the check fails after changing a DTO field without running `openApiUpdate` and passes after running it

## 8. Frontend shell and About view

- [x] 8.1 Add the MUI theme (colours from `siard-suite-app` `root.css`, Roboto), the app shell, React Router routes `/`, `/about` and `/launch-failed` (translated restart message), and the backend SPA fallback (only GET, only paths without a file extension in the last segment, never `/api/*`, `/q/*` or `/launch`); verify with tests for the "Frontend delivery" and "Rejected launch link message" scenarios and a Vitest/RTL test rendering all three routes
- [x] 8.2 Add the one-time `.properties` → JSON conversion script (reads ISO-8859-1 with `\uXXXX` escapes and line continuations, maps `%s`/`%d` and positional `%n$s` to named `{{argN}}`) and migrate the shell and About texts for de/fr/it/en, set up `react-i18next` with typed keys and browser-language detection with English fallback, without persistence; verify with Vitest tests for the three "Interface language" scenarios, and review the generated JSON (umlauts, placeholders) in the PR
- [x] 8.3 Implement the About view with the typed query on `/api/system/info`, the error and no-session states (shown as MUI `Alert`s, which have `role="alert"`), and an `EventSource` connection to `/api/events` opened by the app shell; verify with Vitest/RTL tests for the three "About view" scenarios that locate elements by role and text (e.g. `getByRole("alert")`), not by test IDs

## 9. End-to-end and packaging

- [x] 9.1 Add the Playwright `e2eTest` Gradle task (Chromium) that starts the built backend jar with `open-browser=false` and a short grace period, reads the launch URL from the log, opens it, asserts the clean URL and the session cookie, checks that `/about` shows `SIARD Suite <version>`, closes the page and asserts the agent exits; verify `./gradlew :siard-suite-local-web-app:e2eTest` passes
- [x] 9.2 Configure beryx `runtime`/`jpackage` with the PoC's launcher jvmArgs, jlink modules (incl. `java.desktop`, `jdk.zipfs`), fast-jar `installDist` staging and `patchAppImageCfg`, plus `runtimeZip` for macOS; verify `./gradlew :siard-suite-local-web-app:backend:jpackage` produces a `.deb` locally, that the start script in the `runtimeZip` output launches the agent with the fast-jar layout, and that `java --list-modules` of the bundled runtime lists no `javafx` module

## 10. CI and documentation

- [x] 10.1 Add the `local-web-app` job to `.github/workflows/build.yml` running `check` and `e2eTest` with a cached Playwright browser; verify the job passes on the PR
- [x] 10.2 Add the packaging workflow (matrix windows/ubuntu with `jpackage`, macos with `runtimeZip`, `pull_request` + `push: main` with `paths-ignore` for `siard-suite-app/**`, `docs/**`, `**/*.md`, plus `workflow_dispatch`, `concurrency` cancelling outdated PR runs, actions `@v6`, failing on a missing package); verify the PR run uploads `.exe`/`.msi`, `.deb` and the macOS zip, and that a second push in quick succession cancels the first run
- [x] 10.3 Write `siard-suite-local-web-app/README.md`: prerequisites (JDK 21; Node.js not required because the Gradle node plugin downloads it, but useful for `npm run dev`), a short architecture overview (agent, frontend, launch token → cookie, SSE presence) linking to this change's design.md, the dev workflow (quarkusDev + Vite, dev token, `127.0.0.1` URLs, Swagger UI, and after an API change `openApiUpdate` then `gen:api`), running `./gradlew :siard-suite-local-web-app:check` and `:e2eTest` (noting that the latter downloads a Chromium browser on first run), building the `.deb` locally with `:backend:jpackage` (needs `dpkg`/`fakeroot`), and the CI section stating that PRs trigger packaging builds, why, and how to narrow the trigger later; verify every command in it runs as written

## 11. Manual platform checks

Everything the automated tests cannot cover. Each check uses the build from the PR's packaging workflow (installer or zip) unless it says otherwise; tick it only after doing it on a real machine.

### Windows (BIT machine, installer from the PR workflow)

- [ ] 11.1 Install and start the app with Edge present: an Edge window without address bar (app mode) opens and shows the frontend; check that Edge policies on the BIT machine allow app mode
- [ ] 11.2 Browser lookup on a Windows machine without Edge (any Windows machine, not necessarily BIT): a machine-wide Chrome and a per-user Chrome (`%LOCALAPPDATA%`) each open in app mode; with neither Edge nor Chrome, the default browser opens via `Desktop.browse`
- [ ] 11.3 Leave the window in the background (sleeping tabs) for at least 30 minutes: the agent is still running and the window still works; use this to decide the grace-period value
- [ ] 11.4 Closing the window ends the agent within the grace period plus a few seconds (check Task Manager)

### macOS (zip from the PR workflow)

- [ ] 11.5 Extract the zip and run the start script: with Edge or Chrome in `/Applications`, an app-mode window opens and shows the frontend; repeat with the browser only in `~/Applications`
- [ ] 11.6 With no Chromium-family browser installed, the default browser (Safari) opens via `Desktop.browse`
- [ ] 11.7 Closing the window ends the agent within the grace period plus a few seconds

### Linux (`.deb` from the PR workflow)

- [ ] 11.8 Install the `.deb` and start the app in a desktop session, once under Wayland and once under X11: an app-mode window opens and shows the frontend (X11 done; Wayland open)
- [x] 11.9 Closing the window ends the agent within the grace period plus a few seconds

### All platforms (one platform is enough)

- [x] 11.10 Launch the app twice: two independent windows on different ports; closing one leaves the other working
- [x] 11.11 Start the packaged app with `-Dsiard.agent.open-browser=false` and do not open the launch URL: the agent exits about 2 minutes plus the grace period after start

### Development mode

- [x] 11.12 Run `quarkusDev` and `npm run dev`: `http://127.0.0.1:5173/launch?token=dev-token` (the dev token from `application.properties`) leads to the frontend through the Vite proxy, and the agent keeps running after the window is closed

### Look

- [ ] 11.13 Compare the web app with the desktop app side by side: header colour, background, text colours and the Roboto font (check in the browser's dev tools that the bundled font, not a system fallback, is used)

## Workflow follow-up

- Run `openspec-verify-change` and archive the change after review.
