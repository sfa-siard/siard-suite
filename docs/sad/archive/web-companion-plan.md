# SIARD Suite Web Companion — Product Roadmap

This document is the high-level product roadmap for adding a browser-based Web
Companion to SIARD Suite. It focuses on goals, phases, and customer-facing
decisions. The architecture, security model, and technical decisions are
documented in the
[Software Architecture Documentation - Companion Architecture](sad/software-architecture-documentation-companion-architecture.md).
The one-week backend framework spike is described in the
[Web Companion Spike Plan](sad/web-companion-spike-plan.md).

## Context

SIARD Suite is currently a desktop (JavaFX) and command-line application. The
funding client can no longer deploy Java runtimes or downloaded executables on
managed end-user systems. They need a delivery model that:

- Does not require a Java runtime on the end-user desktop.
- Can be centrally deployed and updated by their IT department.
- Still allows users to archive databases that are only reachable from inside
  restricted networks.

The Web Companion addresses this by adding a browser-accessible frontend and a
headless backend. The **local agent** is the replacement for the JavaFX desktop
application and is built first. The desktop application is phased out once the
local agent reaches feature parity. The **CLI** remains supported for users who
prefer scriptable or automation workflows.

## Goal

Deliver a Web Companion that lets SFA staff and other users archive and restore
relational databases through a browser, while reusing the existing SIARD core
libraries (`siard-api`, JDBC wrappers, `siard-cmd`). The **local agent** is the
first product; as much as possible of its backend and frontend is reused for the
**central hub** later.

## Relationship to other documents

| Document | Purpose |
|---|---|
| [Software Architecture Documentation - Companion Architecture](sad/software-architecture-documentation-companion-architecture.md) | Canonical technical architecture: deployment model, security, authentication, persistence, API, and risks. |
| [Web Companion Spike Plan](sad/web-companion-spike-plan.md) | One-week engineering spike to choose Quarkus or Spring Boot as the backend framework. |
| [Refactoring Plan](refactoring-plan.md) | Independent refactoring of the existing codebase. Some refactorings (e.g., extracting CLI config and orchestrator from `siard-cmd`) make reusing SIARD logic in the Web Companion easier. |

## High-level delivery model

- **Local Agent** — the first deliverable and the long-term replacement for the
  JavaFX desktop application. Runs on the user's machine or a nearby server, uses
  an embedded SQLite database for job history, has no authentication, and binds to
  `127.0.0.1`. Targets users or databases that cannot be reached from a central
  server. The local agent is intended to reach feature parity with the desktop
  app.
- **Central Hub** — the intended primary delivery model for SFA staff once it is
  implemented. Deployed and managed by IT, accessed via browser, uses OIDC through
  Keycloak, persists job state in PostgreSQL. Reuses the same `siard-server` and
  `siard-web` modules built for the local agent.
- **Desktop (JavaFX)** — unchanged for now; no new features planned. Phased out
  once the local agent reaches feature parity.
- **CLI** — still valuable for certain workflows and will be supported long term.

See the companion architecture document for the full architecture.

## Phased roadmap

### Phase 0 — Spike (1 week)

Validate the backend framework and core integration before committing to the
MVP.

- Build minimal Quarkus and Spring Boot prototypes.
- Run an end-to-end archive job through the existing `siard-api`.
- Compare local-agent packaging, container builds, native-image feasibility, and
  developer experience.
- Validate that the same backend can support hub features later (OIDC with
  Keycloak, PostgreSQL persistence).
- Choose the backend framework and record the decision in an ADR.

Deliverable: chosen backend framework, spike branches, updated estimates for
Phase 1.

### Phase 1 — MVP Local Agent (4–6 weeks)

- Create the `siard-server` module with the chosen framework, with `siard-agent`
  and `siard-hub` source sets from the start. The first usable artifact is the
  local agent.
