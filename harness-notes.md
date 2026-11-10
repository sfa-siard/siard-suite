# Harness Notes

## Agent Decisions

Decided: `--import-xml` in the `siard-cli` spec is described as a metadata XML template applied to the archive before metadata download (usable together with `--jdbc-url`), not as an alternative input source — matching actual `SiardFromDb` behavior.
Harness gap: no rule needed; reviewer knowledge gap, resolved against the code.

Decided: the mandatory "authorization scenario" for the `siard-cli` capability maps to credential/security scenarios (password handling, no-leak assertions) because this project has no roles/permissions model.
Harness gap: AGENTS.md has no Roles & Permissions section. For CLI/desktop tools, AGENTS.local.md's "authorization case" rule could specify a fallback scenario type (e.g., credential/input-security scenarios).

Decided: architecture review findings were folded into design.md — `siard-suite-app` ships `siard-from-db`/`siard-to-db` scripts via `CreateStartScripts` (silent breakage risk since Gradle doesn't validate `mainClass`), so the app distribution moves to a single `siard` script too.
Harness gap: none — review process worked as intended.

Decided (TDD merge): spec-derived unit tests were scaffolded before implementation (tasks.md section 1). `PasswordPrompter.prompt()` contract: returns `char[]`, or `null` when no console is available — mapped to usage error exit 2. `SIARD_PASSWORD` lookup is injected as `Function<String,String>` into `SiardCli` since unit tests cannot mutate `System.getenv`.
Spec scenarios intentionally left without a unit scaffold — coverage instead: "Archive a database", "Restore a SIARD file", "Export metadata only", "Existing SIARD file with --overwrite", "Restore with schema mapping", "Existing objects without --overwrite", "Successful operation (exit 0)" → migrated testcontainers ITs (section 6); "Interactive prompt" echo suppression → manual check (no console in tests); "First run in read-only location" → unit test of `SiardConnection` load order (task 4.3) + `installDist` manual check (task 5.1).
Harness gap: SDD+TDD ordering is now proven in this change — a tasks.md convention ("test seam + spec scaffolds first, then implementation turns them green") could be added to AGENTS.local.md so future changes don't re-derive it.

Decided: ADRs were numbered 0006/0007 instead of tasks.md's 0005/0006 — the decisions index already reserved ADR-0005 for the web-companion spike. tasks.md updated accordingly.
Harness gap: none — resolved against the decisions index.

Decided: picocli `required=true`/`ArgGroup` were dropped in favor of manual validation in `call()` — picocli reports missing-required before unmatched arguments, which would hide the unknown option that spec scenarios require to be named ("error identifying --jdbcurl").
Harness gap: worth noting in ADR-0006 (done).

Quality Gate results (pitc_architecture, pitc_quality, pitc_test, pitc_security — run in parallel):
- M1 (blocking): config record `toString()`s could leak the password via generated component output → masked `toString()` added to both records + `ConfigRecordsTest` assertion.
- M2 (blocking): spec self-contradiction — "unsupported scheme → 2" vs "invalid scheme" listed under exit-1 execution errors → spec.md amended (scheme stays exit 2).
- HIGH (security): JDBC URL could embed `user:password@` and reach logs/stderr → `SiardConnection.maskCredentials()` applied at every URL log/print site in both engines and SiardConnection.
- P0 (coverage): "export metadata only" and "existing objects w/o --overwrite" had no real test despite harness-notes claiming IT coverage → `exportXmlOnly_writesMetadataXmlAndExits0` + `restoreExistingObjectsWithoutOverwrite_failsWithConflictHint` added to `PostgresUploadDownloadSiardProjectIT` (both pass).
- MEDIUM/SHOULD auto-fixed: `--password=<value>` emits a stderr warning; `--overwrite` checks `delete()` result; prompted `char[]` is zeroed; shared `usageError` helper extracted; `ProgramInfo` init moved to engine-owned `ProgramInfoInit` (web-companion callers covered); dead `keys()`/`FileWriter` residue removed; DEBUG-level no-leak test added.
- Deliberately NOT applied: `--map-schema` as picocli `Map` type and `required=true` options (would break spec-exact error messages — documented in design.md D3); "etc drop vs defaults" S5 left as design decision (defaults stay as fallback).
- 62 unit tests + postgres IT suite green after fixes.

Harness gap (recurring): harness-notes' "migrated testcontainers ITs" claim was wrong for 2 scenarios — the coverage claim asserted migration that never happened. Suggestion: when recording IT-migration coverage claims in harness-notes, require naming the concrete test class per scenario, not a generic "section 6" pointer.

Decided (propose siard-cli-packaging): distribution artifacts get their own capability `siard-cli-distribution` rather than folding installer requirements into the `siard-cli` behavior spec — packaging is a separate observable contract.
Harness gap: none — standard capability-boundary decision.

Decided (propose siard-cli-packaging): Windows installers install system-wide (no `--win-per-user-install`) — managed BIT deployments conventionally install machine-wide; the GUI's per-user flag is a GUI choice. Flagged for BIT feedback on first MSI.
Harness gap: if BIT confirms a required install scope, it belongs in the spec.

Decided (propose siard-cli-packaging, architecture review): installers do not add `siard` to PATH — jpackage offers no PATH integration and managed deployments should not mutate it; spec contract is "run from the installed location", documented per-OS. Icons are copied into `siard-cmd` resources, not referenced from `siard-suite-app` (would invert module direction). Second installer type via `-PinstallerType=` project property (beryx exposes only one `installerType`).
Harness gap: none — review process worked as intended.

Decided (propose siard-cli-packaging): the req-quality "authorization scenario" again mapped to constraint/security scenarios (JavaFX-free bundle, read-only install, elevation requirement) since the project has no roles model — same precedent as modernize-siard-cli.
Harness gap: still open — AGENTS.local.md could name the CLI-tool fallback scenario type explicitly.

Decided (apply siard-cli-packaging): jpackage `imageName` = "siard" while `installerName` = "siard-cli" — jpackage's `--name` names the launcher, so imageName=siard-cli produced a `siard-cli` binary; the spec requires a `siard` executable inside `siard-cli-*` packages.
Harness gap: none — resolved during implementation verification.

Decided (apply siard-cli-packaging): installers are renamed after jpackage to `siard-cli-<version>-<os>.<ext>` via a doLast on the jpackage task — jpackage emits `<name>-<ver>.msi` and `<name>_<ver>_<arch>.deb/.rpm`, which don't match the spec's release-file contract.
Harness gap: none.

Decided (apply siard-cli-packaging): strict uploads (`if-no-files-found: error`) were combined with per-OS `if:` conditions, and the never-produced GUI `*.exe`/`*.rpm` upload steps were removed — the GUI builds exactly one installer type per OS (msi/dmg/deb), so unconditional strict uploads would fail every matrix job.
Harness gap: tasks.md "make ALL uploads strict" needs the implicit "for artifacts that should exist" reading — a tasks.md convention could state that strictness implies per-OS conditioning.

Decided (apply siard-cli-packaging): rpm could not be verified locally (no rpmbuild on this machine, no sudo); deb verified end-to-end. The `-PinstallerType=rpm` wiring is proven — jpackage received the type and failed only on the missing `rpmbuild` binary, which CI installs.
Harness gap: none — covered by release-guide smoke steps + CI.

Decided (quality gate auto-fix): packaging invariants are now asserted by the producing tasks — `verifyRuntimeImage` (no javafx.* modules, bin/siard exists) wired into the CI CLI step, and an installDist doLast check that the app bin/ contains no siard launcher. Icons moved to `siard-cmd/packaging/icons/` (not jar resources) per architecture review; jpackage rename now throws on missing/ambiguous artifacts.
Harness gap: none.

Decided (sonarqube setup): SonarQube Cloud analysis was added without an OpenSpec change — CI/build tooling, not product behavior covered by capability specs. SonarScanner for Gradle (`org.sonarqube` 7.5.0.8588) applied at root only; `sonar.exclusions=**/generated/**` covers JAXB (`siard-api`) and ANTLR (`sql-parser`) generated sources. Workflow restricted to push/PR on `main` (Free/OSS plan limits branch analysis).
Harness gap: AGENTS.local.md says "never implement a feature without a spec scenario" but doesn't define whether build/CI tooling counts — a one-line carve-out ("CI/tooling changes don't require specs") would prevent re-litigating this.

## Cycle: siard-cli-packaging (2026-11-04)

### Agent Decisions
- jpackage `imageName = "siard"`, `installerName = "siard-cli"` (jpackage --name names the launcher).
- Installers renamed post-jpackage to `siard-cli-<version>-<os>.<ext>`; rename fails loudly on missing/ambiguous artifacts.
- Strict uploads (`if-no-files-found: error`) combined with per-OS `if:` conditions; dead GUI exe/rpm upload steps removed (GUI produces one installer type per OS).
- Icons copied to `siard-cmd/packaging/icons/` (build input, not jar resource — keeps them off the GUI classpath).
- jlink modules: jdeps set + dynamic needs (jdk.crypto.ec, java.naming, jdk.charsets); explicit java.sql/java.xml/java.logging.
- rpm not verifiable locally (no rpmbuild); wiring proven (jpackage received type, failed only on missing binary) — CI installs rpm.

### Harness Suggestions
- Suggestion: tasks.md convention — "make uploads strict" implicitly means strict *and* per-OS conditioned; state it explicitly to avoid a literal reading that breaks matrix jobs.
- Suggestion: when recording IT/manual verification claims, name the concrete command/test per scenario (recurring gap from previous cycle).
- Suggestion (security MEDIUM, repo-wide): consider pinning GitHub Actions to commit SHAs across all workflows — currently tag-pinned (`@v4`/`@v5`/`@v6`). A repo policy decision, not per-change.
- Suggestion: AGENTS.local.md could name the authorization-scenario fallback for CLI/desktop tools (no roles model) — still open from previous cycles.

### Summary
- Tasks completed: 19/19
- Quality gate: passed with warnings (0 CRITICAL, 0 remaining HIGH — arch HIGH auto-fixed; 1 MEDIUM security finding: action SHA pinning, repo-wide policy question)
- Auto-fixed: installer rename now fail-loud (arch HIGH); icons moved out of jar resources (arch MEDIUM); absolute jpackage paths (arch MEDIUM); setup-gradle@v4 (QM MUSS M1); test-credential clarification (QM MUSS M2); verifyRuntimeImage + installDist guard tasks (test gap)
- Escalated: none
- Top 3 harness suggestions: (1) strict-uploads implies per-OS conditioning convention; (2) name concrete verification per scenario; (3) repo-wide action SHA pinning policy
