# siard-cli Specification

## Purpose

Defines the behavior of the `siard` command-line interface: how users archive databases into SIARD files, restore SIARD files into databases, supply credentials, and how the CLI reports usage, errors, and results.

## Requirements

### Requirement: Unified `siard` entry point with subcommands

The CLI SHALL be invoked through a single `siard` command offering the subcommands `archive` (write a database to a SIARD file) and `restore` (load a SIARD file into a database).

#### Scenario: Archive a database

- **WHEN** user runs `siard archive --jdbc-url=<url> --user=<u> --password=<p> --siard-file=out.siard` against a reachable database
- **THEN** the archive is written to `out.siard` and the exit code is `0`

#### Scenario: Restore a SIARD file

- **WHEN** user runs `siard restore --jdbc-url=<url> --user=<u> --password=<p> --siard-file=in.siard` against a reachable, empty target database
- **THEN** the archive contents are loaded into the database and the exit code is `0`

#### Scenario: Missing subcommand

- **WHEN** user runs `siard` with no subcommand
- **THEN** the top-level usage help is printed to standard output and the exit code is `2`

#### Scenario: Unknown subcommand

- **WHEN** user runs `siard frobnicate`
- **THEN** an error naming the unknown subcommand and the list of valid subcommands is printed to standard error and the exit code is `2`

### Requirement: GNU-style options with generated help

The CLI SHALL accept GNU-style long options (with the short aliases defined per subcommand), SHALL print generated usage help via `--help`, and SHALL reject unknown options.

#### Scenario: Help on each command

- **WHEN** user runs `siard --help`, `siard archive --help`, or `siard restore --help`
- **THEN** usage help listing all options with descriptions and defaults is printed to standard output and the exit code is `0`

#### Scenario: Unknown option rejected

- **WHEN** user runs `siard archive --jdbcurl=x`
- **THEN** an "unknown option" error identifying `--jdbcurl` is printed to standard error followed by a usage hint, and the exit code is `2`

#### Scenario: Missing required option

- **WHEN** user runs `siard archive --user=u --password=p --siard-file=out.siard` without `--jdbc-url`
- **THEN** an error naming the missing required option is printed to standard error and the exit code is `2`

#### Scenario: Unsupported JDBC scheme

- **WHEN** user runs `siard archive --jdbc-url=jdbc:foo:bar --user=u --password=p --siard-file=out.siard` where `foo` has no configured driver
- **THEN** an error naming the unsupported JDBC scheme is printed to standard error and the exit code is `2`

### Requirement: `archive` output options

`siard archive` SHALL support `--siard-file` and `--export-xml` outputs, `--schema`, `--overwrite`, `--views-as-tables`, `--login-timeout`, `--query-timeout`, `--import-xml`, `--lob-folder`, and `--mime-type` options. `--import-xml` SHALL name an existing metadata XML template applied to the archive before metadata download. At least one of `--siard-file` or `--export-xml` SHALL be required.

#### Scenario: Export metadata only

- **WHEN** user runs `siard archive --jdbc-url=<url> --user=<u> --password=<p> --export-xml=meta.xml` without `--siard-file`
- **THEN** the database metadata is exported to `meta.xml` and the exit code is `0`

#### Scenario: Import template does not exist

- **WHEN** user runs `siard archive --jdbc-url=<url> --user=<u> --password=<p> --siard-file=out.siard --import-xml=missing.xml` where `missing.xml` does not exist
- **THEN** an error naming the missing template file is printed to standard error and the exit code is `2`

#### Scenario: Neither output given

- **WHEN** user runs `siard archive --jdbc-url=<url> --user=<u> --password=<p>` without `--siard-file` and without `--export-xml`
- **THEN** an error stating that at least one output must be given is printed to standard error and the exit code is `2`

#### Scenario: Existing SIARD file without --overwrite

- **WHEN** user runs `siard archive --jdbc-url=<url> --user=<u> --password=<p> --siard-file=existing.siard` and `existing.siard` already exists
- **THEN** an error stating the file exists and suggesting `--overwrite` is printed to standard error and the exit code is `1`

#### Scenario: Existing SIARD file with --overwrite

- **WHEN** user repeats the same command adding `--overwrite`
- **THEN** the existing file is replaced and the exit code is `0`

### Requirement: `restore` schema mapping and overwrite

