# ADR-0003: Web Companion Job Persistence

## Status

Accepted

## Context

The Web Companion has two runtime roles with very different operational
constraints:

- The **Central Hub** is a multi-user service managed by IT. It needs a robust,
  queryable, backup-friendly persistent store.
- The **Local Agent** must work with zero setup on a user's machine. Users
  cannot be expected to install PostgreSQL, Docker, or any external database.

The persistence model must support job history, status, and troubleshooting. It
must also be possible to implement a consistent job-store abstraction across
both roles.

## Decision

Use a **shared `JobStore` abstraction** with different implementations for each
role:

- **Hub**: **PostgreSQL** for job state and user/job associations.
- **Local Agent**: **SQLite** embedded file database for job history.

The SQLite file lives in the local agent's working directory by default. The
ability to configure a custom path is a future extension, not part of the MVP.

Use **Flyway** for schema migrations in both PostgreSQL and SQLite, keeping the
same schema for both implementations.

Resuming interrupted archive/restore jobs is **out of scope** for the MVP. The
underlying `siard-api` and `siard-cmd` libraries implement operations as atomic,
all-or-nothing processes with no checkpoint/resume support. Persistence is used
for job history, status, and troubleshooting only.

Job history in the local agent is kept indefinitely; the user can delete the
working directory to reset it.

## Consequences

- **Positive**: local agent requires no external database setup; hub uses a
  standard IT-managed database; both roles share the same abstraction and
  schema.
- **Negative**: SQLite and PostgreSQL have different SQL dialects and
  concurrency models; the shared schema must avoid vendor-specific features.
- **Neutral**: Resume remains a future concern; adding it would require changes
  deep in `siard-api`/`siard-cmd`, not just in the Web Companion.

## Alternatives

- **PostgreSQL for both hub and local agent** — rejected because users would
  have to install and manage a database.
- **In-memory only for local agent** — rejected because job history is lost on
  restart and cannot support status/troubleshooting.
- **Different schemas for PostgreSQL and SQLite** — rejected because it fragments
  the abstraction and complicates testing.
