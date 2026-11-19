# SIARD Suite web

The browser-based SIARD Suite apps and the code they share.

## Structure

| Path | Gradle project | npm package | Contents |
|---|---|---|---|
| `local/` | `:siard-web:local` (`backend`, `frontend`) | `@siard-web/local` | The local web app, see [`local/README.md`](local/README.md) |
| `backend-core/` | `:siard-web:backend-core` | – | Shared Quarkus library: error format (`ProblemDetail`, `ProblemException`), archive registry and archive API (`/api/archives/**`) |
| `frontend-shared/` | `:siard-web:frontend-shared` | `@siard-web/shared` | Shared frontend sources: theme, fonts, i18n, `createApiClient<Paths>()`, test helpers |

`siard-web/` is also the npm workspace root: one Node version (`.nvmrc`), one `package-lock.json`,
one `node_modules/`, and the shared ESLint, Prettier and `tsconfig.base.json` configuration.
Gradle downloads Node and runs a single `npm ci` (`:siard-web:npmInstall`) for all packages.
To install the packages without Gradle:

    cd siard-web && npm ci

## Where shared code goes

Code moves to `backend-core` or `frontend-shared` only when it is app-agnostic today, not because
a future app will probably need it. Session handling, the launch flow and the presence channel
(`/api/events`) belong to the local app.

- **Backend**: `backend-core` depends on `siard-api` and Quarkus only. Its CDI beans and JAX-RS
  providers are picked up by the apps through its empty `META-INF/beans.xml`. An app that uses the
  core must declare the JAXB/activation `strictly()` pins itself (see the comment in
  `backend-core/build.gradle.kts`) and pass `extra["siardApiJvmArgs"]` to its tests and launchers.
- **Frontend**: `frontend-shared` is source-only (no build step); apps import it as
  `@siard-web/shared`, `@siard-web/shared/test` and `@siard-web/shared/test/setup`. React, MUI and
  i18next are peer dependencies.

## Archive registry: what an app provides

The core keeps open archives in `ArchiveRegistry` and serves them under `/api/archives/{archiveId}`.
An app provides:

1. **One `ScopeResolver` bean** that maps each `/api/archives/**` request to an `ArchiveScope`
   (none: `401`). There is no default, so an app without one fails at startup. Locally,
   `LocalScopeResolver` returns one fixed scope per agent instance.
2. **A way to obtain archive files** (a picker locally, an upload remotely), calling
   `register(scope, path, onRelease)` for each and passing the returned id to the frontend.
   `register` throws `IOException` for a file that is not a SIARD archive and
   `ProblemException(429)` when a limit is reached; let the latter propagate.
3. **An `onRelease` callback** that frees what the app holds for the file (e.g. deletes an
   uploaded copy). It runs exactly once on every path: failed registration, `DELETE`, idle
   eviction and shutdown. It must not throw or call back into the registry.

Configuration (defaults): `siard.archives.max-per-scope` (5), `max-total` (50), `idle-timeout`
(30 min), `lock-timeout` (30 s).

## Shared API description

`backend-core/openapi.yaml` is the committed description of the shared API; `frontend-shared`
generates its types from it. Each app's own description must contain the `/api/archives/**` paths
and their schemas unchanged (checked by a test in the app). After changing the shared API, run

    ./gradlew :siard-web:backend-core:openApiUpdate :siard-web:local:backend:openApiUpdate

and commit both `openapi.yaml` files. `openApiCheck`, part of each module's `check`, fails while
one is out of date.

## Checks

    ./gradlew :siard-web:check

runs the checks of `backend-core`, `frontend-shared` and every web app, including the boundary checks:

- `verifyCoreBoundary` fails when `siard-cmd`, a `jdbc-*` module, a web app or a JDBC driver is on
  the core's runtime classpath, and lists the offending projects, modules or jars.
- `CoreBoundaryTest` fails when a JDBC driver is registered in the core's tests or a compiled core
  class references the local app's package (`ch.admin.bar.siardsuite.localwebapp`).
- ESLint (`no-restricted-imports`) fails when a file in `frontend-shared` imports from `local/`,
  `remote/` or an app package.
- `frontend-shared/src/boundary.test.ts` fails when the shared sources contain `siard_session_` or
  `/launch`.
- `SharedApiDescriptionTest` (local backend) fails when the local app's description differs from
  `backend-core/openapi.yaml` in an `/api/archives/**` path or a schema it references.
