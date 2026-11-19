# Spec Delta

## Purpose

Lets the user of the local SIARD Suite web app choose or define a database connection profile for the archive workflow, persist those profiles across restarts, and manage them — without ever storing or disclosing passwords.

## ADDED Requirements

### Requirement: DBMS catalog

The agent SHALL expose the supported server-based DBMS through `GET /api/dbms`: PostgreSQL, MySQL, Microsoft SQL Server, Oracle and DB/2. Each entry SHALL carry a stable identifier, a display name, the connection kind and the fields the details form needs, including example values. MS Access SHALL NOT be listed.

#### Scenario: Catalog lists server-based DBMS
- **GIVEN** a valid local session
- **WHEN** `GET /api/dbms` is requested
- **THEN** the response status is 200 with a JSON array of exactly 5 entries
- **AND** each entry has a non-empty `id`, `name`, a kind and a non-empty field list
- **AND** no entry refers to MS Access or any file-based database

#### Scenario: Catalog requires a session
- **GIVEN** the agent is running
- **WHEN** `GET /api/dbms` is requested without a session cookie or with an invalid one
- **THEN** the response status is 401 with content type `application/problem+json`

#### Scenario: Example values are placeholders
- **GIVEN** a valid local session
- **WHEN** the catalog entries are inspected
- **THEN** every example host is a placeholder containing `exampleHost`
- **AND** no example value contains a real machine name, filesystem path or credential

### Requirement: List saved connections

`GET /api/connections` SHALL return all saved connection profiles ordered by name. Each entry SHALL carry an identifier, name, DBMS identifier, host, port, database name, user, options and file fields. No password SHALL appear in any entry.

#### Scenario: Empty list
- **GIVEN** a valid local session and no saved connections
- **WHEN** `GET /api/connections` is requested
- **THEN** the response status is 200 with an empty JSON array

#### Scenario: Populated list
- **GIVEN** a valid local session and two saved connections named "Archive A" and "Archive B"
- **WHEN** `GET /api/connections` is requested
- **THEN** the response status is 200 with both entries ordered by name
- **AND** each entry carries all listed fields except a password

#### Scenario: List requires a session
- **GIVEN** the agent is running
- **WHEN** `GET /api/connections` is requested without a session cookie or with an invalid one
- **THEN** the response status is 401 with content type `application/problem+json`

### Requirement: Save a connection

`POST /api/connections` SHALL create a saved connection profile from a name and DBMS connection fields. The name SHALL be required and unique; field values SHALL be validated against the DBMS kind. The created profile SHALL be retrievable through `GET /api/connections`.

#### Scenario: Successful save
- **GIVEN** a valid local session and a catalog entry for PostgreSQL
- **WHEN** `POST /api/connections` is called with a unique name and valid PostgreSQL fields
- **THEN** the response status is 201
- **AND** a subsequent `GET /api/connections` lists the new profile with the submitted fields

#### Scenario: Duplicate name rejected
- **GIVEN** a valid local session and a saved connection named "Archive A"
- **WHEN** `POST /api/connections` is called with the name "Archive A"
- **THEN** the response status is 409 with content type `application/problem+json`
- **AND** the connection list remains unchanged

#### Scenario: Invalid fields rejected
- **GIVEN** a valid local session
- **WHEN** `POST /api/connections` is called with a missing name, an unknown DBMS identifier, or a non-numeric port
- **THEN** the response status is 400 with content type `application/problem+json`
- **AND** no profile is created

#### Scenario: Save requires a session
- **GIVEN** the agent is running
- **WHEN** `POST /api/connections` is called without a session cookie or with an invalid one
- **THEN** the response status is 401 with content type `application/problem+json`
- **AND** no profile is created

### Requirement: Delete a saved connection

`DELETE /api/connections/{id}` SHALL remove the profile with the given identifier. Deleting an unknown identifier SHALL return 404.

#### Scenario: Successful delete
- **GIVEN** a valid local session and a saved connection
- **WHEN** `DELETE /api/connections/{id}` is called with its identifier
- **THEN** the response status is 204
- **AND** a subsequent `GET /api/connections` no longer lists it

#### Scenario: Unknown identifier
- **GIVEN** a valid local session
- **WHEN** `DELETE /api/connections/{id}` is called with an identifier that does not exist
- **THEN** the response status is 404 with content type `application/problem+json`

#### Scenario: Delete requires a session
- **GIVEN** the agent is running
- **WHEN** `DELETE /api/connections/{id}` is called without a session cookie or with an invalid one
- **THEN** the response status is 401 with content type `application/problem+json`
- **AND** the profile still exists

### Requirement: Passwords are never stored or transmitted

The connection details form SHALL offer a password field for server-based profiles, but its value SHALL NOT be sent to the agent, persisted, logged or included in any API payload in this change. Saved profiles SHALL NOT contain a password field.

#### Scenario: Password stays client-side
- **GIVEN** the details page for a new PostgreSQL connection and a network-request recorder
- **WHEN** the user enters a password and submits the form with a connection name
- **THEN** no recorded request payload contains the password
- **AND** the saved profile and all API responses contain no password

