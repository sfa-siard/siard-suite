# Siard-Suite Refactoring Plan

## Current State Assessment

### What's good
- **Module structure** is reasonable: clear separation into `siard-api`, `siard-cmd`, `siard-suite-app`, `siard-utilities`, `sql-parser`, `zip64-file`, and per-DB JDBC modules
- **Build tooling** is modern: Gradle Kotlin DSL, Java 17 toolchain, Testcontainers for integration tests
- **siard-suite-app** already has a reasonably clean architecture with a `ServicesFacade`, `Navigator`, `Dialogs`, `Presenter` pattern, Lombok usage, and i18n support
- **Integration test coverage** is extensive across multiple DB vendors

### Key Problems Identified

#### 1. God-constructor anti-pattern (`siard-cmd`)
`SiardFromDb` and `SiardToDb` do **everything** in the constructor: parse args, validate, open connections, transfer data, close resources. The constructor is 100+ lines of deeply nested imperative logic. This makes the classes untestable and impossible to compose.

#### 2. Massive procedural classes (`siard-cmd`)
`MetaDataFromDb` is **1284 lines** of sequential `getXxx()` private methods that directly call JDBC `DatabaseMetaData`, mutating state as they go. `MetaDataToDb` is **817 lines** of the same pattern in reverse. There's heavy code duplication between `getProcedureParameters()` / `getFunctionParameters()`, `checkMetaTable()` / `checkMetaView()` / `checkMetaType()` / `checkMetaRoutine()`, etc.

#### 3. Hungarian notation & archaic naming
Pervasive use of prefixes: `_s` (String), `_i` (int), `_b` (boolean), `_l` (long), `_map`, `_file`, `_pi`, `_sc`, etc. Method-end comments like `} /* getParameters */`, section-separator comments (`/*====*/`). Single-letter variable names (`mt`, `ms`, `mc`, `mr`, `mv`).

#### 4. No proper error handling model
Errors are communicated via `System.out.println` / `System.err.println` and integer return codes (`iRETURN_OK = 0`, `iRETURN_WARNING = 4`). No exceptions are used for validation failures. The console output and the logging are redundant and mixed.

#### 5. `siard-api` implementation coupling
Implementation classes like `MetaSchemaImpl`, `MetaTableImpl`, `MetaColumnImpl` are routinely downcast from their interfaces (e.g., `((MetaColumnImpl) mc).getColumnType()`) in `siard-cmd`. This defeats the purpose of the interface/impl separation.

#### 6. Lack of unit tests
`siard-cmd` has only **5 unit tests**. The heavy procedural style with all logic in constructors and private methods makes unit testing nearly impossible without refactoring.

#### 7. `siard-utilities` — opaque abbreviation classes
Classes like `EU`, `SU`, `DU`, `BU`, `FU`, `TZ` are cryptic single/two-letter utility classes with unclear responsibilities. Their usage is scattered everywhere.

#### 8. Inconsistent code formatting
`SiardToDb` uses old C-style brace placement and tabs; `SiardFromDb` and newer code uses Java-standard formatting. Mixed within the same module.

---

## Refactoring Plan — Ordered by Priority

### Phase 1: Foundation (Low Risk, High Value)

**Step 1.1 — Enforce consistent formatting**
- Apply a single code formatter (e.g., `google-java-format` or Palantir) across the whole project via a Gradle plugin
- Remove all section-separator comments (`/*====*/`, `/*----*/`) and method-end comments (`} /* methodName */`)
- This is a one-time mechanical change — creates a clean baseline

**Step 1.2 — Rename Hungarian-notation fields & variables**
- Systematically remove `_s`, `_i`, `_b`, `_l`, `_m` prefixes from fields
- Rename single-letter abbreviations (e.g., `_dmd` → `databaseMetaData`, `_md` → `metaData`, `mt` → `metaTable`)
- Use IDE rename refactoring to keep things safe, module by module

**Step 1.3 — Introduce SLF4J logging consistently, remove `System.out/err`**
- Replace all `System.out.println` / `System.err.println` with proper `log.info` / `log.warn` / `log.error` calls
- Some classes already use `@Slf4j` — extend this to all classes
- Introduce a dedicated output channel (e.g., a `ConsoleReporter` interface) for CLI user-facing output, separate from logging

### Phase 2: Structural Refactoring of `siard-cmd` (Medium Risk, High Value)

**Step 2.1 — Extract CLI argument parsing into dedicated classes**
- Create a `SiardFromDbConfig` and `SiardToDbConfig` value class (record or Lombok `@Value`) that holds all parsed parameters
- Create an `ArgumentParser` that produces these config objects, with proper validation and error reporting
- This decouples argument parsing from business logic

**Step 2.2 — Extract constructor logic into orchestrator methods**
- Move the logic out of `SiardFromDb()` and `SiardToDb()` constructors into explicit `execute()` / `run()` methods
- Separate concerns: connection management, archive creation, metadata transfer, primary data transfer
- Use try-with-resources for `Connection` and `Archive` (or introduce `AutoCloseable` wrappers)

