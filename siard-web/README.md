# SIARD Suite web

The browser-based SIARD Suite apps and the code they share.

## Structure

| Path | Gradle project | npm package | Contents |
|---|---|---|---|
| `local/` | `:siard-web:local` (`backend`, `frontend`) | `@siard-web/local` | The local web app, see [`local/README.md`](local/README.md) |
| `backend-core/` | `:siard-web:backend-core` | – | Shared Quarkus library: error format (`ProblemDetail`, `ProblemExceptionMapper`) |
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
