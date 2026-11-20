# Proposal

## Why

The desktop app presents archiving as a guided wizard: a step indicator across the top (`DrilledMFXStepper` in `archive-stepper.fxml`) and a `ButtonBox` with Back/Next/Cancel pinned to the bottom of every step. The local web app's archive flow is currently two plain pages (`/archive`, `/archive/connection`) with ad-hoc navigation and no shared frame. Matching the desktop layout keeps the workflow recognizable for existing users and gives the archive steps still to come (preview, metadata, download/run) a consistent chrome to plug into.

## What Changes

- A shared wizard layout wraps the archive workflow pages: a step indicator at the top showing the archive steps and marking the current one, and a bottom action bar with consistent navigation controls.
- The step list is defined once as data (ordered, localized titles) so later archive steps can be appended without restructuring the chrome; only the steps that exist are shown as reachable.
- `/archive` (connection picker) and `/archive/connection` (details form) render inside the wizard as the first two steps; their existing content and behaviour are unchanged.
- Navigation actions follow the desktop pattern: Back returns to the previous step where one exists, the step's primary action stays available, and Cancel leaves the archive wizard back to the start page.
- No archive execution, progress reporting, or job cancellation is added — there is no archive run in the web app yet; a future change adds the remaining steps and the run/cancel behaviour into this frame.

## Capabilities

### New Capabilities

- `local-web-archive-wizard`: the wizard frame of the local web app's archive workflow — top step indicator, bottom action bar, step ordering and navigation rules across the archive pages.

### Modified Capabilities

(none — `local-web-connections` is still a pending delta of `connection-profile-management` and is not yet under `openspec/specs/`; this change adds chrome requirements without altering the picker/details requirements it defines)

## Impact

- **Frontend** (`siard-web/local/frontend`): new wizard layout component (stepper header + action bar), applied to `ArchiveView` and `ConnectionDetailsView`; routing unchanged; i18n strings for step titles and button labels added in de/fr/it/en via `frontend-shared`.
- **Backend**: no changes — the chrome is purely presentational.
- **Specs**: new `local-web-archive-wizard` capability; no existing spec requirements change.
- **Depends on**: `connection-profile-management` (delivers the `/archive` pages this chrome wraps); its code is uncommitted on this branch, so this change lands on top of it.
- **Desktop app**: no changes — `siard-suite-app` keeps its JavaFX stepper untouched.
