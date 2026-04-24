# AGENTS.md

SIARD Suite is a toolset for archiving relational databases in the **SIARD 2.2** (Software-Independent Archival of Relational Databases) format. It is developed and financed by the **Swiss Federal Archives (SFA/BAR)** and maintained by **Puzzle ITC AG**.

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

## Language & style

- **Java 17** — use modern Java features (records, sealed classes, pattern matching, text blocks) where appropriate in new code
- The codebase has **two distinct coding styles**:
    - **Legacy code** (mostly in `siard-cmd`, `siard-api`, `siard-utilities`, `jdbc-*`): C-style braces, Hungarian notation prefixes (`_s` for String, `_i` for int, `_b` for boolean, `_file` for File, etc.), verbose Javadoc block headers with `/*====*/` separators. **Preserve this style when editing legacy files.**
    - **Modern code** (mostly in `siard-suite-app` and newer `siard-cmd` classes): Standard Java conventions, no Hungarian notation, clean formatting. **Use this style for new files.**
- When modifying an existing file, **always match the surrounding code style** — do not reformat or modernise legacy code without explicit intent

## Approach

- Think before acting. Read existing files before writing code.
- Be concise in output but thorough in reasoning.
- Prefer editing over rewriting whole files.
- Do not re-read files you have already read unless the file may have changed.
- Test your code before declaring done.
- No sycophantic openers or closing fluff.
- Keep solutions simple and direct. No over-engineering.
- If unsure: say so. Never guess or invent file paths.
- User instructions always override this file.

## Efficiency

- Read before writing. Understand the problem before coding.
- No redundant file reads. Read each file once.
- One focused coding pass. Avoid write-delete-rewrite cycles.
- Test once, fix if needed, verify once. No unnecessary iterations.
- Budget: 50 tool calls maximum. Work efficiently.