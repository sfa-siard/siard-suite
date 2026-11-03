# Design

## Context

`siard-cmd` ships two entry points (`SiardFromDb`, `SiardToDb`) whose constructors do everything: parse args via the home-grown `ch.enterag.utils.cli.Arguments`, validate, connect, transfer data. User output goes through raw `System.out`/`System.err`, while logback additionally writes `ch.admin.bar` at DEBUG to stdout (`siard-cmd/src/main/resources/logback.xml`), interleaving log noise with user output. ~82 integration-test call sites instantiate `new SiardFromDb/SiardToDb(String[])` and assert `getReturn()` — the `String[]` constructor is the de-facto programmatic seam. `siard-suite-app` does not call these two classes from Java code, but **its build ships `siard-from-db`/`siard-to-db` start scripts pointing at them as `mainClass`** (`siard-suite-app/build.gradle.kts` `CreateStartScripts` tasks) — the GUI distribution contains the CLI too, and Gradle does not validate `mainClass`, so removal would break those scripts silently. The app also reuses other `siard-cmd` types (`MetaDataFromDb`, `PrimaryDataToDb`, `SiardConnection`). See proposal.md for motivation.

## Goals / Non-Goals

**Goals:**
- One `siard` binary with `archive`/`restore` subcommands, GNU-style options, generated help, standard exit codes
- Thin CLI layer over an injectable engine: config in, exit code out — no `System.exit` except in `main`
- User-facing text and logs on separate channels
- Preserve the `String[]`-in / `int`-out test seam so the 82 IT call sites migrate mechanically

**Non-Goals:**
- Packaging/distribution (fat jar, Windows installer) → change `siard-cli-packaging`
- Restructuring `MetaDataFromDb`/`MetaDataToDb` or other deep refactors → `docs/refactoring-plan.md`
- New subcommands (inspect/validate/export-csv)
- Renaming the engine classes `SiardFromDb`/`SiardToDb` internals beyond what the new seam requires

## Decisions

### D1 — Picocli as the CLI framework

`info.picocli:picocli` (new dependency). Gives subcommands, GNU-style parsing, generated `--help`/`--version`, unknown-option errors, `interactive=true` prompting, `Map` options, `ArgGroup`s, and pluggable exit codes — all currently hand-rolled or missing.

Alternatives: keep `Arguments` + hand-write help (keeps 2008 UX, no subcommand model); JCommander (weaker help/completion, less maintained).

### D2 — Layering: thin commands over extracted engines

```
SiardCli (main)                       ← @Command, registers subcommands
 ├── ArchiveCommand  (@Command)      ← options only; builds ArchiveConfig
 └── RestoreCommand  (@Command)      ← options only; builds RestoreConfig
        │                 │
        ▼                 ▼
   SiardFromDb.run()   SiardToDb.run()   ← extracted from god constructors
        │                 │
   MetaDataFromDb / PrimaryDataFromDb / MetaDataToDb / PrimaryDataToDb
   (unchanged — also used by siard-suite-app)
```

- `ArchiveConfig` / `RestoreConfig` become Java records (modern style per AGENTS.md) carrying only validated parameters — no I/O channels (see D6), so the records stay usable for non-CLI callers (`siard-suite-app`, web companion).
- `SiardFromDb`/`SiardToDb` keep their names (they are the archival/restore engines, not CLI classes) but change shape: constructor takes the config record, logic moves to `int run(PrintWriter out, PrintWriter err)`. No parsing, no `System.exit`, no `System.out` inside.
- The `siard-suite-app` build's `siardFromDb`/`siardToDb` `CreateStartScripts` tasks are replaced by a single `siard` script with `mainClass = ch.admin.bar.siard2.cmd.SiardCli` — the GUI distribution then ships the same unified CLI.
- Tests call `new CommandLine(new SiardCli()).execute("archive", args...)` — same `String[]`→`int` shape as today.

Alternatives: commands orchestrate directly (re-mixes parse and work — rejected); keep `String[]` constructors as deprecated shims (dead compat surface — rejected, nothing external depends on it).

### D3 — Option surface

| archive | restore |
|---|---|
| `-j`, `--jdbc-url` (required) | `-j`, `--jdbc-url` (required) |
| `-u`, `--user` (required) | `-u`, `--user` (required) |
| `-p`, `--password` (optional value) | `-p`, `--password` (optional value) |
| `-s`, `--siard-file` | `-s`, `--siard-file` (required) |
| `-e`, `--export-xml` | |
| `-o`, `--overwrite` | `-o`, `--overwrite` |
| `-v`, `--views-as-tables` | |
| `-l`, `--login-timeout` | `-l`, `--login-timeout` |
| `-q`, `--query-timeout` | `-q`, `--query-timeout` |
| `-i`, `--import-xml` | |
| `-x`, `--lob-folder` | |
| `-m`, `--mime-type` | |
| `--schema` | `--map-schema=old=new` (repeatable, `Map<String,String>`) |

