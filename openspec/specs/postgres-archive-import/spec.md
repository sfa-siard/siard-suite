# postgres-archive-import Specification

## Purpose

Defines the bounded-memory behavior SIARD Suite must exhibit when restoring SIARD archives into PostgreSQL: inserted rows are written incrementally without being accumulated in heap, column type information is resolved per table rather than per cell, and every SIARD column type round-trips with full fidelity.

## Requirements

### Requirement: PostgreSQL restore does not accumulate inserted rows

When restoring a SIARD archive into PostgreSQL, the system SHALL write rows through a mechanism that does not retain inserted row data in heap for the lifetime of the table, so memory usage stays bounded independently of table size.

#### Scenario: Large binary table restores with bounded memory

- **WHEN** an archive containing a table with hundreds of megabytes of `bytea` data is restored into PostgreSQL
- **THEN** heap usage during row insertion remains bounded independently of total table size, and every row is present in the target table afterwards

#### Scenario: Restored data is complete and correct

- **WHEN** a restore completes for any table
- **THEN** row count, column values, and types in the target database match the archive contents exactly

### Requirement: Column type resolution happens once per table

The system SHALL resolve target column type information at most once per table during restore, not once per inserted cell.

#### Scenario: Table with many cells

- **WHEN** a table with thousands of non-null cells across multiple columns is restored
- **THEN** catalog metadata for its columns is queried a bounded number of times proportional to tables/columns, not to cell count

### Requirement: All SIARD column types round-trip through PostgreSQL restore

Every column type representable in a SIARD 2.2 archive SHALL be written to PostgreSQL with equivalent type fidelity, including large binary and character objects, arrays, and database-specific types.

#### Scenario: Mixed-type table round-trip

- **WHEN** an archive containing `bytea`, oid-domain `BLOB`/`CLOB`, array, temporal, and `OTHER`/`STRUCT` columns is restored and then re-exported or queried
- **THEN** each value matches the archived value with no truncation, encoding corruption, or type degradation

#### Scenario: Very large single cell

- **WHEN** a single cell exceeds available heap (e.g. a multi-hundred-megabyte binary value)
- **THEN** the restore writes it without requiring heap proportional to that cell's full size where streaming is supported
