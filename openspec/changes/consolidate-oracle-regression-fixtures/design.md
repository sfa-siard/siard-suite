# Design

## Context

See proposal.md for motivation and scope. This is a test-only refactor; no stable product specification changes.

`BinaryTypesIT` and `OracleVarCharTypesIT` independently create an instance-level `OracleContainer`, run one archive command, and assert metadata. Their scripts create distinct tables in `IT_USER`. `OracleTestDb` centralizes the image (`gvenzl/oracle-xe:21-slim-faststart`) and five-minute startup timeout. `integrationTestOracle` deliberately uses one fork because parallel Oracle cold starts have been unreliable.

`OracleDataTypeMappingIT` was created to characterize Oracle's datatype mappings, which were subsequently used for an Excel reference. Although its assertions also detect mapping changes, its characterization purpose is distinct from the selected issue regressions. Its code, fixture, lifecycle, expectations, and reporting remain unchanged.

`SiardArchivesHandler` owns files through per-method callbacks. It cannot safely own an archive shared across ordinary test methods without lifecycle changes. Other database tests rely on that existing behavior.

## Goals / Non-Goals

**Goals:**
- Reduce the selected workload from two container lifecycles and two archive commands to one of each per suite execution.
- Preserve focused coverage, independently reported failures, and reproducibility.
- Establish measured evidence and clear admission criteria before extending composition.

**Non-Goals:**
- A catch-all Oracle regression database or cross-database test framework.
- Combining mapping characterization, issue regressions, and archive compatibility tests simply because they use Oracle.
- Persistent container reuse, changes to the global archive handler, or changes to other Oracle classes.
- Replacing explicit expectations with snapshots or round-trip equality; expanding type/value coverage or changing SQL data to make the pilot pass.
- Production, JDBC-wrapper-test, Gradle-task, dependency, or CI changes.

## Decisions

### 1. Bound the pilot to binary and VARCHAR type-export regressions

Create `ch.admin.bar.siard2.cmd.oracle.regression.OracleTypeExportRegressionIT`. Compose these resources after `SqlScripts.Oracle.CREATE_USER_WITH_ALL_PRIVILEGES` exactly once:

| Original test | Fixture constant | Object | Retained checks / proposed test method |
|---|---|---|---|
| `issues.siardsuite129.BinaryTypesIT.downloadArchive` | `SIARDSUITE_129_BINARY` | `IT_USER.ORACLE_BINARY_TEST` | ID, BIT_EQUIVALENT, RAW_SMALL, RAW_MEDIUM, RAW_LARGE, BLOB_DATA; each normalized and original type / `binaryTypeBoundaries_siardsuite129` |
| `issues.siardsuite129.OracleVarCharTypesIT.downloadArchive` | `SIARDSUITE_129_VARCHAR` | `IT_USER.VARCHARTEST` | ID, TEXT2, TEXT3, TEXT4, DAY_OF_YEAR, WEIGHT_IN_KG; each normalized and original type / `varcharTypeBoundaries_siardsuite129` |

Copy every expected type string verbatim, including precision and whitespace. Verify successful archive exit code in shared setup and fail setup if it is nonzero. Retain script routines even when assertions only inspect table columns; they remain part of the input conditions.

Keep scripts separate, with deterministic container initialization names ordered common user, binary, VARCHAR. Do not rename schema objects or narrow archive schema selection relative to the original commands. Do not load the mapping-characterization fixture into the shared source.

Alternatives: include general mapping characterization to maximize startup savings, or merge all issue tests. Rejected because purpose and compatible preconditions determine ownership, not class count or issue-directory placement.

### 2. Use a class-scoped, read-only source and archive

Use existing JUnit 5/Testcontainers dependencies: one static `@Container` from `OracleTestDb.newContainer()`, a per-class test instance, and one `@BeforeAll` archive operation after initialization. Each ordinary `@Test` only inspects the completed archive. No method mutates shared state, depends on another test method, or invokes another archive command.

Use a class-scoped JUnit temporary directory for archive/extraction. Reuse `Unzipper`, `MetadataReader`, and `ContentReader` with the existing parsing behavior. Parse/extract once and expose the result read-only. Keep ownership Oracle-local: do not modify `SiardArchivesHandler` globally or manually call its callbacks. Preserve sequential execution under the existing task configuration.

