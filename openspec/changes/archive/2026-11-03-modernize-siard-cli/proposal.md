# Proposal

## Why

The `siard-cmd` command-line interface has a 2008-era user experience: cryptic single-letter options (`-j=`, `-p=`), no `--help`/`--version`, silent acceptance of unknown options, nonstandard exit codes (0/4/8/12), a mandatory credits banner on every run, and passwords that can only be passed in plaintext on the command line. The entry points are two god-constructor classes that mix parsing, validation, and database work, making the CLI untestable and hard to evolve. Modernizing the CLI improves usability for its diverse user base (federal agencies, archives worldwide) and establishes a clean seam between CLI parsing and archive/restore logic.

## What Changes

- **BREAKING**: Replace `siard-from-db` / `siard-to-db` with a single `siard` binary providing `archive` and `restore` subcommands
- **BREAKING**: Replace single-letter/colon option syntax (`-j:`, `-u:`, `-p:`, `-s:`, `-o`) with GNU-style long options (`--jdbc-url`, `--user`, `--password`, `--siard-file`, `--overwrite`, ...)
- **BREAKING**: Replace 0/4/8/12 exit codes with standard codes: `0` success, `1` execution error, `2` usage error
- **BREAKING**: Remove the `ProgramInfo` credits banner from normal output; version/credits move behind `--version`
- **BREAKING**: Remove stale static HTML manual (`siard-cmd/doc/manual`) and dead launch scripts (`siard-cmd/bin/*.sh|cmd|ps1`) — neither is part of the shipped distribution contents
- Add `--help`/`--version` on the top-level command and both subcommands (generated, always in sync)
- Add interactive password prompt: `--password` without a value prompts on the console; `--password=<v>` passes directly; optional `SIARD_PASSWORD` env var fallback
- Error on unknown options and missing required parameters with clear messages and a usage hint
- Refactor internals (scoped to what the new CLI needs): extract parsed configuration and `run()` logic out of the `SiardFromDb`/`SiardToDb` constructors; separate user-facing console output from logging
- Fix `SiardConnection` writing `etc/jdbcdrivers.properties` on first run (breaks read-only installs): ship a bundled read-only default, allow an explicit external override
- Fix the MySQL driver title copy-paste bug ("SQL Server" → "MySQL")
- Migrate the ~82 integration-test call sites from `new SiardFromDb/SiardToDb(String[])` to the new programmatic entry point
- Update the CLI section of `docs/user-manual/*/user-manual.adoc` for the new command and options
- Adapt `siard-suite-app` where it consumes `siard-cmd` APIs that change

## Capabilities

### New Capabilities

- `siard-cli`: The `siard` command-line interface — subcommands, options, exit codes, credential input, help/version output, and error reporting for archiving databases to SIARD files and restoring SIARD files to databases.

### Modified Capabilities

(none — no existing specs)

## Impact

- **New dependency**: picocli (`info.picocli:picocli`) in `siard-cmd`
- **`siard-cmd` code**: `SiardFromDb`, `SiardToDb` restructured; new `SiardCli` entry point; `SiardConnection` driver-config fix; `utils/` additions as needed
- **`siard-cmd` build**: `build.gradle.kts` start-script/distribution config (single `siard` script), new dependency
- **Integration tests**: ~82 call sites updated (mechanical arg rewrite + new entry point)
- **`siard-suite-app`**: replaces its `siard-from-db`/`siard-to-db` start scripts (its distribution ships the CLI too) with a single `siard` script; adapts to changed `siard-cmd` API surface where needed
- **Docs**: `docs/user-manual/*/user-manual.adoc` CLI sections rewritten; `siard-cmd/doc/manual` and `siard-cmd/bin` deleted
- **Out of scope**: packaging/installers (fat jar, Windows jpackage installer) — separate change `siard-cli-packaging`; deep refactoring of `MetaDataFromDb`/`MetaDataToDb` (see `docs/refactoring-plan.md`)
