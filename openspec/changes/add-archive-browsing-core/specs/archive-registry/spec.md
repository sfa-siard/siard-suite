# Spec Delta

## Purpose

Defines how a web app holds open SIARD archives for its users: how an archive is referenced, who may reach it, how many may be open, and when it is released, so that a server shared by many users neither leaks one user's archive to another nor accumulates open files.

## ADDED Requirements

### Requirement: Archives are referenced by opaque ids

An app SHALL register an archive file for a scope and receive an `archiveId` of at least 22 URL-safe characters that is unguessable and carries no information about the file. All archive API paths address an archive by this id only; no API accepts a filesystem path.

#### Scenario: Registering a valid archive
- **GIVEN** the fixture `sample.siard` and a scope S
- **WHEN** an app registers the file for S
- **THEN** it receives an `archiveId` of at least 22 characters matching `[A-Za-z0-9_-]+`
- **AND** `GET /api/archives/{archiveId}` for scope S returns 200

#### Scenario: Registering a file that is not a valid archive
- **GIVEN** a file without `header/metadata.xml`, a file that is not a ZIP, and a path that does not exist
- **WHEN** an app registers each of them
- **THEN** each registration fails
- **AND** the registry holds no entry for any of them
- **AND** the release callback given at registration is invoked exactly once per failed registration

#### Scenario: Ids are not derivable
- **GIVEN** 1000 registrations of the same file
- **WHEN** the returned ids are compared
- **THEN** all 1000 ids are distinct
- **AND** no id contains the file name

### Requirement: Archives are isolated by scope

Every archive request SHALL be resolved in the scope the app determines for the request. An id that belongs to another scope SHALL be answered exactly like an unknown id: `404` with identical problem details. A request for which the app resolves no scope SHALL be answered `401` before any archive is looked up.

#### Scenario: Same scope reaches its archive
- **GIVEN** an archive registered for scope A
- **WHEN** a request in scope A addresses its id
- **THEN** the response status is 200

#### Scenario: Another scope cannot tell the archive exists
- **GIVEN** an archive registered for scope A and an id that was never issued
- **WHEN** a request in scope B addresses A's id, and another request in scope B addresses the unissued id
- **THEN** both responses have status 404 with content type `application/problem+json`
- **AND** the two response bodies are identical

#### Scenario: Request without a scope
- **GIVEN** an app whose scope resolver yields no scope for a request
- **WHEN** `GET /api/archives/{archiveId}` is requested with a valid id
- **THEN** the response status is 401
- **AND** the body contains no archive metadata

#### Scenario: A foreign scope cannot close an archive
- **GIVEN** an archive registered for scope A
- **WHEN** a request in scope B sends `DELETE /api/archives/{archiveId}`
- **THEN** the response status is 404
- **AND** the archive is still readable in scope A

### Requirement: Open archives are limited per scope

The registry SHALL allow at most 5 open archives per scope and 50 in total by default (configurable). A registration beyond a limit SHALL be rejected without closing any existing archive, and the app SHALL report it as `429` with problem details.

#### Scenario: Registration up to the limit
- **GIVEN** a scope with 4 open archives
- **WHEN** a fifth is registered
- **THEN** the registration succeeds

#### Scenario: Registration beyond the per-scope limit
- **GIVEN** a scope with 5 open archives
- **WHEN** a sixth is registered
- **THEN** the registration is rejected and surfaces as status 429 with content type `application/problem+json`
- **AND** the five existing archives remain readable
- **AND** the release callback of the rejected registration is invoked exactly once

#### Scenario: One scope cannot exhaust another
- **GIVEN** scope A holds 5 open archives
- **WHEN** scope B registers an archive
- **THEN** the registration succeeds

### Requirement: Idle archives are closed

An archive that has not been addressed for the idle timeout (default 30 minutes, configurable) SHALL be closed and its release callback invoked exactly once. Every successful request on the archive restarts the timeout. A request for a closed archive SHALL be answered `404` with problem details stating that the archive is no longer open.

#### Scenario: Activity keeps the archive open
- **GIVEN** an archive and a controllable clock with a 30 minute timeout
- **WHEN** a request is made every 20 minutes, three times
- **THEN** each response status is 200
- **AND** the release callback has not been invoked

#### Scenario: Idle archive is released
- **GIVEN** an archive that has not been addressed for 31 minutes
- **WHEN** the eviction runs
- **THEN** the archive is closed and the release callback has been invoked exactly once
- **AND** `GET /api/archives/{archiveId}` returns 404 with content type `application/problem+json`

#### Scenario: Released archive leaves no readable data
- **GIVEN** an archive that was evicted
- **WHEN** a request addresses its rows or LOB content
- **THEN** the response status is 404 and the body contains no row or LOB data

### Requirement: Archives can be closed explicitly

`DELETE /api/archives/{archiveId}` SHALL close the archive in the caller's scope, invoke its release callback and answer `204`. Closing again SHALL answer `404`.

#### Scenario: Close an archive
- **GIVEN** an open archive in the caller's scope
- **WHEN** `DELETE /api/archives/{archiveId}` is requested
- **THEN** the response status is 204
- **AND** the release callback has been invoked exactly once

#### Scenario: Close twice
- **GIVEN** an archive that was closed by `DELETE`
- **WHEN** `DELETE /api/archives/{archiveId}` is requested again
- **THEN** the response status is 404 with content type `application/problem+json`
- **AND** the release callback is not invoked again

### Requirement: Concurrent requests on one archive are safe

Requests addressing the same archive SHALL be served one at a time, so that concurrent requests never share a read position and always return the same result as when run sequentially.

#### Scenario: Concurrent paging returns the correct pages
- **GIVEN** a table of the fixture with at least 20 rows and 8 concurrent requests for different offsets
- **WHEN** they are all issued at once
- **THEN** each response equals the response of the same request issued alone
- **AND** no response is an error

#### Scenario: A slow request does not block another archive
- **GIVEN** a long-running request on archive X
- **WHEN** a request is made for archive Y of the same scope
- **THEN** it completes without waiting for the request on X

### Requirement: Open archives are released on shutdown

When the app shuts down, every open archive SHALL be closed and its release callback invoked exactly once.

#### Scenario: Shutdown with open archives
- **GIVEN** three open archives
- **WHEN** the application is stopped
- **THEN** each release callback has been invoked exactly once
