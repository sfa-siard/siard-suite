# Proposal

## Why

The Swiss Federal Archives (SFA) cannot install the local web app on their laptops. They need a server-hosted web app (**remote web app**) that lets them open existing `.siard` files in the browser; archiving databases is not needed there. The remote web app should reuse the archive-browsing API and UI of the local web app instead of duplicating them. Today everything lives in one module, `siard-suite-local-web-app`, whose backend is built around the loopback agent and whose frontend is a single app. Before any browsing feature is written, the code that both apps will share needs a home, and the code that must never be shared (loopback security, JDBC drivers) needs a boundary. Doing this as a pure move now keeps the later feature changes small.

## What Changes

- **Move** `siard-suite-local-web-app` to `siard-web/local` (Gradle `:siard-web:local:backend` / `:siard-web:local:frontend`). No behaviour change: same API, installers, installer names and CI coverage.
- **New backend module `siard-web/backend-core`** (`:siard-web:backend-core`): a library for code both web apps use. It depends on `siard-api` and Quarkus only, never on `siard-cmd` or any JDBC driver. This change moves the app-agnostic RFC 9457 error format (`ProblemDetail`, `ProblemExceptionMapper`) and the `siard-api` runtime requirements (JAXB/activation pins, JVM flags) into it. The local backend depends on it.
- **New npm workspace at `siard-web/`** with one lockfile and one Node version, and a new package `siard-web/frontend-shared` (`:siard-web:frontend-shared`) consumed as TypeScript source. It receives the app-agnostic frontend code: theme and fonts, i18n setup and texts, the typed API client factory and shared test helpers. The local frontend consumes it. Views, routes and the app shell stay in the local app.
- **Boundary checks**: the build fails if `backend-core` gets a dependency on `siard-cmd` or a JDBC driver, and lint fails if `frontend-shared` imports from an app.
- **CI, docs and tooling paths** (`build.yml`, the packaging workflow, `mise.toml`, `AGENTS.md`, module README) updated to the new locations.

Out of scope: any new endpoint, view or feature (see `add-archive-browsing-core`), the remote web app itself (`siard-web/remote` is created by its own change), and any change to `siard-suite-app`.

**Precondition:** `add-local-web-app-setup` is archived first. That change is still open with manual platform checks; moving the module under it would invalidate its artifacts' paths. Archiving first also puts its capabilities into `openspec/specs/`, where this change relies on them being unchanged.

## Capabilities

### New Capabilities

- `web-module-boundaries`: which code the web apps may share and which they must keep apart: the dependency boundary of the shared backend core, the dependency boundary of the shared frontend package, and the guarantee that the local web app behaves as before the move.

### Modified Capabilities

None. The requirements of the local web app's capabilities (`local-web-agent`, `local-web-ui`, `system-info`) do not change; the move is verified by their existing scenarios still passing.

## Impact

- **Moved code**: everything under `siard-suite-local-web-app/` to `siard-web/local/`; `openapi.yaml` moves with it.
- **New code**: `siard-web/backend-core`, `siard-web/frontend-shared`, `siard-web/package.json` (workspace root) and `package-lock.json`.
- **Build**: `settings.gradle.kts` includes, Gradle paths `:siard-suite-local-web-app:*` become `:siard-web:local:*`, the node-gradle setup moves to the workspace root, `.nvmrc` moves to `siard-web/`.
- **CI**: `.github/workflows/build.yml` (task paths, Playwright cache key from the lockfile), `.github/workflows/local-web-app-packages.yml` (artifact paths).
- **Docs/tooling**: `AGENTS.md` module table, `mise.toml` comment, the module README (dev commands, structure).
- **Unchanged**: the application's runtime behaviour, installer names (`SIARD-Suite-Web`), the Java package root `ch.admin.bar.siardsuite.localwebapp`, and the API.
