# Proposal

## Why

Both web apps (the local agent and the server-hosted **remote web app** the Swiss Federal Archives need) must let users browse an existing `.siard` archive: its structure, metadata and table data. Writing that feature once, in the shared parts created by `restructure-into-siard-web`, avoids two diverging implementations. The remote app is the first consumer, so the design must hold on a server with many users and many archives open at once. The local app's single-user case would hide those constraints. Notably, `siard-api` only reads rows forward through a cursor (`TableRecordDispenser.skip/get`, no seek) and the desktop app re-opens a cursor and skips on every page; that is acceptable on one desktop and not on a shared server.

## What Changes

- **Shared backend (`siard-web/backend-core`)**: an archive registry and a read-only REST API under `/api/archives/{archiveId}/...` to browse an open archive: archive metadata, schemas, tables and views with their columns, paged table rows, bounded text search within a table, and LOB/long-text content download. DTOs are records mapped from `siard-api`; no `siard-api` object is serialised.
- **Archive registry with a scope seam**: archives are referenced by an unguessable opaque `archiveId`. Each entry belongs to a *scope* supplied by the app through a small interface (local: one fixed scope; remote: the authenticated user or group). A request for an `archiveId` outside the caller's scope answers `404`, the same as an unknown id. The registry enforces limits and closes idle archives. The apps decide how an archive enters the registry (native picker, server store, upload); this change only defines the registration entry point.
- **Shared frontend (`siard-web/frontend-shared`)**: archive browse views (structure tree, metadata, table data grid with server-side paging, search, LOB download) that take an `archiveId` and the app's theme. They contain no shell, routing root or session handling; each app mounts them under its own route.
- **OpenAPI**: the core's paths and schemas are described in a committed `openapi.yaml` of the core, the frontend types are generated from it, and a check asserts that each app's own generated description contains the core's paths and schemas unchanged.
- **Tests with a real archive**: backend tests run the endpoints against the tracked fixture `siard-api/src/test/resources/testfiles/sample.siard`. No app exposes an "open archive" action yet; a test-only registration stands in. That action is the job of the follow-up changes (`add-local-archive-picker`, `add-remote-web-app`).

Out of scope: opening or uploading archives (app-specific, follow-up changes), editing metadata, sorting or exporting tables, routines/types/users/privileges/triggers views, metadata search, archiving databases, the remote app's authentication, and any change to the desktop app.

**Precondition:** `restructure-into-siard-web` is applied.

## Capabilities

### New Capabilities

- `archive-browsing`: the read-only API and shared UI for browsing an open SIARD archive: structure, metadata, paged rows, search and LOB content.
- `archive-registry`: how open archives are held and referenced: opaque ids, scope isolation, limits, idle eviction and release of resources.

### Modified Capabilities

None.

## Impact

- **New code**: archive registry, resources and DTOs in `siard-web/backend-core`; browse views, generated core API types and tests in `siard-web/frontend-shared`; `siard-web/backend-core/openapi.yaml`.
- **API**: new `/api/archives/**` endpoints in every app that includes the core (the local app's `openapi.yaml` grows accordingly; the drift check requires it to be regenerated).
- **Dependencies**: `@mui/x-data-grid` (Community tier, MIT) in the shared frontend package. Nothing new in the backend; the core stays free of `siard-cmd` and JDBC drivers (boundary from `restructure-into-siard-web`).
- **Possible `siard-api` change**: the archive may only be openable read-write today (see design S2). If so, a small read-only open path is added to `siard-api`; this is the only change outside the web modules and is confirmed by a spike before it is made.
- **Risks to validate early** (design Spikes): thread-safety of `ArchiveImpl`, cost of forward-only paging, read-only open, and Jandex/OpenAPI scanning across the module boundary.
