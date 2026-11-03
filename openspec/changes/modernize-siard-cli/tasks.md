# Tasks

## 1. Test seam and spec scaffolds (TDD)

Spec-derived unit tests land before implementation. They go red in this phase and turn green as later phases complete. DB-dependent scenarios are covered by the migrated integration tests in section 6, not by new unit scaffolds.

- [x] 1.1 Add `info.picocli:picocli` to `gradle/libs.versions.toml` and `siard-cmd/build.gradle.kts` — verify dependency resolves
- [x] 1.2 Create `PasswordPrompter` interface (`char[] prompt()` or similar, no impl yet) — verify `siard-cmd` compiles
- [x] 1.3 Create minimal `SiardCli` skeleton: `@Command` with `archive`/`restore` subcommand stubs that return non-conforming exit codes, plus a constructor/factory accepting `PasswordPrompter` for injection — verify `siard-cmd` compiles
- [x] 1.4 Create `SiardCliTest.execute(String... args)` test helper capturing exit code and out/err `StringWriter`s — verify helper compiles
- [x] 1.5 Derive one unit-test class per spec requirement (`SiardCliUsageTest`, `ArchiveCommandParseTest`, `RestoreCommandParseTest`, `PasswordResolutionTest`, `SiardCliOutputTest`): one `@Test` per non-DB scenario, named after it; `PasswordResolutionTest` attaches a logback `ListAppender` for the never-logged scenario — verify `:siard-cmd:test` runs and every new test fails red for the right reason (wrong exit code/output, not compile errors)
- [x] 1.6 Note which spec scenarios have no unit scaffold (DB-dependent, console-echo) and how section 6 ITs or manual checks cover them — verify the list is recorded in harness-notes.md or a comment on task 8.2

## 2. Engine extraction

- [x] 2.1 Create `ArchiveConfig` and `RestoreConfig` records (validated parameters only, no I/O channels) in `siard-cmd` — verify they compile and a unit test constructs them
- [x] 2.2 Refactor `SiardFromDb`: constructor takes `ArchiveConfig`, constructor logic moves to `int run(PrintWriter out, PrintWriter err)`; remove arg parsing, `System.exit`, `System.out/err`, `iRETURN_*` — verify `siard-cmd` compiles
- [x] 2.3 Same refactor for `SiardToDb` with `RestoreConfig` (schema mapping becomes `Map<String,String>` in config) — verify `siard-cmd` compiles
- [x] 2.4 Console-backed `PasswordPrompter` impl (`System.console().readPassword()`, no console → usage error) — verify unit test with injected fake prompter passes

## 3. CLI layer (picocli)

- [x] 3.1 Fill in `SiardCli` (`@Command` with `archive`/`restore` subcommands, `--help`/`--version`, exit codes 0/1/2, `main` does `System.exit(execute(...))`) — verify `siard --help` prints usage and exits `0`, bare `siard` prints usage and exits `2`, `SiardCliUsageTest` goes green
- [x] 3.2 Implement `ArchiveCommand` options per design D3 (manual required/at-least-one-of validation in `call()` — declarative `required`/`ArgGroup` would hide unknown-option names), builds `ArchiveConfig`, resolves password per D4, calls `SiardFromDb.run(out, err)` — verify `ArchiveCommandParseTest` goes green: help `0`, unknown option `2`, missing `--jdbc-url` `2`, unsupported scheme `2`, neither output `2`
- [x] 3.3 Implement `RestoreCommand` with repeatable `--map-schema=old=new` (Map type, malformed value → exit `2`) — verify `RestoreCommandParseTest` goes green
- [x] 3.4 Implement password resolution order (value > prompt > `SIARD_PASSWORD` > error `2`) and password non-exposure — verify `PasswordResolutionTest` goes green

## 4. Output, logging, driver config

- [x] 4.1 Update `siard-cmd/src/main/resources/logback.xml`: remove console appender from root (file appender only) — verify a run produces no log lines on stdout
- [x] 4.2 Replace `ProgramInfo` startup banner usage: `--version` prints name/version/SIARD version only — verify `siard --version` output and that normal runs have no banner, `SiardCliOutputTest` goes green
- [x] 4.3 Fix `SiardConnection`: never write `jdbcdrivers.properties`; load `-D` override → `etc/jdbcdrivers.properties` if present → bundled defaults; fix `mysql_title` "SQL Server"→"MySQL" — verify `siard archive --help` works in a read-only dir and unit test covers the load order

## 5. Build scripts and dead code removal

- [x] 5.1 `siard-cmd/build.gradle.kts`: single `siard` start script (`mainClass = SiardCli`), remove `siard-from-db`/`siard-to-db` script tasks and `doc/manual`/`bin` from dist contents — verify `installDist` produces `bin/siard` that runs `--help`
- [x] 5.2 `siard-suite-app/build.gradle.kts`: replace `siardFromDb`/`siardToDb` `CreateStartScripts` tasks with a single `siard` script — verify app `installDist` produces `bin/siard`
- [x] 5.3 Delete `siard-cmd/bin/` and `siard-cmd/doc/manual/` — verify files gone and dist builds

## 6. Integration test migration

- [x] 6.1 Migrate all ~82 IT call sites to the `SiardCliTest.execute` helper: `new SiardFromDb/SiardToDb(String[])` → helper call; `-j:`→`--jdbc-url=`, `-u:`→`--user=`, `-p:`→`--password=`, `-s:`→`--siard-file=`, `-o`→`--overwrite`, `--schema:`→`--schema=`; `assertEquals(iRETURN_OK, x.getReturn())` → `assertEquals(0, exitCode)` — verify `grep -rn "new SiardFromDb\|new SiardToDb\|iRETURN_" siard-cmd/src` returns no hits
- [x] 6.2 Run `./gradlew :siard-cmd:test` and one DB integration suite (e.g., `integrationTestPostgres`) — verify all pass

## 7. Documentation and ADRs

- [x] 7.1 Rewrite CLI sections in `docs/user-manual/{en,de,fr,it}/user-manual.adoc` for `siard archive`/`restore`, new options, exit codes, password prompt — verify rendered docs contain no `-j=`/`-p=` syntax
- [x] 7.2 Write `docs/sad/decisions/0006-picocli-cli-framework.md` and `0007-read-only-driver-config.md` (0005 was already reserved for the web-companion spike)` per `docs/sad/decisions/README.md` format — verify ADRs follow existing template
- [x] 7.3 Update `siard-cmd/README.md` and root README references to old script names if present — verify no stale `siard-from-db`/`siard-to-db` references in docs

## 8. Final verification

- [x] 8.1 `./gradlew build` (all modules incl. `siard-suite-app`) — verify success
- [x] 8.2 Walk the spec: every scenario in `specs/siard-cli/spec.md` has a passing test or a documented manual check — verify checklist
- [x] 8.3 Self-review diff: modern style in new code, no Hungarian notation added, no `System.out/err`/`System.exit` outside `SiardCli.main` — verify `grep` on changed files
