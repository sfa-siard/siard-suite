# Tasks

Precondition: `add-local-web-app-setup` is archived (design R10). Test scope as in the setup change: one test per spec scenario, nothing beyond; ask before adding any other test.

## 1. Baseline and pure move

- [x] 1.1 Record the baseline on the pre-move tree: number of executed backend tests (`:siard-suite-local-web-app:backend:test` and `quarkusIntTest`), frontend tests and the Playwright test, and a copy of `openapi.yaml`; verify the numbers are recorded below
  - Baseline (commit `8b22cf8a`): backend `test` 34; `quarkusIntTest` 12 (1 skipped: `PackagedAgentIT.notReachableFromOtherNetworkInterfaces`, no non-loopback IPv4 address in the sandboxed build environment); frontend 9 tests in 4 files; Playwright 1. `openapi.yaml` sha256 `1bbc212eff454b71d3db201d53a712b68ae687054747e52a95957cf2421ac7c8` (the file at `8b22cf8a` is the reference copy)
- [x] 1.2 `git mv siard-suite-local-web-app siard-web/local` in a commit with no content changes, then update `settings.gradle.kts` to `siard-web:local:backend` / `siard-web:local:frontend` and fix only the paths needed for the build (`../openapi.yaml` references, `.nvmrc`, README links); verify `./gradlew projects` lists the new paths and no `:siard-suite-local-web-app` project
- [x] 1.3 Verify `./gradlew :siard-web:local:check` and `:siard-web:local:e2eTest` pass with the baseline test counts and that `openApiCheck` passes with a byte-identical `openapi.yaml`
  - After the move: backend `test` 34; `quarkusIntTest` 12 (same test skipped); frontend 9 tests in 4 files; Playwright 1; `openApiCheck` passes and `openapi.yaml` has the same sha256

## 2. Backend core

- [x] 2.1 Create `siard-web/backend-core` (`java-library`, `api` on `siard-api`, the JAXB/activation `strictly()` pins and Quarkus REST/Jackson/OpenAPI, empty `META-INF/beans.xml`) and make the local backend depend on it; before moving code, prove with a throw-away resource that CDI beans and a JAX-RS resource in the core are active in the local app and appear in the generated OpenAPI, and record the result in design R4 (fall back to `quarkus.index-dependency` if not)
- [x] 2.2 Move `ProblemDetail` and `ProblemExceptionMapper` into the core (new package `ch.admin.bar.siardsuite.webcore.error`) and move their existing tests (`ErrorResponseTest`); verify `:siard-web:backend-core:test` and the local app's error-format scenarios pass
- [x] 2.3 Add `verifyCoreBoundary` (R3) to the core's `check`, the empty-`DriverManager` test and the no-reference-to-`localwebapp` test; verify each of the three boundary scenarios by temporarily adding `siard-cmd` to the core and confirming `check` fails with the dependency named, then removing it
- [x] 2.4 Define the `siard-api` JVM flags once as `extra["siardApiJvmArgs"]` in `siard-web/build.gradle.kts` (design R2) and apply them in the core's tests and the local backend; verify `SiardApiIntegrationTest` still passes

## 3. Frontend workspace and shared package

- [x] 3.1 Create `siard-web/package.json` (workspaces, scripts), move `package-lock.json`, `.nvmrc` and the shared tool configs (`tsconfig.base`, ESLint, Prettier) to the workspace root, point node-gradle at it and move `npmInstall` to one Gradle project; verify `./gradlew :siard-web:local:frontend:frontendBuild` succeeds from a clean checkout and that only one `npm ci` runs for a whole `:siard-web:check`
- [x] 3.2 Create `siard-web/frontend-shared` (source-only, `exports: src/index.ts`), move `theme.ts`, `fonts.css` with the font files, `i18n/`, `test/render.tsx`, `test/setup.ts` and `scripts/ConvertMessages.java`, and repoint the local frontend's imports; verify `tsc`, ESLint and the moved tests pass in both packages
- [x] 3.3 Turn `api/client.ts` into `createApiClient<Paths>()` in the shared package and instantiate it in the local frontend with its generated `paths`; verify the typed hooks still compile (`$api.useQuery("get", "/api/system/info")`) and the existing 401-handling tests pass
- [x] 3.4 Add the `no-restricted-imports` rule (R8) and the check for `siard_session_` and `/launch` to `frontend-shared`; verify both boundary scenarios by temporarily importing from the local frontend and confirming lint fails, then reverting
- [x] 3.5 Add `frontend-shared` Gradle tasks (`lint`, `typecheck`, `test`) wired into its `check`, and the aggregate `:siard-web:check`; verify `./gradlew :siard-web:check` runs the shared package's tests once and passes

## 4. CI, documentation and tooling

- [x] 4.1 Update `.github/workflows/ci.yml` (job `local-web-app`: task paths, Playwright cache key from `siard-web/package-lock.json`) and `.github/workflows/packages.yml` (task paths, artifact globs); verify with a PR run of `ci.yml` that checks and e2e pass, and with a `workflow_dispatch` run of `packages.yml` on the branch (it has no PR trigger) that the dist job and the Windows/Linux runtime jobs pass and upload the same artifacts as before (`SIARD-Suite-Web-<version>.zip`, runtime zips, `.deb`, `.msi`)
  - PR run of `ci.yml` green; `workflow_dispatch` run of `packages.yml` green, each of the four upload steps (dists, Windows and Linux runtime zips, `.deb`, `.msi`) uploaded 3 files (desktop app, `siard-cmd`, web app); the Linux web runtime zip was started and works (manual check)
- [x] 4.2 Update `AGENTS.md` (module table: `siard-web/local`, `siard-web/backend-core`, `siard-web/frontend-shared`, with the boundary rules in one sentence), `mise.toml` comment, and the module README (structure, dev commands, where shared code goes, how the boundary checks fail); verify every command in the README runs as written and `./gradlew projects` matches the `AGENTS.md` table
- [x] 4.3 Final integration check: from a clean clone run `./gradlew :siard-web:check :siard-web:local:e2eTest` and compare the executed test counts with the recorded baseline; verify they match
  - Clean clone of `2b88b6a6` (section 4 changes only CI and docs): local backend `test` 32 + backend-core `test` 5 (the 2 moved error tests plus 1 core error test and 2 boundary tests; the local `ErrorResponseTest` keeps 1, see the `local-web-agent` delta) = all 34 baseline tests still run; `quarkusIntTest` 12 (same test skipped); local frontend 9 tests in 4 files; frontend-shared 1 (boundary); Playwright 1; `openapi.yaml` sha256 unchanged

## Workflow follow-up

- Archive the change after review; `add-archive-browsing-core` builds on the resulting layout.
