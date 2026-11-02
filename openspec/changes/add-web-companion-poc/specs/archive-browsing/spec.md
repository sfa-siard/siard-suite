# Spec Delta

## Purpose

Opening a SIARD archive stored on the local filesystem through the local web agent and exposing its metadata tree (schemas, tables, columns) for display in the web frontend.

## ADDED Requirements

### Requirement: Open an archive by local path

The agent SHALL open a `.siard` file at a given local filesystem path and return an identifier for subsequent requests.

#### Scenario: Valid archive path

- **GIVEN** the agent is running
- **AND** a valid `.siard` file exists at path P
- **WHEN** `POST /api/archives` is called with `path` = P and a valid session token
- **THEN** the response status is 201
- **AND** the response body contains an archive id and the file name

#### Scenario: Nonexistent path

- **GIVEN** the agent is running
- **WHEN** `POST /api/archives` is called with a `path` that does not exist and a valid session token
- **THEN** the response status is 400
- **AND** the response body contains an error message

#### Scenario: File is not a valid SIARD archive

- **GIVEN** the agent is running
- **AND** a file exists at path P that is not a valid SIARD archive
- **WHEN** `POST /api/archives` is called with `path` = P and a valid session token
- **THEN** the response status is 400
- **AND** the response body contains an error message

#### Scenario: Request without token

- **GIVEN** the agent is running
- **WHEN** `POST /api/archives` is called without a session token
- **THEN** the response status is 401

### Requirement: Archive metadata tree

The agent SHALL return the metadata tree of an opened archive: schemas containing tables, each table listing its columns with name and type and the table's row count.

#### Scenario: Tree of an opened archive

- **GIVEN** an archive with id A was opened successfully
- **WHEN** `GET /api/archives/A/tree` is called with a valid session token
- **THEN** the response status is 200
- **AND** the body contains a `schemas` array where each entry has `name` and `tables`
- **AND** each table entry has `name`, `rowCount`, and a `columns` array where each column has `name` and `type`

#### Scenario: Unknown archive id

- **GIVEN** the agent is running
- **WHEN** `GET /api/archives/unknown-id/tree` is called with a valid session token
- **THEN** the response status is 404

#### Scenario: Request without token

- **GIVEN** an archive with id A was opened successfully
- **WHEN** `GET /api/archives/A/tree` is called without a session token
- **THEN** the response status is 401

### Requirement: Frontend displays archive tree

The web frontend SHALL let the user enter a local archive path and display the resulting schema/table/column tree.

#### Scenario: User opens an archive in the browser

- **GIVEN** the web frontend is loaded via the launch URL
- **WHEN** the user submits a valid `.siard` path
- **THEN** the frontend renders an element `data-testid="archive-tree"` containing one entry per returned schema, with tables nested under their schema and columns nested under their table

#### Scenario: User submits an invalid path

- **GIVEN** the web frontend is loaded via the launch URL
- **WHEN** the user submits a path that the backend rejects
- **THEN** the frontend renders an element `data-testid="error-message"` containing the backend's error text
- **AND** no `archive-tree` element is rendered

#### Scenario: Frontend API calls carry the token

- **GIVEN** the web frontend was opened via the launch URL containing token T
- **WHEN** the frontend calls any `/api/*` endpoint
- **THEN** the request includes token T
