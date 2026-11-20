# Tasks

## 1. i18n and step model

- [x] 1.1 Add `archiveWizard` keys to `siard-web/frontend-shared/src/i18n/{de,fr,it,en}.json`: step titles "Select connection" and "Connection details" (translated), `back`, `cancel`, and cancel-dialog title/text/confirm/stay copy mirroring the desktop `archiveAbortDialog` semantics. Verify: `cd siard-web/local/frontend && npm test` shows no missing-translation warnings.
- [x] 1.2 Create `siard-web/local/frontend/src/wizard/archiveSteps.ts` declaring the ordered archive steps (`/archive` → "Select connection", `/archive/connection` → "Connection details") plus a resolver mapping a pathname to the active step index (longest path match; unrelated paths → -1). Unit test covers `/archive` → 0, `/archive/connection` → 1, `/` → -1. Verify: `cd siard-web/local/frontend && npm test -- archiveSteps` passes.

## 2. Wizard frame

- [x] 2.1 Create `siard-web/local/frontend/src/wizard/WizardActionsContext.tsx` exposing a `registerPrimaryAction`/`usePrimaryAction` pair so a step can publish `{ label, onClick, disabled }` to the bottom bar and clear it on unmount. Verify: exercised by the layout tests in 2.3.
- [x] 2.2 Implement `ArchiveWizardLayout`: horizontal MUI `Stepper`/`Step`/`StepLabel` (`alternativeLabel`) bound to the resolved active step, `<Outlet/>` for step content, and a bottom action bar with Back (previous step path, `/` on the first step), Cancel and the registered primary-action slot. Cancel opens a confirmation `Dialog` (same pattern as the delete-connection dialog); confirming navigates to `/`, dismissing stays. Make `Layout`'s `Container` a flex column and the wizard layout `flex: 1` column with the bar `mt-auto` so it pins to the bottom. Wire nested routes in `App.tsx` (`archive` parent → layout, `index` → `ArchiveView`, `connection` → `ConnectionDetailsView`). Verify: `npm run typecheck` passes and the app renders both archive routes inside the frame.
- [x] 2.3 Add RTL tests (`ArchiveWizardLayout.test.tsx`, update `App.test.tsx`) covering the spec scenarios: frame on `/archive` and `/archive/connection` but not on `/` or `/about`; first/second step marked active with earlier steps completed; titles in the interface language without raw keys; indicator rendered without a session; Back navigating `/archive/connection` → `/archive` → `/`; Cancel confirm → `/` and dismiss → stay with form values unchanged; action bar on `/archive` shows Back + Cancel and no primary action; session-missing behaviour unchanged. Verify: `cd siard-web/local/frontend && npm test` passes.

## 3. Details step integration

- [x] 3.1 Refactor `ConnectionForm`'s submit into a shared path callable without a form event; register Save as the wizard primary action (`disabled` mirroring `save.isPending`) via `WizardActionsContext`; remove the inline submit `Button` while keeping the form's `onSubmit` so Enter-key submission still works. Verify: `ConnectionDetailsView.test.tsx` updated — Save in the action bar submits a valid form and returns to `/archive`, invalid fields show inline errors with no request, pending disables the action.
- [x] 3.2 Confirm `ArchiveView` needs no primary action and renders unchanged inside the frame. Verify: `ArchiveView.test.tsx` still passes unmodified.

## 4. Integration verification

- [x] 4.1 Run `./gradlew :siard-web:check` — backend tests, boundary checks, OpenAPI check, frontend lint/typecheck/tests all pass.
- [x] 4.2 Manual smoke on this machine: launch the app, walk `/` → Archive → `/archive` → a DBMS or saved connection → `/archive/connection`, exercising the stepper, Back, Cancel confirm/dismiss, save-and-return, and a 360px-wide viewport. Report the result.

- [x] 4.3 Cover the "Narrow viewport" scenario automatically: `e2e/archive-wizard.spec.ts` opens `/archive` and `/archive/connection?dbms=postgresql` at 360×800 against the packaged agent, checks that the step indicator and every action-bar button can be scrolled into view, and asserts `scrollWidth <= 360`; the "Details step action bar" scenario gets an exact button-list test in `ConnectionDetailsView.test.tsx`. Verify: `./gradlew :siard-web:local:e2eTest` and `npm test` pass.

## Workflow follow-up

- CI runs the test suites on Windows, macOS and Linux — if the pipeline is green on all three, the cross-platform requirement is verified; the agent does not mark that done without the user's report.
- Archive the change after the project's review requirements are satisfied.
