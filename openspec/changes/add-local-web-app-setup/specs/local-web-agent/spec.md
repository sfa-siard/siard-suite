# Spec Delta

## Purpose

The locally running SIARD Suite agent serves the web frontend and its API to the user's browser on the same machine. It controls who can talk to it, decides when it runs and when it exits, and ships as a native installer without JavaFX.

## ADDED Requirements

### Requirement: Loopback-only server on a free port

On every launch the agent SHALL listen on `127.0.0.1` only, on a port the operating system assigns as free, and SHALL log a launch URL of the form `http://127.0.0.1:<port>/launch?token=<token>`.

#### Scenario: Agent starts and logs its launch URL
- **GIVEN** the packaged agent is not running
- **WHEN** the user launches it
- **THEN** within 30 seconds a log line contains a launch URL with host `127.0.0.1`, a port between 1024 and 65535, and a non-empty token
- **AND** `GET http://127.0.0.1:<port>/q/health` returns 200

#### Scenario: Port already in use is avoided
- **GIVEN** another process listens on a port P
- **WHEN** the agent is launched
- **THEN** the port in the logged launch URL is not P

#### Scenario: Not reachable from other network interfaces
- **GIVEN** the agent is running on port P and the machine has a non-loopback IPv4 address A
- **WHEN** a TCP connection to `A:P` is attempted
- **THEN** the connection is refused

### Requirement: Browser is opened at launch

After startup the agent SHALL open a browser window at the launch URL: a Chromium-family browser in application-window mode if one is found, otherwise the default browser. Setting `siard.agent.open-browser=false` SHALL suppress this.

#### Scenario: Browser opens on launch
- **GIVEN** a graphical session with a browser installed
- **WHEN** the user launches the agent
- **THEN** a browser window opens at the launch URL within 30 seconds
- **AND** after the session exchange the window shows the frontend

#### Scenario: No browser can be opened
- **GIVEN** no browser can be started (headless system or opening fails)
- **WHEN** the agent is launched
- **THEN** the agent logs the full launch URL
- **AND** `GET /q/health` keeps returning 200

#### Scenario: Browser opening disabled
- **GIVEN** the agent is started with `siard.agent.open-browser=false`
- **WHEN** startup completes
- **THEN** no browser process is started by the agent
- **AND** the launch URL is logged

### Requirement: Session established by single-use launch token

`GET /launch?token=<token>` with a valid, unused token SHALL respond with status 303, `Location: /`, `Cache-Control: no-store`, `Referrer-Policy: no-referrer`, and a session cookie named `siard_session_<port>` with the `HttpOnly` and `SameSite=Strict` attributes. A token SHALL be accepted only once.

#### Scenario: Valid token is exchanged for a session cookie
- **GIVEN** the agent is running on port P with an unused launch token T
- **WHEN** `GET /launch?token=T` is requested with `Host: 127.0.0.1:P`
- **THEN** the response status is 303 and the `Location` header is exactly `/`
- **AND** the response sets cookie `siard_session_P` with `HttpOnly` and `SameSite=Strict`
- **AND** the response has `Cache-Control: no-store` and `Referrer-Policy: no-referrer`

#### Scenario: Token reused
- **GIVEN** token T has already been exchanged once
- **WHEN** `GET /launch?token=T` is requested again
- **THEN** the response status is 401
- **AND** the response sets no cookie

#### Scenario: Invalid or missing token
- **GIVEN** the agent is running
- **WHEN** `GET /launch` is requested without a token or with a token that was never issued
- **THEN** the response status is 401
- **AND** the response sets no cookie

### Requirement: API access requires session and loopback host

Every request to `/api/*` SHALL carry the valid session cookie of this agent instance and a `Host` header exactly equal to `127.0.0.1:<port>`. The frontend's static resources and `/q/health` SHALL be reachable without a session.

#### Scenario: Authorized API request
- **GIVEN** a browser holds the session cookie of the agent on port P
- **WHEN** it requests `GET /api/system/info` with `Host: 127.0.0.1:P`
- **THEN** the response status is 200

#### Scenario: Static frontend without session
- **GIVEN** the agent is running
- **WHEN** `GET /` or `GET /q/health` is requested without a cookie
- **THEN** the response status is 200

#### Scenario: Missing or invalid session
- **GIVEN** the agent is running on port P
- **WHEN** `GET /api/system/info` is requested without a cookie, or with `siard_session_P` set to a different value
- **THEN** the response status is 401

#### Scenario: Foreign Host header
- **GIVEN** a request carries the valid session cookie
- **WHEN** its `Host` header is `localhost:P`, `[::1]:P` or `evil.example:P`
- **THEN** the response status is 403

### Requirement: Event stream

`GET /api/events` SHALL open a server-sent event stream whose events carry JSON data with a `type` property. The first event on every new stream SHALL have `type` equal to `connected`.

#### Scenario: Stream opens with a connected event
- **GIVEN** a browser holds a valid session
- **WHEN** it opens `GET /api/events`
- **THEN** the response has content type `text/event-stream`
- **AND** within 2 seconds an event arrives whose data parses as JSON with `"type": "connected"`

#### Scenario: Idle stream is kept alive
- **GIVEN** an open event stream with no events to send
- **WHEN** 60 seconds pass
- **THEN** the client has received at least one keep-alive comment line
- **AND** the stream is still open

#### Scenario: Stream without session
- **GIVEN** the agent is running
- **WHEN** `GET /api/events` is requested without a valid session cookie
- **THEN** the response status is 401

