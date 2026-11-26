# Tasks

## 1. Agent file API

- [x] 1.1 Add `localwebapp.files` with path parsing/validation (D2) and `GET /api/files` listing with filters, sorting, 10000-entry cap and the 10 s limit on a pool of 8 daemon threads without queue (D3, Risks); verify a test with an injected blocking lister gets 504 after the limit and 503 at once when all 8 workers are blocked, both without `Retry-After`, and with `FilesResourceTest` covering every scenario of "Folder listing", "Listing filters" and "Large folders" (temp folders, incl. a hidden entry and an unreadable folder on POSIX; 401 via `Sessions`): `./gradlew :siard-web:local:backend:test --tests '*FilesResourceTest'`
- [x] 1.2 Add `GET /api/files/entry` (incl. `writable`); verify the "Entry lookup" scenarios in `FilesResourceTest`
- [x] 1.3 Add `Places` with the per-OS rules of D4 and `GET /api/files/places`; verify `PlacesTest` for Windows (fake `reg query` output, OneDrive path), macOS (`/Volumes` fake) and Linux (`user-dirs.dirs`, fallback) plus the "Places" scenarios in `FilesResourceTest`
- [x] 1.4 Add `FileManagerLauncher` with the per-OS commands of D5 and `POST /api/files/reveal`; verify `FileManagerLauncherTest` asserts the exact argument lists for file/folder on all three OS, the Linux fallback and 503, and `FilesResourceTest` covers the "Show in file browser" scenarios (400/404 start no process, 401)
- [x] 1.5 Regenerate and commit `siard-web/local/openapi.yaml` (`./gradlew :siard-web:local:backend:openApiUpdate`) and document the `/api/files` endpoints in `siard-web/local/README.md`; verify `./gradlew :siard-web:local:backend:check` passes (incl. `SharedApiDescriptionTest`, `openApiCheck`)

## 2. Open archive by path

- [x] 2.1 Add `ArchiveFilesResource` (`POST /api/archive-files`, D2) and update the archive section of `siard-web/README.md` ("No app exposes open archive yet"); verify `ArchiveFilesResourceTest` covers all "Open an archive from a local path" scenarios incl. unchanged file after `DELETE` (checksum), 422, 429 and 401, then regenerate `openapi.yaml` and run `./gradlew :siard-web:local:backend:check`

## 3. Chooser building blocks (frontend)

- [x] 3.1 Regenerate the local API types (`src/api/schema.d.ts`) and add `normalisePastedPath` (D6); verify `normalisePastedPath.test.ts` covers quoted Windows paths, macOS paths, `file://` URIs with `%20`, `file:///C:/...`, UNC, empty and relative input
- [x] 3.2 Add i18n keys for the chooser and the inspect view in de, fr, it, en (`frontend-shared/src/i18n`); verify the existing i18n key-parity test passes (`npm test` in `siard-web`)

## 4. File chooser dialog

- [x] 4.1 Implement `FileChooserDialog` navigation (places, listing, parent, hidden toggle, path field, errors in the dialog) and `useFileChooser`; verify `FileChooserDialog.test.tsx` covers the "Chooser navigation" scenarios incl. the unreachable-location message for 504 and the workers-exhausted message for 503, each asserted with an arbitrary detail text so only the status code decides, and the truncated-listing notice, and the "Path field" scenarios incl. a quoted file path that is selected directly with confirm enabled and a quoted folder path, with a mocked API
- [x] 4.2 Implement open, save (extension appending, replace confirmation, non-writable folder) and folder modes; verify `FileChooserDialog.test.tsx` covers the "Chooser modes" scenarios incl. cancel and 401
- [x] 4.3 Implement "Show in file browser" and the narrow-viewport layout; verify tests for the "Show in file browser from the chooser" scenarios (selection, current folder, 503, 401) and `npm run lint` in `siard-web/local/frontend`

## 5. Inspect workflow and start page

- [x] 5.1 Add `ArchiveInspectView` at `/archives/:archiveId` with Close and the "no longer open" message (D7); verify `ArchiveInspectView.test.tsx` covers the "Inspect workflow" scenarios (open, close → `DELETE` and `/`, 404, 422 message, no path in URLs)
- [x] 5.2 Wire the start page Open button to the chooser and `POST /api/archive-files`; add tests for the "Open workflow entry point" scenarios (pointer, keyboard, cancel with focus return, 401) in `StartView.test.tsx`; verify `npm test` passes
- [x] 5.3 Remove "Open" from the inert loop of `StartView.test.tsx` ("query text is ignored and pointer and keyboard activation are inert") as recorded in design.md D8, then verify `npm test` passes with Upload and Export still asserted inert

- [x] 5.4 Show DATALINK cells in `backend-core` as the desktop app does (their text, empty per cell when it cannot be read) and never open content outside the archive (`CellRenderer`, `Value.getDataLinkUrl()` in `siard-api`); verify tests on `sample-datalink-2-2.siard` cover the scenarios "Data link cells show their text", "A data link that cannot be read is shown empty" (a crafted non-UTF-8 file) and "Content outside the archive is not followed" (`CraftedArchive`, a BLOB with a URL, with and without a stored file)
- [x] 5.5 Fix the structure tree's links in `frontend-shared` (found in the manual run: they were resolved against the current page and piled up in the address); verify `ArchiveBrowser.test.tsx` covers the "Switch between tables and views" scenario

## 6. Integration

- [x] 6.1 Run `./gradlew :siard-web:check` and verify it passes (boundary checks, lint, tests of all web modules)
- [x] 6.2 Run `just web-dev` locally (Linux), open a `.siard` file through Open, use "Show in file browser" on it and close the archive; verify each step behaves as specified

## 7. Manual checks on other platforms (user)

- [ ] 7.1 **User step (Windows):** build and start the local app (`just web-dev` or the installer from CI), then: places show Desktop/Documents/Downloads (also with OneDrive redirection) and the drive letters; pasting a path copied with "Copy as path" from Explorer selects the file; "Show in file browser" opens Explorer with a file in a folder with spaces selected; a `.siard` file opens and browses. Report results; not done by the agent without the report.
- [ ] 7.2 **User step (macOS):** run `just web-dev`, then: places show home folders, `/` and volumes in `/Volumes`; Option+Cmd+C path pastes correctly; "Show in file browser" opens Finder with the file selected; a `.siard` file opens. Report results; not done by the agent without the report.
