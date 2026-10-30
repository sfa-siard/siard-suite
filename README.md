# SIARD Suite 2.2

[![Build](https://github.com/sfa-siard/siard-suite/actions/workflows/build.yml/badge.svg)](https://github.com/sfa-siard/siard-suite/actions/workflows/build.yml)
[![Integration Tests](https://github.com/sfa-siard/siard-suite/actions/workflows/integration-tests.yml/badge.svg)](https://github.com/sfa-siard/siard-suite/actions/workflows/integration-tests.yml)
[![License: CDDL-1.0](https://img.shields.io/badge/License-CDDL--1.0-blue.svg)](LICENSE.txt)
[![GitHub release](https://img.shields.io/github/v/release/sfa-siard/siard-suite)](https://github.com/sfa-siard/siard-suite/releases)

## About

SIARD Suite is a comprehensive toolset for archiving relational databases in the SIARD (Software-Independent Archival of Relational Databases) format. The suite ensures long-term preservation and accessibility of database contents, independent of the original database management system.

**Key Features:**
- **Graphical Interface**: User-friendly GUI for archiving, restoring, searching, and exporting databases
- **Command-Line Tools**: Powerful CLI for automated workflows and scripting
- **Multi-Database Support**: Compatible with PostgreSQL, MySQL, Oracle, MS SQL Server, DB2, and MS Access
- **SIARD Format 2.2**: Compliant with the latest SIARD standard for database archival

**Project Structure:**
This is a Gradle-based monorepo containing multiple modules:
- `siard-suite-app` - Desktop application with GUI
- `siard-cmd` - Command-line interface
- `siard-api` - Core API for reading/writing SIARD files
- `jdbc-*` - Database-specific JDBC wrappers
- Supporting libraries and utilities
## Declaration

Contributions to the codebase have been and will be made with the support of AI-assisted
coding tools. All contributions are reviewed by human maintainers before
merging, and contributors remain responsible for the code they submit.

When AI tools meaningfully contribute to a change, this is should be disclosed via a
`Co-Authored-By:` or `Assisted-By:` trailer in the commit message.


## Developer Setup

Java 21 with JavaFX is required. Download it here:
- [Azul Zulu JDK 21 with FX](https://www.azul.com/downloads/?version=java-21-lts&package=jdk-fx#zulu)

- or use [mise](https://mise.jdx.dev/):
```shell
mise install
```

[Docker](https://docs.docker.com/engine/install/) is required for running integration tests because they use [Testcontainers](https://www.testcontainers.org/)).

### Running the Application

Note: There is a [justfile](./justfile) that provides convenient aliases for common tasks.

**GUI Application:**
```shell
./gradlew :siard-suite-app:run
```

**Command-Line Interface:**

First, build the distribution:
```shell
./gradlew :siard-cmd:installDist
```

Then run the CLI tools:
```shell
# Download database to SIARD archive
./siard-cmd/build/install/siard-cmd/bin/siard-from-db <arguments>

# Upload SIARD archive to database
./siard-cmd/build/install/siard-cmd/bin/siard-to-db <arguments>
```



### Building the Project

**Build all modules:**
```shell
./gradlew clean build -x test
```
Note: running all tests takes quite long!

**Build specific module:**
```shell
./gradlew :module-name:build
```

The build creates distributions in `build/distributions/` with executable scripts and all dependencies.

### Running Tests

**Run all tests:**
```shell
./gradlew test
```

**Run tests for a specific module:**
```shell
./gradlew :module-name:test
```

Examples:
```shell
./gradlew :siard-api:test           # Core API tests
./gradlew :jdbc-postgres:test       # PostgreSQL wrapper tests
./gradlew :siard-cmd:test           # CLI tests
```

**Integration tests (siard-cmd):**
```shell
./gradlew :siard-cmd:integrationTest                    # All integration tests
./gradlew :siard-cmd:integrationTestPostgres            # PostgreSQL only
./gradlew :siard-cmd:integrationTestMysql               # MySQL only
```

**Note:** all tests require Docker to be running (uses [Testcontainers](https://www.testcontainers.org/)).

### Dependency Analysis

The project uses the [Dependency Analysis Gradle Plugin](https://github.com/autonomousapps/dependency-analysis-gradle-plugin) to detect dependency issues.

**Analyze all dependencies:**
```shell
./gradlew buildHealth
```

This generates a report showing:
- **Unused dependencies**: Dependencies declared but not actually used in code
- **Used transitive dependencies**: Dependencies used in code but not explicitly declared
- **Misused dependencies**: Dependencies that should use a different configuration (e.g., `api` instead of `implementation`)

**View the report:**
```shell
# Console output shows summary
./gradlew buildHealth

# Detailed HTML report
open build/reports/dependency-analysis/build-health-report.html
```

**Fix issues automatically (where possible):**
```shell
./gradlew buildHealth --auto-correct
```

**Analyze a specific module:**
```shell
./gradlew :module-name:buildHealth
```

**Note:** The plugin may suggest removing dependencies that are actually needed as transitive dependencies. Always verify suggestions before applying them.

### Creating Platform-Specific Packages

**Create application image:**
```shell
./gradlew jpackageImage
```
The image will be available at `./build/jpackage/siard-suite` (OS-specific).

**Create installer:**
```shell
./gradlew jpackage
```
Creates platform-specific installers (DMG, EXE, MSI, DEB, or RPM).

**Ubuntu users:** Install `alien` if RPM building fails:
```shell
sudo apt install alien
```

## Versioning and Releases

⚠️ **Read the [release guide](release-guide.md) before creating releases!**

Versions follow the SIARD format version (2.2.x) and are managed with the [Axion Release Plugin](https://github.com/allegro/axion-release-plugin).

**Check current version:**
```shell
./gradlew currentVersion
```

**Create a release:**
```shell
./gradlew release
```
This creates a tag and pushes it to remote, triggering GitHub Actions to build deliverables.

**Note:** Official GitHub releases must be created manually by BAR.

## Documentation

Documentation is written in [AsciiDoc](https://asciidoctor.org/) and bundled with the application.

**Generate PDF documentation:**
```shell
./gradlew :siard-suite-app:asciidoctorPdf
```

**Available Documentation:**
- [User Manual](docs/user-manual/en/user-manual.adoc)
- [Software Architecture Document](docs/sad/software-architecture-documentation.md)


** Undertand Anything

see: https://understand-anything.com/

Run it locally, use 
```
/understand-dashboard 
```

or faster and without using tokens:

```
cd ~/.understand-anything-plugin/packages/dashboard                                                                                                                                                                                                                            
GRAPH_DIR=<siard-project-root> npx vite --host 127.0.0.1
```

##  Code Formatting & Git Blame

We recently performed a bulk reformatting of the entire Java codebase to standardize our style. To avoid seeing "style-only" changes when using git blame, we use a .git-blame-ignore-revs file.
This file contains the commit hashes of the formatting commits. Github will automatically use this file and does not show the formatting commits in the blame.

To ignore these formatting commits in your local terminal or IDE, run the following command once in the project root:

```bash
git config blame.ignoreRevsFile .git-blame-ignore-revs
```
