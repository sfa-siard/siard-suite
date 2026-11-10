# Design

## Context

See `proposal.md` (Why) for the motivation. Constraints and current state that shape the approach:

- The PoC on branch `poc/executable-web-companion-application` (modules `siard-web-backend-poc` / `siard-web-frontend-poc`) proved that Quarkus can host `siard-cmd`/`siard-api` and be packaged with the repo's beryx `runtime`/`jpackage` toolchain. Its packaging findings are reused here. Its archive endpoints, its file picker and its documentation are not.
- `siard-api` needs javax-era JAXB internals, the `xercesSaxParserFactory` system property and `--add-opens` for `java.xml` xerces/xalan and `java.base/java.lang`, at runtime **and** in tests.
- The desktop app (`siard-suite-app`) is the behavioural reference. Its non-UI code (`service/`, `model/`) is written against `javafx.concurrent`/observables and cannot be reused as is; later feature changes port it. In this change, `siard-suite-app` stays untouched and keeps shipping.
- Guiding principle: **match the desktop app's behaviour wherever reasonable.** Deliberate deviations are noted where they occur.

## Goals / Non-Goals

**Goals:**

- A foundation that later feature changes (archive browsing, archiving, upload) only extend: module layout, security model, lifecycle, API and event conventions, frontend stack, packaging, CI.
- Every risky integration exercised end to end by at least one automated test: `siard-api` under Quarkus, the full JDBC driver chain in the package, launch token → cookie → API → SSE in a real browser.

**Non-Goals:**

- Job model, progress events, `beforeunload` protection (first job change).
- Origin header check (see D4 note).
- Dedicated browser profile, tray icon.
- Publishing web installers in tagged releases (`deliverables.yml`).

## Decisions

### D1: Nested module `siard-suite-local-web-app` with `backend` and `frontend`

`include("siard-suite-local-web-app:backend")` / `include("siard-suite-local-web-app:frontend")`. Both subprojects set `base.archivesName` (`siard-suite-local-web-app-backend` / `-frontend`) so jars are not called `backend.jar`. The Java package root is `ch.admin.bar.siardsuite.localwebapp`. As in the PoC, the frontend's `jar` packs the Vite `dist/` into `META-INF/resources`, and the backend depends on it, so the installer contains a single app.

*Alternatives:* two flat modules like the PoC (consistent with the rest of the repo, but splits one deliverable over two top-level folders); Quinoa (expects the UI inside the backend module).

### D2: Quarkus JVM mode, fast-jar, packaging carried over from the PoC

Taken over unchanged:
- fast-jar layout with the `installDist` staging hook;
- `patchAppImageCfg`, which keeps only the run jar on `app.classpath`;
- `platform()` (not `enforcedPlatform()`) for the Quarkus BOM, plus `strictly()` pins for `jaxb-runtime`/`txw2` 2.3.9, `jaxb-core` 2.3.0.1 and `jakarta.activation-api` 1.2.2;
- new in this change: `strictly()` pins for the PostgreSQL, MySQL and MS SQL Server drivers to the version-catalog versions, because the advisory BOM otherwise aligns them to its own (it raised MySQL 9.3.0 → 9.7.0 and PostgreSQL 42.7.11 → 42.7.13), and the shipped drivers must be the ones the `siard-cmd` integration tests cover. The other drivers need no pin because the BOM does not manage their coordinates: Oracle (`jdbc-oracle/lib/ojdbc7.jar` file dependency; `com.oracle.ojdbc:xdb`/`xmlparserv2`, while the BOM manages `com.oracle.database.*`), DB2 (`jdbc-db2/lib/db2jcc4.jar` file dependency, `com.mysitex.sx.lib:db2jcc_license_cu`; the BOM manages `com.ibm.db2:jcc`) and Access (Jackcess). The MariaDB driver is only an integration-test dependency of `siard-cmd` and is not shipped;
- logback excluded from `siard-cmd`;
- jlink modules including `java.desktop` and `jdk.zipfs`;
- launcher `jvmArgs` with the xerces property and `--add-opens`;
- a numeric `appVersion` for jpackage.

