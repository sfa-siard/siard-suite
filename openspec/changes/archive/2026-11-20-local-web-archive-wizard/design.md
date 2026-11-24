# Design

## Context

See proposal.md — Why. The archive flow is two sibling routes (`/archive`, `/archive/connection`) under `ConnectedLayout` in `siard-web/local/frontend/src/App.tsx`, each rendering its own view with ad-hoc buttons. The desktop app wraps its archive steps in a stepper plus a per-step `ButtonBox` (Back/Next/Cancel) pinned to the bottom, and its Cancel opens a confirmation dialog that returns to the start view. The web app uses MUI v9, react-router v8 nested routes, react-i18next with all strings in `siard-web/frontend-shared/src/i18n/*.json`, and vitest + Testing Library for view tests.

## Goals / Non-Goals

**Goals:**

- One wizard frame (top step indicator + bottom action bar) shared by all archive steps, matching the desktop layout's structure.
- A single step list that later archive steps (preview, metadata, download/run) can extend without restructuring the chrome.
- The step's primary action rendered inside the bottom bar, keeping the step's existing validation and behaviour.

**Non-Goals:**

- Archive execution, progress reporting or job cancellation (no run exists yet).
- Additional wizard steps, changes to non-archive routes, or any backend change.
- Sharing the chrome with the remote web app — it lives in the local frontend only; it can move to `frontend-shared` if a second app ever needs it.

## Decisions

- **Nested layout route.** `archive` becomes a parent route rendering `ArchiveWizardLayout` with `<Outlet/>`; `index` → `ArchiveView`, `connection` → `ConnectionDetailsView`. The frame persists across step navigation and wraps every step state (loading, error, unknown identifier) because it is a route layout, not part of the views. Alternative — each view rendering the frame — rejected: duplicated chrome and remounting state on every step change. This mirrors the existing `Layout`/`ConnectedLayout` nesting.

- **Step list as data.** A module (e.g. `src/wizard/archiveSteps.ts`) declares the ordered steps once: `{ path, titleKey }` pairs relative to `/archive`. The layout resolves the active index by matching `useLocation().pathname` against the step paths (longest match wins), so `/archive` → step 0, `/archive/connection` → step 1, and future steps only append entries. Step titles are the spec-pinned strings "Select connection" / "Connection details" under new `archiveWizard.*` i18n keys in all four shared JSON files.

- **MUI `Stepper` + `Step` + `StepLabel`** (horizontal, `alternativeLabel`) for the indicator. MUI marks steps before `activeStep` as completed automatically. Alternatives rejected: `MobileStepper` (dots only, and its built-in next/back buttons would duplicate the action bar); a custom stepper (reinventing themed components).

- **Bottom bar pinned by flex layout, rendered by the layout.** `Layout`'s `Container` becomes a flex column; the wizard layout is `flex: 1` column with the bar `mt-auto`, so the bar sits at the viewport bottom on short pages and scrolls naturally on long ones. Alternative — `position: fixed` — rejected: it overlaps content and needs manual padding compensation.

- **Bar contents derived per step.** Back target = previous step's path, or `/` on the first step — computed from the step list, no per-view config. Cancel is identical on every step: it opens a confirmation `Dialog` (same pattern as the existing delete-connection dialog); confirming navigates to `/`, dismissing stays. New `archiveWizard.*` keys cover the dialog copy; the submit keeps the existing `connections.save` label.

- **Primary action via context registration.** The layout provides a `WizardActionsContext` with a `registerPrimaryAction` callback; a step registers `{ label, onClick, disabled }` on mount and clears it on unmount. The details step registers Save — `disabled` mirrors the pending save — which invokes the same submit path as the form (validation, `POST /api/connections` for a new or `PUT /api/connections/{id}` for a saved connection, return to `/archive`); the picker registers nothing, so its bar shows only Back and Cancel. The form keeps its `onSubmit` handler so Enter-key submission still works; its inline submit button moves into the bar. Alternatives rejected: a `form=` attribute button (cannot reflect `isPending` without extra plumbing); hardcoding per-route actions (breaks when steps are appended).

- **Components live in `siard-web/local/frontend/src/wizard/`**, not `frontend-shared`: the capability is local-only and the shared package stays app-agnostic. i18n strings still go into the shared JSON files, matching the existing convention that all app strings live there.

- **Native Enter submission.** Keep a hidden native submit control in the details form because browsers do not implicitly submit a multi-field form without a submit control after the visible Save action moves to the bar.

## Risks / Trade-offs

- Moving Save out of the form breaks tests that query it inside the form → update `ConnectionDetailsView.test.tsx` to target the action bar; Enter-submit stays covered via `onSubmit`.
- Context registration means the primary action appears only after the step mounts → acceptable; Back/Cancel render immediately.
- Two labelled steps fit at 360px; five future steps may crowd → `alternativeLabel` plus horizontal overflow handling; revisit when the step count grows (explicitly out of scope now).
- Exact cancel-dialog wording is new copy → keep it close to the desktop `archiveAbortDialog` semantics ("leave the archive workflow?"); final strings are an i18n detail for the tasks phase.