**Step 2.3 — Break up `MetaDataFromDb` into smaller collaborators**
- Extract logical groups of methods into focused classes:
  - `TableMetaDataReader` — columns, primary keys, foreign keys, unique keys, row counts
  - `ViewMetaDataReader` — views and view columns
  - `RoutineMetaDataReader` — procedures, functions, parameters
  - `TypeMetaDataReader` — UDTs, distinct types, attributes
  - `GlobalMetaDataReader` — users, roles, privileges
- Each reader takes `DatabaseMetaData` + the relevant `Meta*` target and populates it
- Same approach for `MetaDataToDb`

**Step 2.4 — Eliminate code duplication**
- `getProcedureParameters()` and `getFunctionParameters()` are nearly identical → extract a common `ParameterReader`
- `checkMetaTable()`, `checkMetaView()`, `checkMetaType()`, `checkMetaRoutine()` share the same pattern → generalize with a `MetaDataValidator` taking a strategy/lambda

### Phase 3: Improve `siard-api` Design (Medium Risk, Medium Value)

**Step 3.1 — Eliminate downcasting from interfaces to impls**
- Audit all `((MetaColumnImpl) mc).getColumnType()` style casts in `siard-cmd`
- Either expose the needed methods on the interface, or introduce a richer internal API that `siard-cmd` uses directly
- This is critical for testability — you can't mock interfaces if callers immediately downcast

**Step 3.2 — Modernize collection access patterns**
- Replace index-based iteration (`for (int i = 0; i < ms.getMetaTables(); i++)`) with proper `Iterable`/`List` returns
- e.g., `MetaSchema.getMetaTables()` returning `int` + `getMetaTable(int)` → add `List<MetaTable> metaTables()` or `Stream<MetaTable> metaTables()`
- Keep old methods as `@Deprecated` initially for backward compatibility

**Step 3.3 — Replace `IOException` for business errors**
- Many places throw `IOException` for business logic violations ("Table name must be unique within schema!"). Introduce domain exceptions (e.g., `SiardValidationException`, `DuplicateNameException`)

### Phase 4: Improve Testability (Low Risk, High Value)

**Step 4.1 — Add unit tests for the extracted classes from Phase 2**
- Each new `*Reader`, `*Config`, `*Validator` class should be testable with mocked `DatabaseMetaData` / `MetaData`
- Target: meaningful coverage for `siard-cmd` core logic (currently near zero)

**Step 4.2 — Introduce test fixtures / builders for `siard-api` objects**
- Creating test data for `MetaSchema`, `MetaTable`, etc. is currently very hard due to the JAXB-backed impl classes
- Create builder utilities or test factories (could live in `testFixtures` source set which `jdbc-base` already uses)

### Phase 5: Modernize `siard-utilities` (Low Risk, Low–Medium Value)

**Step 5.1 — Rename or deprecate cryptic utility classes**
- `EU` → `ExceptionUtils`, `SU` → `StringUtils` (or replace with Apache Commons / Guava where possible), `DU` → `DateUtils`, `BU` → `ByteUtils`, `FU` → `FileUtils`
- Introduce deprecations pointing to the new names, migrate callers module-by-module

**Step 5.2 — Evaluate which utilities can be replaced by standard library / well-known libs**
- Java 17 has many features that make some of these utils unnecessary (e.g., `String.isBlank()`, `Files.*`, `HexFormat`)

### Phase 6: Minor Improvements (Ongoing)

**Step 6.1 — Adopt records / sealed classes where appropriate**
- Config / value types → Java records
- Type hierarchies that are closed → sealed interfaces

**Step 6.2 — Migrate remaining JUnit 4 tests to JUnit 5**
- `siard-cmd` still uses `junit:junit:4.13.2` alongside JUnit 5. Unify.

**Step 6.3 — Consider a proper CLI framework**
- Replace the hand-rolled `Arguments` class with picocli or JCommander for `SiardFromDb` / `SiardToDb` — would give argument parsing, validation, help generation, and shell completion for free.

---

## Suggested Execution Order

| Order | Step | Risk | Effort | Value | Done |
|-------|------|------|--------|-------|------|
| 1 | 1.1 Formatting | Low | Small | High (baseline) | ✅ |
| 2 | 1.3 Logging cleanup | Low | Medium | High | |
| 3 | 2.1 Extract CLI config | Low | Medium | High | |
| 4 | 2.2 Extract constructor logic | Medium | Medium | High | |
| 5 | 4.1 Add unit tests | Low | Medium | High | |
| 6 | 2.3 Break up MetaDataFromDb | Medium | Large | High | |
| 7 | 2.4 Eliminate duplication | Low | Medium | Medium | |
| 8 | 3.1 Eliminate downcasting | Medium | Medium | High | |
| 9 | 1.2 Rename Hungarian notation | Low | Large (mechanical) | Medium | |
| 10 | 3.2 Modernize collections | Medium | Medium | Medium | |
| 11 | 3.3 Domain exceptions | Low | Small | Medium | |
| 12 | 5.1–5.2 Utility cleanup | Low | Medium | Low–Medium | |
| 13 | 6.1–6.3 Modernizations | Low | Small–Medium | Low | |

Each step is designed to be independently mergeable. The integration tests provide a safety net for the riskier structural changes in Phase 2–3. Always run the full integration test suite after each step.