The backend depends on `siard-cmd`, so the full driver chain ships and is scanned (PoC decision D5). On macOS no installer is built: the beryx `runtimeZip` task produces a zip with the bundled runtime and a start script, the same mechanism `deliverables.yml` uses for the desktop app's runtime zips.

### D3: Binding and host name

`quarkus.http.host=127.0.0.1` without a profile prefix (Quarkus' prod default would be `0.0.0.0`), and `quarkus.http.port=0` so the operating system assigns a free port. This avoids the PoC's race between finding a port, releasing it and binding it. After the `io.quarkus.vertx.http.HttpServerStart` event (async), the actual port is read from `io.quarkus.vertx.http.HttpServer#getPort()`, not from the event's `HttpServerOptions`.

*Verified (task 2.2):* with `quarkus.http.port=0`, the event's `options().getPort()` is `-1`. `HttpServer` is a CDI bean (registered by `HttpServerRuntimeInfoProvider`) and `getPort()` returned the port the process listens on, both in the packaged jar (`NORMAL`) and in `quarkusDev`. Mechanism: `@Inject HttpServer` (or `Arc.container().instance(HttpServer.class)`) read in an `@ObservesAsync HttpServerStart` observer. Its Javadoc only promises the bean "in normal mode", but it is also available under `@QuarkusTest` (task 4.1: tests read the random test port through it).

`127.0.0.1` is used everywhere (launch URL, Vite proxy target) and the Host check accepts only `127.0.0.1:<port>`. Reasons:
- Node ≥ 17 resolves `localhost` to `::1` first, and we do not listen there.
- Cookies are scoped by host name, so a single host name avoids a session that only half works.

*Alternative:* also allowing `localhost`/`[::1]`: no benefit, and a wider check.

### D4: Launch token exchanged for a session cookie

1. At startup a random single-use launch token and a random session secret are created.
2. `GET /launch?token=…` checks the token in constant time, checks that it is at most 2 minutes old, and invalidates it.
3. It answers `303` with a hand-set `Location: /` header, `Set-Cookie: siard_session_<port>=<secret>; HttpOnly; SameSite=Strict; Path=/`, `Cache-Control: no-store` and `Referrer-Policy: no-referrer`.

`Location` is set as a raw header because JAX-RS `seeOther()`/`temporaryRedirect()` resolve relative URIs against the base URI. In dev that would turn into `http://127.0.0.1:8080/` and leave the Vite origin.

Every failed exchange (expired, used, unknown, missing) answers identically with `303 Location: /launch-failed` and no cookie. That frontend route shows a translated "this start link has expired or was already used, please restart SIARD Suite" message. Using one response for all cases keeps the code small and reveals nothing about the token; the user's action is the same in every case. The 2-minute limit is a constant (tests use an injectable clock), so a launch URL leaked to a log is useless soon even if it was never opened.

The Host check reads Vert.x's `request.authority()`, not the raw `Host` header: in HTTP/2 the host arrives as the `:authority` pseudo-header, and Java's `HttpClient` upgrades to h2c, so a header-only check rejected such clients (found in task 4.1). A Vert.x route filter on **every** request (including `/launch`, `/q/*` and static files) enforces the exact `Host: 127.0.0.1:<port>`; a mismatch gets `403` with problem details pointing to `http://127.0.0.1:<port>/`, so a user who typed `localhost` knows what to do. A filter on `/api/*` enforces the cookie. Tests must therefore use `127.0.0.1` as base URI (RestAssured defaults to `localhost`). The frontend therefore never handles the token (the browser sends the cookie automatically). This works for `EventSource`, which cannot set headers.

The launch URL is logged at startup. Because the token is single-use, a leaked log line is worthless once the window has opened.

- **Port in the cookie name:** cookies are not port-scoped. **Independent instances (D7) rely on this**; without it, two agents would overwrite each other's session.
- **Note for later:** if an Origin check is added, Vite's `changeOrigin` does not rewrite `Origin`, so in dev it arrives as `http://localhost:5173`. A dev allowance must be guarded like D8.

*Alternatives:* the PoC's Bearer token read by JS from the URL (token stays in the history and is visible to JS); `history.replaceState` cleanup (token still in JS memory).

### D5: Event stream and presence-based exit

`GET /api/events` is a single multiplexed SSE stream per window. It serves as both the presence signal and the event channel for later features. A single stream keeps us within the browser's limit of 6 HTTP/1.1 connections per origin regardless of the number of jobs.

The event type lives in the JSON data (`type`), not in SSE's `event:` field. That gives one `onmessage` handler and one TypeScript union. On a new stream the server first sends `connected`, then a keep-alive comment every 15 s; a failed write marks the connection as closed.

The agent counts open authorized streams. When the count reaches 0, an exit timer starts (`siard.agent.idle-exit-grace`, default 20 s); a new stream cancels it. The default is a starting value, to be tuned after BIT testing.

If no stream has connected by the time the launch token has expired (2 min after start) plus the grace period, the agent exits too: nobody can obtain a session any more, for example because the browser never opened. The grace period on top lets a click just before expiry still connect. The deadline is checked against the agent's clock, like the token expiry. Both exits are enabled by the same `idle-exit-grace` setting.

*Alternatives:*
- Explicit Quit only: users would never quit the agent.
- Timer heartbeats: browsers throttle timers in hidden windows to roughly one per minute, which causes false exits.
- Watching the browser process: requires a dedicated profile (deferred).
- WebSocket: bidirectional is not needed, and SSE is simpler.

### D6: Typed events in the OpenAPI description

Events are modelled as a sealed interface `AgentEvent` with record subtypes. Jackson writes `type` automatically (`@JsonTypeInfo(use = NAME, property = "type")` + `@JsonSubTypes`/`@JsonTypeName`), so records carry no hand-filled type field.

SmallRye OpenAPI 4.3.5 does not read `@JsonTypeInfo`/`@JsonSubTypes`/`@JsonTypeName` (it handles only `JsonProperty`, `JsonPropertyOrder`, `JsonIgnore*`, `JsonUnwrapped`, `JsonValue` and `JsonView`; found by inspecting the jar). MicroProfile OpenAPI 4.1 has no `itemSchema` (an OpenAPI 3.2 feature). The SSE operation therefore declares `@APIResponse(content = @Content(mediaType = "text/event-stream", schema = @Schema(implementation = AgentEvent.class)))`, and the following variants are tried **in order of preference** (task 2.1):

1. **`discriminatorMapping` only**: `@Schema(oneOf = …, discriminatorProperty = "type", discriminatorMapping = …)` on `AgentEvent`, if `openapi-typescript` injects the literal `type` into each union member itself.
2. **`@SchemaProperty` + shared constant + test**: each record declares `type` as a required single-value enum, using the same constant as `@JsonTypeName`. A test iterates over `AgentEvent.class.getPermittedSubclasses()`, asserts that each subtype is covered by `@JsonSubTypes`/`@JsonTypeName`, serialises it with the application's `ObjectMapper`, and compares `type` with the spec's enum.
3. **Hand-written TypeScript event types**: if neither variant yields a usable union, the event types are written by hand in the frontend (`src/api/events.ts`) next to the generated REST types. This gives up the automatic Java ↔ TypeScript check for events only; there are few event types and they change rarely. Code that rewrites the generated spec (an `OASFilter`) was considered and rejected as over-engineering for that.

**Chosen variant: 1, `discriminatorMapping` only** (task 2.1). Checked on Quarkus 3.40.1 / SmallRye OpenAPI 4.3.5 / openapi-typescript 7.13.0 with a temporary second event:
- SmallRye ignores the Jackson annotations: the member schemas had **no** `type` property, confirming the jar inspection.
- With `@Schema(oneOf = …, discriminatorProperty = "type", discriminatorMapping = …)` on `AgentEvent`, the spec contains `oneOf` + `discriminator.mapping`, and openapi-typescript adds `type: "connected"` etc. to each member itself ("discriminator enum property added by openapi-typescript"). `AgentEvent` becomes a union of the members.
- A `switch (e.type)` with a `never` default compiled under `--strict` and narrowed to the member's fields; accessing another member's field in the wrong branch failed to compile.
- On the wire, Jackson writes `{"type":"connected"}`.

Variants 2 and 3 and the Jackson ↔ spec drift test are therefore not needed. Remaining manual duty: a new event needs an entry in `permits`, `@JsonSubTypes`, `oneOf` and the mapping (documented in `AgentEvent`'s Javadoc).

### D7: Independent instances

Every launch is its own agent: own JVM, port, token, session and lifetime, exactly like the desktop app, which has no single-instance logic. The instances do not coordinate, so there is no instance file, lock or control endpoint. This relies on the port-specific cookie name (D4).

On macOS the app is started from a zip with a start script, not as an `.app` bundle, so it behaves like Windows and Linux: every launch is a new instance. A reopen handler (`java.awt.desktop.AppReopenedListener`) is only needed if a macOS `.app` is ever shipped.

*Alternative:* single instance with a lock file plus handoff: a whole subsystem (secret on disk, extra endpoint, retry logic) for no gain over the desktop app's behaviour.

### D8: Launch modes decide behaviour, not profiles

`quarkus.profile` can be overridden at runtime (`-Dquarkus.profile=dev`), and the project is open source, so the dev values are public. Launch-dependent behaviour is therefore keyed on `LaunchMode.current()`:

| | `NORMAL` (packaged) | `DEVELOPMENT` (quarkusDev) | `TEST` |
|---|---|---|---|
| Token / session secret | random; token single-use, expires after 2 min | fixed dev values from `%dev.` config; token reusable, no expiry | random; token single-use, expires after 2 min |
| `siard.agent.open-browser` (default) | true | false | false |
| `siard.agent.idle-exit-grace` (default) | 20 s | disabled | disabled |

Token and secret are **never** configurable outside `DEVELOPMENT`. The two flags can be set in any mode; the Playwright test runs the packaged jar (`NORMAL`) with `open-browser=false` and a short grace period, and reads the launch URL from the log.

### D9: Browser launch

1. Try Chromium-family browsers in `--app=<url>` mode: on Windows, Edge and then Chrome via their registered install paths (App Paths registry or the known `Program Files` locations), not by spawning nine names through `cmd /c start`; on Linux/macOS, the known executables or app bundles.
2. Otherwise use `Desktop.browse`.
3. Otherwise log the URL.

Edge is **not** assumed to be present. No dedicated `--user-data-dir` for now: whether BIT policies allow it is unknown. It can be added later without affecting anything else.

### D10: API conventions

- Unversioned `/api/...`: frontend and backend always ship together.
- DTOs are records mapped from `siard-api` types and never serialise `siard-api` objects directly.
- Errors are RFC 9457 problem details produced by one central `ExceptionMapper`, without stack traces or exception messages; details are logged on the server. *Deferred to the first change with an intentional client error (e.g. 400 for an invalid archive):* a dedicated `ProblemException(status, detail)` whose detail is passed through. Framework `WebApplicationException` messages stay hidden because they can contain internals. Later, a stable `type` per error would let the frontend translate the message.
- `GET /api/system/info` returns `appVersion` (`ProgramInfo` / the build version), `javaVersion` and `jdbcDrivers` (from `DriverManager.getDrivers()`). This proves the packaged driver chain loads.
- A backend-only `@QuarkusTest` opens the tracked fixture `siard-api/src/test/resources/testfiles/sample.siard` via `ArchiveImpl` to prove `siard-api` works under Quarkus, without an endpoint.

### D11: OpenAPI pipeline

`quarkus-smallrye-openapi` generates the description at build time into `backend/build/openapi/` (via `quarkus.smallrye-openapi.store-schema-directory`). The Gradle task `openApiUpdate` copies the YAML to the committed `siard-suite-local-web-app/openapi.yaml`. The frontend generates `src/api/schema.d.ts` from the committed file with `openapi-typescript` (`npm run gen:api`). `schema.d.ts` is not committed: `typecheck` and `build` run `gen:api` first, so the TypeScript types cannot drift from the YAML.

Committing the file breaks a build cycle: the backend embeds the frontend jar, so the frontend cannot depend on the backend build. It also makes API changes visible in PR diffs. `openApiCheck` (part of `check`) fails if the generated file differs from the committed one, ignoring line endings, and names `openApiUpdate` as the fix. The check compares files rather than asking git, so it gives the same answer locally and in CI. Quarkus always writes both `openapi.yaml` and `openapi.json`; writing to the build directory keeps the JSON out of the source tree.

The client is `openapi-fetch` + `openapi-react-query` (typed TanStack Query hooks).

*Verified (task 2.3):* the schema is written on `quarkusBuild` **and** at `quarkusDev` startup. A relative `store-schema-directory` resolves against the backend module directory. Not checked: whether a live reload rewrites it. After an API change: `openApiUpdate`, then `npm run gen:api`.

The very first `openapi.yaml` cannot come from a build, because of the cycle above; it was bootstrapped once with an empty description (task 7.2) and is regenerated by `openApiUpdate` since.

The committed spec contains no build- or environment-dependent values: `info.version` is the constant `1` (the API is unversioned, D10) and `quarkus.smallrye-openapi.auto-add-server=false` suppresses the `servers` entries, so the drift check only fails on real API changes.

*Alternatives:* hand-written TS types (drift); `orval` (more generated code); `openapi-generator` (heavy).

### D12: Frontend stack

- **Base:** React + TypeScript (strict) + Vite.
- **UI:** MUI, themed after the desktop app's `root.css` colours and the Roboto font, to keep today's look.
- **Routing:** React Router: `/` (start) and `/about`. The backend serves `index.html` only for GET requests to unknown paths without a file extension in the last segment; `/api/*`, `/q/*`, `/launch` and missing files with an extension (e.g. a mistyped `.js`) return a real 404.
- **Data:** TanStack Query.
- **i18n:** `react-i18next` with typed keys. The texts are JSON per language under `frontend/src/i18n/`, converted **once** from `siard-suite-app`'s `messages_*.properties` by a script in this change. The script must read the files as ISO-8859-1 with `\uXXXX` escapes and line continuations (the de/fr/it files are Latin-1 bytes, the en file uses `\u` escapes), e.g. via `java.util.Properties.load(InputStream)`, and map placeholders to named arguments: sequential `%s`/`%d` → `{{arg0}}`, `{{arg1}}`, …, positional `%1$s` → `{{arg0}}` (index − 1), with the call sites adjusted accordingly and edited in JSON afterwards. The language is taken from the browser on every start, with English as fallback, and is **not** persisted: browser storage is per origin and the origin changes with every random port, and the desktop app does not persist the language either. Only shell and About texts are migrated now.
- **Quality:** ESLint (typescript-eslint, react-hooks) and Prettier, Vitest + React Testing Library, all in Gradle `check`. One Playwright smoke test (Chromium) in a separate `e2eTest` task covers launch URL → clean URL → About shows the version → page closed → agent exits.

### D13: Dev workflow

- `./gradlew :siard-suite-local-web-app:backend:quarkusDev` on port 8080 (`%dev.quarkus.http.port`), plus `npm run dev` (Vite on 5173).
- Vite proxies `/api` and `/launch` to `http://127.0.0.1:8080` with `changeOrigin: true`, so the backend sees `Host: 127.0.0.1:8080` and the Host check needs no dev exception. SSE works through the proxy.
- In `DEVELOPMENT` the fixed token and secret (D8) survive hot restarts, so the browser session keeps working after Java changes.
- Swagger UI (dev only) is available at `http://127.0.0.1:8080/q/swagger-ui`.

### D14: CI and installers

- **`build.yml`:** a new `local-web-app` job (ubuntu) runs `:siard-suite-local-web-app:check` (JUnit/`@QuarkusTest`, OpenAPI drift, ESLint, `tsc`, Vitest) and `:siard-suite-local-web-app:e2eTest` (Playwright, browser cached). Node comes from the Gradle node plugin.
- **Installer workflow** (new file, replacing the PoC's `poc-web-installer.yml`):
  - matrix: windows and ubuntu run `:siard-suite-local-web-app:backend:jpackage`, macos runs `:siard-suite-local-web-app:backend:runtimeZip`;
  - uploads `.exe`/`.msi`, `.deb` and the macOS zip; a missing expected package fails the run;
  - actions at the versions used by the other workflows (`@v6`).
- **Installer workflow triggers:**
  - `pull_request` and `push` to `main` with `paths-ignore: ['siard-suite-app/**', 'docs/**', '**/*.md']`. Every other module (the whole `siard-cmd` dependency chain, root build files, version catalog, Gradle wrapper) can break packaging. An ignore list automatically includes new modules.
  - `concurrency: { group: ${{ github.workflow }}-${{ github.event.pull_request.number || github.ref }}, cancel-in-progress: ${{ github.event_name == 'pull_request' }} }`: a new push to a PR cancels its still-running, outdated packaging builds; pushes to `main` are not cancelled, so every merge produces packages.
  - `workflow_dispatch`, which only works once the file is on `main`. The PR trigger is what builds installers during this change.
  - The module README documents that PRs trigger installer builds, so the trigger can be narrowed once packaging is stable.
- `deliverables.yml` stays untouched until feature parity.

### D15: Test scope

Tests are kept minimal: **one test per spec scenario**, and nothing beyond that. No extra edge-case combinations, no tests for trivial getters, configuration or DTOs, no snapshot tests. `@QuarkusTest` is preferred over Playwright; Playwright is limited to the single smoke test (D12). The exception is the security-relevant scenarios (launch token, Host check, session cookie, `LaunchMode` credentials): they are the core of this change and are tested thoroughly. Any other test is added only after asking.

How the scenarios are run (task 4.x):
- **`@QuarkusTest`** (`TEST` mode) for everything that does not depend on the launch mode. A `MutableClock` alternative replaces the agent's clock for expiry, and a test-only `/api/test-probe` resource stands in for API endpoints that do not exist yet. It also provides the "handler throws" case.
- **Packaged app** (`NORMAL` mode) for scenarios about the real launch mode. These live in the Quarkus plugin's `src/integrationTest` source set and run via `quarkusIntTest` (part of `check`). `@QuarkusIntegrationTest` is not used because it fixes the port and cannot read the agent's log. A `PackagedAgent` helper starts `quarkus-run.jar` with `siard.agent.open-browser=false`, reads the launch URL from its output, and always stops the process: via try-with-resources and a JVM shutdown hook. Its HTTP client uses no proxy, so an inherited proxy setting cannot reroute loopback requests. The "other interfaces" test only accepts "connection refused" and is skipped when the machine has no non-loopback IPv4 address.
- **Development mode** ("Development mode uses fixed credentials") is a unit test of `LaunchCredentials` with `LaunchMode.DEVELOPMENT`, plus a manual `quarkusDev` check; running `quarkusDev` in a test is too heavy.

## Risks / Trade-offs

- **[Edge sleeping tabs / BIT Edge policies may freeze SSE or block app mode]** → test on a BIT Windows machine with an installer from the PR workflow before merging; D5's grace period and D9's fallbacks limit the impact.
- **[macOS untested: start script from the runtime zip with the fast-jar layout, AWT browser launch]** → macOS runner in the packaging workflow; manual test on a Mac before relying on it.
- **[No macOS installer: users start the app from a terminal]** → accepted for now; an `.app`/`.dmg` (plus the reopen handler from D7) can be added later.
- **[Event typing variant may not render as expected]** → ordered fallbacks in D6, checked first (task 2.1).
- **[Packaging regressions from changes anywhere in the core]** → packaging builds (Windows/Linux installers, macOS zip) on PRs with `paths-ignore`; costs runner time on three OSes per push, limited by `concurrency` with `cancel-in-progress` for PRs.
- **[Closing the window kills the agent even mid-operation]** → same as the desktop app today; `beforeunload` prompt during jobs comes with the first job change.
- **[Each launch is a full JVM]** → same memory and start time as the desktop app.
- **[Fixed dev credentials are public]** → active only under `LaunchMode.DEVELOPMENT` and covered by a test asserting the packaged jar rejects them.
- **[i18n texts diverge between desktop and web during coexistence]** → accepted; the desktop app is retired at parity.
- **[Many tabs could exhaust the 6 HTTP/1.1 connections per origin]** → one stream per window; acceptable for typical use.

## Migration Plan

Additive. The new modules build and ship separately from `siard-suite-app`. Rollback means removing `siard-suite-local-web-app/`, its `settings.gradle.kts` includes, the `build.yml` job and the installer workflow.

## Open Questions

- Final grace-period value: 20 s default, tuned after BIT testing.
