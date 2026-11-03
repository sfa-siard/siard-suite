# Tasks

## 1. Runtime image for siard-cmd

- [ ] 1.1 Apply the beryx `runtime` plugin in `siard-cmd/build.gradle.kts` with `additive = true`, `launcher { noConsole = false }`, `jvmArgs` including `xercesSaxParserFactory`, and the same `options` set as `siard-suite-app`; verify `./gradlew :siard-cmd:runtime` produces an image and its `bin/siard --version` prints program name, app version and SIARD version with exit code `0`
- [ ] 1.2 Derive the jlink `modules` list via `jdeps` on the siard-cmd runtime classpath (no `javafx.*` modules — spec requirement); verify the embedded runtime's `java --list-modules` output contains no module starting with `javafx.`
- [ ] 1.3 Smoke-test the image end to end: `bin/siard archive --help` exits `0` with help on stdout, `bin/siard frobnicate` exits `2` with the unknown-subcommand error on stderr, `bin/siard --help` exits `0` when the image directory is read-only (no writes into the install dir), and (if a testcontainer DB is available) a real `archive` run exits `0`
- [ ] 1.4 Configure per-OS `imageZip`/`imageDir` names (`siard-cli-<version>-<os>.zip`) mirroring the app's OS-conditional block; verify `./gradlew :siard-cmd:runtimeZip` produces the zip containing `bin/siard`/`siard.bat`

## 2. jpackage installers for siard-cmd

- [ ] 2.1 Add the `jpackage { }` block for siard-cmd: `imageName`/`installerName` `siard-cli`, `appVersion` from the project version, vendor `BAR`, icon **copied** into `siard-cmd` resources (not referenced from `siard-suite-app` — that would invert the module direction); verify `./gradlew :siard-cmd:jpackageImage` produces an app image containing a `siard` launcher that runs `--version` with exit code `0` and has no JavaFX modules
- [ ] 2.2 Configure per-OS installer options — Windows `msi`+`exe` (system-wide install, no `--win-per-user-install`), Linux `deb`+`rpm`, macOS `dmg`; wire `installerType` to a `-PinstallerType=` project property with a per-OS default so a second `jpackage` invocation produces the second type on the same app image; verify `./gradlew :siard-cmd:jpackage` and `./gradlew :siard-cmd:jpackage -PinstallerType=<other>` each produce their installer on a matching OS
- [ ] 2.3 Verify installer output locally where possible (`.deb`/`.rpm` on Linux) and record the Windows/macOS verification as release-guide smoke steps; each produced artifact carries the release version in its name

## 3. Remove the CLI from the GUI distribution

- [ ] 3.1 Delete the `siardCli` `CreateStartScripts` task and its `dependsOn` wiring in `siard-suite-app/build.gradle.kts` (`startScripts`, `installDist`); verify `./gradlew :siard-suite-app:installDist` produces a `bin/` directory containing no `siard`/`siard.bat` launcher
- [ ] 3.2 Keep the `implementation(project(":siard-cmd"))` dependency (the app uses siard-cmd engine classes); verify `./gradlew :siard-suite-app:build -x test` compiles and `:siard-suite-app:test` passes

## 4. Release workflow

- [ ] 4.1 Extend `.github/workflows/deliverables.yml`: in the existing win/mac/linux matrix add `:siard-cmd:runtimeZip jpackage` steps plus the `-PinstallerType=` second invocation on Windows/Linux, and qualify the existing unqualified `runtimeZip jpackage` step to `:siard-suite-app:` so it doesn't fire for both modules; verify the YAML parses and the steps match the app's job pattern
- [ ] 4.2 Add `rpm`/`rpmbuild` setup on `ubuntu-latest` (WiX/Inno are already present — the GUI MSI builds today); change all installer upload steps — existing GUI ones included — from `if-no-files-found: ignore` to strict so a missing artifact fails the pipeline; verify via a workflow run (test tag or PR trigger) that `.msi`, `.exe`, `.deb`, `.rpm`, `.dmg` artifacts are produced
- [ ] 4.3 Add upload-artifact steps for all CLI installers and image zips under `siard-cli-*` names; verify a test-tag release shows every artifact from spec "All installer artifacts are produced for a release"

## 5. Documentation

- [ ] 5.1 Correct the SFA constraint in `release-guide.md`: SFA workstations have no Java installed, users cannot download/run binaries, only BIT-packaged installers are deployed, and BIT forbids JavaFX in the CLI bundle; verify the `java -jar` CLI smoke-test steps are replaced by installer/image-zip smoke steps (check whether the GUI `java -jar` path is still accurate for the GUI)
- [ ] 5.2 Update the CLI sections of `docs/user-manual/*/user-manual.adoc` (en/de/fr/it) to describe obtaining and installing the standalone CLI (native installer or image zip) instead of running a script bundled with the GUI, documenting the install location of the `siard` launcher per OS (launchers are not added to PATH); verify `./gradlew :siard-suite-app:asciidoctorPdf` still renders
- [ ] 5.3 Update `README.md` and `justfile` where they reference the GUI-bundled CLI script or dev build (`just cli-build` etc.) to match the new packaging tasks; verify the referenced commands exist
- [ ] 5.4 Write ADR `0008-standalone-cli-distribution.md` in `docs/sad/decisions/` recording the standalone-installer decision (no JavaFX, GUI no longer ships the CLI, jpackage over fat jar/native-image); verify it follows the format of existing ADRs

## 6. Verification

- [ ] 6.1 Run `./gradlew build -x integrationTest` and the unit test suite; verify all modules compile and tests pass
- [ ] 6.2 Inspect a built `siard-cmd` distribution and a `siard-suite-app` distribution side by side: CLI artifacts contain a working `siard` launcher, the app `bin/` contains none — matching every scenario in `specs/siard-cli-distribution/spec.md`
- [ ] 6.3 Self-review the diff: no unrelated changes, release-notes-worthy items listed (BREAKING: CLI removed from GUI distribution; new artifacts)
