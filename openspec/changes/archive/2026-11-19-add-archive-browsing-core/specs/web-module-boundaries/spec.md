# Spec Delta

## MODIFIED Requirements

### Requirement: Shared error format

API errors of every web app built on the shared backend core SHALL be returned by the core as RFC 9457 problem details (`application/problem+json`) containing at least `status` and `title`. They SHALL NOT expose stack traces or internal exception messages. A `detail` SHALL only be included when the code raising the error sets it deliberately for the client (`ProblemException`); it never carries an exception message. The core applies no session or access control of its own.

#### Scenario: Not found without any session
- **GIVEN** a test application started with only the core and no local module
- **WHEN** `GET /api/does-not-exist` is requested without any cookie
- **THEN** the response status is 404 with content type `application/problem+json`, not the local agent's `401` or `403` access-control response
- **AND** the body is JSON with `"status": 404` and a non-empty `title`

#### Scenario: Unexpected failure
- **GIVEN** a test application with only the core, and an API handler that fails with an unexpected exception
- **WHEN** the request completes
- **THEN** the response status is 500 with content type `application/problem+json`

#### Scenario: No internal details leaked
- **GIVEN** a test application with only the core, and an unexpected exception with message M that occurred while handling a request
- **WHEN** the error response body is inspected
- **THEN** it contains neither M, nor the string `Exception`, nor a stack frame such as `at ch.admin`

#### Scenario: Deliberate detail is returned
- **GIVEN** a test application with only the core, and an API handler that throws `ProblemException(400, "D")`
- **WHEN** the request completes
- **THEN** the response status is 400 with content type `application/problem+json`
- **AND** the body is JSON with `"status": 400` and `"detail": "D"`