`siard restore` SHALL support `--map-schema=<archive-schema>=<db-schema>` as a repeatable option, `--overwrite`, `--login-timeout`, and `--query-timeout`.

#### Scenario: Restore with schema mapping

- **WHEN** user runs `siard restore ... --siard-file=in.siard --map-schema=HR=HR_COPY --map-schema=SALES=SALES2`
- **THEN** archived schemas `HR` and `SALES` are created in the target database as `HR_COPY` and `SALES2`, and the exit code is `0`

#### Scenario: Malformed schema mapping

- **WHEN** user runs `siard restore ... --siard-file=in.siard --map-schema=BADVALUE` where the value contains no `=`
- **THEN** an error explaining the expected `archive-schema=db-schema` syntax is printed to standard error and the exit code is `2`

#### Scenario: Existing objects without --overwrite

- **WHEN** user runs `siard restore` against a target where objects to be created already exist, without `--overwrite`
- **THEN** an error listing the conflict and suggesting `--overwrite` is printed to standard error and the exit code is `1`

### Requirement: Credential input without plaintext exposure

The database password SHALL be supplied by, in precedence order: a `--password=<value>` argument, an interactive console prompt when `--password` is given without a value, or the `SIARD_PASSWORD` environment variable. The password SHALL NOT be echoed or logged.

#### Scenario: Password on command line

- **WHEN** user passes `--password=secret`
- **THEN** the value `secret` is used and the password does not appear in any output or log

#### Scenario: Password never leaked to output or logs

- **WHEN** a run completes (successfully or with an error) after `--password=<value>` was supplied
- **THEN** the literal password string occurs zero times on standard output, on standard error, and in the log file — including at debug log level

#### Scenario: Interactive prompt

- **WHEN** user passes `--password` with no value on an interactive console
- **THEN** the CLI prompts for the password without echoing input and uses the entered value

#### Scenario: Prompt requested without console

- **WHEN** user passes `--password` with no value and no console is available (e.g., piped stdin)
- **THEN** an error explaining that no console is available for prompting is printed to standard error and the exit code is `2`

#### Scenario: Password from environment

- **WHEN** `SIARD_PASSWORD` is set and `--password` is absent
- **THEN** the environment variable value is used and the exit code reflects the operation result, not a missing-parameter error

#### Scenario: No password supplied at all

- **WHEN** neither `--password` nor `SIARD_PASSWORD` is given
- **THEN** an error naming the missing credential with a hint to `--password` for prompting is printed to standard error and the exit code is `2`

### Requirement: Standard exit codes

The CLI SHALL exit with `0` on success, `1` on execution errors, and `2` on usage errors.

#### Scenario: Successful operation

- **WHEN** an archive or restore completes successfully
- **THEN** the process exit code is `0`

#### Scenario: Execution error

- **WHEN** an archive or restore fails at runtime (e.g., unreachable database, missing output file on restore)
- **THEN** a diagnostic is printed to standard error and the exit code is `1`

#### Scenario: Usage error

- **WHEN** arguments are malformed, unknown, or required parameters are missing
- **THEN** the error and a usage hint are printed and the exit code is `2`

### Requirement: Clean, channel-separated output

User-facing output SHALL go to standard output (results, help) and standard error (diagnostics). Log output SHALL NOT be interleaved into standard output, and no credits banner SHALL be printed during normal operation.

#### Scenario: No banner on normal runs

- **WHEN** user runs any `siard` subcommand
- **THEN** standard output contains only command results — no author/credits banner and no log lines

#### Scenario: Diagnostics on stderr

- **WHEN** a command fails
- **THEN** error text appears on standard error while standard output contains no error text

### Requirement: Version reporting

`siard --version` SHALL print the program name, application version, and supported SIARD format version.

#### Scenario: Print version

- **WHEN** user runs `siard --version`
- **THEN** a single block containing the program name, application version, and SIARD version is printed to standard output and the exit code is `0`

### Requirement: Runs from read-only install locations

The CLI SHALL start and print help or version correctly when its installation directory is not writable, loading JDBC driver mappings from bundled defaults unless an explicit external configuration is provided.

#### Scenario: First run in read-only location

- **WHEN** user runs `siard archive --help` from an installation directory without write permission
- **THEN** help prints normally and no files are created or modified inside the installation directory, and the exit code is `0`