Alternatives: colocating methods with instance containers still restarts them; sharing only the server still repeats archiving; modifying the global handler introduces cross-suite risk. The selected lifecycle reduces work without those changes.

### 3. Keep focused execution and diagnostics

Keep binary and VARCHAR regressions independently named with issue provenance. Diagnostics identify the qualified column and expected/actual normalized or original type. A failure in one family must not suppress execution of the other; shared setup failure must fail the task, not produce skipped-success reporting.

Retire the two original classes only after equivalence is demonstrated. Targeted execution moves to `OracleTypeExportRegressionIT` and its named methods. Do not leave duplicate legacy execution enabled.

### 4. Admission criteria for future feature and regression tests

Guiding principle: consolidate compatible execution conditions within one behavioral area, while retaining independent assertions. Issue identifiers describe provenance; behavior determines ownership. Sharing infrastructure does not itself justify sharing a fixture or archive operation.

A future test may join this type-export suite only when all of these hold:

1. It tests successful Oracle type/value export, not mapping inventory generation, restore compatibility, permissions, or another behavioral area.
2. It uses the same image/version, database settings, credentials/privileges, and connection mode.
3. Its small fixture objects coexist without changing another case's preconditions, visibility, or outcome.
4. Its assertions can inspect the existing archive operation's output; it requires no special command options or operation sequence.
5. Its checks are read-only and independent of method order.
6. It retains a distinct test name and qualified-object diagnostics; adding it does not replace specific assertions with broad operation success.
7. A new bug reproducer is first shown to expose the original failure in isolation and then in the composed fixture, using the affected revision or a controlled fault injection where feasible. If that cannot be demonstrated, keep it separate pending evidence.

Create a separate test/fixture when any criterion fails, especially for SSL, alternate versions/settings, restricted privileges, expected export failures, historical archive inputs, restore mutations, or large-data/memory measurements. A new feature follows the same rules as an issue regression. For example, a RAW-value truncation regression can join only with explicit affected-value assertions; type metadata alone does not prove value preservation.

These rules guide later changes; this pilot adds no new regression or feature coverage.

### 5. Preserve independent Oracle coverage

- `OracleDataTypeMappingIT` and `SIARDSUITE_163_DATATYPE_MAPPING`: unchanged, separate characterization workload, including its own container and archive operation.
- `MultipleSchemasIT`: unchanged credentials, expected exit code 1 / `ORA-01031`, and schema-selection checks.
- `SchemaNameUnderscoreIT`: unchanged export checks and historical-archive restoration.
- `OracleGetBigDecimalIT`: unchanged separate source/restore target.
- `OracleUploadDownloadSiardProjectIT`: unchanged known-example round trip and comparison exclusions; this pilot does not claim to repair excluded metadata coverage.
- `PackagesIT`, `OracleEscapeCharacterIT`, every other Oracle test, other engines, and `jdbc-oracle`: unchanged.

### 6. Measure execution, not cache hits

Before changing tests, record the two-test baseline in this design: revision, host/runner resources, JDK, image digest/pull state, command, observed container/archive counts, test execution time, and wall time. Use three runs per version on comparable resources with images available but fresh database containers each time. Do not delete unrelated Docker resources.

Baseline selected workload:

```text
./gradlew :siard-cmd:integrationTestOracle --tests '*BinaryTypesIT' --tests '*OracleVarCharTypesIT' --rerun-tasks --no-build-cache --console=plain
```

Candidate selected workload:

```text
./gradlew :siard-cmd:integrationTestOracle --tests '*OracleTypeExportRegressionIT' --rerun-tasks --no-build-cache --console=plain
```

Full Oracle task, for both versions:

```text
./gradlew :siard-cmd:integrationTestOracle --rerun-tasks --no-build-cache --console=plain
```

Measure Gradle wall time and test execution time separately; rerun compilation equally rather than mistaking it for database work. Confirm actual test execution, not cache/up-to-date skips. Use the same pipeline configuration for both versions. Full-task timings include the unchanged mapping-characterization test; selected-workload timings do not.

