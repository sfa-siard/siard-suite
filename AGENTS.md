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
| `siard-web/local` | Local web app replacing the JavaFX UI: `backend` (Quarkus agent, REST API) and `frontend` (React/TypeScript, served by the backend) |
| `siard-web/backend-core` | Shared Quarkus backend library of the web apps (error format, archive registry and browsing API under `/api/archives`) |
| `siard-web/frontend-shared` | Shared frontend package of the web apps (theme, fonts, i18n, typed API client, test helpers) |
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

Web module boundaries (checked by `./gradlew :siard-web:check`): `backend-core` must not depend on `siard-cmd`, a `jdbc-*` module, a JDBC driver or a web app, and `frontend-shared` must not import from an app or handle sessions. See `siard-web/README.md`.

## Language & style

- **Java 21** — use modern Java features (records, sealed classes, pattern matching, text blocks) where appropriate in new code
- The codebase is in transition from a **legacy style** to a **modern style**:
    - **Legacy style** (mostly in `siard-cmd`, `siard-api`, most of `siard-utilities`, `jdbc-*`): C-style braces, Hungarian notation prefixes (`_s` for String, `_i` for int, `_b` for boolean, `_file` for File, etc.), verbose Javadoc block headers with `/*====*/` separators.
    - **Modern style** (mostly in `siard-suite-app` and newer `siard-cmd` classes): Standard Java conventions, no Hungarian notation, clean formatting, SLF4J logging.
- **New code always uses the modern style** — even inside legacy files. New files, classes, methods and fields get plain names, standard conventions and SLF4J logging, regardless of surrounding code. Mixed style within a file is expected during the transition.
- When touching legacy code, keep edits minimal and leave the surrounding legacy-style code as it is — do not reformat or modernise code you are not changing.

## Integration tests (Docker)

- The `siard-cmd` database integration tests (`integrationTestOracle`, `integrationTestPostgres`, `integrationTestMysql`, ...) start databases with Testcontainers. They need Docker access and a writable Gradle user home (`~/.gradle`).
- Run each Gradle test task as its own command. Do not chain it with other commands (`&&`, pipes), and process reports in a separate step.
- If a run fails before any database starts (Gradle lock/cache errors, `Can not connect to Ryuk`), it is a sandbox problem, not a test failure: stop and ask the user to allow the task outside the sandbox or to run it themselves. Do not disable Ryuk, override the Docker host or change test configuration to work around it.
- For timing comparisons, use `--rerun-tasks --no-build-cache` and exclude runs that never reached the database.


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
