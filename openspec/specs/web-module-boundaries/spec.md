# web-module-boundaries Specification

## Purpose

Defines which code the SIARD web apps (local and remote) may share and which code must stay out of the shared parts, so that a server-hosted app never inherits the local agent's loopback security model or the database-archiving drivers.

## Requirements

### Requirement: Backend core carries no archiving or driver code

The shared backend core SHALL NOT have `siard-cmd`, any `jdbc-*` module or any JDBC driver on its runtime classpath, and SHALL NOT register a JDBC driver.

#### Scenario: Core classpath is clean
- **GIVEN** the shared backend core as built
- **WHEN** the build verifies the core's resolved runtime classpath
- **THEN** the check passes
- **AND** no resolved dependency belongs to `siard-cmd`, a `jdbc-*` module, or the PostgreSQL, MySQL, MariaDB, MS SQL Server, Oracle, DB2 or Access (Jackcess) drivers

#### Scenario: A forbidden dependency is added
- **GIVEN** the core declares a dependency on `siard-cmd` or on the PostgreSQL driver
- **WHEN** `./gradlew :siard-web:backend-core:check` runs
- **THEN** the build fails with a message naming the forbidden dependency

#### Scenario: No JDBC driver is registered by the core
- **GIVEN** a JVM started with only the core's runtime classpath
- **WHEN** `java.sql.DriverManager.getDrivers()` is enumerated
- **THEN** the result is empty

### Requirement: Backend core carries no local-agent code

The shared backend core SHALL NOT contain or depend on the local agent's launch token, session cookie, Host check, event stream or browser launch, so that a server built on the core cannot inherit loopback assumptions.

#### Scenario: Core has no dependency on the local module
- **GIVEN** the shared backend core as built
- **WHEN** its resolved dependencies are listed
- **THEN** `:siard-web:local:backend` is not among them

#### Scenario: Core classes do not reference the local package
- **GIVEN** the compiled classes of the core
- **WHEN** they are scanned for references to the package `ch.admin.bar.siardsuite.localwebapp`
- **THEN** no class references it

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

### Requirement: Shared frontend package contains no app shell

The shared frontend package SHALL NOT import code from the local or the remote frontend, so that each app keeps full control of its shell, routes and layout.

#### Scenario: Shared package lints clean
- **GIVEN** the shared frontend package as committed
- **WHEN** `./gradlew :siard-web:frontend-shared:check` runs
- **THEN** ESLint, `tsc` and the package's tests pass

#### Scenario: Import of an app is rejected
- **GIVEN** a file in the shared package imports from the local frontend (relative path or package name)
- **WHEN** the shared package's lint runs
- **THEN** it fails with a `no-restricted-imports` error naming the file

#### Scenario: Shared package ships no session handling
- **GIVEN** the shared package's exports
- **WHEN** they are inspected for the cookie name prefix `siard_session_` and the `/launch` path
- **THEN** neither occurs in the shared package's source
