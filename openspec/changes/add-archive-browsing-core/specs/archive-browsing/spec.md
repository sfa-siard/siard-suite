# Spec Delta

## Purpose

Lets a user of either web app browse an already opened SIARD archive read-only: its metadata, schemas, tables and views, the rows of a table in pages, a bounded text search within a table, and the content of large objects, through one shared API and one shared set of views.

## ADDED Requirements

### Requirement: Archive metadata

`GET /api/archives/{archiveId}` SHALL return 200 with the archive's `dbName`, `description`, `dataOwner`, `dataOriginTimespan`, `archiver`, `archiverContact`, `archivalDate`, `version` and the list of its schemas (`index`, `name`).

#### Scenario: Metadata of the sample archive
- **GIVEN** the fixture `sample.siard` is open in the caller's scope
- **WHEN** `GET /api/archives/{archiveId}` is requested
- **THEN** the response status is 200 with content type `application/json`
- **AND** `dbName` is `Sample Database`, `archiver` is `Hartwig Thomas` and `archivalDate` is `2018-02-01`
- **AND** `schemas` contains exactly one entry with `index` 0 and `name` `SampleSchema`

#### Scenario: Unknown archive id
- **GIVEN** an id that was never issued
- **WHEN** `GET /api/archives/{archiveId}` is requested
- **THEN** the response status is 404 with content type `application/problem+json`

#### Scenario: Write methods are rejected
- **GIVEN** an open archive
- **WHEN** `PUT /api/archives/{archiveId}` or `POST /api/archives/{archiveId}` is requested
- **THEN** the response status is 405 with content type `application/problem+json`
- **AND** the archive file is unchanged (same size and modification time)

### Requirement: Schema, table and view structure

The API SHALL expose, addressed by zero-based indexes: a schema with its tables (`index`, `name`, `rows`) and views (`index`, `name`); a table with its description, row count and columns (`index`, `name`, `type`, `nullable`, `description`, `lob`); and a view with its description, defining query and columns.

#### Scenario: Tables of a schema
- **GIVEN** `sample.siard` is open
- **WHEN** `GET /api/archives/{archiveId}/schemas/0` is requested
- **THEN** `tables` lists `TSIMPLE` with `rows` 4 and `TCOMPLEX` with `rows` 2
- **AND** `views` lists `VIEW1` and `VIEW2`

#### Scenario: Columns of a table
- **GIVEN** `sample.siard` is open
- **WHEN** `GET /api/archives/{archiveId}/schemas/0/tables/0` is requested
- **THEN** `rows` is 4 and `columns` equals the column names, in order, of the archive's `TSIMPLE` metadata
- **AND** every column has a non-empty `type` and a boolean `nullable`

#### Scenario: Index out of range
- **GIVEN** `sample.siard` is open
- **WHEN** `GET /api/archives/{archiveId}/schemas/7` or `.../schemas/0/tables/99` or `.../schemas/-1` is requested
- **THEN** each response status is 404 with content type `application/problem+json`

#### Scenario: Names are data, never paths
- **GIVEN** a schema or table whose name contains `../` or a path separator
- **WHEN** its structure is requested by index
- **THEN** the name is returned verbatim as JSON text
- **AND** no filesystem access outside the archive occurs

### Requirement: Paged table rows

`GET /api/archives/{archiveId}/schemas/{s}/tables/{t}/rows?offset=&limit=` SHALL return the `total` row count, the `offset` and at most `limit` rows, each with its zero-based `row` number and one cell per column. `offset` defaults to 0, `limit` to 100 and is at most 1000.

#### Scenario: Page of rows
- **GIVEN** the table `payment` (16049 rows) of `sfdbsakila.siard` is open
- **WHEN** `rows?offset=200&limit=50` is requested
- **THEN** `total` is 16049, `offset` is 200 and exactly 50 rows are returned
- **AND** their `row` numbers are 200 to 249 in order

