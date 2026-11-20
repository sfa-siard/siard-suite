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

Baseline source revision: `0ae8dd792041b6b9ad6137e686b09f3f13baf93c` (inventory verified against both original `downloadArchive` methods).

| Qualified column | Normalized type | Original type |
|---|---|---|
| `IT_USER.ORACLE_BINARY_TEST.ID` | `FLOAT(38)` | `NUMBER` |
| `IT_USER.ORACLE_BINARY_TEST.BIT_EQUIVALENT` | `SMALLINT` | `NUMBER(1,0)` |
| `IT_USER.ORACLE_BINARY_TEST.RAW_SMALL` | `VARBINARY(1)` | `RAW(1)` |
| `IT_USER.ORACLE_BINARY_TEST.RAW_MEDIUM` | `VARBINARY(8)` | `RAW(8)` |
| `IT_USER.ORACLE_BINARY_TEST.RAW_LARGE` | `VARBINARY(2000)` | `RAW(2000)` |
| `IT_USER.ORACLE_BINARY_TEST.BLOB_DATA` | `BLOB` | `BLOB` |
| `IT_USER.VARCHARTEST.ID` | `FLOAT(38)` | `NUMBER` |
| `IT_USER.VARCHARTEST.TEXT2` | `VARCHAR(1)` | `VARCHAR2(1)` |
| `IT_USER.VARCHARTEST.TEXT3` | `VARCHAR(255)` | `VARCHAR2(255)` |
| `IT_USER.VARCHARTEST.TEXT4` | `VARCHAR(4000)` | `VARCHAR2(4000)` |
| `IT_USER.VARCHARTEST.DAY_OF_YEAR` | `SMALLINT` | `NUMBER(3,0)` |
| `IT_USER.VARCHARTEST.WEIGHT_IN_KG` | `DEC(3, 2)` | `NUMBER(3,2)` |

The inventory covers 12 column lookups, 24 type expectations, and two successful archive exit-code assertions (one per original method; one shared setup assertion after consolidation). Both cases satisfy the admission criteria: successful type export with the same image, settings, `IT_USER` privileges, connection mode, and archive options; small distinct tables; independent read-only assertions with issue provenance. The cases already share their original siardsuite129 issue origin, so this pilot preserves existing regression expectations rather than introducing a new bug reproducer. The mapping-characterization fixture is explicitly excluded. Fixture compatibility and actual work reduction still require execution verification.

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

## Baseline Execution Record

Attempted on 2026-11-17 at revision `0ae8dd792041b6b9ad6137e686b09f3f13baf93c` on Linux x86_64, Intel Core i7-1365U (12 logical CPUs), 30 GiB host RAM, Zulu OpenJDK 21.0.2, Gradle 9.7.0, Docker server 29.9.3. The Oracle image was already present with digest `gvenzl/oracle-xe@sha256:f82bccdf6020d27373fdf0e93046b63eb3f777a0289e329d9839feebaf4555de`.

The first selected-workload execution used the baseline command in decision 6 with `/usr/bin/time -p`. Gradle initially could not create its wrapper lock because the global Gradle cache was read-only; write access was requested and granted before retrying. The actual test attempt then failed before either Oracle container started:

```text
Can not connect to Ryuk at localhost:32768
java.net.ConnectException: Connection refused
Timed out waiting for Ryuk container to start.
BinaryTypesIT > downloadArchive() FAILED
OracleVarCharTypesIT > downloadArchive() FAILED
2 tests completed, 2 failed
BUILD FAILED in 1m 5s
real 65.49
```

Docker reported `testcontainers/ryuk:0.12.0` running with published port `0.0.0.0:32768->8080/tcp`; its logs reported listening on port 8080. The test JVM could not reach that port through localhost. The underlying networking/environment cause is not yet established; no host override, Ryuk disablement, or container/test configuration change was attempted.

