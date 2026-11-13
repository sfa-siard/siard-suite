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

### Requirement: Session established by launch token

`GET /launch?token=<token>` with a token that is unused and not older than 2 minutes SHALL respond with status 303, `Location: /`, `Cache-Control: no-store`, `Referrer-Policy: no-referrer`, and a session cookie named `siard_session_<port>` with the `HttpOnly` and `SameSite=Strict` attributes. A token SHALL be accepted only once.

#### Scenario: Valid token is exchanged for a session cookie
- **GIVEN** the agent is running on port P with an unused launch token T issued less than 2 minutes ago
- **WHEN** `GET /launch?token=T` is requested with `Host: 127.0.0.1:P`
- **THEN** the response status is 303 and the `Location` header is exactly `/`
- **AND** the response sets cookie `siard_session_P` with `HttpOnly` and `SameSite=Strict`
- **AND** the response has `Cache-Control: no-store` and `Referrer-Policy: no-referrer`

#### Scenario: Token exchanged just before expiry
- **GIVEN** an unused launch token T issued 1 minute and 59 seconds ago
- **WHEN** `GET /launch?token=T` is requested
- **THEN** the response sets cookie `siard_session_<port>`

#### Scenario: Second exchange of the same token
- **GIVEN** token T has already been exchanged once
- **WHEN** `GET /launch?token=T` is requested again
- **THEN** the response sets no cookie

### Requirement: Rejected launch link

A launch request whose token is expired, already used, unknown or missing SHALL respond with status 303, `Location: /launch-failed`, `Cache-Control: no-store`, `Referrer-Policy: no-referrer`, and SHALL NOT set a session cookie.

#### Scenario: Expired token
- **GIVEN** an unused launch token T issued more than 2 minutes ago
- **WHEN** `GET /launch?token=T` is requested
- **THEN** the response status is 303 and the `Location` header is exactly `/launch-failed`
- **AND** the response sets no cookie

#### Scenario: Reused, unknown or missing token
- **GIVEN** the agent is running
- **WHEN** `GET /launch` is requested with an already used token, a token that was never issued, or no token
- **THEN** the response status is 303 and the `Location` header is exactly `/launch-failed`
- **AND** the response sets no cookie

#### Scenario: Rejection reveals nothing about the token
- **GIVEN** launch requests with an expired, a used and an unknown token
- **WHEN** the three responses are compared
- **THEN** status, headers and body are identical
- **AND** the `Location` header contains no query string

### Requirement: Loopback Host header required

Every request, including `/launch`, `/api/*`, `/q/*` and static resources, SHALL carry a `Host` header exactly equal to `127.0.0.1:<port>`. Any other Host SHALL be answered with status 403 and a problem-details body telling the user to open the application via `http://127.0.0.1:<port>/`.

#### Scenario: Correct Host
- **GIVEN** the agent is running on port P
- **WHEN** `GET /q/health` is requested with `Host: 127.0.0.1:P`
- **THEN** the response status is 200

#### Scenario: Launch link via a different host name
- **GIVEN** an unused, valid launch token T
- **WHEN** `GET /launch?token=T` is requested with `Host: localhost:P`
- **THEN** the response status is 403 with content type `application/problem+json`
- **AND** the `detail` contains `http://127.0.0.1:P/`
- **AND** no cookie is set and T can still be exchanged via `Host: 127.0.0.1:P`

#### Scenario: Foreign Host header
- **GIVEN** the agent is running on port P
- **WHEN** `GET /`, `GET /api/system/info` (with a valid session cookie) or `GET /q/health` is requested with `Host` `[::1]:P`, `evil.example:P` or without a port
- **THEN** each response status is 403
- **AND** no response contains application data or the frontend's `index.html`

### Requirement: API access requires session

Every request to `/api/*` SHALL carry the valid session cookie of this agent instance. The frontend's static resources and `/q/health` SHALL be reachable without a session.

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

### Requirement: API access only from the agent's own pages

A request to `/api/*` whose `Sec-Fetch-Site` header is present and not `same-origin` or `none` SHALL be rejected with status 403 and content type `application/problem+json`, for every method and even with a valid session cookie. Requests without the header SHALL be handled as before.