#### Scenario: Last partial page and offset beyond the end
- **GIVEN** the table `payment` is open
- **WHEN** `rows?offset=16040&limit=100` and `rows?offset=20000&limit=100` are requested
- **THEN** the first returns exactly 9 rows
- **AND** the second returns 200 with an empty `rows` list and `total` 16049

#### Scenario: Invalid paging parameters
- **GIVEN** an open table
- **WHEN** rows are requested with `offset=-1`, with `limit=0`, with `limit=1001` and with `offset=abc`
- **THEN** each response status is 400 with content type `application/problem+json`
- **AND** none of the bodies contains row data

#### Scenario: Forward paging does not rescan
- **GIVEN** the table `payment` is open and the first page (offset 0, limit 100) was returned
- **WHEN** the next pages (offsets 100, 200, ... 1900) are requested in order
- **THEN** for each page the number of records read from the archive file is at most 100
- **AND** requesting offset 0 again afterwards also reads at most 100 records

### Requirement: Cell values

Each cell SHALL be `null` for an SQL NULL, an object `{"type":"text","value":…,"truncated":…}` for textual and numeric content, or `{"type":"lob","length":…}` for binary and large-object content. Text longer than 4096 characters SHALL be cut at 4096 with `truncated` true. Structured values (arrays, user-defined types) SHALL be delivered as text.

#### Scenario: Text, null and numeric cells
- **GIVEN** the table `TSIMPLE` of `sample.siard` is open
- **WHEN** its rows are requested
- **THEN** a NULL cell is JSON `null`
- **AND** every other cell has `type` `text`, a string `value` equal to what the desktop app shows for the same cell, and `truncated` false

#### Scenario: Large text is truncated
- **GIVEN** a text cell of 100000 characters
- **WHEN** its row is requested
- **THEN** the cell's `value` has length 4096 and `truncated` is true
- **AND** the full text is available through the content endpoint

#### Scenario: Large object cells carry no content
- **GIVEN** the table `TCOMPLEX` of `sample.siard` with its binary and external LOB columns
- **WHEN** its rows are requested
- **THEN** each LOB cell has `type` `lob` and a `length` greater than 0
- **AND** the response body is smaller than 20000 bytes although the archive's LOB files total more than 100000 bytes

### Requirement: Large object content

`GET /api/archives/{archiveId}/schemas/{s}/tables/{t}/rows/{row}/columns/{c}/content` SHALL stream the cell's full bytes with content type `application/octet-stream`, `Content-Disposition: attachment` with a file name derived only from the table, row and column indexes, and `X-Content-Type-Options: nosniff`.

#### Scenario: Download of a binary LOB
- **GIVEN** `sample.siard` is open and a LOB cell of known length
- **WHEN** its content endpoint is requested
- **THEN** the response status is 200 and the body's length equals the cell's `length`
- **AND** the body's bytes equal the entry's bytes in the archive

#### Scenario: Cell without content
- **GIVEN** a NULL cell, a column that is not a LOB or text column, and a row number beyond the table
- **WHEN** the content endpoint is requested for each
- **THEN** a NULL cell and a row beyond the table answer 404 with `application/problem+json`
- **AND** a numeric column answers the cell's text as bytes with status 200

#### Scenario: Active content is never rendered by the browser
- **GIVEN** a LOB whose bytes are `<script>alert(1)</script>` and whose name suggests `.html`
- **WHEN** its content is requested
- **THEN** the content type is `application/octet-stream`, `Content-Disposition` starts with `attachment` and `X-Content-Type-Options` is `nosniff`
- **AND** the file name contains no character outside `[A-Za-z0-9._-]`

### Requirement: Bounded search in a table

`GET .../tables/{t}/search?q=&matchCase=&afterRow=` SHALL find the next row after `afterRow` (default -1) with a text cell containing `q`, scanning at most 20000 rows or 5 seconds per request, and return `found`, `row`, `column`, and `complete`. When the scan stops without a match before the end, `found` is false, `complete` is false and `nextRow` tells where to continue.

