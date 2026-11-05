# Spec Delta

## Purpose

Reports which SIARD Suite version and runtime the local agent is running, both through the API and in the frontend's About view, as the desktop app's info dialog does today.

## ADDED Requirements

### Requirement: System information endpoint

`GET /api/system/info` SHALL return 200 with a JSON object containing `appVersion` (the build's application version), `javaVersion`, and `jdbcDrivers` (the class names of the JDBC drivers registered in the agent).

#### Scenario: Information of the packaged agent
- **GIVEN** the packaged agent built with version V is running and a browser holds a valid session
- **WHEN** `GET /api/system/info` is requested
- **THEN** the response status is 200
- **AND** `appVersion` equals V and `javaVersion` is non-empty
- **AND** `jdbcDrivers` contains at least one driver each for PostgreSQL, MySQL, MS SQL Server, Oracle, DB2 and MS Access

#### Scenario: Unsupported method
- **GIVEN** a valid session
- **WHEN** `POST /api/system/info` is requested
- **THEN** the response status is 405 with content type `application/problem+json`

#### Scenario: Request without session
- **GIVEN** the agent is running
- **WHEN** `GET /api/system/info` is requested without a valid session cookie
- **THEN** the response status is 401
- **AND** the body contains no `appVersion`

### Requirement: About view

The frontend SHALL provide an About view at `/about` showing the title `SIARD Suite <appVersion>` and the application description text in the current interface language.

#### Scenario: About view shows the version
- **GIVEN** a browser with a valid session and `appVersion` V
- **WHEN** the user navigates to `/about`
- **THEN** the page shows the text `SIARD Suite V`
- **AND** the description text matches the current language's translation

#### Scenario: Information cannot be loaded
- **GIVEN** `GET /api/system/info` fails with a 500 response
- **WHEN** the user navigates to `/about`
- **THEN** an element with `data-testid="error-message"` is shown
- **AND** no version number is shown

#### Scenario: No session
- **GIVEN** the browser has no valid session (for example the page was opened without the launch URL)
- **WHEN** the user navigates to `/about`
- **THEN** a message with `data-testid="session-required"` tells the user to start the application again
- **AND** no version number is shown
