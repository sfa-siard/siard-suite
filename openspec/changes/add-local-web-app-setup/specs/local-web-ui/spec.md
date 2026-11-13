# Spec Delta

## Purpose

App-wide behaviour of the SIARD Suite web frontend, independent of any single feature: how the frontend is delivered to the browser and which interface language it shows.

## ADDED Requirements

### Requirement: Frontend delivery

The agent SHALL serve `index.html` for `/` and for GET requests to unknown paths whose last segment has no file extension, so client-side routes survive a reload. Paths under `/api/`, `/q/` and `/launch`, and missing files with an extension (e.g. `.js`), SHALL NOT fall back to `index.html`.

#### Scenario: Deep link reload
- **GIVEN** the agent is running
- **WHEN** `GET /about` is requested
- **THEN** the response status is 200 with content type `text/html`

#### Scenario: Unknown API path
- **GIVEN** a browser holds a valid session
- **WHEN** `GET /api/does-not-exist` is requested
- **THEN** the response status is 404 with content type `application/problem+json`

#### Scenario: Missing static file
- **GIVEN** the agent is running
- **WHEN** `GET /assets/does-not-exist.js` is requested
- **THEN** the response status is 404
- **AND** the body is not the frontend's `index.html`

#### Scenario: Reserved paths do not fall back
- **GIVEN** the agent is running
- **WHEN** `GET /q/does-not-exist` or `GET /launch/extra` is requested
- **THEN** the response status is 404
- **AND** the body is not the frontend's `index.html`

#### Scenario: Path traversal
- **GIVEN** the agent is running
- **WHEN** `GET /../../etc/passwd` or `GET /%2e%2e/%2e%2e/etc/passwd` is requested
- **THEN** the response does not contain the contents of any file outside the frontend resources

### Requirement: Interface language

On every start the frontend SHALL display its texts in the browser's preferred language if it is one of de, fr, it, en, and in English otherwise. The choice SHALL NOT be persisted across launches.

#### Scenario: Supported browser language
- **GIVEN** the browser's preferred language is `fr-CH`
- **WHEN** the frontend loads
- **THEN** the About view's description text is shown in French

#### Scenario: Unsupported browser language
- **GIVEN** the browser's preferred language is `es`
- **WHEN** the frontend loads
- **THEN** the texts are shown in English

#### Scenario: Malformed language value
- **GIVEN** the browser reports an empty or malformed language tag
- **WHEN** the frontend loads
- **THEN** the texts are shown in English and no script error is logged to the console

### Requirement: Rejected launch link message

The frontend SHALL show, at `/launch-failed`, a message in the current interface language saying that the start link has expired or was already used and that SIARD Suite must be restarted.

#### Scenario: Expired link in the browser
- **GIVEN** the user opens a launch URL more than 2 minutes after the agent started
- **WHEN** the browser follows the redirect
- **THEN** the page at `/launch-failed` shows the message asking to restart SIARD Suite
- **AND** no version number or other application data is shown

#### Scenario: Message in an unsupported browser language
- **GIVEN** the browser's preferred language is `es`
- **WHEN** `/launch-failed` is shown
- **THEN** the message is shown in English

#### Scenario: Token not visible after rejection
- **GIVEN** the user opened a launch URL with a rejected token
- **WHEN** `/launch-failed` is shown
- **THEN** the address bar shows `/launch-failed` without the token