The bounded opportunity is eliminating one startup and one archive operation. Record the baseline before implementing; if it reveals no worthwhile opportunity, report the evidence and recommend stopping rather than broadening scope. No minimum percentage or whole-pipeline target is assumed.

## Acceptance Criteria

- **AC1 — Coverage:** Every lookup and normalized/original type expectation from the two selected original methods appears in the migrated checks. Shared archiving exits with 0. Nothing is relaxed, ignored, or replaced with operation success alone. The mapping-characterization test and fixture are unchanged.
- **AC2 — Work reduction:** A complete selected-suite execution starts one Oracle source container and invokes archive once, compared with two of each at baseline. Counts come from lifecycle/command evidence, not elapsed-time inference. The separate characterization workload is not counted as consolidated.
- **AC3 — Diagnostics:** Each named family runs independently via Gradle (for example `--tests '*OracleTypeExportRegressionIT.binaryTypeBoundaries_siardsuite129'`). A temporary wrong expected type fails its family with column context while the other family executes during a full selected-suite run. Restore correct expectations afterward. Shared setup failure produces a failing task.
- **AC4 — Isolation/security:** Suite temporary paths are unique and cleaned after completion; the source container stops. No mutation affects characterization or permissions tests. No new logs expose passwords, credential-bearing command lines, or SQL secrets.
- **AC5 — Performance:** Record all three before/after selected and full-task timings, medians, and absolute/relative changes. The selected median must improve before claiming runtime benefit. If it does not improve, or the full task shows repeatable slowdown, report the pilot unsuccessful and seek a decision rather than force consolidation or expand scope. Savings need not meet an invented threshold.
- **AC6 — Boundaries/portability:** Every remaining Oracle test, explicitly including `OracleDataTypeMappingIT`, passes unchanged. No production, other-engine, driver-test, CI, Gradle-task, or dependency changes. Use portable Java/JUnit paths and existing resource resolution. Verify Linux execution and record Windows/macOS results where a compatible runtime is available; record unavailable platforms explicitly.
- **AC7 — Maintainability:** Document future-test admission and separation rules in this design and verify the two selected regressions meet them. Keep issue provenance and modular SQL resources; neither this suite nor the pilot absorbs characterization coverage.

## Risks / Trade-offs

- [Only two tests are consolidated, so total savings may be modest] -> Measure before implementation and after; do not sell the full Oracle job's 16–20 minutes as time attributable to these two tests or extend scope just to improve the numbers.
- [Fixture initialization errors may not fail container startup] -> Capture baseline initialization output and verify both expected tables/columns exist; a ready container does not prove a complete fixture.
- [Existing VARCHAR data contains `12.56` for `NUMBER(3,2)`, potentially producing a precision error] -> Reproduce at baseline without changing data. Report any existing error and its effect; fixing fixture data is a separate decision.
- [Routine names or session settings interact] -> Use deterministic ordering, compare initialization errors, and verify the exact assertion matrix. Escalate new conflicts rather than relaxing checks.
- [One broken fixture blocks both families] -> Report shared setup failures explicitly and preserve modular resources and named checks.
- [Cleanup occurs after the first method] -> Avoid the per-method handler; verify both methods can read shared state and a second fresh run succeeds.
- [The suite becomes a dumping ground] -> Apply all admission criteria; retain separate characterization, permissions, restore, and special-condition tests.
- [Timing noise or pulls disguise benefit] -> Compare three actual runs per version on equivalent resources and separate selected-workload results from full-task results.

## Migration Plan

1. Record the two-test assertion matrix and baseline selected/full-task measurements before editing tests. Preserve characterization as a separate baseline workload.
2. Add the composed type-export suite and verify exact expectations, focused execution, lifecycle, and diagnostics.
3. Retire only the two superseded classes after equivalence is established; retain their SQL and issue provenance.
4. Run all Oracle tests unchanged outside that replacement, candidate measurements, and scoped quality reviews. Evaluate whether measured savings justify keeping the pilot.
5. Rollback by restoring the two original classes and removing only the new suite/support. No deployed state, production data, dependencies, or pipelines require rollback.
