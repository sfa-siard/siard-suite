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
headless backend. The existing desktop and CLI applications remain unchanged.

## Goal

Deliver a Web Companion that lets SFA staff and other users archive and restore
relational databases through a browser, while reusing the existing SIARD core
libraries (`siard-api`, JDBC wrappers, `siard-cmd`).

## Relationship to other documents

| Document | Purpose |
|---|---|
| [Software Architecture Documentation - Companion Architecture](sad/software-architecture-documentation-companion-architecture.md) | Canonical technical architecture: deployment model, security, authentication, persistence, API, and risks. |
| [Web Companion Spike Plan](sad/web-companion-spike-plan.md) | One-week engineering spike to choose Quarkus or Spring Boot as the backend framework. |
| [Refactoring Plan](refactoring-plan.md) | Independent refactoring of the existing codebase. Some refactorings (e.g., extracting CLI config and orchestrator from `siard-cmd`) make reusing SIARD logic in the Web Companion easier. |

## High-level delivery model

- **Central Hub** — primary delivery model for SFA staff. Deployed and managed
  by IT, accessed via browser, uses OIDC through Keycloak, persists job state in
  PostgreSQL.
- **Local Agent** — secondary model for databases or users that cannot be reached
  from a central server. Runs on the user's machine or a nearby server, uses an
  embedded SQLite database for job history, no authentication.
- **Desktop / CLI** — unchanged; continues to serve users who can run Java
  binaries or need direct database access.

See the companion architecture document for the full architecture.

## Phased roadmap

### Phase 0 — Spike (1 week)

Validate the backend framework and core integration before committing to the
MVP.

- Build minimal Quarkus and Spring Boot prototypes.
- Run an end-to-end archive job through the existing `siard-api`.
- Compare container builds, native-image feasibility, OIDC integration with
  Keycloak, PostgreSQL persistence, and developer experience.
- Choose the backend framework and record the decision in an ADR.

Deliverable: chosen backend framework, spike branches, updated estimates for
Phase 1.

### Phase 1 — MVP Backend (4–6 weeks)

- Create the `siard-server` module with the chosen framework.
- Implement archive-from-DB job flow (`POST /api/v1/jobs/archive`).
- Add job status, progress (SSE), and download endpoints.
- Support hub mode with PostgreSQL and local-agent mode with SQLite using a
  shared `JobStore` abstraction.
- Integrate Keycloak OIDC authentication for the hub.
- Implement per-user file isolation and basic resource limits.

Deliverable: a working backend that can archive a database and produce a
downloadable SIARD file.

### Phase 2 — MVP Frontend (4–6 weeks)

- Create the `siard-web` module as a React/Vite SPA.
- DB connection form.
- Job submission and real-time progress display.
- SIARD download.
- "Pending access" page for users without a SIARD role.
- Admin "pending users" page.

Deliverable: browser-based archive workflow.

### Phase 3 — Feature parity and hardening (6–8 weeks)

- Restore, browse, and export endpoints.
- ZIP package upload/download for archives with external LOBs.
- Cancellation, retries, and improved error reporting.
- Frontend parity with the desktop application.
- Persistent job store hardening (indexes, retention, cleanup).

Deliverable: core feature parity with desktop app for common workflows.

### Phase 4 — Enterprise readiness (4–6 weeks)

- Container image and deployment documentation for IT.
- Audit logging and structured logging.
- Metrics, health endpoints, and operator runbook.
- CI/CD integration for container builds and frontend tests.
- SBOM generation and dependency scanning.
- Split production artifacts into `siard-agent` and `siard-hub`.

Deliverable: production-ready hub for IT deployment.

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

## Risks

| Risk | Mitigation |
|---|---|
| Backend framework choice is wrong for `siard-api` integration | Resolve with the Phase 0 spike before committing. |
| `siard-api` native image is hard to build | Keep hub on JVM container; native image only for local-agent future consideration. |
| External LOB ZIP packaging confuses users | Provide clear UI guidance and validation. |
| Refactoring `siard-cmd` delays Web Companion reuse | Extract small, stable APIs first; do not wait for full modernization. |
| License compatibility with new frameworks | Verify CDDL-1.0 compatibility before adding dependencies; generate SBOMs. |
| UI logic currently lives in JavaFX controllers | Reimplement web flows incrementally; reuse `siard-api` and JDBC wrappers underneath. |
