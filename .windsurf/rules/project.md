---
trigger: always
description: 
globs: 
---

# Project: SIARD Suite

## Overview

SIARD Suite is a toolset for archiving relational databases in the **SIARD 2.2** (Software-Independent Archival of Relational Databases) format. It is developed and financed by the **Swiss Federal Archives (SFA/BAR)** and maintained by **Puzzle ITC AG**.

- **Repository**: https://github.com/sfa-siard/siard-suite
- **License**: CDDL-1.0
- **Supported platforms**: Windows, macOS, Linux

## What SIARD Suite does

- Archive relational databases into a standardized, software-independent format (`.siard` files)
- Restore SIARD archives back into databases
- Browse, search, and export archived data
- Supports: PostgreSQL, MySQL/MariaDB, Oracle, MS SQL Server, DB2, MS Access

## Monorepo structure

This is a **Gradle multi-module monorepo**. All modules live in the same repository and are built together.

| Module | Purpose |
|---|---|
| `siard-suite-app` | Desktop GUI application (JavaFX) |
| `siard-cmd` | Command-line interface (`SiardFromDb`, `SiardToDb`) |
| `siard-api` | Core API for reading/writing SIARD files (JAXB-based) |
| `jdbc-base` | Abstract base JDBC wrapper with test fixtures |
| `jdbc-postgres` | PostgreSQL JDBC wrapper |
| `jdbc-mysql` | MySQL JDBC wrapper |
| `jdbc-mssql` | MS SQL Server JDBC wrapper |
| `jdbc-oracle` | Oracle JDBC wrapper |
| `jdbc-db2` | DB2 JDBC wrapper |
| `jdbc-access` | MS Access JDBC/ODBC wrapper |
| `sql-parser` | SQL parser (ANTLR-based) |
| `zip64-file` | ZIP64 file format handler |
| `siard-utilities` | Shared utility classes |

### Key dependency flow

```
siard-suite-app → siard-cmd → siard-api → sql-parser, zip64-file, siard-utilities
                            → jdbc-*    → jdbc-base → sql-parser, siard-utilities
```

## Key stakeholders

- **Swiss Federal Archives (SFA/BAR)**: Product owner. SFA staff cannot run executables directly — they run the app via `java -jar lib/siard-suite.jar`. This constraint must always be considered.
- **Puzzle ITC AG**: Development contractor.

## Documentation

- User manuals in AsciiDoc: `docs/user-manual/{en,de,fr,it}/`
- Software Architecture Document: `docs/sad/sad.adoc`
- PDF generation: `./gradlew :siard-suite-app:asciidoctorPdf`
- Localisations: German, English, French, Italian