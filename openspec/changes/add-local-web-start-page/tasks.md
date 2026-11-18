# Tasks

## 1. Artwork and localized content

- [x] 1.1 Copy the neutral/red SIARD, database and export artwork plus arrow artwork from `siard-suite-app/src/main/resources/ch/admin/bar/siardsuite/icons/` into a new `siard-web/frontend-shared/src/assets/start-page/` directory and expose asset URLs from `frontend-shared/src/index.ts`. Preserve desktop originals; verify aspect ratios and neutral/red pairings and run the shared package typecheck (add asset typing if required).
- [x] 1.2 Add namespaced action labels and complete rich-text descriptions to `siard-web/frontend-shared/src/i18n/{de,en,fr,it}.json`, using the desktop properties as source text with correct decoding. Add tests for key parity and expected descriptions/emphasized verbs in each locale; verify with `npm run test --workspace @siard-web/shared` from `siard-web`.

## 2. Responsive local overview

- [x] 2.1 Write failing local component tests for the named workflow buttons, translated overview, inert pointer/keyboard activation and ignored untrusted query text. Replace `StartView.tsx`'s welcome heading with the desktop diagram using existing MUI primitives and app-owned styles, safe translated emphasis, decorative artwork and named controls; verify the new tests pass with `npm run test --workspace @siard-web/local` from `siard-web`.
- [x] 2.2 Implement the narrow vertical composition and natural description wrapping, keeping one button per action and the existing `App.tsx` shell/container unchanged. Add browser tests in `siard-web/local/frontend/e2e/` for relative desktop positions at 1280x800, vertical layout/no horizontal overflow at 360x800, an intermediate width and enlarged text; parameterize supported languages. Capture desktop/narrow screenshots and verify against the issue reference using `./gradlew :siard-web:local:e2eTest` from the repository root.
- [x] 2.3 Update the welcome-heading expectation in `siard-web/local/frontend/src/App.test.tsx` to the new overview, retaining assertions for Info/About and launch-failed routes. Verify component tests pass and no header, routing, agent-lifecycle or JavaFX code changes were introduced.

## 3. Hover, focus and motion

- [x] 3.1 Add failing interaction tests, then implement the specified red-artwork mapping for Archive/Open/Upload/Export and reset behavior. Track hover and focus separately with pointer precedence and focus restoration after pointer exit. Verify each mapping, unrelated neutral artwork, visible focus and reset in local component tests.
- [x] 3.2 Add the active arrow's decorative moving dot using CSS animation, hide/stop it when inactive, and suppress moving dots under reduced motion while retaining highlighting. Extend browser tests to verify movement along the source-to-destination arrow, reset after pointer/focus exit, keyboard feedback, reduced-motion behavior and stable image dimensions; run `./gradlew :siard-web:local:e2eTest`.
- [x] 3.3 Extend browser tests to monitor activation with click, Enter and Space: no workflow navigation, dialogs, file choosers, downloads or workflow API calls, and no token/credential/path disclosure. Ignore expected existing event-stream and bundled-asset traffic. Verify these tests and the unchanged launch/About/window-close smoke test pass through `./gradlew :siard-web:local:e2eTest`.

## 4. Integrated verification

- [x] 4.1 From `siard-web`, run `npm run lint`, `npm run typecheck`, `npm run test` and `npm run build`; from the repository root run `./gradlew :siard-web:check` and `./gradlew :siard-web:local:e2eTest`. Verify all succeed, inspect captured screenshots for fidelity/readability and confirm the diff contains no backend, installer, JavaFX or shared-start-page composition changes.
- [ ] 4.2 Extend `.github/workflows/ci.yml` so the local web-app job runs on Linux, Windows and macOS, with OS-specific Playwright caches and retained browser reports/screenshots. Run `./gradlew :siard-web:local:e2eTest` on the Windows CI runner for this change. Confirm all browser tests including desktop/narrow layouts, animation, focus/reduced-motion and launch/About/close pass. Record the CI run and result; do not mark complete from Linux results or workflow configuration alone.
- [ ] 4.3 Run `./gradlew :siard-web:local:e2eTest` on the macOS CI runner for this change. Confirm all browser tests including desktop/narrow layouts, animation, focus/reduced-motion and launch/About/close pass. Record the CI run and result; do not mark complete from Linux results or workflow configuration alone.
