# Spec Delta

## ADDED Requirements

### Requirement: Archive scope of the local agent

The local agent SHALL provide the backend core's scope resolver: every `/api/archives/**` request that passed the agent's session check SHALL be resolved to one fixed scope of this agent instance. A request without a valid session SHALL be rejected by the session check with `401` before any archive is looked up.

#### Scenario: Registered archive is reachable with the session
- **GIVEN** a valid session and the fixture `sample.siard` registered through the local agent's archive registry
- **WHEN** `GET /api/archives/{archiveId}` is requested
- **THEN** the response status is 200

#### Scenario: Archive API without a session
- **GIVEN** the fixture `sample.siard` registered through the local agent's archive registry
- **WHEN** `GET /api/archives/{archiveId}` is requested without a session cookie
- **THEN** the response status is 401
- **AND** the archive's last-used time is unchanged

#### Scenario: Archive of another agent instance
- **GIVEN** a valid session and an `archiveId` that was issued by another agent instance
- **WHEN** `GET /api/archives/{archiveId}` is requested
- **THEN** the response status is 404 with problem details identical to those for an id that was never issued
