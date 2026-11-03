# Tasks

## 1. Engine extraction

- [ ] 1.1 Create `ArchiveConfig` and `RestoreConfig` records (validated parameters only, no I/O channels) in `siard-cmd` — verify they compile and a unit test constructs them
- [ ] 1.2 Refactor `SiardFromDb`: constructor takes `ArchiveConfig`, constructor logic moves to `int run(PrintWriter out, PrintWriter err)`; remove arg parsing, `System.exit`, `System.out/err`, `iRETURN_*` — verify `siard-cmd` compiles
- [ ] 1.3 Same refactor for `SiardToDb` with `RestoreConfig` (schema mapping becomes `Map<String,String>` in config) — verify `siard-cmd` compiles
- [ ] 1.4 Create `PasswordPrompter` interface + console-backed impl (no console → usage error) — verify unit test with injected fake prompter passes

## 2. CLI layer (picocli)

- [ ] 2.1 Add `info.picocli:picocli` to `gradle/libs.versions.toml` and `siard-cmd/build.gradle.kts` — verify dependency resolves
- [ ] 2.2 Create `SiardCli` (`@Command` with `archive`/`restore` subcommands, `--help`/`--version`, exit codes 0/1/2, `main` does `System.exit(execute(...))`) — verify `siard --help` prints usage and exits `0`, bare `siard` prints usage and exits `2`
- [ ] 2.3 Create `ArchiveCommand` with options per design D3 (`ArgGroup(multiplicity="1")` for `--siard-file`/`--export-xml`), builds `ArchiveConfig`, resolves password per D4, calls `SiardFromDb.run(out, err)` — verify spec scenarios: help `0`, unknown option `2`, missing `--jdbc-url` `2`, unsupported scheme `2`, neither output `2`
- [ ] 2.4 Create `RestoreCommand` with repeatable `--map-schema=old=new` (Map type, malformed value → exit `2`) — verify spec scenarios pass
- [ ] 2.5 Unit-test password resolution order (value > prompt > `SIARD_PASSWORD` > error `2`) and that the password never reaches stdout/stderr/log — verify tests pass

## 3. Output, logging, driver config

- [ ] 3.1 Update `siard-cmd/src/main/resources/logback.xml`: remove console appender from root (file appender only) — verify a run produces no log lines on stdout
- [ ] 3.2 Replace `ProgramInfo` startup banner usage: `--version` prints name/version/SIARD version only — verify `siard --version` output and that normal runs have no banner
- [ ] 3.3 Fix `SiardConnection`: never write `jdbcdrivers.properties`; load `-D` override → `etc/jdbcdrivers.properties` if present → bundled defaults; fix `mysql_title` "SQL Server"→"MySQL" — verify `siard archive --help` works in a read-only dir and unit test covers the load order

## 4. Build scripts and dead code removal

- [ ] 4.1 `siard-cmd/build.gradle.kts`: single `siard` start script (`mainClass = SiardCli`), remove `siard-from-db`/`siard-to-db` script tasks and `doc/manual`/`bin` from dist contents — verify `installDist` produces `bin/siard` that runs `--help`
- [ ] 4.2 `siard-suite-app/build.gradle.kts`: replace `siardFromDb`/`siardToDb` `CreateStartScripts` tasks with a single `siard` script — verify app `installDist` produces `bin/siard`
- [ ] 4.3 Delete `siard-cmd/bin/` and `siard-cmd/doc/manual/` — verify files gone and dist builds

## 5. Integration test migration

- [ ] 5.1 Add test helper (e.g., `SiardCliTest.execute(subcommand, args...)` → exit code) in `siard-cmd` test sources — verify helper works in one migrated test
- [ ] 5.2 Migrate all ~82 IT call sites: `new SiardFromDb/SiardToDb(String[])` → helper call; `-j:`→`--jdbc-url=`, `-u:`→`--user=`, `-p:`→`--password=`, `-s:`→`--siard-file=`, `-o`→`--overwrite`, `--schema:`→`--schema=`; `assertEquals(iRETURN_OK, x.getReturn())` → `assertEquals(0, exitCode)` — verify `grep -rn "new SiardFromDb\|new SiardToDb\|iRETURN_" siard-cmd/src` returns no hits
- [ ] 5.3 Run `./gradlew :siard-cmd:test` and one DB integration suite (e.g., `integrationTestPostgres`) — verify all pass

## 6. Documentation and ADRs

- [ ] 6.1 Rewrite CLI sections in `docs/user-manual/{en,de,fr,it}/user-manual.adoc` for `siard archive`/`restore`, new options, exit codes, password prompt — verify rendered docs contain no `-j=`/`-p=` syntax
- [ ] 6.2 Write `docs/sad/decisions/0005-picocli-cli-framework.md` and `0006-read-only-driver-config.md` per `docs/sad/decisions/README.md` format — verify ADRs follow existing template
- [ ] 6.3 Update `siard-cmd/README.md` and root README references to old script names if present — verify no stale `siard-from-db`/`siard-to-db` references in docs

## 7. Final verification

- [ ] 7.1 `./gradlew build` (all modules incl. `siard-suite-app`) — verify success
- [ ] 7.2 Walk the spec: every scenario in `specs/siard-cli/spec.md` has a passing test or a documented manual check — verify checklist
- [ ] 7.3 Self-review diff: modern style in new code, no Hungarian notation added, no `System.out/err`/`System.exit` outside `SiardCli.main` — verify `grep` on changed files
