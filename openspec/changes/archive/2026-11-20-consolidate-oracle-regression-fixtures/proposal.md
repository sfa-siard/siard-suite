# Proposal

## Why

Oracle integration tests repeatedly start the same database image and archive separate small fixtures. Pilot consolidation of two related, issue-derived type-export regressions to determine whether eliminating one startup and archive operation produces worthwhile savings without losing focused coverage.

## What Changes

- Consolidate only `BinaryTypesIT` and `OracleVarCharTypesIT` (both siardsuite129) into `OracleTypeExportRegressionIT`, with one source container and one archive operation per suite execution instead of two of each.
- Compose their existing SQL resources after one common user setup; retain separate fixture fragments and issue provenance rather than creating a monolithic SQL file.
- Preserve every existing column lookup, expected normalized type, original type, and successful archive assertion, with independently named binary and VARCHAR regression checks.
- Keep `OracleDataTypeMappingIT` and its fixture unchanged and separate: its purpose is mapping characterization used as a reference for documentation, not this suite's issue-regression workload.
- Make shared archive lifetime explicit and isolate its temporary files between runs.
- Define admission criteria for future feature and regression tests based on behavior and compatible execution conditions, not issue numbers or a desire to minimize class count.
- Measure the two-test baseline before implementation and compare actual uncached execution afterward, including full Oracle integration-task timings. Runtime benefit remains unproven; do not force further consolidation to justify this pilot.
- Leave all other Oracle tests, known-archive restore/round-trip coverage, permissions, driver tests, other databases, and CI configuration unchanged.

## Capabilities

### New Capabilities

None. This is a test-only refactor, not a new product capability. `.openspec.yaml` declares `skip_specs: true`; acceptance criteria are recorded in the design and tasks.

### Modified Capabilities

None. Existing archive/restore behavior and the `siard-cli` contract remain unchanged.

## Impact

- Test code: the two selected classes under `siard-cmd/src/integrationTest/java/ch/admin/bar/siard2/cmd/oracle/issues/siardsuite129/`, their replacement suite, and Oracle-local support only if needed.
- Test resources: binary and VARCHAR scripts under `oracle/issues/siardsuite129/` remain modular and traceable. `oracle/issues/siardsuite163/` remains unchanged.
- Build entry point remains `:siard-cmd:integrationTestOracle`, including its single-fork constraint.
- No production code, public API, dependency, database image/version, pipeline, installer, or mapping-characterization changes.
- Existing scripts assume an Oracle XE environment. Host-side resource and temporary-file handling must remain portable across Linux, macOS, and Windows where the required Oracle container runtime is available.
- The bounded saving is one source-container startup and one archive operation per selected-suite execution, not a promise that the whole Oracle job or PR pipeline will reach a particular duration.
