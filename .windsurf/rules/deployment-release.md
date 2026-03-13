---
trigger: always
description: 
globs: 
---

# Deployment & Release

## Versioning

- Versions follow the SIARD format version: **2.2.x** (e.g. `2.2.135`)
- Managed by the **Axion Release Plugin** — version is derived from Git tags, not hardcoded
- Check current version: `./gradlew currentVersion`
- Never manually set the version in build files

## Release process

1. Ensure all changes are on `main` and CI is green
2. Run smoke tests (see below)
3. Create a release: `./gradlew release` — this creates a Git tag (`v2.2.x`) and pushes it
4. The tag push triggers the **deliverables** GitHub Actions workflow, which builds:
   - Distribution ZIP (for SFA staff: `java -jar` usage)
   - `siard-cmd` distributions (CLI tools)
   - User manual PDF
   - Native installers for Linux (DEB/RPM), macOS (DMG), Windows (EXE/MSI)
   - Runtime image ZIPs per platform
5. Official GitHub releases are created **manually by BAR** (not automated)

## CI/CD (GitHub Actions)

| Workflow | Trigger | Purpose |
|---|---|---|
| `build.yml` | Push to `main`, `feature/*`, `issue/*`, `doc/*`; PRs to `main` | Compile + unit tests (matrix per module) |
| `integration-tests.yml` | Push/PR to `main`; manual dispatch | Integration tests per database (matrix) |
| `windows-build.yml` | PRs to `main` | Windows-specific build verification |
| `deliverables.yml` | Tag push (`v*.**`) | Build all distributable artifacts |
| `issues.yml` | Issue opened | Auto-add issues to GitHub project board |

### CI environment

- Runner: `ubuntu-latest` (+ `macOS-latest`, `windows-latest` for native packages)
- JDK: Azul Zulu 17 with JavaFX (`jdk+fx`)
- Timezone: `Europe/Zurich`
- GUI tests run headless: `-Djava.awt.headless=true -Dtestfx.headless=true`

## Git branching conventions

- `main` — stable branch, target for PRs
- `feature/*` — feature branches
- `issue/*` — issue/bugfix branches
- `doc/*` — documentation-only branches

## Smoke testing before release

Before releasing, follow the steps in `release-guide.md`:

1. Build: `./gradlew clean jpackage`
2. Start test databases: `docker compose up -d --build` (from `docker/`)
3. Test SFA environment: `cd build/install/SIARD-Suite/ && java -jar lib/siard-suite-2.2.*.jar`
4. Test packaged distribution: `cd build/jpackage/SIARD-Suite/ && ./bin/SIARD-Suite`
5. Verify: app starts, can archive a simple database, can open the archive

### Test database credentials

| Database | URL | User | Password |
|---|---|---|---|
| MS SQL Server | `jdbc:sqlserver://localhost:1433;databaseName=siard` | `sa` | `Yukon900` |
| Oracle | `jdbc:oracle:thin:@localhost:1521/siard` | `siard` | `siard` |
| MS Access | File-based: `docker/msaccess/*.accdb` | — | — |

## Docker

- `docker/docker-compose.yaml` provides local test databases (MSSQL, Oracle, Postgres, MariaDB)
- Integration tests use **Testcontainers** (spin up containers automatically, no manual Docker setup needed)
- Docker is required for running any tests