#### Scenario: Match found
- **GIVEN** the table `payment` is open and a value that occurs first in row 7 of a text column
- **WHEN** the search for that value is requested with `afterRow=-1`
- **THEN** `found` is true, `row` is 7 and `column` is the index of the matching column

#### Scenario: Continue after a match and reach the end
- **GIVEN** the previous search found row 7 and no further row matches
- **WHEN** the search is repeated with `afterRow=7`
- **THEN** `found` is false and `complete` is true

#### Scenario: Scan budget is exhausted
- **GIVEN** the table `payment` (16049 rows) and a search value that occurs nowhere, with the budget configured to 5000 rows
- **WHEN** the search is requested with `afterRow=-1`
- **THEN** `found` is false, `complete` is false and `nextRow` is 5000
- **AND** repeating with `afterRow=4999` continues at row 5000

#### Scenario: Invalid search input
- **GIVEN** an open table
- **WHEN** search is requested with an empty `q` and with a `q` of 201 characters
- **THEN** each response status is 400 with content type `application/problem+json`

#### Scenario: Search honours case option
- **GIVEN** a value that occurs only as `Smith`
- **WHEN** `q=smith` is searched once with `matchCase=true` and once with `matchCase=false`
- **THEN** the first returns `found` false and the second returns `found` true

### Requirement: The archive API is described and stays consistent across apps

The archive endpoints SHALL be described by a committed OpenAPI description of the shared backend. Each app's own generated description SHALL contain the shared paths and schemas unchanged.

#### Scenario: Core description is current
- **GIVEN** the repository
- **WHEN** the shared backend's description check runs
- **THEN** it passes only if the generated description equals the committed one

#### Scenario: App description carries the shared API
- **GIVEN** the local app's generated description
- **WHEN** its `/api/archives/**` paths and their schemas are compared with the shared description
- **THEN** they are equal

#### Scenario: API drift fails the build
- **GIVEN** a changed row DTO field in the shared backend without regenerating the description
- **WHEN** `./gradlew :siard-web:backend-core:check` runs
- **THEN** the build fails and names the command that regenerates the description

### Requirement: Shared browse views

The shared frontend package SHALL provide archive browse views that, given an `archiveId`, show the archive's structure as a tree of schemas, tables and views, a table's columns and its rows in a grid paged on the server (100 rows per page), a search field that jumps to the next match, and a download action for large-object cells. All texts SHALL exist in German, French, Italian and English.

#### Scenario: Browse a table
- **GIVEN** an app mounts the browse views for `sample.siard` with a mocked API of its fixture data
- **WHEN** the user selects the table `TSIMPLE`
- **THEN** the grid shows 4 rows and the column headers of `TSIMPLE`
- **AND** the row count `4` is shown

#### Scenario: Paging requests only the needed page
- **GIVEN** a table of 16049 rows is shown
- **WHEN** the user goes to the third page
- **THEN** exactly one rows request with `offset=200&limit=100` is made
- **AND** the grid shows rows 200 to 299

#### Scenario: Search jumps to the match
- **GIVEN** a table is shown and the API reports a match in row 750
- **WHEN** the user searches and chooses "next"
- **THEN** the page containing row 750 is shown and row 750 is marked
- **AND** when the API answers `found: false, complete: false` the view continues the search from `nextRow` until it is complete

#### Scenario: Archive no longer open
- **GIVEN** a table is shown
- **WHEN** a request is answered 404 with the "no longer open" problem
- **THEN** the views show a message that the archive was closed and must be opened again
- **AND** no stale rows remain visible

#### Scenario: Session lost
- **GIVEN** a table is shown
- **WHEN** a request is answered 401
- **THEN** the views report it to the host app through a callback and show no row data

#### Scenario: Language
- **GIVEN** the interface language is German
- **WHEN** the browse views are shown
- **THEN** their labels are German and none shows a raw translation key