### Requirement: Exit when no window is connected

In normal launch mode the agent SHALL exit once no authorized event stream has been open for the configured grace period (`siard.agent.idle-exit-grace`, default 20 seconds).

#### Scenario: Last window closed
- **GIVEN** the packaged agent with exactly one open event stream
- **WHEN** that stream is closed
- **THEN** the agent process exits within the grace period plus 5 seconds

#### Scenario: Reload within the grace period
- **GIVEN** the only event stream closes
- **WHEN** a new authorized event stream opens before the grace period has elapsed
- **THEN** the agent is still running after twice the grace period

#### Scenario: Unauthorized connections do not keep the agent alive
- **GIVEN** the only authorized event stream closes
- **WHEN** a client repeatedly requests `GET /api/events` without a valid session during the grace period
- **THEN** each request returns 401
- **AND** the agent exits within the grace period plus 5 seconds

### Requirement: Independent instances

Each launch SHALL start an independent agent with its own port, launch token, session cookie and lifetime. Instances SHALL NOT coordinate with or hand off to one another.

#### Scenario: Two launches
- **GIVEN** an agent is running on port P1
- **WHEN** the user launches the application again
- **THEN** a second agent logs a launch URL with a port P2 different from P1
- **AND** both `GET http://127.0.0.1:P1/q/health` and `GET http://127.0.0.1:P2/q/health` return 200

#### Scenario: Closing one instance's window
- **GIVEN** two agents each with one open event stream
- **WHEN** the stream of the first agent is closed
- **THEN** only the first agent exits
- **AND** the second agent still returns 200 on `/q/health`

#### Scenario: Session of one instance is rejected by another
- **GIVEN** a browser holds a session cookie `siard_session_P1` for the agent on P1
- **WHEN** it requests `GET /api/system/info` from the agent on P2 with `Host: 127.0.0.1:P2`
- **THEN** the response status is 401

### Requirement: Reopen on macOS

On macOS, when the user activates the already running application (for example from the Dock), the agent SHALL open a new browser window with a new single-use launch URL.

#### Scenario: Reopen opens a window
- **GIVEN** the agent runs on macOS with no open browser window and is within its grace period
- **WHEN** the user activates the application from the Dock
- **THEN** a browser window opens at a new launch URL within 10 seconds
- **AND** the agent does not exit after the grace period

#### Scenario: Reopen after window was closed long ago
- **GIVEN** the agent has already exited because no window was connected
- **WHEN** the user activates the application from the Dock
- **THEN** a new agent starts and opens a browser window

#### Scenario: Reopen issues a fresh token
- **GIVEN** a reopen opened a window with token T2 after an earlier launch with token T1
- **WHEN** the tokens are compared and `GET /launch?token=T1` is requested
- **THEN** T2 differs from T1
- **AND** the request with T1 returns 401

### Requirement: Fixed development credentials only in development mode

A fixed, configured development token and session secret SHALL be used only when the agent runs in Quarkus development mode. In every other launch mode, token and secret SHALL be random per launch and not configurable.

#### Scenario: Development mode uses fixed credentials
- **GIVEN** the backend runs via `quarkusDev`
- **WHEN** `GET /launch?token=<configured dev token>` is requested
- **THEN** the response status is 303

#### Scenario: Packaged agent with a dev profile override
- **GIVEN** the packaged agent is started with `-Dquarkus.profile=dev`
- **WHEN** `GET /launch?token=<configured dev token>` is requested
- **THEN** the response status is 401

#### Scenario: Token cannot be set by configuration
- **GIVEN** the packaged agent is started with a token property set to value V
- **WHEN** `GET /launch?token=V` is requested
- **THEN** the response status is 401
- **AND** the logged launch URL contains a token different from V

### Requirement: Error responses

API errors SHALL be returned as RFC 9457 problem details (`application/problem+json`) containing at least `status` and `title`. They SHALL NOT expose stack traces or internal exception messages.

#### Scenario: Not found
- **GIVEN** a valid session
- **WHEN** `GET /api/does-not-exist` is requested
- **THEN** the body is JSON with `"status": 404` and a non-empty `title`

#### Scenario: Unexpected failure
- **GIVEN** an API handler fails with an unexpected exception
- **WHEN** the request completes
- **THEN** the response status is 500 with content type `application/problem+json`

#### Scenario: No internal details leaked
- **GIVEN** an unexpected exception with message M occurred while handling a request
- **WHEN** the error response body is inspected
- **THEN** it contains neither M, nor the string `Exception`, nor a stack frame such as `at ch.admin`

### Requirement: Native installer

The agent SHALL be distributable as a native installer per platform (Windows `.exe`/`.msi`, Linux `.deb`, macOS `.dmg`) that bundles a Java runtime without JavaFX modules and runs without a system-installed Java.

#### Scenario: Install and launch
- **GIVEN** the installer for the current platform has been built
- **WHEN** a user installs it and launches the application from the created shortcut
- **THEN** a browser window shows the frontend's About view after navigating to it

#### Scenario: No system Java
- **GIVEN** a machine without any installed Java runtime
- **WHEN** the installed application is launched
- **THEN** `GET /q/health` on the logged port returns 200

#### Scenario: Runtime contains no JavaFX
- **GIVEN** the installed application's bundled runtime
- **WHEN** its module list is printed with `java --list-modules`
- **THEN** no module name starts with `javafx`
