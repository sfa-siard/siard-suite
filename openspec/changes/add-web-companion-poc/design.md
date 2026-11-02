# Design

## Context

See `proposal.md` for motivation (BIT JavaFX ban → web frontend migration path). Relevant current state:

- `siard-api` exposes `Archive` (`ch.admin.bar.siard2.api`) with `ArchiveImpl.newInstance()` for opening `.siard` files; metadata is reachable via `MetaSchema`/`MetaTable`/`MetaColumn`. Internally it uses JAXB (`com.sun.xml.bind` internals), Woodstox StAX (loaded via ServiceLoader), and requires `--add-opens` JVM flags plus the `xercesSaxParserFactory` system property — the same flags the `siard-suite-app` launcher already sets.
- No module outside `siard-suite-app` references JavaFX (verified via build files). `siard-suite-app` packages with the beryx `runtime` plugin → jlink + jpackage, already producing `.exe`/`.msi` on `windows-latest` in `deliverables.yml`.
- `master.siard` and `Contacts database.siard` exist in the repo root and can serve as test fixtures.
- This is a greenfield addition inside the monorepo — no existing code is touched.

## Goals / Non-Goals

**Goals:**

- Prove the target stack end-to-end: Quarkus (JVM mode) hosting `siard-api`, serving a React frontend, packaged as a Windows installer via the existing jpackage toolchain.
- Establish the module and build wiring (`siard-web-backend`, `siard-web-frontend`) that future web-UI work builds on.
- Establish the local-agent lifecycle pattern: random port, launch token, browser open, explicit shutdown.

**Non-Goals:**

- JDBC archive upload/download, progress streaming, job management.
- Reimplementing existing JavaFX screens; no changes to `siard-suite-app`.
- GraalVM native compilation, installer signing, i18n, accessibility conformance.
- Browsing arbitrary local directories from the UI (path is typed, not picked).

## Decisions

### D1 — Two Gradle modules; frontend ships as a resources jar

`siard-web-backend` (Quarkus) and `siard-web-frontend` (React+Vite+TypeScript, npm via `com.github.node-gradle.node`). The frontend module's `jar` task packs the Vite `dist/` output into `META-INF/resources`; the backend declares `implementation(project(":siard-web-frontend"))`, so Quarkus serves the UI from the classpath with zero config and the packaged artifact is a single jar.

- *Alternative — Quinoa*: Quarkus' frontend extension wires npm + dev proxy automatically but expects the UI inside the backend module, contradicting the two-module requirement.
- *Alternative — frontend as loose files next to the jar*: complicates jpackage layout and the installed layout; the jar approach keeps packaging identical to today.

Java package root for new code: `ch.admin.bar.siardsuite.web` (follows the `siard-suite-app` namespace, which is the GUI-side naming convention; `siard2.*` is the core-library namespace). Note: the `ch.admin.bar.siardsuite.*` namespace is shared across two artifacts (app and web-backend). This is safe while both remain classpath apps — packages are distinct (`siardsuite.web` vs `siardsuite.<existing>`) — but the shared namespace must not be split at package level if either module ever gets a `module-info`.

### D2 — Quarkus JVM mode (uber-jar), packaged with the existing beryx jpackage toolchain

The backend is built with `quarkus.package.jar.type=uber-jar`, producing a single self-contained runner jar whose entry point is `io.quarkus.bootstrap.runner.QuarkusEntryPoint`. The module applies the `application` plugin with `mainClass` set to that entry point, so the existing beryx `runtime` (jlink) + `jpackage` pipeline treats it exactly like `siard-suite-app`: jlink produces a JRE-only runtime image (Quarkus apps are classpath apps — no JPMS module for the app itself), and `jpackage` packages the uber-jar against it → `.exe`/`.msi` on Windows and `.deb` on Linux (jpackage selects the installer type per OS; `.deb` requires `dpkg` tooling on the build host). The Linux package is part of the PoC so the full pipeline is verifiable on the dev machine without a Windows box.

- *Alternative — Quarkus fast-jar (`quarkus-app/` layout) + `jpackage --input`*: the default fast-jar layout spreads code across `quarkus-run.jar` + `lib/` + `app/`, which does not map onto the beryx `application` distribution model without custom staging. Rejected — uber-jar reuses the proven pipeline unchanged.
- *Alternative — GraalVM native*: single `.exe` without JRE, but `siard-api` relies on JAXB RI internals, reflectively-loaded MSV, and ServiceLoader StAX — native-image would need extensive reflection/resource config for no PoC benefit. Explicitly deferred; can be revisited as a separate change.
- *Alternative — different packager (launch4j, install4j)*: would duplicate what the repo + CI already do.

The launcher's `runtime.launcher.jvmArgs` must include every flag `siard-api` needs — including `--add-opens java.base/java.lang`, which `siard-suite-app` currently passes only to `JavaExec`/test tasks and **omits from its launcher block**. The list is: `xercesSaxParserFactory` system property, `--add-opens java.xml/com.sun.org.apache.xerces.internal.jaxp`, `--add-opens java.xml/com.sun.org.apache.xalan.internal.xsltc.trax`, `--add-opens java.base/java.lang` — minus all `javafx.graphics` opens.

