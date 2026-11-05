# Spec Delta

## Purpose

App-wide behaviour of the SIARD Suite web frontend, independent of any single feature: how the frontend is delivered to the browser and which interface language it shows.

## ADDED Requirements

### Requirement: Frontend delivery

The agent SHALL serve the frontend's `index.html` for `/` and for any non-API path that does not match a static resource, so client-side routes survive a reload. Unknown `/api/*` paths SHALL NOT fall back to `index.html`.

#### Scenario: Deep link reload
- **GIVEN** the agent is running
- **WHEN** `GET /about` is requested
- **THEN** the response status is 200 with content type `text/html`

#### Scenario: Unknown API path
- **GIVEN** a browser holds a valid session
- **WHEN** `GET /api/does-not-exist` is requested
- **THEN** the response status is 404 with content type `application/problem+json`

#### Scenario: Path traversal
- **GIVEN** the agent is running
- **WHEN** `GET /../../etc/passwd` or `GET /%2e%2e/%2e%2e/etc/passwd` is requested
- **THEN** the response does not contain the contents of any file outside the frontend resources

### Requirement: Interface language

On every start the frontend SHALL display its texts in the browser's preferred language if it is one of de, fr, it, en, and in English otherwise. The choice SHALL NOT be persisted across launches.

#### Scenario: Supported browser language
- **GIVEN** the browser's preferred language is `fr-CH`
- **WHEN** the frontend loads
- **THEN** the About view title is shown in French

#### Scenario: Unsupported browser language
- **GIVEN** the browser's preferred language is `es`
- **WHEN** the frontend loads
- **THEN** the texts are shown in English

#### Scenario: Malformed language value
- **GIVEN** the browser reports an empty or malformed language tag
- **WHEN** the frontend loads
- **THEN** the texts are shown in English and no script error is logged to the console
