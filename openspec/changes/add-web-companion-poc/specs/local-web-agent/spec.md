# Spec Delta

## Purpose

A locally running companion process that serves the SIARD web frontend and REST API on localhost, protected by a per-launch session token, and is distributable as a Windows installer with a bundled Java runtime.

## ADDED Requirements

### Requirement: Local server startup

The agent SHALL start a web server bound to 127.0.0.1 on a dynamically selected free port, generate a random session token, and open the user's default browser at the agent URL containing that token.

#### Scenario: Successful launch

- **GIVEN** the agent executable is installed
- **WHEN** the user launches the agent
- **THEN** a web server listens on 127.0.0.1 on a free port
- **AND** the default browser is opened at the agent URL
- **AND** the URL contains a per-launch session token

#### Scenario: No browser available

- **GIVEN** the agent is launched on a system where no default browser can be opened
- **WHEN** the browser open call fails
- **THEN** the agent logs the full agent URL including the session token
- **AND** the agent keeps running and serving requests

#### Scenario: Static frontend reachable without token

- **GIVEN** the agent is running
- **WHEN** the frontend page or a static asset is requested without a session token
- **THEN** the response status is 200

### Requirement: Session token enforcement

Every request to an `/api/*` endpoint MUST present the session token issued at launch, either as a bearer token or as a query parameter. Non-API endpoints (static frontend, health) are exempt.

#### Scenario: Request with valid token

- **GIVEN** the agent is running with session token T
- **WHEN** a request to an `/api/*` endpoint presents token T
- **THEN** the request is processed normally and does not return 401 or 403

#### Scenario: Request with invalid token

- **GIVEN** the agent is running with session token T
- **WHEN** a request to an `/api/*` endpoint presents a token different from T
- **THEN** the response status is 401

#### Scenario: Request without token

- **GIVEN** the agent is running
- **WHEN** a request to an `/api/*` endpoint contains no token
- **THEN** the response status is 401

### Requirement: Agent shutdown

The agent SHALL provide a token-protected endpoint that terminates the agent process.

#### Scenario: Authenticated shutdown

- **GIVEN** the agent is running
- **WHEN** a request to `POST /api/system/shutdown` presents the valid session token
- **THEN** the response status is 204
- **AND** the agent process terminates within 5 seconds

#### Scenario: Shutdown with invalid token

- **GIVEN** the agent is running
- **WHEN** a request to `POST /api/system/shutdown` presents an invalid token
- **THEN** the response status is 401
- **AND** the agent process continues running

#### Scenario: Shutdown without token

- **GIVEN** the agent is running
- **WHEN** a request to `POST /api/system/shutdown` contains no token
- **THEN** the response status is 401
- **AND** the agent process continues running

### Requirement: Native installer packaging

The agent SHALL be packageable as a platform-native installer (Windows `.exe`/`.msi`, Linux `.deb`) that bundles a Java runtime containing no JavaFX modules and runs without a system-installed JRE.

#### Scenario: Install and launch on Windows

- **GIVEN** the Windows installer has been built
- **WHEN** a user installs and launches the agent on Windows
- **THEN** the agent serves the web frontend at a localhost URL
- **AND** the default browser opens at that URL

#### Scenario: Install and launch on Linux

- **GIVEN** the `.deb` package has been built
- **WHEN** a user installs and launches the agent on Linux
- **THEN** the agent serves the web frontend at a localhost URL
- **AND** the default browser opens at that URL

#### Scenario: Run without system JRE

- **GIVEN** the agent is installed on a Windows machine with no system Java runtime
- **WHEN** the agent is launched
- **THEN** the agent starts and serves requests

#### Scenario: Packaged agent still enforces token

- **GIVEN** the packaged agent is running on Windows
- **WHEN** a request to an `/api/*` endpoint contains no token
- **THEN** the response status is 401

#### Scenario: Bundled runtime contains no JavaFX

- **GIVEN** the installed agent's bundled runtime
- **WHEN** the module list is inspected
- **THEN** no module whose name starts with `javafx` is present
