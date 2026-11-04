# ADR-0006: Picocli as the CLI Framework

**Status:** Accepted

## Context

The `siard-cmd` command line used the home-grown `ch.enterag.utils.cli.Arguments` parser behind two god-constructor entry points (`SiardFromDb`, `SiardToDb`). It offered cryptic single-letter/colon options (`-j:`, `-p:`), silently accepted unknown options, had no generated `--help`/`--version`, used nonstandard exit codes (0/4/8/12), and could not model subcommands. Modernizing the CLI required a parser that provides GNU-style options, subcommands, generated help, and standard exit-code conventions without hand-rolling them.

## Decision

Adopt `info.picocli:picocli` as the CLI framework in `siard-cmd`.

- A single `siard` command exposes the subcommands `archive` and `restore` (`SiardCli`, `ArchiveCommand`, `RestoreCommand`).
- Commands are thin: they parse options, build `ArchiveConfig`/`RestoreConfig` records, resolve the password, and delegate to `SiardFromDb.run(out, err)` / `SiardToDb.run(out, err)` — the engines contain no parsing, `System.exit`, or `System.out` access.
- Exit codes: `0` success, `1` execution error, `2` usage error (picocli's `exitCodeOnInvalidInput`/`exitCodeOnExecutionException` defaults).
- Required options and the at-least-one-of `--siard-file`/`--export-xml` rule are validated in `call()` rather than declaratively, so that unknown options are always reported as unmatched (picocli reports missing required options before unmatched arguments, which would hide the unknown option from the error message).
- `PasswordPrompter` and the `SIARD_PASSWORD` lookup are injectable so password resolution is unit-testable without a console or mutable environment.

## Consequences

- GNU-style `--long` options with short aliases, generated help that stays in sync with the option surface, and unknown-option rejection.
- The `String[]`-in/`int`-out test seam (`new CommandLine(new SiardCli()).execute(...)`) preserves the shape the ~82 integration-test call sites used before; migration was mechanical.
- New third-party dependency in `siard-cmd` (picocli, single JAR, no transitive dependencies).
- `--password` without a value can consume a following non-option token (picocli `arity="0..1"` quirk); documented to use `--password=<value>` or place `--password` last.

## Alternatives

- **Keep `Arguments` + hand-written help**: rejected — preserves the 2008 UX, no subcommand model, help drifts out of sync.
- **JCommander**: rejected — weaker generated help and completion support, less actively maintained.
- **Deprecated `String[]` constructor shims**: rejected — dead compatibility surface; nothing external depends on it.
