# Spec Delta

## Purpose

Defines which code the SIARD web apps (local and remote) may share and which code must stay out of the shared parts, so that a server-hosted app never inherits the local agent's loopback security model or the database-archiving drivers.

## ADDED Requirements

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

#### Scenario: Core endpoints are not protected by the local session
- **GIVEN** a test application started with only the core and no local module
- **WHEN** a request without any cookie reaches a core-provided error response
- **THEN** it is answered with `application/problem+json`, not with the local agent's `401` or `403` access-control responses

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

### Requirement: Local web app unchanged by the restructure

The local web app built from `siard-web/local` SHALL behave exactly as the module built from `siard-suite-local-web-app` before the move: the same API, the same installer and zip names, and passing all scenarios of the `local-web-agent`, `local-web-ui` and `system-info` capabilities.

#### Scenario: Checks and smoke test pass after the move
- **GIVEN** the repository after the restructure
- **WHEN** `./gradlew :siard-web:check` and `./gradlew :siard-web:local:e2eTest` run
- **THEN** both exit with code 0
- **AND** the number of executed backend tests, frontend tests and the Playwright test equals the number executed before the move

#### Scenario: Packaged names are unchanged
- **GIVEN** the repository after the restructure
- **WHEN** `./gradlew :siard-web:local:backend:runtimeZip` runs
- **THEN** the zip is named `SIARD-Suite-Web.<version>-<platform>.zip`

#### Scenario: API description is unchanged
- **GIVEN** the repository after the restructure
- **WHEN** `./gradlew :siard-web:local:backend:openApiCheck` runs
- **THEN** it passes against the moved `openapi.yaml`
- **AND** the moved file's contents are byte-identical to the pre-move file

#### Scenario: Old paths no longer exist
- **GIVEN** the repository after the restructure
- **WHEN** `./gradlew projects` is run
- **THEN** no project path starts with `:siard-suite-local-web-app`