### D3 — Local-agent lifecycle: random port, launch token, explicit shutdown

`main()` starts Quarkus in-process, bound to `127.0.0.1` on a random free port. At launch the backend generates a random session token (UUID) and opens the system browser via `java.awt.Desktop.browse()` pointing at `http://127.0.0.1:<port>/?token=<token>` (AWT `Desktop` is in `java.desktop`, no JavaFX needed). A `ContainerRequestFilter` requires the token on every `/api/*` request (as `Authorization: Bearer` or `?token=`); the frontend captures it from the launch URL and sends it on subsequent calls. `POST /api/system/shutdown` (token-protected) exits the JVM.

- *Alternative — fixed port*: collisions on managed federal machines; random port removes that failure class.
- *Alternative — no token*: any web page could POST to `localhost` (CSRF/DNS rebinding); the token is a cheap, standard mitigation.
- *Alternative — system tray icon / auto-exit on last tab close*: more UX polish than a PoC needs; AWT `SystemTray` remains an option later without JavaFX.

### D4 — PoC API surface is deliberately thin

```
POST /api/archives                 {path} → 201 {id, name}        open via ArchiveImpl
GET  /api/archives/{id}/tree       → 200 {schemas:[{name,tables:[{name,rowCount,columns:[{name,type}]}]}]}
POST /api/system/shutdown          → 204, JVM exits
GET  /q/health                     → Quarkus health (unauthenticated, liveness only)
```

The React UI is one screen: a path input, an "Open" action, and a rendered schema→table→column tree. `master.siard` is the verification fixture.

- *Alternative — include row data preview*: valuable but doubles the API surface and touches primary-data plumbing (`RecordDispenser`); deferred to a follow-up change.

### D5 — Full SIARD dependency chain ships in the PoC package

The backend depends on `project(":siard-cmd")`, which transitively brings `siard-api`, `sql-parser`, `siard-utilities`, `jdbc-base`, and all `jdbc-*` modules with their JDBC drivers, Tika, JAXB RI, Woodstox, and MSV. The *implemented* scope stays the same — only archive browsing is exposed — but the packaged artifact carries the complete dependency surface for two reasons:

- **BIT security scan**: the federal review will scan the delivered package for vulnerable dependencies. Shipping the real dependency set means the scan result is representative; a minimal package would produce a misleading clean bill now and risk findings later.
- **Packaging proof for the real artifact**: the uber-jar must correctly merge `META-INF/services` (Woodstox StAX is resolved via ServiceLoader, UCanAccess and JDBC drivers likewise) and strip conflicting signature files from signed driver jars. Proving this now, on the full set, removes a packaging unknown before real transfer work starts.

- *Alternative — minimal deps (`siard-api` only)*: rejected. A smaller installer is not worth a non-representative security scan and deferred packaging risk.

### D6 — Testing: API-level `@QuarkusTest`, manual packaging verification

Backend tests use `@QuarkusTest` + REST Assured against the `master.siard` fixture, covering each spec scenario (open, tree, token rejection, shutdown). Frontend has a smoke-level Vitest test; UI correctness is verified manually. Windows packaging is verified by running `jpackage` on a Windows machine/`windows-latest` runner and launching the installed app.

## Risks / Trade-offs

- **`siard-api` internals under Quarkus** (Xerces parser factory, `--add-opens`, ServiceLoader StAX) → JVM mode means these are ordinary jars; the launcher flags are carried over (D2); an early `@QuarkusTest` opening `master.siard` proves it before UI work begins.
- **Uber-jar merging of the full chain** (ServiceLoader entries, signed JDBC driver jars, duplicate `META-INF` resources) → Quarkus uber-jar merges service-provider configs and strips signature files; verified by the startup driver-enumeration smoke check and by opening `master.siard` in the packaged app (tasks 2.4, 5.2).
- **Token handling in the SPA** (token in URL leaks into browser history) → acceptable for a localhost PoC; a hardened version can hand the token via a one-time redirect/cookie later. Noted, not solved.
- **No file picker** (typed path only) → acceptable for PoC; a `/api/fs/browse` endpoint is the known follow-up.
- **Unsigned installer triggers SmartScreen** → expected; code signing is a release-time concern, not PoC.
- **jpackage can't cross-build** → Windows artifact verification requires a Windows runner (already in CI matrix) or a Windows dev box; the `.deb` package covers the same pipeline end-to-end on the dev's Linux machine meanwhile.

## Migration Plan

None — additive change. Rollback is deleting the two modules and the `settings.gradle.kts` includes. `siard-suite-app` is untouched and remains the shipping UI.

## Open Questions

- Exact Quarkus 3.x LTS version to pin in `libs.versions.toml` — resolved at implementation time (latest stable LTS).
- Whether `deliverables.yml` gains web-artifact outputs in this change or a follow-up — packaging is proven locally/on-demand first.
