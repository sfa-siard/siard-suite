# SIARD Suite 2.2

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

## Developer Setup

### Prerequisites

**Java 17 with JavaFX** is required. Download from:
- [Azul Zulu JDK 17 with FX](https://www.azul.com/downloads/?version=java-17-lts&package=jdk-fx#zulu)

**For [asdf](https://asdf-vm.com/) users:**
```shell
asdf install
```

**Docker** is required for running integration tests (uses [Testcontainers](https://www.testcontainers.org/)).

### Running the Application

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
- [Software Architecture Document](docs/sad/sad.adoc)

## Declaration
Contributions to the codebase have been made with the support of Windsurf. Windsurf is AI-powered code completion tool, that is trained exclusively on natural language and source code data with [permissive licenses](https://windsurf.com/blog/copilot-trains-on-gpl-codeium-does-not). 




