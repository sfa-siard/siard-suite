## MODIFIED Requirements

### Requirement: Connection details page

The `/archive/connection` route SHALL show a responsive desktop-inspired form for the selected DBMS, with editable new-connection defaults or saved values and an empty password. The heading SHALL identify the fixed DBMS without a DBMS input. Saving SHALL depend only on the optional connection name, with no toggle. Submission SHALL return to `/archive`; unknown identifiers SHALL show an error state. Defaults and layout SHALL match the scenarios below.

#### Scenario: New connection defaults for each DBMS
- **GIVEN** a valid local session and a new-connection choice for each supported server-based DBMS
- **WHEN** its details page loads
- **THEN** the host input value is `localhost`
- **AND** the port input value is `5432` for PostgreSQL, `3306` for MySQL, `1433` for Microsoft SQL Server, `1521` for Oracle or `50000` for DB/2
- **AND** host and port are editable values rather than placeholders
- **AND** database name, username, password, options and connection name start empty
- **AND** no field is prefilled with saved data

#### Scenario: User edits are not replaced by defaults
- **GIVEN** a new connection form where the user changed host to `db.example.org` and port to `15432`
- **WHEN** the user edits the database name
- **THEN** host remains `db.example.org` and port remains `15432`

#### Scenario: Desktop field arrangement
- **GIVEN** a valid local session and a 1280 by 800 CSS-pixel viewport
- **WHEN** the connection details page loads
- **THEN** server and compact port inputs share the upper-left half and database name occupies the upper-right half
- **AND** username appears below server and password appears below database name
- **AND** existing additional fields follow those rows, then the JDBC URL input spanning the form width
- **AND** the optional connection name is the last input, spanning the form width
- **AND** the heading identifies the selected DBMS and no DBMS input or save toggle is rendered

#### Scenario: Narrow form stacks without overflow
- **GIVEN** a valid local session and a 360 by 800 CSS-pixel viewport
- **WHEN** the connection details page loads
- **THEN** inputs stack in the order server, port, database name, username, password, existing additional fields, JDBC URL, connection name
- **AND** every input is reachable by scrolling and the page scroll width is no greater than the viewport width

#### Scenario: Saved connection prefilled
- **GIVEN** a saved PostgreSQL connection "Archive A" with host `db.example.org`, port `15432` and empty options
- **WHEN** the user opens its details page
- **THEN** host, port, database name, user and options show the saved values, including the custom port and empty options
- **AND** the password field is empty
- **AND** the heading identifies PostgreSQL and no DBMS input is rendered
- **AND** the selected DBMS cannot be changed to another DBMS

#### Scenario: Refined form preserves credential protection
- **GIVEN** a valid local session, a new connection form and a network-request recorder
- **WHEN** the user submits valid fields with a connection name and a password entered
- **THEN** no recorded request payload contains the password
- **AND** the password input masks its value
- **AND** the saved profile and API responses contain no password

#### Scenario: Wizard steps share a content width
- **GIVEN** the archive wizard at a desktop viewport
- **WHEN** the user navigates between the picker and connection details
- **THEN** both pages use the wizard's centered content container with a maximum width of 960 CSS pixels
- **AND** their headings have the same left edge
- **AND** the container shrinks to the available width on narrow screens and DBMS choices wrap as needed

#### Scenario: Connection details are separated from the profile name
- **GIVEN** the connection details page
- **WHEN** the form is displayed
- **THEN** a horizontal divider spans the form between the connection parameters and the optional connection name
- **AND** no fieldsets are rendered
- **AND** the existing responsive field order and optional-name saving behavior are preserved

#### Scenario: Toggle password visibility
- **GIVEN** a connection details form with a password entered and masked by default
- **WHEN** the user activates the eye button labelled in the interface language
- **THEN** the password is displayed as plain text without changing its value or submitting the form
- **WHEN** the user activates the button again
- **THEN** the password is masked again
- **AND** toggling visibility never sends the password in a request

#### Scenario: Validation blocks submit
- **GIVEN** the details page for a new connection
- **WHEN** the user submits with a required field empty or a port that is not a number between 1 and 65535
- **THEN** inline validation messages appear on the invalid fields
- **AND** no save request is sent

#### Scenario: Save and return
- **GIVEN** the details page for a new connection with all fields valid and the name "Archive B" entered
- **WHEN** the user submits the form
- **THEN** a `POST /api/connections` request is sent with the entered fields and name
- **AND** the route returns to `/archive` where the new profile is listed

#### Scenario: Save an opened saved connection
- **GIVEN** the details page for the saved connection "Archive A"
- **WHEN** the user submits the form, with or without changes
- **THEN** a `PUT /api/connections/{id}` request is sent for that profile and no `POST /api/connections` request
- **AND** the route returns to `/archive`

