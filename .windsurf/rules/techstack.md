---
trigger: always
description: 
globs: 
---

# Tech Stack

## Language & Runtime

- **Java 17** (Azul Zulu JDK FX distribution — JavaFX bundled)
- Source encoding: UTF-8
- Local dev: managed via `asdf` (see `.tool-versions`). JAVA_HOME must point to a JDK 17 with JavaFX.

## Build System

- **Gradle** with Kotlin DSL (`build.gradle.kts`)
- Gradle wrapper checked in (`./gradlew`)
- **Axion Release Plugin** (`pl.allegro.tech.build.axion-release`) for Git-tag-based versioning
- **Foojay Toolchains** plugin for automatic JDK provisioning in CI

### Important Gradle commands

| Command | Purpose |
|---|---|
| `./gradlew clean build -x test` | Build all modules (skip tests) |
| `./gradlew :siard-suite-app:run` | Run the GUI application |
| `./gradlew :siard-cmd:installDist` | Build CLI distribution |
| `./gradlew :module:test` | Run unit tests for a module |
| `./gradlew :siard-cmd:integrationTest` | Run all integration tests |
| `./gradlew :siard-cmd:integrationTestPostgres` | Run Postgres integration tests only |
| `./gradlew jpackage` | Create native platform installers |
| `./gradlew currentVersion` | Show current version from Git tags |

### Local JAVA_HOME override

On this machine, always prefix Gradle commands with:
```bash
JAVA_HOME=/home/mburri/.asdf/installs/java/zulu-javafx-17.58.21
```

## GUI Framework

- **JavaFX 17.0.7** (cross-platform: win/mac/linux classifiers all included)
- **MaterialFX 11.13.5** for modern Material Design UI components
- **TestFX** for headless GUI testing (with Monocle)
- FXML-based views

## Key Libraries

| Library | Purpose |
|---|---|
| Lombok | Boilerplate reduction (`@Data`, `@Builder`, etc.) |
| JAXB (javax.xml.bind 2.3.x) | XML binding for SIARD metadata schemas |
| ANTLR 4.5.2 | SQL parsing |
| Woodstox | StAX XML processing |
| Apache Tika | MIME type detection for binary data |
| Logback | Logging (SLF4J backend) |
| Apache Commons Text | String utilities |
| Jsoup | HTML pretty-printing for export |

## Database Drivers

| Database | Driver |
|---|---|
| PostgreSQL | `org.postgresql:postgresql:42.2.5` |
| MySQL | `com.mysql:mysql-connector-j:8.3.0` |
| MariaDB | `org.mariadb.jdbc:mariadb-java-client:2.7.4` (tests only) |
| MS SQL Server | `sqljdbc41.jar` (vendored in `jdbc-mssql/lib/`) |
| Oracle | `ojdbc7.jar` (vendored in `jdbc-oracle/lib/`) |
| DB2 | `db2jcc4.jar` (vendored in `jdbc-db2/lib/`) |

Note: some JDBC drivers are vendored as JAR files in module `lib/` directories because they are not available on Maven Central.

## Testing

- **JUnit 5** (Jupiter) — primary test framework
- **JUnit 4** (Vintage engine) — some legacy tests remain
- **Testcontainers** — Docker-based integration tests for all supported databases
- **Mockito** — mocking
- **AssertJ** — fluent assertions (siard-cmd)
- **Docker required** for all tests (unit tests in jdbc-* modules also use Testcontainers)

### Integration test structure

Integration tests live in `siard-cmd/src/integrationTest/java/` with a separate source set. Database-specific tests are in sub-packages (`postgres`, `mysql`, `mssql`, `oracle`, `db2`, `msaccess`, `mariadb`).

## Documentation Tooling

- **Asciidoctor** (Gradle plugins: `jvm.convert`, `jvm.pdf`) for user manual generation
- **PlantUML** (via `com.cosminpolifronie.gradle.plantuml`) for architecture diagrams
- Custom PDF theme: `docs/theme/siard-theme.yml`

## Packaging

- **org.beryx.runtime** plugin for jlink/jpackage — creates platform-specific installers (DMG, EXE/MSI, DEB/RPM) and runtime images with bundled JRE
- Distribution ZIP also created for SFA staff who run via `java -jar`