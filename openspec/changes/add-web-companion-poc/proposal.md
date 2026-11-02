# Proposal

## Why

The BIT (Swiss Federal Office of IT) does not allow packages containing JavaFX on devices used by federal employees. The current `siard-suite-app` is a JavaFX desktop application and therefore cannot be rolled out to its primary target audience. The long-term goal is to replace the JavaFX frontend with a web frontend served by a local agent process. This change is a **proof of concept** that validates the target architecture end-to-end: a Quarkus-based local server reusing the existing `siard-api`/`siard-cmd` core, a React web frontend, and packaging as a Windows installer via jpackage.

## What Changes

- **New module `siard-web-backend`**: Quarkus application (JVM mode, no GraalVM) that starts on a random localhost port, serves a REST API over `siard-api`, opens the system browser on launch, and supports explicit shutdown. It depends on `siard-cmd`, transitively shipping the full SIARD chain (`siard-api`, `sql-parser`, `siard-utilities`, `jdbc-base`, all `jdbc-*` with drivers) — so a BIT security scan of the PoC package covers the real dependency surface. The entire chain is JavaFX-free, so the deliverable is BIT-compliant by construction.
- **New module `siard-web-frontend`**: React + Vite application, built via Gradle (node plugin), packaged as a jar exposing the production build under `META-INF/resources` so the backend serves it without extra wiring.
- **REST API for archive browsing (PoC slice)**: open a `.siard` file from the local filesystem, read its schema/table/column metadata tree via `Archive` (`siard-api`), expose it to the frontend. Uses the existing `master.siard` fixture for verification.
- **Session token protection**: the backend issues a one-time token at launch; all `/api/*` requests require it (defense against cross-site requests to localhost / DNS rebinding).
- **Native packaging**: the backend is packaged with the existing beryx `runtime`/`jpackage` toolchain (same as `siard-suite-app`) producing an installable `.exe`/`.msi` on Windows and `.deb` on Linux, each containing a bundled JRE — no JavaFX modules.
- **Coexistence**: `siard-suite-app` remains untouched and ships as its own artifact. The web companion is a parallel deliverable, not a replacement in this change.

Out of scope for the PoC: JDBC-based archive upload/download, job progress streaming, i18n, accessibility conformance, GraalVM native builds, reimplementation of the 26 existing FXML screens.

## Capabilities

### New Capabilities

- `local-web-agent`: The locally running companion process — starts a web server bound to localhost on a random port, issues a launch token, opens the system browser, serves the frontend and REST API, and shuts down on request. Also covers packaging the agent as an installable Windows artifact.
- `archive-browsing`: Opening a `.siard` archive located on the local filesystem through the agent and exposing its metadata tree (schemas, tables, columns) via the REST API for display in the web frontend.

### Modified Capabilities

<!-- None — openspec/specs/ is empty; no existing capability requirements change. -->

## Impact

- **New code only** — two new Gradle modules added to `settings.gradle.kts`; no existing module's code is modified. The `AGENTS.md` monorepo module table is updated to document the two new modules.
- **Dependencies added**: Quarkus platform/BOM and `quarkus-rest`/jackson in `siard-web-backend`; a Gradle node/npm plugin in `siard-web-frontend`; additions to `gradle/libs.versions.toml`.
- **Build/CI**: the existing `deliverables.yml` matrix pattern can later produce `siard-web-backend` artifacts; PoC verifies `jpackage` locally/CI on `windows-latest`.
- **No runtime/API breaking changes** to `siard-suite-app`, `siard-cmd`, or `siard-api`.
