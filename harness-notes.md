# Harness Notes

## Agent Decisions

Decided: `--import-xml` in the `siard-cli` spec is described as a metadata XML template applied to the archive before metadata download (usable together with `--jdbc-url`), not as an alternative input source — matching actual `SiardFromDb` behavior.
Harness gap: no rule needed; reviewer knowledge gap, resolved against the code.

Decided: the mandatory "authorization scenario" for the `siard-cli` capability maps to credential/security scenarios (password handling, no-leak assertions) because this project has no roles/permissions model.
Harness gap: AGENTS.md has no Roles & Permissions section. For CLI/desktop tools, AGENTS.local.md's "authorization case" rule could specify a fallback scenario type (e.g., credential/input-security scenarios).

Decided: architecture review findings were folded into design.md — `siard-suite-app` ships `siard-from-db`/`siard-to-db` scripts via `CreateStartScripts` (silent breakage risk since Gradle doesn't validate `mainClass`), so the app distribution moves to a single `siard` script too.
Harness gap: none — review process worked as intended.
