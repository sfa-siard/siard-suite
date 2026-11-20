# Proposal

## Why

The local web app's Archive button is currently inert. The desktop app solves the same problem with a modal dialog offering up to three recent connections or a new-connection path; the web app needs a proper page-based flow plus persistent storage for saved connections, since the agent has no preferences store yet. This change delivers connection profile management — the entry point of the archive workflow — without yet connecting to any database.

## What Changes

- Archive button on the start page becomes active and navigates to a new `/archive` page.
- `/archive` page lists saved connections (full list, delete supported) and offers the five server-based DBMS (PostgreSQL, MySQL, Microsoft SQL Server, Oracle, DB/2) for a new connection. MS Access is deferred: browsers cannot provide filesystem paths, and the planned native-file-picker approach is out of scope.
- `/archive/connection` page shows the connection details form: prefilled from a saved connection (password always empty), or initialized with editable `localhost` and the selected DBMS's default port for a new connection. The desktop-inspired layout pairs server/port with username, then database name with password; additional fields follow and the optional connection name comes last. No disabled DBMS input or save toggle is shown; the heading identifies the fixed DBMS. Saving is opt-in via a connection name; passwords are never transmitted or persisted.
- New REST API under `/api/`: DBMS catalog and saved-connection CRUD, behind the existing session checks, errors as RFC 9457 problem details.
- New SQLite store in the per-OS application-data directory holding saved connections (no passwords). The `file` column is reserved for the future MS Access flow.
- DBMS/connection model (`Dbms`, `DbmsConnectionData`, properties, `DbmsRegistry` equivalent) ported into the local backend in modern style. The desktop app keeps its own copy untouched; the model moves to `backend-core` later when the remote app needs it.
- OpenAPI description and frontend typed client regenerated.

## Capabilities

### New Capabilities

- `local-web-connections`: DBMS catalog, saved-connection persistence and CRUD API, the `/archive` picker page and `/archive/connection` details page, including credential-handling rules (passwords never stored or logged).

### Modified Capabilities

- `local-web-start-page`: the "Presentation-only workflow controls" requirement changes — the Archive button now navigates to `/archive`; Open, Upload and Export remain inert.

## Impact

- **Backend** (`siard-web/local/backend`): new `connections`/`dbms` packages — model port, SQLite store, JAX-RS resources; `sqlite-jdbc` dependency added.
- **Frontend** (`siard-web/local/frontend`): new `ArchiveView` and `ConnectionDetailsView`, routing, i18n strings (de/fr/it/en), API client types.
- **API**: `openapi.yaml` gains `/api/dbms` and `/api/connections` endpoints; frontend client regenerated.
- **Desktop app**: no changes — `siard-suite-app` keeps its own frozen model and preferences.
- **Platforms**: Windows, macOS, Linux — SQLite file location resolves per OS; no platform-specific code beyond path resolution.
