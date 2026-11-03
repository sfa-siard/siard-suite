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
