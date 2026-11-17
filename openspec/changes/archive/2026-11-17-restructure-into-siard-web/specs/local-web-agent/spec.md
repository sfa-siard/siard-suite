# Spec Delta

## Purpose

The error format moved into the shared backend core (`web-module-boundaries`, "Shared error format"), where it is specified and tested once. The local agent only has to show that it answers with that format behind its session.

## REMOVED Requirements

### Requirement: Error responses

**Reason**: The error format is provided by the shared backend core and specified there once (`web-module-boundaries`, "Shared error format", with the scenarios "Not found without any session", "Unexpected failure" and "No internal details leaked").

**Migration**: Replaced by "Shared error format behind the session", which only requires the local agent to answer with the core's format.

## ADDED Requirements

### Requirement: Shared error format behind the session

API errors of the local agent SHALL be returned in the shared error format of the backend core (RFC 9457 problem details, `application/problem+json`, see `web-module-boundaries` "Shared error format"), also for requests that passed the agent's session check.

#### Scenario: Not found with a valid session
- **GIVEN** a valid session
- **WHEN** `GET /api/does-not-exist` is requested
- **THEN** the response status is 404 with content type `application/problem+json`
- **AND** the body is JSON with `"status": 404` and a non-empty `title`
