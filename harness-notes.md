# Harness Notes

## Agent Decisions

Decided: in `add-web-companion-poc` specs, the mandatory "authorization" scenario type maps to **session-token enforcement** (missing/invalid token → 401) because AGENTS.md defines no Roles & Permissions section — SIARD Suite is a local single-user tool where the only access boundary is the launch token. Harness gap: AGENTS.md should state that for local-agent/CLI-style changes, token-rejection scenarios are the convention for the "authorization" scenario type.

Decided (superseded): `siard-web-backend` depends on `project(":siard-api")` only for the PoC — later reversed at user direction: the package must carry the full `siard-cmd`/`jdbc-*` chain so a BIT security scan covers the real dependency surface and uber-jar merging of drivers/ServiceLoader configs is proven early. Implemented scope is unchanged (archive browsing only). Documented as design decision D5.

Decided: Quarkus packaging uses `uber-jar` + `application.mainClass = io.quarkus.bootstrap.runner.QuarkusEntryPoint` so the existing beryx `runtime`/`jpackage` pipeline applies unchanged (architecture review HIGH finding). Documented as design decision D2.

## Harness Suggestions

Suggestion: add an ADR location/convention to AGENTS.md (e.g., `docs/sad/adrs/` or `docs/adrs/`). Reason: the architecture reviewer flagged D1 (resources-jar over Quinoa) and D2 (Quarkus JVM over GraalVM) as durable cross-change decisions worth ADRs, but the repo has no established ADR home — the decision currently lives only in `design.md`, which is archived with the change.

Suggestion: `openspec/config.yaml` `context:` should carry the non-inferable project constraints — "deliverables for federal-employee devices must not contain JavaFX (BIT policy)" and "artifacts must run on Windows, macOS, Linux". Reason: these constraints shaped the whole change but are not discoverable from the code; every future agent cycle would otherwise have to rediscover them.

Suggestion: AGENTS.md could gain a short "Roles & Permissions" section even if it just states "single local user; access boundary is the launch token". Reason: the spec quality rules require role names for authorization scenarios, and future agents will otherwise re-derive this mapping each cycle.
