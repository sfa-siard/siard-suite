# Proposal

## Why

The `siard` CLI (modernized in `modernize-siard-cli`) currently ships only as a Gradle distribution zip that requires a pre-installed Java 21. That does not serve the two audiences that matter most: SFA staff, whose workstations are locked down so only installers packaged by BIT can be deployed (no Java is installed, and BIT forbids JavaFX in the CLI bundle, so the CLI cannot ride along inside the SIARD Suite GUI installer), and external users who should not need to install Java first. The CLI needs its own platform-native installers and runtime-bundled distributions, built and published on release.

## What Changes

- Add standalone, runtime-bundled CLI distributions for `siard-cmd`, all JavaFX-free:
  - jlink application-image zips per OS (Windows, Linux, macOS) with a `bin/siard` launcher and embedded JRE — unzip and run, no Java installation needed
  - Windows native installers: `.msi` and `.exe`
  - Linux native installers: `.deb` and `.rpm`
  - macOS installer: `.dmg`
- The bundled runtime contains only the JDK modules the CLI needs — no JavaFX, keeping the package deployable by BIT
- **BREAKING**: Remove the `siard`/`siard.bat` start script (`siardCli` `CreateStartScripts` task) from the `siard-suite-app` distribution — the CLI is now a standalone product, not a passenger inside the GUI installer
- Extend `deliverables.yml` so the new installers and image zips are built per-OS on release tags and uploaded as release artifacts
- Update documentation:
  - `release-guide.md`: replace the outdated SFA constraint (previously "staff can only run `java -jar`" — in fact no Java is installed; only BIT-packaged installers can be deployed) and add smoke tests for the new installers
  - `docs/user-manual/*/user-manual.adoc` CLI sections and `README.md` where they describe how to obtain/run the CLI
- Keep the plain `siard-cmd` dist zip/tar unchanged for users who already have Java 21

## Capabilities

### New Capabilities

- `siard-cli-distribution`: The platform-native, runtime-bundled distribution artifacts of the `siard` CLI — which artifacts are produced per release, that they run without a pre-installed JRE, contain no JavaFX, and expose a working `siard` executable.

### Modified Capabilities

(none — no existing spec-level CLI behavior changes; the removal of the script from the GUI distribution is a packaging change, not a change to documented `siard-cli` behavior)

## Impact

- **`siard-cmd/build.gradle.kts`**: apply beryx runtime plugin; jlink module list (no JavaFX), launcher config (console app), `jpackage` installer options per OS (msi+exe/deb+rpm/dmg)
- **`siard-suite-app/build.gradle.kts`**: remove `siardCli` task and its `dependsOn` wiring in `startScripts`/`installDist`
- **`.github/workflows/deliverables.yml`**: build/upload steps for CLI installers + image zips across the OS matrix; replace `if-no-files-found: ignore` on installer uploads with strict uploads so a missing artifact fails the release pipeline instead of passing silently
- **Docs**: `release-guide.md`, `docs/user-manual/*/user-manual.adoc`, `README.md`
- **Out of scope**: fat/shadow jar (rejected — serves no audience that the installers don't), GraalVM native image, package-manager channels (Homebrew/winget/scoop — possible follow-up), code signing/macOS notarization (dmg ships unsigned, same status as the GUI today)
