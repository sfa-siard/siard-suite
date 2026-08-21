# SIARD Suite Web Companion — Spike Plan

This plan describes the one-week spike to select the backend framework for the
SIARD Suite Web Companion and to validate the core architectural assumptions
defined in the [Software Architecture Documentation - Companion Architecture](software-architecture-documentation-companion-architecture.md).

## Goal

Decide whether the Web Companion backend is built with **Quarkus** or
**Spring Boot** by running a representative end-to-end archive job in both
frameworks and comparing them against the project's constraints.

The spike must also prove that the chosen architecture can:

- Build a runnable container image.
- Expose a REST API consumed by a web frontend.
- Authenticate users via OIDC in hub mode.
- Persist job state in PostgreSQL.
- Run an archive job through the existing `siard-api` and JDBC wrappers.
- Produce a downloadable SIARD file or ZIP package (when external LOBs are used).

## Duration

**One week** (recommended: five working days).

The week is split into two parallel tracks where possible, but one developer
should own each framework branch to keep comparisons fair.

## Scope

In scope:

- Two minimal backend prototypes: one in Quarkus, one in Spring Boot.
- One minimal React frontend for submitting an archive job and viewing progress.
- Container image build for each backend prototype.
- Native-image feasibility check for at least one framework (time permitting).
- OIDC authentication using a mock identity provider (e.g., Keycloak in Testcontainers).
- PostgreSQL persistence for job state.
- One end-to-end archive job against an in-memory or containerized database.

Out of scope:

- Restore, browse, or export workflows.
- Full external LOB ZIP packaging (validate concept only).
- Production-grade security hardening.
- Multi-user isolation beyond basic ownership.
- Real SFA/BIT identity provider integration.
- Final CI/CD pipeline.

## Assumptions

- The spike uses a local or Testcontainers PostgreSQL database.
- The archive target is a small, well-known schema such as the PostgreSQL
  `dvdrental` sample, a H2 in-memory database, or a simple custom schema.
- The OIDC mock runs in Docker/Testcontainers and exposes standard endpoints.
- Existing `siard-api`, `siard-cmd`, and JDBC wrapper modules are available as
  Gradle project dependencies.

## Tasks

### Day 1 — Setup and common API contract

- [ ] Create two spike branches: `spike/quarkus` and `spike/spring-boot`.
- [ ] Define a minimal OpenAPI contract for the spike:
  - `POST /api/v1/jobs/archive`
  - `GET /api/v1/jobs/{id}`
  - `GET /api/v1/jobs/{id}/events` (Server-Sent Events for progress)
  - `GET /api/v1/jobs/{id}/download`
- [ ] Set up a shared React frontend prototype that calls the contract.
- [ ] Create a Testcontainers-based integration test harness that starts
  PostgreSQL and a mock OIDC provider.

### Day 2 — Quarkus prototype

- [ ] Bootstrap a Quarkus application in a new Gradle module
  (`siard-server-quarkus-spike`).
- [ ] Add dependencies: REST extension, OIDC extension, Flyway or Hibernate
  Panache, JDBC PostgreSQL driver.
- [ ] Implement the four endpoints using the contract from Day 1.
- [ ] Integrate `siard-api` to run a real archive job inside the JVM.
- [ ] Write an integration test that archives a small database end-to-end.

### Day 3 — Spring Boot prototype

- [ ] Bootstrap a Spring Boot application in a new Gradle module
  (`siard-server-spring-boot-spike`).
- [ ] Add dependencies: Spring Web, Spring Security OAuth2 Resource Server,
  Spring Data JPA, Flyway, PostgreSQL driver.
- [ ] Implement the same four endpoints.
- [ ] Integrate `siard-api` to run a real archive job inside the JVM.
- [ ] Write an integration test that archives a small database end-to-end.

### Day 4 — Container, native image, and operations comparison

