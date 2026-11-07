# siard-cli-distribution Specification

## Purpose

Defines the standalone, runtime-bundled distribution artifacts published for the `siard` CLI: which artifacts exist per release and the guarantees they give users — no separately installed Java required, no JavaFX in the bundle, and a working `siard` executable on each supported OS.

## Requirements

### Requirement: Runtime-bundled application images per OS

Each release SHALL publish an application-image archive for each supported OS (Windows, Linux, macOS) containing a `siard` launcher and an embedded Java runtime, so the CLI runs on a machine with no Java installed.

#### Scenario: Run CLI from unpacked image without system Java

- **WHEN** a user unpacks the image archive for their OS on a machine with no Java installed and runs `bin/siard --version`
- **THEN** the program name, application version and supported SIARD version are printed and the exit code is `0`

#### Scenario: Image CLI reports usage error for unknown subcommand

- **WHEN** a user runs `bin/siard frobnicate` from an unpacked image
- **THEN** an error naming the unknown subcommand is printed to standard error and the exit code is `2`

#### Scenario: Run image from a read-only location

- **WHEN** a user runs `bin/siard --help` from an unpacked image whose directory is not writable
- **THEN** usage help is printed to standard output, the exit code is `0`, and the CLI does not attempt to write into its installation directory

### Requirement: Native installers per OS

Each release SHALL publish native installer artifacts for the CLI: `.msi` and `.exe` for Windows, `.deb` and `.rpm` for Linux, and `.dmg` for macOS.

#### Scenario: All installer artifacts are produced for a release

- **WHEN** the release build pipeline completes for a release version
- **THEN** files matching `siard-cli-<version>-win.msi`, `siard-cli-<version>-win.exe`, `siard-cli-<version>-linux.deb`, `siard-cli-<version>-linux.rpm` and `siard-cli-<version>-mac.dmg` exist in the release output

#### Scenario: CLI works after installation via native installer

- **WHEN** a user installs the CLI on a supported OS using the native installer and runs `siard --version` from the installed location
- **THEN** the program name, application version and supported SIARD version are printed and the exit code is `0`

#### Scenario: Installer rejects an incompatible OS

- **WHEN** a user attempts to install a CLI installer built for a different OS or architecture
- **THEN** the installation fails with an error indicating the package is not supported on this system and no `siard` launcher is left usable

#### Scenario: Windows system-wide install requires elevation

- **WHEN** a user runs the Windows installer without administrator privileges
- **THEN** the installer reports that elevated privileges are required and no files are installed

### Requirement: Bundled runtime contains no JavaFX

The embedded Java runtime in every CLI distribution artifact SHALL NOT contain JavaFX modules, so the package satisfies the BIT deployment policy that forbids JavaFX in the CLI bundle.

#### Scenario: No JavaFX modules in bundled runtime

- **WHEN** the module list of the embedded runtime in any CLI image or installer is inspected
- **THEN** no module whose name starts with `javafx.` is present

### Requirement: CLI behavior identical across artifacts

The `siard` executable exposed by every image and installer SHALL provide the same subcommands, options and exit codes as documented in the `siard-cli` capability.

#### Scenario: Help works from an installed artifact

- **WHEN** a user runs `siard archive --help` from a distribution artifact
- **THEN** usage help for `archive` is printed to standard output and the exit code is `0`

#### Scenario: Unknown option rejected from an installed artifact

- **WHEN** a user runs `siard archive --jdbcurl=x` from a distribution artifact
- **THEN** an "unknown option" error identifying `--jdbcurl` is printed to standard error and the exit code is `2`

### Requirement: CLI is not shipped inside the SIARD Suite application distribution

The SIARD Suite desktop application distribution SHALL NOT contain a `siard` or `siard.bat` launcher; the CLI is distributed only through its own artifacts.

#### Scenario: No siard launcher in the application distribution

- **WHEN** a user unpacks or installs the SIARD Suite application distribution
- **THEN** no `siard` or `siard.bat` launcher exists in its `bin` directory
