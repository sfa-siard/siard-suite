# SIARD Suite local web app

The local web app is the browser-based replacement for the JavaFX desktop UI (`siard-suite-app`).
It is a per-launch local agent plus a React frontend, packaged as a single installer.

## Prerequisites

- **JDK 21**
- Node.js is **not** required: the Gradle node plugin downloads the version pinned in
  `frontend/.nvmrc`. A local Node installation is still useful for `npm run dev` (Vite) and for
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
[`../openspec/changes/add-local-web-app-setup/design.md`](../openspec/changes/add-local-web-app-setup/design.md).

## Development

Terminal 1 (backend, port 8080, fixed dev token, hot restart):

    ./gradlew :siard-suite-local-web-app:backend:quarkusDev

Terminal 2 (frontend, Vite on `http://127.0.0.1:5173`, proxies `/api` and `/launch` to the backend):

    cd siard-suite-local-web-app/frontend && npm run dev

Open `http://127.0.0.1:5173/launch?token=dev-token` (the dev token lives in
`backend/src/main/resources/application.properties` and is only honoured in development mode).
Always use `127.0.0.1`, never `localhost` — the Host check only accepts `127.0.0.1`.

Swagger UI is available at `http://127.0.0.1:8080/q/swagger-ui` while `quarkusDev` runs.

After an API change, regenerate the committed description and the frontend types:

    ./gradlew :siard-suite-local-web-app:backend:openApiUpdate
    cd siard-suite-local-web-app/frontend && npm run gen:api

then commit `openapi.yaml`. `schema.d.ts` is generated, not committed.

## Checks

    ./gradlew :siard-suite-local-web-app:check

runs backend unit and packaged-agent tests, the OpenAPI drift check, ESLint, `tsc` and Vitest.

    ./gradlew :siard-suite-local-web-app:e2eTest

runs the Playwright smoke test (launch URL → clean URL → About shows the version → page closed →
agent exits) against the packaged agent. The first run downloads a pinned Chromium build.

## Packaging

Windows and Linux build installers via jpackage; macOS gets a runtime zip instead of an installer:

    ./gradlew :siard-suite-local-web-app:backend:jpackage    # .deb on Linux, installer on Windows
    ./gradlew :siard-suite-local-web-app:backend:runtimeZip  # macOS zip with a start script

Building the `.deb` locally needs `dpkg` and `fakeroot`.

## CI

Pull requests (and pushes to `main`) trigger the packaging workflow
(`local-web-app-packages.yml`), which builds the Windows/Linux installers and the macOS zip and
uploads them as artifacts. It runs on PRs because changes anywhere in the `siard-cmd` dependency
chain can break packaging; `siard-suite-app/**`, `docs/**` and markdown-only changes are ignored.
Once packaging has proven stable, the trigger can be narrowed.
