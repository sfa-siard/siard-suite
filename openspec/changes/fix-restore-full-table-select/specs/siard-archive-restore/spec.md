# Spec Delta

## Purpose

Defines the behavior SIARD Suite must exhibit when restoring SIARD archives into relational databases: preparing table writes must not require reading the target table's row data, and memory usage during restore must stay bounded independently of table size.

## ADDED Requirements

### Requirement: Restore does not transfer existing table rows

When preparing to write rows into a target table during restore, the system SHALL obtain the table's column metadata without reading the table's row data, so that no row contents are transferred from the database to the client for write preparation.

#### Scenario: Restore issues no row-returning table read

- **WHEN** a SIARD archive is restored into a database
- **THEN** the queries issued against each target table for write preparation return column metadata but no row data

#### Scenario: Download direction is unaffected

- **WHEN** an archive is created from a database (download direction)
- **THEN** table rows are read and exported exactly as before

### Requirement: Restore memory usage is bounded independently of table size

When restoring a SIARD archive, the system SHALL keep heap usage during each table's restore bounded independently of that table's total size, including tables containing large binary or character objects.

#### Scenario: Large table restores with bounded memory

- **WHEN** a table containing hundreds of megabytes of row data is restored
- **THEN** heap usage during that table's restore does not grow proportionally to the table's total size

#### Scenario: Restore does not buffer rows it never reads

- **WHEN** a table is restored and its row data is not needed by the write mechanism
- **THEN** the restore does not additionally retain a copy of the table's rows in heap

### Requirement: Restore writes all archived rows with full fidelity

When a restore of a table completes, every archived row SHALL be written to the target table with values and types preserved, regardless of whether the write mechanism's result set contains fetched rows.

#### Scenario: Completed restore matches archive

- **WHEN** a restore of a table completes successfully
- **THEN** the row count and column values in the target database match the archive contents exactly

#### Scenario: Insert works on a row-free write context

- **WHEN** rows are inserted through a result set that holds no fetched rows
- **THEN** every archived row is still inserted into the target table
