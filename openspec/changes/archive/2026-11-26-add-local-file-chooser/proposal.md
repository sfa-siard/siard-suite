# Proposal

## Why

The local web app is meant to replace the JavaFX desktop app, which selects files and folders in
several workflows (inspect an archive, upload an archive to a database, export to HTML/CSV). A
browser cannot hand an absolute path to the page, and archives must not be uploaded to the local
agent, so the agent itself has to let the user pick files on their computer. The archive browse
API and views already exist; only a way to open an archive in the local app is missing.

## What Changes

- New in-app file chooser in the local frontend, backed by the agent, with three modes:
  - **open**: pick an existing file, offering only files with the expected extensions
    (e.g. `.siard`);
  - **save**: pick a folder and enter a file name; the workflow's one extension (e.g. `.siard`)
    is added when missing and overwriting an existing file needs confirmation;
  - **folder**: pick a folder.
- The chooser shows places (home, Desktop, Documents, Downloads, drives / volumes), a folder
  listing (folders first, hidden entries off by default with a toggle) and a path field that
  accepts typed or pasted paths, including quoted paths and `file://` URIs as file managers copy
  them.
- "Show in file browser" in the chooser opens the system file manager with the selected entry
  selected (or the current folder), so the user can use system tools such as search.
- New local agent endpoints under `/api/files` (places, folder listing, entry lookup, reveal) and
  `POST /api/archive-files` to open an archive from an absolute path. The archive is read in place;
  nothing is uploaded or copied.
- The start page's **Open** button starts the "Inspect an existing archive" workflow: open mode
  filtered to `.siard`, then the opened archive is shown in the existing archive browse views.
- Out of scope: the recent-files list, the archive-creation (save) and export (folder) workflows
  and the upload workflow. Save and folder modes are delivered and tested as part of the chooser,
  but no workflow uses them yet.

## Capabilities

### New Capabilities

- `local-web-file-chooser`: the agent's local file-system API (places, folder listing, entry
  lookup, show in file browser) and the frontend chooser with its open, save and folder modes.
- `local-web-archive-inspection`: opening a local SIARD archive by absolute path in the local app
  and showing it with the archive browse views.

### Modified Capabilities

- `local-web-start-page`: the Open button is no longer presentation-only; it starts the inspect
  workflow. Upload and Export stay inert.
- `archive-browsing`: DATALINK cells are shown as in the desktop app, as text or empty per cell
  when that text cannot be read, instead of one such cell failing the whole page of rows (found
  while inspecting `test.siard` in the manual run). A cell that points outside the archive by URL
  (`dlurlpathonly`) is shown empty and never opened, the one deliberate deviation from the
  desktop app. The structure tree's links no longer pile up in the address when switching
  between tables and views (also found in the manual run).

## Impact

- `siard-web/local/backend`: new `files` package (resources, places per OS, file-manager launcher
  per OS), archive-opening resource using `ArchiveRegistry.register`; `openapi.yaml` regenerated.
- `siard-web/backend-core`: DATALINK cells shown as in the desktop app, cells pointing outside
  the archive shown empty and never read (`CellRenderer`); its `/api/archives/**` description is
  unchanged.
- `siard-api`: one accessor, `Value.getDataLinkUrl()`.
- `siard-web/local/frontend`: chooser component, path-paste normalisation, `/archives/:archiveId`
  route, Open wiring on the start page, generated API types.
- `siard-web/frontend-shared`: new i18n keys (de, fr, it, en) with a key-parity test; the archive
  browser's structure tree links from the mount point and no longer nests `<li>` elements.
- No new dependencies; Windows, macOS and Linux each need their own places and file-manager
  handling.
