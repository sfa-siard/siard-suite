# Spec Delta

## Purpose

Lets the user of the local web app open a SIARD archive on their computer by its absolute path, read in place by the agent, and browse it with the shared archive views.

## ADDED Requirements

### Requirement: Open an archive from a local path

`POST /api/archive-files` with `{"path": <absolute path>}` SHALL register the file in place in the agent's archive scope and answer 201 with its `archiveId`. The file SHALL NOT be copied, moved, changed or deleted, also not when the archive is closed.

#### Scenario: Valid archive
- **GIVEN** a valid local session and a valid SIARD archive at an absolute path
- **WHEN** the client posts that path to `/api/archive-files`
- **THEN** the agent answers 201 with an `archiveId`
- **AND** `GET /api/archives/{archiveId}` answers 200 with the archive's `dbName`
- **AND** after `DELETE /api/archives/{archiveId}` the file still exists with unchanged content

#### Scenario: Unusable path or file
- **GIVEN** a valid local session
- **WHEN** the client posts a relative path, a path that does not exist, a folder, or an existing file that is not a SIARD archive
- **THEN** the agent answers 400, 404, 400 or 422 respectively with problem details and returns no `archiveId`
- **AND** when the open-archive limit is reached, the agent answers 429 and no open archive is closed

#### Scenario: Opening needs a session
- **GIVEN** a request without a valid session cookie
- **WHEN** it posts the path of a valid archive to `/api/archive-files`
- **THEN** the agent answers 401 and registers no archive

### Requirement: Inspect workflow

Choosing a `.siard` file in the chooser started from the start page's Open button SHALL open it and navigate to `/archives/{archiveId}`, which shows the archive with the shared archive browse views. A Close action SHALL close the archive and return to `/`.

#### Scenario: Inspect an archive
- **GIVEN** a valid local session and the chooser opened from Open
- **WHEN** the user chooses a valid `.siard` file
- **THEN** the route becomes `/archives/{archiveId}` and the archive's schemas are shown
- **AND** activating Close sends `DELETE /api/archives/{archiveId}` and the route becomes `/`

#### Scenario: Archive cannot be opened or is gone
- **GIVEN** a valid local session
- **WHEN** the user chooses a file the agent answers with 422 (or 429, or another error), or visits `/archives/{archiveId}` for an archive that was closed or evicted
- **THEN** an error in the current interface language says the file cannot be opened as a SIARD archive (422), that too many archives are open (429), that the archive could not be opened (other errors), or that it is no longer open
- **AND** a failed open leaves the user on the start page; on the archive page, a link leads back to `/`

#### Scenario: No path in the address bar
- **GIVEN** a valid local session and a network-request recorder
- **WHEN** the user inspects an archive
- **THEN** the archive's file path appears neither in the address bar nor in any request URL; it is sent only in the body of `POST /api/archive-files`