#### Scenario: Taken name shown on the name field
- **GIVEN** the details page with a name that another saved connection already uses
- **WHEN** the user submits the form and the save is rejected with 409
- **THEN** the name field shows that the name is already taken and the route stays on the page
- **AND** the message is cleared on the next submit

#### Scenario: Submit without name does not save
- **GIVEN** the details page for a new connection with valid fields and an empty name field
- **WHEN** the user submits the form
- **THEN** no `POST /api/connections` request is sent
- **AND** the route returns to `/archive`

#### Scenario: Unknown identifier
- **GIVEN** a valid local session
- **WHEN** the user visits `/archive/connection` with an unknown DBMS or connection identifier
- **THEN** the page shows a localized error state and offers a way back to `/archive`

#### Scenario: Details API failure shows an error state
- **GIVEN** the catalog or connections API responds with an error
- **WHEN** the details page loads
- **THEN** the page shows a localized error message instead of a broken form

#### Scenario: Details without a session
- **GIVEN** the agent rejects the API requests with 401 because the session is missing or invalid
- **WHEN** the user visits `/archive/connection?connection=<id>` or `?dbms=postgresql`
- **THEN** the localized missing-session message is shown instead of the form
- **AND** no saved connection values are displayed and no save request is sent

## ADDED Requirements

### Requirement: JDBC URL kept in sync with the connection inputs

The connection details page SHALL show the JDBC URL of the selected DBMS built from server, port, database name and options, and update it on every change of those inputs. A URL edited by the user that is valid for the page's DBMS SHALL update those inputs. An invalid URL SHALL show an inline error, leave the inputs unchanged and block saving. The URL SHALL NOT be sent to the agent or persisted.

#### Scenario: URL derived from the inputs for each DBMS
- **GIVEN** a valid local session and a new connection form with host `db.example.org`, port `15432`, database name `archive` and empty options
- **WHEN** the form is shown for PostgreSQL, MySQL, Microsoft SQL Server, Oracle or DB/2
- **THEN** the JDBC URL input value is `jdbc:postgresql://db.example.org:15432/archive`, `jdbc:mysql://db.example.org:15432/archive`, `jdbc:sqlserver://db.example.org:15432;databaseName=archive`, `jdbc:oracle:thin:@db.example.org:15432/archive` or `jdbc:db2://db.example.org:15432/archive` respectively

#### Scenario: Input edits update the URL
- **GIVEN** a new PostgreSQL connection form with host `localhost`, port `5432` and database name `archive`
- **WHEN** the user changes the port to `15432` and enters `ssl=true` as options
- **THEN** the JDBC URL input value is `jdbc:postgresql://localhost:15432/archive?ssl=true` without any further user action

#### Scenario: URL edits update the inputs
- **GIVEN** a new PostgreSQL connection form
- **WHEN** the user replaces the JDBC URL with `jdbc:postgresql://db.example.org:15432/archive?ssl=true`
- **THEN** host is `db.example.org`, port is `15432`, database name is `archive` and options is `ssl=true`
- **AND** username, password and connection name keep their values

#### Scenario: Saved connection shows its URL
- **GIVEN** a saved Microsoft SQL Server connection with host `db.example.org`, port `1433`, database name `archive` and options `encrypt=false`
- **WHEN** the user opens its details page
- **THEN** the JDBC URL input value is `jdbc:sqlserver://db.example.org:1433;databaseName=archive;encrypt=false`

#### Scenario: Invalid URL is rejected
- **GIVEN** a PostgreSQL connection form with host `db.example.org` and a network-request recorder
- **WHEN** the user enters `jdbc:mysql://other:3306/x`, `postgres://db` or `jdbc:postgresql://db.example.org` as JDBC URL
- **THEN** the JDBC URL input shows a localized error message
- **AND** host is still `db.example.org`
- **WHEN** the user then submits the form with a connection name
- **THEN** no `POST /api/connections` request is sent and the route stays on the page
- **WHEN** the user then edits any of server, port, database name or options
- **THEN** the JDBC URL is rebuilt from the inputs and the error disappears

#### Scenario: URL is not saved and carries no credentials
- **GIVEN** a new PostgreSQL connection form with user `archivist`, password `s3cret` and a network-request recorder
- **WHEN** the user submits valid fields with the name "Archive C"
- **THEN** the JDBC URL input value contains neither `archivist` nor `s3cret`
- **AND** the recorded `POST /api/connections` payload has no JDBC URL property and does not contain `s3cret`

#### Scenario: URL without a session
- **GIVEN** the agent rejects the API requests with 401 because the session is missing or invalid
- **WHEN** the user visits `/archive/connection?connection=<id>`
- **THEN** the localized missing-session message is shown and no JDBC URL input is rendered
- **AND** no saved host or database name appears on the page
