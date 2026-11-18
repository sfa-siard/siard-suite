# Design

## Context

See proposal.md for motivation and specs/local-web-start-page/spec.md for acceptance behavior. The current local `StartView.tsx` is a welcome heading. `App.tsx` owns the header, route container and connected layout; it must retain those responsibilities. The shared theme provides dark shell colors and Roboto; the screenshot's workflow buttons instead use JavaFX blue (#006699).

The desktop reference is `siard-suite-app/src/main/resources/ch/admin/bar/siardsuite/fxml/start.fxml`; `StartPresenter.java` defines the hover-to-artwork mappings. Neutral and red artwork already exists under that module's `icons/` directory. Its `i18n/messages_{de,en,fr,it}.properties` provide source text. The local frontend already uses React, MUI, react-i18next, Vitest and Playwright. No additional package is necessary.

Design is included because responsive diagram layout, interaction state and local/shared ownership benefit from explicit decisions before coding.

## Goals / Non-Goals

**Goals:**
- Preserve the reference diagram's visual identity without copying JavaFX's rigid pixel grid.
- Keep interaction state deterministic across pointer and keyboard input.
- Allow a future remote app to compose an unrelated start page using the same underlying visual resources if useful.

**Non-Goals:**
- A generic workflow engine, shared start-page schema or local/remote mode switch.
- Global theme changes to make every application button blue.
- Reproducing JavaFX navigation or connecting placeholder controls to future features.

## Decisions

### 1. App-owned composition, shared static artwork

Keep composition and action-specific feedback in `siard-web/local/frontend/src/views/StartView.tsx` and small adjacent app-owned files if needed. Copy the relevant existing `archive.png`, `archive_red.png`, `siard-db.png`, `siard-db_red.png`, `export.png`, `export_red.png` and arrow artwork into a new `siard-web/frontend-shared/src/assets/start-page/` directory during implementation. Expose asset URLs through the shared package's existing public entry point rather than importing JavaFX resources across build boundaries. Keep the JavaFX originals unchanged.

Alternatives: a shared configurable StartPage would couple the remote app to an unconfirmed layout and actions; importing desktop resources directly would make web bundling depend on another application's resource tree. Small duplicated static files are an acceptable cost for independent builds.

### 2. Responsive grid with in-flow content

Use CSS grid/flex layout and MUI primitives rather than a screenshot background or absolutely positioned text. At desktop widths arrange source/Archive, central SIARD/Open, and Upload/Export destinations in three regions. Maintain the reference's central illustration proportions, rounded corners/shadow, blue (#006699) buttons and neutral artwork. Keep `App.tsx`'s header and container unchanged; tune spacing within StartView.

Proposed implementation default: switch below 1000 CSS pixels to a single-column composition ordered Archive, central SIARD/Open, Upload, Export. Keep descriptions next to their relevant groups and arrows aligned to their source/destination direction. Decorative SIARD context may repeat on narrow layouts only if needed to make outgoing flow unambiguous; do not duplicate action buttons. Allow natural text wrapping and vertical scrolling. Test the required 1280px and 360px viewports plus an intermediate width and enlarged text. The exact spacing can be tuned against the screenshot without changing the behavioral contract.

Alternative: scaling or horizontally scrolling the fixed JavaFX grid would make text too small or hide controls, contrary to the agreed responsive behavior.

### 3. One derived visual action, no workflow handlers

Track hovered and keyboard-focused action identifiers separately; derive the active visual action as hovered action first, otherwise focused action. Leaving one button while another retains focus restores that focused action's feedback; leaving/blurring all actions resets artwork and stops dots. Use the JavaFX mapping from the spec. Use neutral/red image variants rather than approximate CSS color filters. Keep image dimensions fixed to avoid layout shift.

Use native button semantics (`type="button"`) and visible focus styling. The buttons remain visually enabled to reproduce the reference, but have no navigation or workflow activation handlers in this slice. Tests enforce inert click, Enter and Space behavior. This deliberately temporary affordance is confined to this issue's presentation-only scope.

### 4. CSS animation and accessibility defaults

Animate one decorative dot along the active arrow using CSS transforms/keyframes, not timer-driven React updates. Reset/unmount the dot when inactive so it is hidden and stopped. Under `prefers-reduced-motion: reduce`, suppress the moving dot while preserving artwork highlighting. Apply artwork feedback on keyboard focus too. These are recorded accessibility defaults, not extra workflow functionality. Decorative images/arrows/dots are hidden from assistive technology; the diagram has an accessible name, and descriptions and named native buttons communicate its meaning without relying on color.

Alternative: reproducing JavaFX PathTransition exactly adds unnecessary JS timing and lifecycle complexity for straight arrows. Reduced motion and keyboard feedback intentionally improve the desktop reference's input coverage.

### 5. Reuse language infrastructure, not fragmented sentence assembly

Add namespaced start-page keys to the existing shared de/en/fr/it JSON resources. Use the desktop text as source material, preserving proper decoding of its properties files. Use complete translated descriptions with safe rich-text emphasis for verbs, rather than concatenated fragments whose ordering assumes English. Do not accept query-string labels or raw HTML. The app-wide browser language/fallback behavior stays unchanged.

### 6. Verification split

Use local component tests for accessible controls, text, pointer/focus state, reset, inert activation and untrusted query handling. Extend browser coverage in `local/frontend/e2e/` for actual geometry, all four language layouts, hover artwork, dot movement/reset, reduced motion and keyboard focus. Observe requests relative to the existing agent event stream; no new workflow traffic is allowed, but existing lifecycle traffic and bundled asset requests are expected.

Keep the existing agent launch/About/close smoke test. Add browser screenshot capture for comparison with the issue at desktop size and a narrow viewport; prefer layout/state assertions over platform-sensitive font/shadow pixel thresholds. Run through the existing Gradle e2e task, which supplies the packaged agent. Windows/macOS checks are explicit user verification steps when only Linux is available.

## Risks / Trade-offs

- Raster artwork may soften at high DPI -> reuse its native aspect ratios and avoid excessive enlargement; do not replace recognizable artwork with unrelated icons.
- Four translations expand differently -> wrap descriptions naturally and test desktop/narrow layouts in all supported languages.
- Pointer and focus can coexist -> use the explicit precedence/reset policy and test returning to the focused action.
- A diagram rearranged vertically can lose meaning -> retain source/destination artwork and action descriptions, and visually inspect the narrow layout.
- Native-looking inert buttons can imply unavailable functionality -> accept this only for the explicitly presentation-only change; no fake dialogs or success feedback.
- Shared assets could grow into a shared app composition -> share static resources and language infrastructure only, leaving routing, sessions and action wiring app-owned.

## Migration Plan

No persistence, API or installer migration is needed. Ship through the existing frontend build and packaged agent. Rollback restores the previous StartView, its tests and newly introduced resources; shell/session behavior is unchanged. Do not alter the JavaFX application.
