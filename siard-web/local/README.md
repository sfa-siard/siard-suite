# SIARD Suite local web app

The local web app is the browser-based replacement for the JavaFX desktop UI (`siard-suite-app`).
It is a per-launch local agent plus a React frontend, packaged as a single installer.

## Prerequisites

- **JDK 21**
- Node.js is **not** required: the Gradle node plugin downloads the version pinned in
  `siard-web/.nvmrc`. A local Node installation is still useful for `npm run dev` (Vite) and for
  running the frontend's lint/typecheck without Gradle.

## Architecture

- **`backend/`** — a Quarkus agent embedding `siard-cmd`/`siard-api`. It binds to `127.0.0.1` on a
  random port, opens a browser in app mode and serves the frontend and the `/api` endpoints.
- **`frontend/`** — React + TypeScript (strict) + Vite + MUI. The built `dist/` is packed into a
  jar under `META-INF/resources`, which the backend serves; the installer therefore contains a
  single app.
- **Session model** — at startup the agent logs a launch URL
  `http://127.0.0.1:<port>/launch?token=…`. The single-use, 2-minute token is exchanged for an
  `HttpOnly` session cookie; every request must have the exact `Host: 127.0.0.1:<port>` header and
  every `/api/*` call must carry the cookie.
- **Presence** — the app shell holds one SSE stream (`GET /api/events`). When the last stream
  closes, the agent exits after a grace period.
- **`openapi.yaml`** — the committed API description. The frontend generates its TypeScript types
  from it; `openApiCheck` (part of `check`) fails when it drifts.

The decisions behind this are documented in
[`../../openspec/changes/archive/2026-11-16-add-local-web-app-setup/design.md`](../../openspec/changes/archive/2026-11-16-add-local-web-app-setup/design.md).

## Development

From the repository root, `just web-dev` (alias `just wd`) starts both servers below and opens
the launch URL in the browser once they are up.

Terminal 1 (backend, port 8080, fixed dev token, hot restart):

    ./gradlew :siard-web:local:backend:quarkusDev

Terminal 2 (frontend, Vite on `http://127.0.0.1:5173`, proxies `/api` and `/launch` to the backend):

    cd siard-web/local/frontend && npm run dev

Open `http://127.0.0.1:5173/launch?token=dev-token` (the dev token lives in
`backend/src/main/resources/application.properties` and is only honoured in development mode).
Always use `127.0.0.1`, never `localhost` — the Host check only accepts `127.0.0.1`.

Swagger UI is available at `http://127.0.0.1:8080/q/swagger-ui` while `quarkusDev` runs.

After an API change, regenerate the committed description and the frontend types:

    ./gradlew :siard-web:local:backend:openApiUpdate
    cd siard-web/local/frontend && npm run gen:api

then commit `openapi.yaml`. `schema.d.ts` is generated, not committed.

## Checks

    ./gradlew :siard-web:local:check

runs backend unit and packaged-agent tests, the OpenAPI drift check, ESLint, `tsc` and Vitest.

    ./gradlew :siard-web:local:e2eTest

runs the Playwright smoke test (launch URL → clean URL → About shows the version → page closed →
agent exits) against the packaged agent. The first run downloads a pinned Chromium build.

## Packaging

Windows and Linux build installers via jpackage; every OS also gets a runtime zip (bundled runtime
plus a start script). On macOS the zip is the deliverable, as there is no installer; on Windows
and Linux the zips are built for parity with the desktop app's deliverables and are not
distributed yet.

    ./gradlew :siard-web:local:backend:jpackage    # .deb on Linux, installer on Windows
    ./gradlew :siard-web:local:backend:runtimeZip  # zip with a start script, per OS

Building the `.deb` locally needs `dpkg` and `fakeroot`.

### Known limitations

- The Windows runtime zip is untested.
- On Windows, the zip starts the agent through `SIARD-Suite-Web.bat`, which keeps a console
  window open while the agent runs; closing that window ends the agent. The installer has no
  console window.

## CI

The `local-web-app` job of `.github/workflows/ci.yml` runs `./gradlew :siard-web:check` and the
Playwright smoke test. The packaging workflow (`.github/workflows/packages.yml`, on pushes to
`main`, `v*` tags and manual dispatch) builds the local app's distribution zip together with the
other modules' packages; on `v*` tags and manual dispatch it also builds the Windows and Linux
runtime zips and installers.
