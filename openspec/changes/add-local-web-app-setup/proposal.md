# Proposal

## Why

The BIT (Swiss Federal Office of IT) does not allow packages containing JavaFX on devices used by federal employees, so the JavaFX desktop app `siard-suite-app` cannot be rolled out to its main audience. The plan is to replace it step by step with a React/TypeScript frontend backed by a local Quarkus agent that reuses the existing `siard-cmd`/`siard-api` core. A proof of concept (branch `poc/executable-web-companion-application`) validated the stack. This change sets up the production foundation that every later feature builds on: modules, agent lifecycle, security model, API conventions, frontend foundations, packaging and CI.

## What Changes

- **New nested module `siard-suite-local-web-app`** with subprojects `backend` (Quarkus, JVM mode) and `frontend` (React + TypeScript + Vite). The frontend is packaged as a resources jar and served by the backend.
- **Local agent lifecycle**: binds to `127.0.0.1` on an OS-assigned free port, opens the browser at a launch URL with a single-use token, and exits once no browser window has been connected for a grace period. Every launch is an independent instance, like the desktop app.
- **Session security**: the single-use launch token is exchanged for a port-specific HttpOnly, `SameSite=Strict` session cookie. All `/api/*` requests require the cookie and a `127.0.0.1:<port>` Host header.
- **Server-sent event stream** `/api/events`: a typed event channel per window that also serves as the presence signal for the idle exit. Later job-progress events reuse it.
- **Thin end-to-end feature**: `GET /api/system/info` (app version, Java version, registered JDBC drivers) and a React About view that reimplements the desktop app's info dialog.
- **API conventions**: unversioned `/api/...`, record DTOs, RFC 9457 problem-details errors. The OpenAPI description is committed to the repo and the frontend's TypeScript types are generated from it.
- **Frontend foundations**: MUI themed after the desktop app's look, routing, data fetching, i18n for de/fr/it/en with the browser language chosen at every start, linting and tests. Only the shell and About texts are translated in this change.
- **Packaging**: native installers (Windows `.exe`/`.msi`, Linux `.deb`, macOS `.dmg`) with a bundled JRE without JavaFX, reusing the packaging workarounds proven in the PoC.
- **CI**: a web-app job in `build.yml` (unit, component, OpenAPI drift and Playwright smoke tests) and a separate installer workflow for all three platforms. Release deliverables (`deliverables.yml`) stay unchanged until feature parity.

Out of scope: archive browsing, database archiving and upload, the job model, a dedicated browser profile, and any change to `siard-suite-app`.

## Capabilities

### New Capabilities

- `local-web-agent`: the locally running agent: loopback server, launch and browser opening, session establishment and API access control, presence-based exit, independent instances, launch modes, error format, and native installer packaging.
- `local-web-ui`: app-wide frontend behaviour: delivery of the frontend (including client-side routes surviving a reload) and the interface language.
- `system-info`: reporting the agent's version and runtime information through the API and showing it in the frontend's About view.

### Modified Capabilities

None. The existing `siard-cli` capability is not affected.

## Impact

- **New code**: `siard-suite-local-web-app/backend`, `siard-suite-local-web-app/frontend`, and `siard-suite-local-web-app/openapi.yaml`.
- **Build**: `settings.gradle.kts` (nested includes) and `gradle/libs.versions.toml` (Quarkus platform, node-gradle plugin).
- **CI**: new job in `.github/workflows/build.yml` and a new installer workflow. `deliverables.yml` is untouched.
- **Docs**: `AGENTS.md` module table, plus a README for the new module covering the dev workflow and CI behaviour.
- **Dependencies**: Quarkus (REST, Jackson, SmallRye Health, SmallRye OpenAPI); npm packages for React, MUI, React Router, TanStack Query, openapi-typescript/openapi-fetch, react-i18next, Vitest, Testing Library, Playwright, ESLint and Prettier.
- **No changes** to `siard-suite-app`, `siard-cmd`, `siard-api` or the `jdbc-*` modules.
