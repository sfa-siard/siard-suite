# Spec Delta

## MODIFIED Requirements

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
- **WHEN** a request is answered 404
- **THEN** the views show a message that the archive is not or no longer open and must be opened again
- **AND** no stale rows remain visible

#### Scenario: Session lost
- **GIVEN** a table is shown
- **WHEN** a request is answered 401
- **THEN** the views report it to the host app through a callback and show no row data

#### Scenario: LOB cell offers a download
- **GIVEN** a table is shown whose row 0 has a large-object cell of 2048 bytes in column 1
- **WHEN** the grid shows that cell
- **THEN** the cell shows the size and a download link to `/api/archives/{archiveId}/schemas/0/tables/0/rows/0/columns/1/content`

#### Scenario: View shows its query and columns
- **GIVEN** an archive with the view `VIEW1`
- **WHEN** the user selects `VIEW1`
- **THEN** the view's query and the name and type of each of its columns are shown

#### Scenario: Language
- **GIVEN** the interface language is German
- **WHEN** the browse views are shown
- **THEN** their labels are German and none shows a raw translation key

#### Scenario: Switch between tables and views
- **GIVEN** the browse views show a table, a view or a page of rows below the mount point
- **WHEN** the user selects another table or view in the structure tree, repeatedly
- **THEN** each selection shows that table or view
- **AND** every link of the tree points to the same address below the mount point (e.g. `/archives/{archiveId}/schemas/0/tables/1`), wherever the views are

## ADDED Requirements

### Requirement: Data link cells and content outside the archive

A DATALINK cell SHALL be shown as the desktop app shows it: as text holding the content of its file, or empty (`{"type":"text","value":"","truncated":false}`) when that content cannot be read as text, decided per cell. A cell of any type that points outside the archive by URL (`dlurlpathonly`) SHALL be shown empty and its content SHALL NOT be read: its download is empty, search finds nothing in it, and the URL is never opened.

#### Scenario: Data link cells show their text
- **GIVEN** the table `TSIMPLE` of `sample-datalink-2-2.siard` is open, whose column `COLUMN_DATALINK` holds files of 79 bytes of ASCII text
- **WHEN** its rows are requested, a `COLUMN_DATALINK` cell's content is downloaded and the table is searched for that text
- **THEN** the response status is 200 and each `COLUMN_DATALINK` cell is a text cell holding its file's text, as the desktop app shows it
- **AND** the download answers that text and the search finds it in that column

#### Scenario: A data link that cannot be read is shown empty
- **GIVEN** a crafted variant of `sample-datalink-2-2.siard` whose first `COLUMN_DATALINK` file holds bytes that are not UTF-8 (like the JPEG in `test.siard`)
- **WHEN** the rows of `TSIMPLE` are requested
- **THEN** the response status is 200 and that cell is `{"type":"text","value":"","truncated":false}`, as the desktop app shows it
- **AND** the other `COLUMN_DATALINK` cells keep their text, and the empty cell's download is empty

#### Scenario: Content outside the archive is not followed
- **GIVEN** a crafted variant of `sample-datalink-2-2.siard` whose table schema allows `dlurlpathonly` and whose first `CBLOB` cell holds the URL of a local file, once instead of its stored file and once next to it
- **WHEN** the rows of `TSIMPLE` are requested, the cell's content is downloaded and the table is searched for the file's name
- **THEN** the cell is `{"type":"text","value":"","truncated":false}`, the download is empty and the search finds nothing
- **AND** the linked file is never opened

#### Scenario: Data link cells need a session
- **GIVEN** `sample-datalink-2-2.siard` registered in a scope and a request without a valid session (no scope)
- **WHEN** the rows of `TSIMPLE` or the content of a `COLUMN_DATALINK` cell are requested
- **THEN** the response status is 401 with problem details
- **AND** the body contains no cells and no cell content