`archive` requires at least one of `--siard-file`/`--export-xml` and required options (`--jdbc-url`, `--user`) are validated **manually in `call()`**, not via `required = true`/`ArgGroup`: picocli reports missing-required options *before* unmatched arguments, which would mask the unknown option that spec scenarios require to be named in the error (see ADR-0006). `restore`'s positional schema pairs become `--map-schema` (self-documenting, order-free; `archive-schema=db-schema` pairs are validated by the command rather than the picocli `Map` type, so malformed values produce a spec-exact error naming the expected syntax).

### D4 — Password resolution order

1. `--password=<v>` → use directly
2. `--password` (no value, `arity="0..1"`) → prompt via `PasswordPrompter` (interface, `System.console().readPassword()` impl; no console → usage error "no console available for password prompt")
3. `SIARD_PASSWORD` env var → use
4. none → missing-parameter error, exit 2, hint to use `--password`

Prompting never triggers in tests: all ITs pass an explicit value. `PasswordPrompter` is injectable for unit tests — `SiardCli` takes a `PasswordPrompter` (default: console impl) so tests inject a fake. Likewise the `SIARD_PASSWORD` lookup is injectable (`Function<String,String>`, default `System::getenv`) because unit tests cannot mutate the process environment. Password non-exposure is verified by capturing out/err writers plus a logback `ListAppender` at DEBUG.

### D5 — Exit codes

`0` success, `1` execution error, `2` usage/parse error (picocli defaults: `exitCodeOnInvalidInput=2`, `exitCodeOnExecutionException=1`). "Objects exist, use --overwrite" maps to `1` (it is a runtime condition, not bad usage). `iRETURN_*` constants are deleted; tests assert `0`.

### D6 — Output channels

- User-facing text → `PrintWriter`s from picocli `spec.commandLine().getOut()/getErr()`, passed as `run(out, err)` parameters (not inside the config records, which remain pure data) and used by the engines instead of `System.out/err`. Injectable in tests.
- Logs → logback file appender only. Remove the console appender (or demote to `WARN`) from `siard-cmd`'s `logback.xml` so DEBUG noise no longer pollutes stdout.
- `ProgramInfo` banner → only rendered by `--version` (name, version, SIARD version, copyright — not the 4-list credits block; full credits stay in the manual).

### D7 — Driver config without first-run write

`SiardConnection` currently writes `etc/jdbcdrivers.properties` next to the jar when missing. New behavior: load from (a) `-Dch.admin.bar.siard2.cmd.drivers` override if set, else (b) `etc/jdbcdrivers.properties` if the file exists, else (c) the bundled defaults — never write. Required precondition for read-only install locations (packaging change). Also fix the `mysql_title=SQL Server` copy-paste bug.

### D8 — Test migration

Mechanical rewrite of ~82 sites: `new SiardFromDb(new String[]{...})` → `SiardCliTest.execute("archive", ...)` helper (or direct `CommandLine.execute`); arg mapping `-j:`→`--jdbc-url=`, `-u:`→`--user=`, `-p:`→`--password=`, `-s:`→`--siard-file=`, `-o`→`--overwrite`, `--schema:`→`--schema=`; `assertEquals(iRETURN_OK, x.getReturn())` → `assertEquals(0, exitCode)`. A shared test helper keeps the diff small.

## Risks / Trade-offs

- Interactive prompt blocking non-TTY invocations → `--password` without value errors out cleanly when no console exists; explicit values unaffected
- `siard-suite-app` compile breakage if `siard-cmd` APIs change → engines keep names; app-touching types (`SiardConnection`, `MetaData*`, `PrimaryData*`) keep signatures except the driver-config fix, verified by full build
- Picocli interactive option quirk: `--password` consumes the next token if it isn't a flag → `arity="0..1"` + documenting `--password` as last arg or using `=` form
- Single `siard` script on Windows is `siard.bat` — fine; no console-launcher concerns until jpackage (separate change)
- Removing `etc/` from dist contents loses user-editable driver mapping → mitigated by `-D` override + bundled defaults covering all supported DBs

## Migration Plan

1. Add picocli dependency; create `SiardCli`, `ArchiveCommand`, `RestoreCommand`, config records, `PasswordPrompter`
2. Extract `run()` + config records in `SiardFromDb`/`SiardToDb`; switch output to injected writers
3. Fix `SiardConnection` (no-write + MySQL title); fix `logback.xml` (no console appender)
4. Update `build.gradle.kts` (single `siard` script, dist contents without `doc/manual`/`bin`); delete `siard-cmd/bin`, `siard-cmd/doc/manual`; replace the `siardFromDb`/`siardToDb` script tasks in `siard-suite-app/build.gradle.kts` with a single `siard` script
5. Migrate integration tests mechanically
6. Update `docs/user-manual/*/user-manual.adoc` CLI sections; write ADRs for picocli adoption and the no-write driver config (`docs/sad/decisions/0005+`)
7. Build all modules incl. `siard-suite-app`; run unit + a DB integration suite

Rollback: change is internal to the repo; revert the commit. No data or external-API migration needed — old scripts/options simply no longer exist (documented as BREAKING in release notes).

## Open Questions

- Whether `archive`'s "export metadata only" flow (`--export-xml` without `--siard-file`) deserves its own `export-metadata` subcommand later — deferred, current option group preserves existing behavior
