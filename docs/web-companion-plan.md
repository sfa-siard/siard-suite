# SIARD Suite Web Companion — Draft Plan

## Context

SIARD Suite is currently a desktop (JavaFX) and command-line application. The
funding client can no longer deploy Java runtimes or downloaded executables on
managed end-user systems. They need a delivery model that:

- Does not require a Java runtime on the end-user desktop.
- Can be centrally deployed and updated by their IT department.
- Still allows users to archive databases that are only reachable from inside
  restricted networks.

This plan proposes a **Web Companion** with a headless backend and a web
frontend. The existing desktop and CLI applications remain unchanged.

## High-level concept: Companion Agent Architecture

A lightweight **companion server** wraps the existing SIARD core libraries
(`siard-api`, the JDBC wrappers, `siard-cmd`). A **web frontend** talks to this
server over HTTP/REST.

The companion can run in two roles:

1. **Local agent** — runs on the user's machine, has the user's network access,
   controlled from the browser.
2. **Hub** — runs centrally, reachable from many users, manages jobs and users.

The existing `siard-cmd` CLI is already the simplest form of a "near the
database" agent. The web companion is a more user-friendly, browser-driven
alternative.

## Backend technology

[Javalin](https://javalin.io/) is the preferred candidate for the companion
server.

- Lightweight, embedded Jetty, fast startup.
- Good fit for a small headless agent controlled by a web UI.
- Less overhead than Spring Boot or Quarkus.

Trade-off: Javalin does not provide enterprise features (OIDC, metrics,
persistent job queues, scheduling) out of the box. Those must be added
explicitly or handled by an external gateway.

For a full-featured central hub, Spring Boot or Quarkus may be easier to
operate. The final choice depends on whether the primary use case is a local
agent or a managed multi-user service.

## Deployment modes

### Local agent mode

- Binds to `127.0.0.1` only.
- No authentication.
- In-memory job tracking.
- Single-user file storage.
- Users run the companion locally and access it via the browser.

### Hub mode

- Binds to a configured external interface.
- Requires authentication and authorization.
- Persistent job store.
- Per-user / per-job file isolation.
- IT deploys and updates the service centrally.

### One artifact or two?

Technically one artifact can support both modes with a runtime flag such as
`--server.mode=local|hub`. However, auto-detecting the mode from the environment
is risky: a misconfiguration could silently expose an unauthenticated service
to the network.

Recommended approach:

- **For the spike**: start with one Javalin artifact and an explicit
  `--server.mode` flag. `local` is the safe default; `hub` requires deliberate
  opt-in.
- **For production**: build two artifacts from one codebase:
  - `siard-agent` — minimal, localhost-only, no auth.
  - `siard-hub` — full server with auth, persistence, and multi-user isolation.

## Network reachability

In hub mode, the server can only archive databases it can reach itself. A user's
browser does not extend the server's network access. This is a fundamental
constraint.

Implications:

- Databases only reachable from a user's machine still require a **local agent**.
- Databases inside a corporate network require the hub to run inside that
  network (or on a host with VPN/network access).
- `siard-cmd` remains a valid option for automated or advanced use cases in
  restricted networks.

## Authentication

- **Local agent mode**: no auth needed. The service is not reachable from the
  network.
- **Hub mode**: auth is required to prevent users from seeing or downloading each
  other's SIARD files and to control who can archive or restore data.

Enterprise options for hub mode: OIDC/SAML SSO, API keys, or integration with
an existing API gateway.

## Security

### Security posture compared to current applications

| Aspect | Desktop app (`siard-suite-app`) | Local web agent | Central hub |
|---|---|---|---|
| **Runtime context** | User's OS account. | User's OS account. | Service account / server. |
| **Network origin** | User's machine. | User's machine. | Server's network. |
| **Authentication** | OS login. | None needed if bound to `127.0.0.1`. | Required (OIDC/API key/etc.). |
| **Authorization** | Single user. | Single user. | Multi-user; per-job/per-file access control. |
| **Data isolation** | Filesystem permissions. | Filesystem permissions. | Must enforce server-side isolation. |
| **Attack surface** | Local JavaFX UI, file I/O, JDBC. | Adds an HTTP API + browser frontend. | Full network service + data store. |
| **Trust boundary** | User trusts their own machine. | User trusts their own machine. | Users trust hub operator; operator can access everything. |
| **Compromise impact** | One user, one machine. | One user, one machine. | All users, all reachable DBs, all stored SIARD files. |

### Key differences

- **Desktop and local agent are similar in principle**: both run under the user's
  OS account and connect to databases from the user's network context. The local
  agent introduces an HTTP server and a browser attack surface. If it is ever
  misconfigured to bind to `0.0.0.0`, it becomes an unauthenticated network
  service.
- **Hub is a different class of risk**: it centralizes credentials, database
  access, and archived data. It needs authentication, authorization, audit
  logging, transport encryption, and strict file isolation. A compromised hub
  is much worse than a compromised local agent.

### Security requirements for the companion

1. **Bind-address enforcement**  
   Local agent mode must bind to `127.0.0.1` only and refuse to start on a
   non-loopback interface. Hub mode must require an explicit configuration flag
   and external bind address.

2. **CSRF and origin handling**  
   Even on `localhost`, a malicious website could make requests to the local
   agent. The server should validate origins or only serve the web frontend
   from itself (same-origin).

3. **Job result access control**  
   In hub mode, only the job owner (or granted roles) may query a job and
   download its SIARD file.

4. **Credential handling**  
   Database credentials must not be logged or stored in plaintext. In hub mode,
   prefer a secrets manager or short-lived encryption rather than keeping
   passwords in the job store.

5. **File upload and download safety**  
   Validate uploaded SIARD files, prevent path traversal in downloads, and avoid
   serving files outside the configured working directory.

6. **DoS and resource exhaustion**  
   Limit concurrent long-running jobs, file sizes, and temporary disk usage so
   one user cannot exhaust the server.

7. **Transport security**  
   Hub mode must use TLS, even behind a reverse proxy. Local agent mode may use
   plain HTTP on `localhost` but should still validate origins.

8. **Audit logging**  
   In hub mode, log who submitted which job, against which database, and who
   downloaded which result.

9. **Frontend supply chain**  
   The web frontend will depend on npm packages. Generate an SBOM and scan
   dependencies as part of CI/CD.

10. **Secure defaults**  
    The artifact must default to local mode, refuse to run as root or admin, and
    fail safe rather than fall back to an open configuration.

## External LOBs

SIARD archives can store large objects (LOBs) outside the `.siard` file in an
external LOB folder. The SIARD metadata references that folder, typically with a
relative URI such as `../externalLobs/`. This keeps the `.siard` file small but
creates a two-part artifact: the `.siard` file and the external LOB folder must
always stay together.

### Why this is tricky in hub mode

In hub mode the `.siard` file and the external LOB folder are produced on the
server. Delivering them to a user, or accepting them from a user, is harder
than a single-file workflow:

- **Download**: a browser cannot download a folder directly. If only the
  `.siard` file is downloaded, the LOB references are broken.
- **Upload**: a browser cannot easily select a local folder for upload. The user
  must upload both the `.siard` file and the external LOB folder together.
- **Path references**: the SIARD metadata stores relative paths. Moving the
  `.siard` file away from its LOB folder breaks the archive unless the relative
  relationship is preserved.
- **Restore**: the server needs both parts in the correct relative locations
  before it can read or restore the archive.
- **Validation**: when reading an uploaded archive, the server must verify that
  the referenced external LOB folder exists and that LOB paths do not escape the
  archive directory (path traversal).

### Options for hub mode

1. **Zip package**  
   Download and upload a ZIP containing the `.siard` file and the external LOB
   folder in the correct relative layout. This is the most faithful option but
   requires users to understand that they are handling a package, not a single
   file.

2. **Re-internalize LOBs on download**  
   Rewrite the archive so that LOBs are stored inside the `.siard` file before
   download. Simple for the user, but it can create very large downloads and
   changes the archive format. May not be acceptable for preservation workflows.

3. **Disable external LOBs in hub mode**  
   Force all LOBs to be stored inside the `.siard` file. Simple, but changes
   archive size and behavior compared to the desktop application.

4. **Server-managed external storage**  
   Store LOBs in object storage and rewrite references. Powerful but non-standard
   and adds significant complexity.

### Recommendation

- **Local agent mode**: keep the existing behavior. The agent can write and read
  the `.siard` + external LOB folder pair directly on the user's filesystem.
- **Hub mode**: support external LOBs through **ZIP package downloads and
  uploads** (option 1). Add clear validation and user feedback so that LOB
  references are never accidentally broken.

## Proposed module layout

Add two new Gradle modules to the existing monorepo:

- `siard-server` — Java 21 backend (Javalin-based).
- `siard-web` — TypeScript/React SPA.

Existing modules (`siard-suite-app`, `siard-cmd`, `siard-api`, JDBC wrappers)
remain unchanged.

## Backend API sketch

The API is job-oriented because archiving and restoring can run for a long
time.

- `POST /jobs/archive` — submit a database-to-SIARD job.
- `POST /jobs/restore` — submit a SIARD-to-database job.
- `GET /jobs/{id}` — query status and result.
- `GET /jobs/{id}/download` — fetch the generated SIARD file.
- WebSocket or SSE endpoint for progress updates.
- `GET /health` and `/metrics` for operations.

## Phased roadmap

### Phase 0 — Spike (1-2 weeks)

- Confirm Javalin (or evaluate Spring Boot / Quarkus if hub mode is dominant).
- Define the job API contract and async model.
- Prove end-to-end: web form → companion server → `siard-api` → downloadable
  SIARD.

### Phase 1 — MVP backend (4-6 weeks)

- Create the `siard-server` module.
- Implement archive-from-DB job flow.
- Add download and progress endpoints.
- Support local mode by default; add explicit opt-in for hub mode.

### Phase 2 — MVP frontend (4-6 weeks)

- Create the `siard-web` module.
- DB connection form.
- Job submission and progress display.
- SIARD download.

### Phase 3 — Feature parity and hardening (6-8 weeks)

- Restore, browse, and export endpoints.
- Persistent job store.
- Cancellation, retries, and improved error reporting.
- Frontend parity with the desktop application.

### Phase 4 — Enterprise readiness (4-6 weeks)

- Container image and/or GraalVM native image.
- Hub-mode authentication (OIDC / API keys).
- Metrics, structured logging, and audit logging.
- CI/CD integration and documentation.

## Open questions for customer discussion

1. Is the primary need a **central hub** shared by many users, or a **local
   browser-driven agent** for restricted networks?
2. Which databases must be archived from user-only networks, and which are
   reachable from a central server?
3. Does the client require authentication and audit logging, or is
   unauthenticated local use acceptable?
4. Is a container-based central deployment preferred, or do they need a native
   executable for local use?
5. Does the client need multi-tenancy or simple per-user file isolation?

## Risks

- **GraalVM native image**: `siard-api` uses JAXB and JDBC drivers; native-image
  metadata may be non-trivial.
- **License compatibility**: CDDL-1.0 is compatible with Apache-2.0 dependencies,
  but verify distribution requirements when bundling new frameworks.
- **UI logic reuse**: much of the business flow may currently live in JavaFX
  controllers and need to be reimplemented in the web frontend.
- **Security boundary**: mixing local and hub modes in one artifact increases
  the risk of accidental exposure.

## Recommendation

1. Validate the idea with a Javalin-based spike that exposes one archive
   endpoint.
2. Start in **local agent mode** so the client can archive restricted databases
   without authentication complexity.
3. Add **hub mode** only after the local-agent workflow is proven.
4. Plan to split the build into `siard-agent` and `siard-hub` artifacts before
   the first production release.