#### Scenario: Request from a page on another local port
- **GIVEN** a browser holds the session cookie of the agent on port P
- **WHEN** a page served from `http://127.0.0.1:<another port>` makes it send `GET /api/system/info`, `GET /api/events` or `POST /api/system/info` to the agent (`Sec-Fetch-Site: same-site`)
- **THEN** each response status is 403 with content type `application/problem+json`
- **AND** no response contains `appVersion`

#### Scenario: Request from a foreign site
- **GIVEN** a browser holds the session cookie of the agent on port P
- **WHEN** a page of another site makes it send `GET /api/system/info` to the agent (`Sec-Fetch-Site: cross-site`)
- **THEN** the response status is 403

#### Scenario: Requests from the agent's own pages and from non-browser clients
- **GIVEN** a valid session cookie for the agent on port P
- **WHEN** `GET /api/system/info` is requested with `Sec-Fetch-Site: same-origin`, with `Sec-Fetch-Site: none`, or without the header
- **THEN** each response status is 200

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

In normal launch mode the agent SHALL exit once no authorized event stream has been open for the configured grace period (`siard.agent.idle-exit-grace`, default 20 seconds). If no authorized event stream has opened by the time the launch token has expired plus the grace period, the agent SHALL exit as well, since no new session can be obtained.

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

#### Scenario: Window closed while its stream opens
- **GIVEN** a browser window opens `GET /api/events`
- **WHEN** the connection is already closed by the time the agent starts the stream
- **THEN** the window is not counted as connected
- **AND** the agent exits within the grace period plus 5 seconds once no other window is connected

#### Scenario: No window connects after launch
- **GIVEN** the agent has started and no authorized event stream has opened
- **WHEN** the launch token lifetime plus the grace period has passed
- **THEN** the agent exits

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

### Requirement: Fixed development credentials only in development mode

A fixed, configured development token and session secret SHALL be used only when the agent runs in Quarkus development mode; there the token is reusable and does not expire. In every other launch mode, token and secret SHALL be random per launch and not configurable.

#### Scenario: Development mode uses fixed credentials
- **GIVEN** the backend runs via `quarkusDev`
- **WHEN** `GET /launch?token=<configured dev token>` is requested, also repeatedly and later than 2 minutes after startup
- **THEN** the response status is 303 with `Location: /` and sets the session cookie

#### Scenario: Packaged agent with a dev profile override
- **GIVEN** the packaged agent is started with `-Dquarkus.profile=dev`
- **WHEN** `GET /launch?token=<configured dev token>` is requested
- **THEN** the `Location` header is `/launch-failed` and no cookie is set

#### Scenario: Token cannot be set by configuration
- **GIVEN** the packaged agent is started with a token property set to value V
- **WHEN** `GET /launch?token=V` is requested
- **THEN** the `Location` header is `/launch-failed` and no cookie is set
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

### Requirement: Platform packages

The agent SHALL be distributable as a native installer for Windows (`.exe`/`.msi`) and Linux (`.deb`), and as a zip with a start script for macOS. Each bundles a Java runtime without JavaFX modules and runs without a system-installed Java.

#### Scenario: Install and launch on Windows and Linux
- **GIVEN** the installer for the current platform has been built
- **WHEN** a user installs it and launches the application from the created shortcut
- **THEN** a browser window shows the frontend's About view after navigating to it

#### Scenario: Start on macOS from the zip
- **GIVEN** the macOS zip has been built and extracted
- **WHEN** a user runs its start script from a terminal
- **THEN** the launch URL is logged and a browser window shows the frontend

#### Scenario: No system Java
- **GIVEN** a machine without any installed Java runtime
- **WHEN** the installed or extracted application is launched
- **THEN** `GET /q/health` on the logged port returns 200

#### Scenario: Runtime contains no JavaFX
- **GIVEN** the bundled runtime of any of the platform packages
- **WHEN** its module list is printed with `java --list-modules`
- **THEN** no module name starts with `javafx`