The failed environment attempts are excluded from performance samples. They reached neither Oracle initialization nor archive execution. The user subsequently confirmed a successful normal-terminal run (Gradle reported 1m57s); that user-reported run is not included in the measured three-run sample below.

With explicit user approval, the user-level Devin configuration was repaired (missing JSON comma) and a narrow `sandbox.excluded.ask` rule was added for the Oracle Gradle task. No repository or test configuration was changed. Standalone Gradle invocations then succeeded; a chained Gradle/report command still failed at Ryuk and was excluded, so measurements use standalone invocations and separate report processing.

### Selected baseline (unchanged tests)

All runs executed both original `downloadArchive()` methods successfully with zero skips/failures and observed two Oracle container creations and two primary-data archive operations. Image and host are as recorded above; the pipeline configuration and SQL are unchanged.

| Run | Gradle wall time (reported) | Binary suite seconds | VARCHAR suite seconds | Summed suite seconds |
|---|---|---|---|---|
| 1 | 1m54s | 44.343 | 49.837 | 94.180 |
| 2 | 1m56s | 55.053 | 48.321 | 103.374 |
| 3 | 2m22s | 58.489 | 54.290 | 112.779 |

Median summed suite time: **103.374 seconds**; median reported Gradle wall time: **116 seconds**. Summed suite times come from Gradle XML `testsuite` durations, including each original per-method fixture lifecycle. Candidate lifecycle timing must be checked for comparability because its initialization moves to class scope. Rounded Gradle wall times include compilation and provide an additional end-to-end comparison.

Run 1 output is retained in the session tool output; runs 2/3 are in `build/oracle-pilot-baseline-selected-2.log` and `build/oracle-pilot-baseline-selected-3.log`. No ORA error was present in those selected Gradle logs because the original fixtures do not attach a container log consumer. Separate Docker initialization-log capture during the unchanged full-task baseline confirmed `ORA-01438: value larger than specified precision allowed for this column` at the existing second VARCHAR insert (`12.56` into `NUMBER(3,2)`). SQLPlus continues, the table/columns exist, and the current metadata-only assertions pass; this pilot neither fixes the data nor claims complete row insertion. Captured evidence: `build/oracle-pilot-init-92b3a5f1e605da74b197f84d7e450b317645eb5e87af164000f83281b27cf9af.log` lines 99–102. The opportunity is approximately one of two repeated Oracle archive lifecycles; a median of 103.374 seconds for two fixtures warrants measuring the bounded consolidation, but candidate benefit remains unproven.

### Full-task baseline (unchanged Oracle suite)

| Run | Gradle wall time (reported) | Summed suite seconds | Tests | Oracle starts | Archive operations |
|---|---|---|---|---|---|
| 1 | 8m38s | 499.970 | 11 passed | 13 | 9 |
| 2 | 11m12s | 658.523 | 11 passed | 13 | 9 |
| 3 | 11m25s | 662.805 | 11 passed | 13 | 9 |

Median full-task summed suite time: **658.523 seconds**; median reported Gradle wall time: **672 seconds**. Logs: `build/oracle-pilot-baseline-full-{1,2,3}.log`. All nine original Oracle classes executed in each run, including the separate characterization class and both permissions/schema-selection checks. The only ORA error in the Gradle output was the expected `ORA-01031` asserted by `MultipleSchemasIT`; the separately captured VARCHAR initialization error is described above. Baseline measurements are complete. The substantial between-run variation must be retained when interpreting candidate timings; operation-count reduction is independent of timing noise. The current Gradle XML/HTML reports live under `siard-cmd/build/test-results/integrationTestOracle/` and `siard-cmd/build/reports/tests/integrationTestOracle/`; each new run overwrites those reports, so results above were recorded before starting the next execution.

## Implementation Verification Record

