# Proposal

## Why

The local web app currently has only a welcome heading. Issue #208 calls for a recognizable replacement for the JavaFX start page: the SIARD workflow diagram, including its visual feedback, without implementing the workflows yet.

## What Changes

- Replace the local start-route content with the JavaFX-style Archive/Open/Upload/Export diagram, using the issue screenshot as the desktop visual reference.
- Preserve the current web header, navigation, About page, launch-failure page and connection lifecycle.
- Reproduce red artwork highlighting and moving arrow dots on hover; provide equivalent keyboard-focus feedback and respect reduced-motion preferences.
- Rearrange the diagram vertically on narrow windows without horizontal page scrolling.
- Translate the action labels and descriptions into the four existing interface languages.
- Keep the local start-page composition app-owned. A future remote app may use a different layout and functions; share only reusable artwork and existing infrastructure.

Out of scope: opening files, dialogs, backend calls for the four actions, database workflows, exports, remote-app implementation, header redesign, new installers, and JavaFX changes.

## Capabilities

### New Capabilities

- `local-web-start-page`: the local app's responsive workflow overview, visual interaction feedback and presentation-only entry points.

### Modified Capabilities

None. The app-wide language and delivery requirements in `local-web-ui` remain unchanged.

## Impact

- Local frontend: `StartView`, app-owned layout/styles, component tests and browser tests; existing shell test expectations must reflect the replacement of the welcome heading.
- Shared frontend: reusable artwork and translation resources, with no app-specific routing or configurable shared start-page component.
- Reuse the repository's JavaFX neutral/red artwork and localized text as source material; no new runtime dependency or API is required.
- Verification covers desktop and narrow layouts, hover/focus/reset, reduced motion, all four languages and unchanged navigation/lifecycle. The browser UI must work on Windows, macOS and Linux.
- Reference: https://github.com/sfa-siard/siard-suite/issues/208 and its attached screenshot.