- [ ] Build a container image for each prototype.
  - Quarkus: compare JVM container and, if feasible, native container build.
  - Spring Boot: compare Dockerfile, Jib, and/or Buildpacks builds.
- [ ] Measure startup time, image size, and memory footprint.
- [ ] Document how each framework handles:
  - Configuration and profiles for `local` vs `hub` mode.
  - Health and metrics endpoints.
  - Structured logging format.
  - OpenAPI documentation generation.
- [ ] Run the integration tests inside the container for at least one framework.

### Day 5 — Evaluation and decision

- [ ] Score both prototypes against the decision criteria below.
- [ ] Document the choice and the reasoning in an Architecture Decision Record
  (ADR).
- [ ] List the second-place framework's trade-offs and a migration path if the
  decision needs to be revisited.
- [ ] Identify the first concrete tasks for the MVP backend.

## Deliverables

1. Two spike branches with working prototypes.
2. A comparison document or spreadsheet scored against the criteria below.
3. An Architecture Decision Record (`docs/decisions/XXXX-web-companion-backend-framework.md`).
4. Updated estimates for the MVP backend based on the chosen framework.
5. A list of open questions that only appear once implementation starts
  (e.g., exact OIDC group claims, JDBC driver distribution, LOB streaming).

## Decision Criteria

Each criterion is weighted by importance. Score each framework from 1 (poor) to
5 (excellent).

| Criterion | Weight | Why it matters |
|---|---|---|
| **Compatibility with `siard-api` and JDBC wrappers** | High | The backend must reuse the existing Java libraries with minimal rework. |
| **Container build and image size** | High | The hub is deployed as a container; image size and startup time matter for operations. |
| **Native-image feasibility** | Medium | A native image is desirable for a future local agent but not required for the hub. |
| **OIDC integration effort** | High | Hub mode requires OIDC/SAML SSO; the framework should make this straightforward. |
| **Persistence and migrations** | Medium | Job state is stored in PostgreSQL; migrations must be reliable and versioned. |
| **Developer experience in the monorepo** | High | The team should be productive with the chosen stack inside the existing Gradle build. |
| **Observability (metrics, health, logging)** | Medium | Operations needs health, metrics, and structured logs. |
| **Frontend/API ergonomics** | Medium | REST, SSE, and OpenAPI should be easy to implement and document. |
| **License compatibility** | High | Dependencies must be compatible with CDDL-1.0 distribution. |
| **Community and long-term maintenance** | Medium | The framework should have active maintenance and good documentation. |

## Decision Rules

- If one framework scores clearly higher on the high-weight criteria, choose it.
- If scores are close, prefer the framework with the lower-risk native-image path
  and the simpler container build.
- If neither framework can run `siard-api` in the same JVM without significant
  adaptation, escalate before committing to either.

## Risks and Mitigations

| Risk | Mitigation |
|---|---|
| `siard-api` or JDBC drivers fail in a native image | Time-box native-image evaluation. If it fails, the hub can still run as a JVM container; native image becomes a local-agent-only future option. |
| OIDC mock differs from real SFA/BIT provider | Record the assumptions (group claim format, PKCE, scopes). Plan a validation task with the real provider as soon as possible. |
| Container build is slow or produces huge images | Compare Jib, Buildpacks, and Quarkus extensions. Document the chosen build and why. |
| Front-end integration reveals API design issues | Keep the API contract minimal. Iterate on SSE shape and error responses during the spike. |
| One framework needs substantially more boilerplate | Capture concrete line counts and developer friction; do not rely on impressions. |

## Next Steps After the Spike

1. Merge or archive the spike branches.
2. Create the real `siard-server` module using the chosen framework.
3. Create the `siard-web` module with the React/Vite/TanStack Query stack.
4. Implement the first slice: `POST /api/v1/jobs/archive` and `GET /api/v1/jobs/{id}`.
5. Set up CI/CD to build the container image and run Testcontainers-based tests.
6. Schedule a follow-up task to validate OIDC against the real identity provider.
