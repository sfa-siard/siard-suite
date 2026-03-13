---
trigger: always
---

# Coding Conventions

## Language & style

- **Java 17** — use modern Java features (records, sealed classes, pattern matching, text blocks) where appropriate in new code
- The codebase has **two distinct coding styles**:
  - **Legacy code** (mostly in `siard-cmd`, `siard-api`, `siard-utilities`, `jdbc-*`): C-style braces, Hungarian notation prefixes (`_s` for String, `_i` for int, `_b` for boolean, `_file` for File, etc.), verbose Javadoc block headers with `/*====*/` separators. **Preserve this style when editing legacy files.**
  - **Modern code** (mostly in `siard-suite-app` and newer `siard-cmd` classes): Standard Java conventions, no Hungarian notation, clean formatting. **Use this style for new files.**
- When modifying an existing file, **always match the surrounding code style** — do not reformat or modernise legacy code without explicit intent

## Package structure

| Module | Base package |
|---|---|
| `siard-suite-app` | `ch.admin.bar.siardsuite` |
| `siard-cmd` | `ch.admin.bar.siard2.cmd` |
| `siard-api` | `ch.admin.bar.siard2.api` |
| `jdbc-*` | `ch.admin.bar.siard2.jdbc` (+ db-specific sub-packages) |
| `sql-parser` | `ch.enterag.sqlparser` |
| `siard-utilities` | `ch.enterag.utils` |
| `zip64-file` | `ch.enterag.zip64` |

## Lombok

Lombok is used project-wide. Prefer Lombok annotations over boilerplate:
- `@Slf4j` for logging (creates `log` field) — this is the standard logging pattern
- `@Data`, `@Value`, `@Builder`, `@Getter`, `@Setter` for POJOs
- `@NoArgsConstructor`, `@AllArgsConstructor`, `@RequiredArgsConstructor`
- `@val` for local variable type inference (used in legacy code)

## Logging

- Use **SLF4J via Lombok's `@Slf4j`** annotation — do not use `java.util.logging` or direct Logback imports
- Backend: Logback (`logback-classic`)
- Configuration files: `etc/logging.properties` and `etc/debug.properties` per module

## XML handling

- JAXB 2.3.x (javax namespace, not jakarta) for XML binding — the project is NOT on Jakarta EE
- Custom SAX parser factory required: `-Djavax.xml.parsers.SAXParserFactory=com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl`
- Multiple `--add-opens` JVM flags needed at runtime (see `build.gradle.kts` files)

## Testing

- New tests: **JUnit 5** (Jupiter) with `@Test`, `@BeforeEach`, etc.
- Legacy tests: JUnit 4 (run via Vintage engine) — don't convert unless necessary
- Integration tests go in `siard-cmd/src/integrationTest/java/` under a database-specific sub-package
- GUI tests use **TestFX** in headless mode (Monocle)
- Use **Testcontainers** for any test that needs a database — never depend on external running databases
- Use **AssertJ** for assertions in new test code

## Important constraints

- **SFA compatibility**: The app must always be launchable via `java -jar lib/siard-suite.jar` without any native executables. Never break this path.
- **Cross-platform**: All three platforms (Win/Mac/Linux) must be supported. JavaFX classifiers for all platforms are included.
- **Encoding**: Always UTF-8. Set explicitly in Gradle and in source files where relevant.
- **Vendored JARs**: Some JDBC drivers (`ojdbc7.jar`, `sqljdbc41.jar`, `db2jcc4.jar`) are committed in module `lib/` directories. Do not try to replace them with Maven dependencies.
