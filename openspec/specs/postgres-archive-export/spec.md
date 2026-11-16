# postgres-archive-export Specification

## Purpose

Defines the bounded-memory, streaming behavior SIARD Suite must exhibit when exporting primary data from PostgreSQL: table rows are fetched incrementally, per-cell type metadata is resolved once, and binary/clob mime-type detection neither accumulates cell objects nor leaks server-side large objects.

## Requirements

### Requirement: PostgreSQL table export streams rows incrementally

When exporting a PostgreSQL table, the system SHALL fetch result rows through a server-side cursor with a bounded fetch size instead of materializing the entire result set in heap, and SHALL preserve the complete row stream and archive content.

#### Scenario: Large binary table exports with bounded memory

- **WHEN** a table containing hundreds of megabytes of `bytea` data is exported from PostgreSQL
- **THEN** heap usage during row fetching remains bounded independently of total table size, and every row is written to the archive in order

#### Scenario: Result set is fully consumable

- **WHEN** a streamed PostgreSQL result set is iterated from first to last row while the export transaction is open
- **THEN** all rows are returned with intact column values, including large binary payloads

#### Scenario: Export survives interleaved per-cell queries

- **WHEN** a streamed result set encounters column types requiring catalog lookups (e.g. `OTHER`, `STRUCT`, interval) or oid-domain `BLOB`/`CLOB` values that issue large-object calls
- **THEN** those calls execute without disturbing the open cursor and all rows remain readable

### Requirement: Column type metadata is resolved once per result set

The PostgreSQL JDBC layer SHALL resolve each column's type and type name at most once per `ResultSet`, instead of issuing a catalog query on every metadata access.

#### Scenario: Repeated metadata access on one column

- **WHEN** code reads the type or type name of the same column multiple times on one result set
- **THEN** the catalog is queried at most once for that column and subsequent reads return the cached value

#### Scenario: Metadata remains correct per column

- **WHEN** a result set contains columns of different types
- **THEN** each column reports its own correct type and type name regardless of access order

### Requirement: Mime-type detection does not retain cell objects

During export, the system SHALL NOT accumulate per-cell mime-type entries for the lifetime of a table; each detected mime type SHALL be applied to its cell and then releasable.

#### Scenario: Table with many binary cells

- **WHEN** a table containing thousands of binary cells is exported
- **THEN** memory usage does not grow proportionally to the number of binary cells after their mime types are applied

#### Scenario: Mime types still applied correctly

- **WHEN** binary or clob cells undergo mime-type detection during export
- **THEN** each cell's detected mime type is recorded on the cell exactly as before the change

### Requirement: LOB detection streams are closed after use

Streams opened on the database to feed mime-type detection for `BLOB`/`CLOB` values SHALL be closed once detection completes, releasing the underlying server-side large object.

#### Scenario: Export of oid-domain LOB cells

- **WHEN** a table containing `BLOB` or `CLOB` columns is exported and mime-type detection runs per cell
- **THEN** each detection stream is closed and no large-object descriptors remain open on the database session
