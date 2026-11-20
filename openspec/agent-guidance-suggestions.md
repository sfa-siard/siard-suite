## Harness Suggestions

Suggestion: [connection-profile-management] in openspec/config.yaml
Reason: The specs rule demands a missing/invalid-session scenario for every local-web requirement, but it is unclear how it applies to UI page requirements (the page only shows a message on 401) and to storage requirements such as persistence; verification flagged picker, details and entry-point requirements while the existing start-page main spec has no session scenarios. State whether a UI requirement may use an input/credential-security scenario instead, and what the session case looks like for frontend pages.

Suggestion: [connection-profile-management] in AGENTS.md
Reason: `./gradlew :siard-web:check` does not run the Playwright tests; layout and viewport scenarios (1280/360 px, 960 px container) are only covered by `./gradlew :siard-web:local:frontend:e2eTest`, which tasks and verification did not mention. Document that UI layout scenarios need an e2e test and that e2eTest must be run separately.

## Processed

- [local-web-archive-wizard] authorization scenario for the local web app: config.yaml `rules.specs` now names the missing/invalid session case (2026-11-20).
- [fix-postgres-import-memory] scenario rule not enforced: config.yaml `operations.apply` now treats a missing scenario type as CRITICAL during verification (2026-11-20).
- [consolidate-oracle-regression-fixtures] Testcontainers in sandbox: AGENTS.md "Integration tests (Docker)" section (2026-11-20).
- [consolidate-oracle-regression-fixtures] removing replaced tests: config.yaml `rules.tasks` equivalence matrix + approval rule (2026-11-20).