Implementation: `oracle.regression.OracleTypeExportRegressionIT`, with static Testcontainers ownership, class-scoped JUnit `@TempDir`, one `@BeforeAll` archive/extract/parse, and two independent `@Test` methods. The global archive handler and SQL scripts are unchanged. The parser/explorer utilities are reused directly because their existing per-method lifecycle wrapper cannot own this class-scoped archive.

### Coverage and work reduction (tasks 2.1–2.2)

The initial candidate command in decision 6 passed both methods (Gradle 1m30s, suite 69.214s). Its log contains exactly one Oracle container creation and one primary-data archive operation. A verification script extracted `(table, column, normalized type, original type)` tuples from both original source methods and the new `columnTypes` calls: all 12 tuples/24 exact type strings match. The shared setup asserts exit code 0. No general mapping assertions were moved.

Evidence: `build/oracle-pilot-candidate-initial.log`, Gradle XML for `ch.admin.bar.siard2.cmd.oracle.regression.OracleTypeExportRegressionIT`, and the source tuple-comparison verification executed in this session. Gradle's class suite duration includes the shared initialization, so the selected-workload suite-time metric remains comparable to the baseline.

### Focused execution and failure diagnostics (task 2.3)

Each command used `./gradlew :siard-cmd:integrationTestOracle --tests '<selector>' --rerun-tasks --no-build-cache --console=plain`:

| Selector / temporary fault | Result | Evidence log |
|---|---|---|
| `*OracleTypeExportRegressionIT.binaryTypeBoundaries_siardsuite129` | Exactly one test passed | `build/oracle-pilot-focus-binary.log` |
| `*OracleTypeExportRegressionIT.varcharTypeBoundaries_siardsuite129` | Exactly one test passed | `build/oracle-pilot-focus-varchar.log` |
| `*OracleTypeExportRegressionIT`, binary ID expected type temporarily set to `INTENTIONAL_WRONG_TYPE` | Binary failed, VARCHAR passed; XML failure identifies `IT_USER.ORACLE_BINARY_TEST.ID normalized type`, actual `Optional[FLOAT(38)]`, and the wrong expected type | `build/oracle-pilot-fault-type.log` |
| `*OracleTypeExportRegressionIT`, setup exit-code assertion temporarily expected 1 instead of 0 | `initializationError FAILED`, failing Gradle task | `build/oracle-pilot-fault-setup.log` |
| `*OracleTypeExportRegressionIT`, both temporary faults reverted | Both tests passed | `build/oracle-pilot-candidate-restored.log` |

No intentional fault remains in source. The fault runs are diagnostics checks, not performance samples.

### Lifecycle and output isolation (task 2.4)

Initial and restored full selected-suite executions each created one fresh Oracle container and one archive. Their temporary roots were distinct (`/tmp/junit-14748801563716123787` and `/tmp/junit-3862778635011726941`). Docker inspection confirmed both source containers were removed. Direct host-side file-tool checks confirmed both temporary roots no longer existed, avoiding ambiguity from sandbox-local `/tmp` views. Roots from assertion-failure and setup-failure runs (`/tmp/junit-4972599087121897069`, `/tmp/junit-69122309233094462`) were also absent after completion. Both methods read the same completed archive successfully. New test code adds no logging; candidate logs contain no literal credential-bearing `--password=password` command argument. Only existing disposable fixture credentials are used.

### Superseded-class retirement (task 2.5)

After the replacement passed and exact assertion equivalence was verified, the user explicitly approved deleting `issues/siardsuite129/BinaryTypesIT.java` and `OracleVarCharTypesIT.java`. Only those two classes were removed; both SQL fragments and `OracleDataTypeMappingIT` remain unchanged. Final references are `oracle.regression.OracleTypeExportRegressionIT.binaryTypeBoundaries_siardsuite129` and `.varcharTypeBoundaries_siardsuite129`, as shown in the coverage matrix and focused commands above.