#### Scenario: Reopened profile has empty password
- **GIVEN** a saved connection created with a password entered in the form
- **WHEN** the user opens its details page again
- **THEN** the password field is empty
- **AND** no API response or page content reveals a stored password

#### Scenario: Errors and logs disclose no password
- **GIVEN** a password was entered in the details form
- **WHEN** the form is submitted with invalid fields and the agent log is inspected
- **THEN** no problem-details response and no log entry contains the password

### Requirement: Profiles persist across restarts

Saved connection profiles SHALL be stored in a SQLite database in the per-OS application-data directory, created automatically on first use. Profiles SHALL survive agent restarts.

#### Scenario: Persistence across restart
- **GIVEN** a saved connection and a running agent
- **WHEN** the agent is stopped and started again and `GET /api/connections` is requested with a valid session
- **THEN** the saved connection is still listed

#### Scenario: Store location per platform
- **GIVEN** a fresh profile with no database file
- **WHEN** the agent first writes a saved connection on Windows, macOS or Linux
- **THEN** the SQLite file is created inside that platform's application-data directory for the app
- **AND** the file is readable and writable only by the current user

#### Scenario: Missing store is created automatically
- **GIVEN** the application-data directory exists but contains no SQLite file
- **WHEN** the agent starts and `GET /api/connections` is requested
- **THEN** the response status is 200 with an empty array and the database file is created

### Requirement: Connection picker page

The `/archive` route SHALL show a heading, the list of saved connections and the new-connection choices for the five server-based DBMS. Each saved connection SHALL show its name and target summary. Each entry SHALL offer a delete action with confirmation. Selecting a saved connection or a DBMS SHALL navigate to the details page. The page SHALL be localized in de, fr, it and en and usable on narrow viewports.

#### Scenario: Picker shows both sections
- **GIVEN** a valid local session, one saved connection and the catalog
- **WHEN** the user visits `/archive`
- **THEN** the saved connection is listed with its name and target summary
- **AND** the five server-based DBMS are offered as new-connection choices
- **AND** MS Access is not offered

#### Scenario: Selection navigates to details
- **GIVEN** the picker page with a saved connection "Archive A" and the PostgreSQL DBMS choice
- **WHEN** the user selects "Archive A" and later selects PostgreSQL
- **THEN** the route becomes `/archive/connection` carrying the saved connection's identifier
- **AND** on the second selection it carries the PostgreSQL identifier instead

#### Scenario: Delete with confirmation
- **GIVEN** the picker page listing a saved connection
- **WHEN** the user activates its delete action and confirms
- **THEN** a `DELETE /api/connections/{id}` request is sent
- **AND** the entry disappears from the list
- **WHEN** the user activates delete and cancels the confirmation instead
- **THEN** no request is sent and the entry remains

#### Scenario: Empty state
- **GIVEN** a valid local session and no saved connections
- **WHEN** the user visits `/archive`
- **THEN** an empty-state message is shown in the interface language
- **AND** the five DBMS choices remain usable

#### Scenario: Untrusted query text is not page content
- **GIVEN** a valid local session
- **WHEN** the user visits `/archive?name=%3Cscript%3Ealert(1)%3C%2Fscript%3E`
- **THEN** the page shows its translated labels and saved data only
- **AND** the supplied value is neither inserted into the page nor executed

#### Scenario: API failure shows an error state
- **GIVEN** the connections API responds with an error
- **WHEN** the user visits `/archive`
- **THEN** the page shows a localized error message instead of crashing
- **AND** the DBMS choices remain visible if the catalog loaded

### Requirement: Connection details page

The `/archive/connection` route SHALL render the connection form for the DBMS identified by its query parameters: empty with example placeholders for a new DBMS, or prefilled from a saved connection with an empty password field and a read-only DBMS indicator. Submitting with a connection name SHALL save the profile and return to `/archive`; submitting without a name SHALL NOT save. Unknown identifiers SHALL show an error state.

#### Scenario: New connection form
- **GIVEN** the picker page and the PostgreSQL DBMS choice
- **WHEN** the details page loads
- **THEN** the form shows the server-based fields with the catalog's example values as placeholders
- **AND** no field is prefilled with saved data

#### Scenario: Saved connection prefilled
- **GIVEN** a saved PostgreSQL connection "Archive A" with host `db.example.org`
- **WHEN** the user opens its details page
- **THEN** host, port, database name, user and options show the saved values
- **AND** the password field is empty
- **AND** the DBMS is shown as fixed and cannot be changed to another DBMS

#### Scenario: Validation blocks submit
- **GIVEN** the details page for a new connection
- **WHEN** the user submits with a required field empty or the port non-numeric
- **THEN** inline validation messages appear on the invalid fields
- **AND** no save request is sent

#### Scenario: Save and return
- **GIVEN** the details page for a new connection with all fields valid and the name "Archive B" entered
- **WHEN** the user submits the form
- **THEN** a `POST /api/connections` request is sent with the entered fields and name
- **AND** the route returns to `/archive` where the new profile is listed

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