- Implement archive-from-DB job flow for the local agent (`POST
  /api/v1/jobs/archive`).
- Add job status, progress (SSE), and download endpoints.
- Use SQLite persistence for job history through a shared `JobStore` abstraction.
- No authentication, localhost-only binding, and a bundled JRE zip distribution.
- Create the `siard-web` frontend module and serve the same SPA from the local
  agent.

Deliverable: a working local agent that can archive a database and produce a
downloadable SIARD file.

### Phase 2 — Feature parity and hardening (6–8 weeks)

- Restore, browse, and export endpoints in the local agent.
- ZIP package for archives with external LOBs.
- Cancellation, retries, and improved error reporting.
- Frontend parity with the desktop application.
- SQLite hardening (indexes, retention, cleanup).
- Start user acceptance testing against the desktop app and define the phase-out
  criteria for JavaFX.

Deliverable: local agent reaches core feature parity with the desktop app.

### Phase 3 — Central Hub (4–6 weeks)

- Add OIDC authentication and Keycloak integration.
- Add PostgreSQL persistence for hub job state.
- Implement per-user file isolation and resource limits.
- Container image and IT deployment documentation.
- Audit logging, metrics, health endpoints, and operator runbook.
- CI/CD integration for local agent archive, container builds, and frontend
  tests.
- SBOM generation and dependency scanning.

Deliverable: production-ready central hub for IT deployment.

### Phase 4 — Desktop phase-out and enterprise readiness (2–4 weeks)

- Deprecate the JavaFX desktop application once local agent feature parity is
  confirmed.
- Finalize end-user documentation and migration guide.
- Enterprise hardening: structured logging, audit log retention, backup/restore
  of hub persistence, disaster recovery runbook.

Deliverable: desktop application declared end-of-life; Web Companion in full
production use.

## Dependencies on existing-code refactoring

Some refactorings in [docs/refactoring-plan.md](refactoring-plan.md) make
the Web Companion easier to build and maintain:

- Extracting `SiardFromDbConfig` / `SiardToDbConfig` and an explicit
  `execute()`/`run()` method from `siard-cmd` makes it easier to drive the same
  logic from the web backend without copy-pasting CLI argument parsing.
- Eliminating downcasting from `siard-api` interfaces to implementations makes
  the core logic easier to unit-test and compose in the web backend.
- Introducing domain exceptions instead of `IOException` for business errors
  gives the web backend clearer error handling.

These refactorings can proceed in parallel with the Web Companion work, but
early alignment on the extracted APIs avoids rework.

## Open questions / customer decisions

1. **EIAM integration details**: exact OIDC endpoints, group/role claim format,
   and whether PKCE is required.
2. **Hub network placement**: for databases only reachable inside restricted
   networks, how will IT place or connect the central hub?
3. **SIARD retention on the hub**: how long should generated archives be kept
   before automatic deletion?
4. **Audit log retention**: confirm SFA/BIT policy (the architecture suggests
   seven years for an archival context).
5. **Frontend languages**: confirm German + English for the MVP; identify any
   required additional languages.
6. **Desktop phase-out criteria**: confirm the feature-parity milestone and
   timeline for deprecating the JavaFX desktop application.

## Risks

| Risk | Mitigation |
|---|---|
| Backend framework choice is wrong for `siard-api` integration | Resolve with the Phase 0 spike before committing. |
| `siard-api` native image is hard to build | Keep the local agent on a bundled JVM initially; native image is a later optimization. |
| External LOB ZIP packaging confuses users | Provide clear UI guidance and validation. |
| Refactoring `siard-cmd` delays Web Companion reuse | Extract small, stable APIs first; do not wait for full modernization. |
| License compatibility with new frameworks | Verify CDDL-1.0 compatibility before adding dependencies; generate SBOMs. |
| UI logic currently lives in JavaFX controllers | Reimplement web flows incrementally; reuse `siard-api` and JDBC wrappers underneath. |