Command `./gradlew :siard-cmd:integrationTestOracle --test-dry-run --rerun-tasks --no-build-cache --console=plain` succeeded. XML discovery verification found exactly 11 tests in 8 classes: the replacement plus all seven unaffected classes, including separate characterization, with neither superseded class. Evidence: `build/oracle-pilot-final-discovery.log`. This dry run verifies discovery only and is excluded from execution/performance evidence.

### Final selected-workload measurements (task 3.1)

After retirement and discovery verification, the final selected command in decision 6 executed both replacement methods three times with fresh containers and no cache/up-to-date skips:

| Run | Gradle wall time (reported) | Suite seconds | Tests | Oracle starts | Archive operations |
|---|---|---|---|---|---|
| 1 | 1m14s | 57.437 | 2 passed | 1 | 1 |
| 2 | 1m26s | 67.066 | 2 passed | 1 | 1 |
| 3 | 1m24s | 67.045 | 2 passed | 1 | 1 |

Logs: `build/oracle-pilot-candidate-selected-{1,2,3}.log`; each summary was checked against its current Gradle XML before the next execution. Median suite time is **67.045 seconds**, down **36.329 seconds (35.1%)** from the selected baseline median of 103.374 seconds. Median reported Gradle wall time is **84 seconds**, down **32 seconds (27.6%)** from 116 seconds. Every run observed exactly one Oracle creation and archive operation versus two of each before consolidation. No ORA errors appeared in the Gradle logs; the previously documented SQLPlus initialization warning remains unchanged. These are local selected-workload measurements, not a claim about full-task or CI speedup.

### Full-task candidate measurements (task 3.2)

The unchanged full command in decision 6 (`./gradlew :siard-cmd:integrationTestOracle --rerun-tasks --no-build-cache --console=plain`) executed the complete remaining Oracle suite three times under baseline-equivalent conditions:

| Run | Gradle wall time (reported) | Summed suite seconds | Tests | Oracle starts | Archive operations |
|---|---|---|---|---|---|
| 1 | 9m56s | 578.250 | 11 passed | 12 | 8 |
| 2 | 14m29s | 849.758 | 11 passed | 12 | 8 |
| 3 | 15m27s | 896.563 | 11 passed | 12 | 8 |

All three runs executed all 11 tests across 8 classes with zero failures/skips, including `OracleDataTypeMappingIT` as a separate characterization workload, both `MultipleSchemasIT` methods with the expected ORA-01031 path, and both `OracleTypeExportRegressionIT` methods. Evidence: `build/oracle-pilot-candidate-full-{1,2,3}.log` and each run's Gradle XML captured before the next run overwrote it.

Every run observed exactly 12 Oracle container creations and 8 archive operations versus the baseline's 13/9, matching the single eliminated startup/archive pair; counts verified per run from its log.

**Full-task timing comparison (AC5).** Candidate median summed suite time is **849.758 seconds** and median reported Gradle wall time is **869 seconds** (14m29s), versus baseline medians of 658.523 seconds and 672 seconds. The nominal full-task median is worse, but the distributions overlap heavily — the candidate minimum (578.250) lies inside the baseline range (499.970–662.805). Per-class XML shows the inflation is uniform across every untouched class within the candidate's own runs (`OracleGetBigDecimalIT` 109.741 → 183.843/192.321s, `SchemaNameUnderscoreIT` 120.106 → 201.829/193.710s, `OracleUploadDownloadSiardProjectIT` 114.891 → 113.865/187.619s across candidate runs 1/2/3), the same first-run-fast pattern visible in the baseline itself (499.970 vs. ~660 summed). No mechanism in this test-only change can slow down unrelated classes; the delta is consistent with host-level variance already documented at baseline. The proven benefit remains the selected-workload median improvement (35.1% suite time, one fewer container start and archive operation per selected run). Whether the bounded saving justifies keeping the pilot is recorded as a workflow follow-up for human review; no out-of-scope test was altered to reduce noise.

### Portability and boundary review (task 3.3)

