# Design

## Context

See `proposal.md` (Why). Facts that shape the approach, verified in the repo:

- `siard-suite-local-web-app` has two Gradle subprojects. The backend depends on `siard-cmd` (all JDBC drivers) and embeds the frontend jar. The Gradle aggregate tasks `check` and `e2eTest` live in the module's own `build.gradle.kts`.
- The frontend is a stand-alone npm project (own `package-lock.json`, `.nvmrc`), built through the node-gradle plugin with `npmInstallCommand = ci` and `download = true`. Its Gradle input list names `../openapi.yaml`.
- Files that name the old paths outside the module: `settings.gradle.kts`, `.github/workflows/build.yml` (task paths and the Playwright cache key from `package-lock.json`), `.github/workflows/local-web-app-packages.yml` (artifact globs), `mise.toml` (comment pointing at `.nvmrc`), `AGENTS.md` (module table), `harness-notes.md` (historical, left alone). The open change `add-local-web-app-setup` names them in prose.
- App-agnostic pieces today: backend `ProblemDetail` and `ProblemExceptionMapper` import only Jackson, SLF4J and Jakarta. Frontend `theme.ts`, `fonts.css` plus font files, `i18n/` (setup, four JSON files, typed-keys declaration), `api/client.ts` (fetch client, `ApiError`), `test/render.tsx` and `test/setup.ts`, and `scripts/ConvertMessages.java`.
- Local-only pieces that must stay local: everything in `agent/`, `events/`, `web/RequestFilters`, `LaunchResource`, `SpaFallback`, and `system/` (`SystemResource` reports `jdbcDrivers` and uses `siard-cmd`'s `VersionsExplorer`, so it cannot be shared and the remote app will get its own system endpoint). On the frontend: `App.tsx`, `views/*`, `api/events.ts`.
- There is no `buildSrc`/`build-logic`; shared Gradle values are passed via `extra[...]` in the root build (`xercesSaxParserFactory`).

## Goals / Non-Goals

**Goals:**
- A pure move plus extraction: the local app behaves and ships exactly as before, verified by its existing tests.
- A shared backend library and a shared frontend package that the next change (`add-archive-browsing-core`) and the remote app can build on without touching the local app's security model.
- Boundaries that are enforced by the build, not by convention.

**Non-Goals:**
- Any new endpoint, view or behaviour.
- Creating `siard-web/remote`. Its own change adds it.
- Moving local-only code into the shared parts "because remote will probably want it". Extraction is by what is app-agnostic today; later changes extend the shared parts when they need to.
- Changing the OpenAPI pipeline (D11 of the setup change). Only the paths move.

## Decisions

### R1: Layout: shared parts as siblings, each app keeps its backend and frontend together

```
siard-web/
  package.json, package-lock.json, .nvmrc    npm workspace root
  backend-core/        :siard-web:backend-core      Java library, siard-api + Quarkus only
  frontend-shared/     :siard-web:frontend-shared   npm package, TypeScript source
  local/
    backend/           :siard-web:local:backend
    frontend/          :siard-web:local:frontend
    openapi.yaml, build.gradle.kts (aggregates)
  remote/              (added by its own change)
```

The local app moves intact to `siard-web/local`. An app's backend embeds that same app's frontend jar (as today), so the two stay side by side, and the existing `check`/`e2eTest` aggregates simply move with it.

*Alternative:* `siard-web/{backend,frontend}/{core,local,remote}` (layers first). It splits each deliverable over two trees and makes the local move bigger without any gain: a backend never embeds another app's frontend.

### R2: `backend-core` is a Java library with `api` dependencies for what siard-api needs

`backend-core` uses `java-library`. Its `api` dependencies are `siard-api`, the JAXB/activation `strictly()` pins (`jaxb-runtime`, `jaxb-core`, `txw2`, `jakarta.activation-api`) and Quarkus REST/Jackson/OpenAPI. Pins declared as `api` propagate to the consuming apps, so the remote app gets the same `siard-api` runtime fix without copying it. Each app still applies `platform(libs.quarkus.bom)` itself (advisory, not enforced, see setup change D2). The JVM flags `siard-api` needs (xerces property, `--add-opens`) stay defined once in the root build's `extra` next to `xercesSaxParserFactory`, and are applied by each app's `Test` task and launcher.

*Alternative:* a convention plugin in `buildSrc`. The repo has none, and two consumers do not justify introducing it.

### R3: The core's boundary is enforced by a Gradle check

`backend-core` gets a `verifyCoreBoundary` task wired into `check`. It resolves `runtimeClasspath` and fails with the offending coordinates when it finds `siard-cmd`, a `jdbc-*` project, or a known driver group (`org.postgresql`, `com.mysql`, `com.microsoft.sqlserver`, `org.mariadb.jdbc`, `com.oracle.*`, `com.ibm.db2`, `com.mysitex.sx.lib`, `com.healthmarketscience.jackcess`). A JUnit test asserts `DriverManager.getDrivers()` is empty. A check on the compiled classes asserts there is no reference to `ch.admin.bar.siardsuite.localwebapp`.

Why a build check: the point of the boundary is that a server must not be able to open outbound database connections with user-supplied credentials. A dependency slipping in via a convenience import should be impossible to merge, not only discouraged.

### R4: Bean and endpoint discovery across the module boundary

Quarkus does not discover CDI beans or JAX-RS resources in a dependency jar unless it is indexed. The core's `ProblemExceptionMapper` is such a provider. Options: (a) `quarkus.index-dependency.<name>.group-id/artifact-id` in each app's `application.properties`, (b) an empty `META-INF/beans.xml` in the core, (c) the Jandex Gradle plugin. Chosen for the first attempt: **(b) `beans.xml`**, because it needs no per-app configuration that a new app could forget and no new plugin. This is **unverified** (task 2.1): it must be proven that the mapper is active in the local app and, for the next change, that SmallRye OpenAPI scans resources from the core. If (b) does not cover OpenAPI scanning, fall back to (a) and document it in the module README.

### R5: One npm workspace, one Node, one lockfile

`siard-web/package.json` declares `workspaces: ["frontend-shared", "local/frontend"]`; the remote app's change adds `remote/frontend`. The lockfile and `.nvmrc` live at `siard-web/`; `mise.toml` and the node-gradle `version` read the new location. `npmInstall` (`npm ci`) runs once at the workspace root, in a Gradle project that owns it; the frontend projects' `lint`, `typecheck`, `test`, `build` tasks depend on it and run `npm run <script> --workspace <name>`.

*Alternative:* a `file:` dependency without workspaces. It leaves two lockfiles and two installs, and nothing guarantees one React/MUI version across apps; two copies of React in one bundle break hooks.

### R6: `frontend-shared` is consumed as TypeScript source

The package exports `src/index.ts` directly; there is no build step. Vite, Vitest, `tsc` and ESLint of the app compile it together with the app's own code. Workspaces symlink the package, which resolves to a real path outside `node_modules`, so tools treat it as source.

*Alternative:* build the package as a library (Vite lib mode) and consume `dist/`. It adds a build ordering problem in Gradle, a second declaration file output, and a way for app and package to drift without the compiler noticing.

### R7: What moves into `frontend-shared` and how it is parametrised

| Moves | Notes |
|---|---|
| `theme.ts`, `fonts.css`, font files | the app passes the theme to its own `ThemeProvider`; an app may extend or replace it |
| `i18n/` (setup, de/en/fr/it JSON, `i18next.d.ts`) | the desktop-migrated texts are app-agnostic by origin. The three local-only texts (rejected launch link, lost session, load error) stay in the same files for now (see Risks) |
| `api/client.ts` | becomes `createApiClient<Paths>()` returning `{ fetchClient, $api }` so each app instantiates it with its own generated `paths`; `ApiError` and `isUnauthorized` move as is |
| `test/render.tsx`, `test/setup.ts` | shared test helpers |
| `scripts/ConvertMessages.java` | goes with the i18n texts it produces |

Stays in `local/frontend`: `App.tsx`, `main.tsx`, `views/*`, `api/events.ts`, `api/schema.d.ts` (generated from `../openapi.yaml`), Playwright e2e. A shared **shell** is deliberately not extracted: the remote app may have different design requirements (open question of the exploration), and a shell can be extracted later from two real examples.

### R8: The shared frontend boundary is a lint rule

`frontend-shared` has no dependency on any app package, and its ESLint config sets `no-restricted-imports` for paths into `local/` and `remote/` and for the app package names. Because the package is source-only, the rule runs as part of its normal `lint`. A grep-style test asserts that the strings `siard_session_` and `/launch` do not occur in its source, so session handling cannot creep into the shared parts.

### R9: Gradle task surface

- `:siard-web:check` aggregates everything (`backend-core`, `frontend-shared`, `local`); `:siard-web:local:check` and `:siard-web:local:e2eTest` replace the old module-level aggregates.
- The CI job `local-web-app` runs `:siard-web:check` then `:siard-web:local:e2eTest`. The Playwright cache key hashes `siard-web/package-lock.json`.
- The packaging workflow's artifact globs change to `siard-web/local/backend/build/...`; its trigger rules and `paths-ignore` stay as designed in the setup change (D14).

### R10: Sequencing and history

This change is applied after `add-local-web-app-setup` is archived. Its artifacts keep naming the old paths as history. The setup change's capabilities contain no module paths (checked), so no spec delta is needed.

## Risks / Trade-offs

- **[Move breaks git history of the module]** → move with `git mv` in a commit that contains no content changes, extraction in a separate commit, so `git log --follow` and review stay readable.
- **[`beans.xml` indexing does not cover everything (OpenAPI scan, resource discovery)]** → verified first in task 2.1 with a throw-away resource; fallback R4(a).
- **[Strict version pins as `api` could conflict in a future Quarkus-only app]** → they exist because of `siard-api`'s javax JAXB use; they are in the core precisely so every consumer of `siard-api` gets them.
- **[Shared i18n JSON holds three local-only texts]** → accepted; the texts are about the loopback launch. If the remote app needs its own error texts, split by i18next namespace then (a mechanical change).
- **[One npm lockfile means both apps upgrade React/MUI together]** → intended: it is what keeps the shared views working in both.
- **[Fixed Windows/macOS behaviour of node-gradle with a workspace root]** → CI already runs the packaging build on all three OSes; no local-only verification is accepted for this change.
- **[Playwright and Vite config paths change (`../openapi.yaml`, `.nvmrc`)]** → the move task re-runs `e2eTest`; the spec's "unchanged" scenarios compare test counts before and after.

## Migration Plan

1. Archive `add-local-web-app-setup`.
2. Move (pure `git mv`), then fix paths until the existing checks pass.
3. Create the core and the shared package, move the app-agnostic code, repoint the local app.
4. Update CI, docs, tooling.

Rollback: revert the commits; nothing is published, and `deliverables.yml` is untouched.

## Open Questions

- Package names for the workspace (`@siard-web/shared`, `@siard-web/local`) are an implementation choice made in task 3.1; nothing else depends on them.
