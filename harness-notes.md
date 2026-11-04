# Harness Notes

## Agent Decisions

Decided: in `add-web-companion-poc` specs, the mandatory "authorization" scenario type maps to **session-token enforcement** (missing/invalid token → 401) because AGENTS.md defines no Roles & Permissions section — SIARD Suite is a local single-user tool where the only access boundary is the launch token. Harness gap: AGENTS.md should state that for local-agent/CLI-style changes, token-rejection scenarios are the convention for the "authorization" scenario type.

Decided (superseded): `siard-web-backend` depends on `project(":siard-api")` only for the PoC — later reversed at user direction: the package must carry the full `siard-cmd`/`jdbc-*` chain so a BIT security scan covers the real dependency surface and uber-jar merging of drivers/ServiceLoader configs is proven early. Implemented scope is unchanged (archive browsing only). Documented as design decision D5.

Decided: Quarkus packaging uses `uber-jar` + `application.mainClass = io.quarkus.bootstrap.runner.QuarkusEntryPoint` so the existing beryx `runtime`/`jpackage` pipeline applies unchanged (architecture review HIGH finding). Documented as design decision D2.

## Harness Suggestions

Suggestion: add an ADR location/convention to AGENTS.md (e.g., `docs/sad/adrs/` or `docs/adrs/`). Reason: the architecture reviewer flagged D1 (resources-jar over Quinoa) and D2 (Quarkus JVM over GraalVM) as durable cross-change decisions worth ADRs, but the repo has no established ADR home — the decision currently lives only in `design.md`, which is archived with the change.

Suggestion: `openspec/config.yaml` `context:` should carry the non-inferable project constraints — "deliverables for federal-employee devices must not contain JavaFX (BIT policy)" and "artifacts must run on Windows, macOS, Linux". Reason: these constraints shaped the whole change but are not discoverable from the code; every future agent cycle would otherwise have to rediscover them.

Suggestion: AGENTS.md could gain a short "Roles & Permissions" section even if it just states "single local user; access boundary is the launch token". Reason: the spec quality rules require role names for authorization scenarios, and future agents will otherwise re-derive this mapping each cycle.

## Cycle: add-web-companion-poc (2026-11-02)

### Implementation decisions
- Decided: MicroProfile `ConfigSource` (ServiceLoader) provides `siard.agent.port` + `siard.agent.token` at config-load time, instead of a launcher main() — works identically under `java -jar`, jpackage launcher, quarkusDev, and tests. The planned `SiardWebLauncher` class was removed.
- Decided: `platform()` (advisory) instead of `enforcedPlatform()` for the Quarkus BOM — `strictly()` pins must win over BOM alignment for javax-era glassfish JAXB (2.3.x) and `jakarta.activation-api` 1.2.2 (javax.* packages), which siard-api requires.
- Decided: fast-jar (not uber-jar) after discovering `QuarkusEntryPoint` resolves `quarkus-application.dat` relative to the run jar's parent; plus `installDist` staging hook and a `patchAppImageCfg` task trimming `app.classpath` to the run jar only.
- Decided: `jdk.zipfs` added to jlink modules (SmallRye config needs jar: filesystem provider); `java.desktop` for `Desktop.browse`.
- Decided: logback excluded from `siard-cmd` dep — double SLF4J binding with Quarkus' JBoss log manager.
- Deferred: task 5.2 (Windows install+launch verification) moved to workflow follow-up — jpackage cannot cross-build; verifiable only on a `windows-latest` runner.

### Harness suggestions
- Suggestion: AGENTS.md "Common Commands / Build" should note `siard-api` consumers need the `--add-opens java.xml/...` + `java.base/java.lang` jvmArgs and the `xercesSaxParserFactory` sysprop at runtime AND test time — rediscovered the hard way here.
- Suggestion: add a harness rule/recipe for packaging a Quarkus app with beryx+jpackage in this repo (fast-jar staging + app.classpath patch + jdk.zipfs) — reusable for future Quarkus modules.
- Suggestion: `openspec/config.yaml` context should state "dev/CI hosts are Linux; Windows artifacts can only be verified on windows-latest runners" — task authors then scope verification tasks correctly.

### Quality Gate outcome
- Tasks: 17/18 (5.2 Windows verification deferred to workflow follow-up)
- Architecture: ✓ — 2 HIGH auto-fixed (LaunchMode check → AgentShutdown bean + test alternative; ConfigSource values made JVM-static)
- QM-Guide: ⚠ — M1 mitigated (frontend sends Bearer header; URL-token launch retained per spec + documented), M3 documented (devDep-only vulns)
- Test coverage: ⚠ — all API scenarios covered by @QuarkusTest; frontend component-render test is the remaining gap
- Security: ⚠ — HIGH auto-fixed (Host-header DNS-rebinding check), MEDIUM auto-fixed (generic archive error responses, detail logged server-side). No CRITICAL.