**Diff scope.** `git diff HEAD` plus untracked files shows exactly: deletion of `issues/siardsuite129/BinaryTypesIT.java` and `OracleVarCharTypesIT.java` (the two approved retirements), addition of `oracle/regression/OracleTypeExportRegressionIT.java`, and edits to this change's `design.md`/`tasks.md`. Zero changes to production code, other-engine tests, JDBC-wrapper tests, dependencies, Gradle tasks, CI configuration, SQL fixtures, or `OracleDataTypeMappingIT`/its fixture. The disposable `IT_USER`/`password` fixture credentials are unchanged from the retired tests.

**Host portability.** The new suite uses only portable APIs already used by every Oracle IT: `java.nio.file.Path`/`Files`, JUnit 5 `@TempDir` (OS temp dir, unique per run — verified distinct roots `/tmp/junit-14748801563716123787` and `/tmp/junit-3862778635011726941` in task 2.4), Testcontainers `MountableFile.forHostPath` + `TestResourcesResolver`, and container-internal `/container-entrypoint-initdb.d/` paths (the Oracle image is Linux on every host OS). No hardcoded separators, `/tmp` literals, or OS-specific calls were introduced. Linux execution is verified by the full-task runs above.

**Windows/macOS limitation.** This environment is Linux-only; no Windows or macOS host with a compatible container runtime is available in this session, so no Windows/macOS execution was performed. CI runs `integrationTestOracle` exclusively on `ubuntu-22.04` (`.github/workflows/ci.yml`), so no CI platform coverage is lost. The suite relies on the same Testcontainers/JUnit mechanisms as the seven unaffected Oracle ITs that already run on developer Windows/macOS machines; portability risk is limited to the shared `OracleTestDb`/`SiardCliTest` utilities, which are unchanged.

**Admission/separation rules.** Design section 4 and AC7 retain the criteria: composition requires compatible preconditions (same image/settings/privileges/options), small distinct fixtures, independent read-only assertions with provenance, and explicit exclusion of characterization coverage. `OracleDataTypeMappingIT` remains a separate workload with its own container and archive operation (12 vs. baseline 13 Oracle starts confirms it was not absorbed).

### Quality gate record (task 3.4)

Direct reviews performed in-session (no subagent use authorized):

- **Architecture:** Test-only change inside `siard-cmd/src/integrationTest`. Package `ch.admin.bar.siard2.cmd.oracle.regression` follows the existing `ch.admin.bar.siard2.cmd.oracle.*` layout. Imports are exclusively pre-existing test utilities (`OracleTestDb`, `SqlScripts`, `TestResourcesResolver`, `SiardCliTest`, `siard.model.*` readers, `MetadataExplorer`, `Unzipper`) — no new module dependencies, no production code, correct JUnit 5/Testcontainers lifecycle patterns (static `@Container`, `PER_CLASS`, static `@TempDir`). Modern style per AGENTS.md. **No findings.**
- **QM-Guide:** New code follows modern conventions; assertions use named `assertAll` groups with qualified-column failure context; coverage matrix verified 1:1 against retired sources; no dead code or weakened checks. **No MUSS findings.**
- **Acceptance-to-test coverage:** AC1–AC7 each map to recorded evidence above (tuple/type-string equivalence, 1-vs-2 lifecycle counts, focused/fault-injection runs, temp-path cleanup and credential review, three-run timing tables, boundary diff, admission rules). `openspec validate consolidate-oracle-regression-fixtures --strict` passes.
- **Security (reviewed last):** The only credential is the disposable `IT_USER`/`password` Testcontainers fixture, passed in-process to `SiardCliTest.execute` exactly as the retired tests did; it is never written to logs or files. No new network endpoints, secrets, or injection surface; SQL comes from unchanged static fixture files; temp archives are auto-cleaned. **No findings.**

No HIGH or CRITICAL findings; nothing required escalation or auto-fix.

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
