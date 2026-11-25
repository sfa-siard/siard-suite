# Design

## Context

- See proposal.md (Why). Requirements are in the three delta specs.
- `ArchiveRegistry.register(scope, path, onRelease)` (backend-core) opens an archive file in
  place; `LocalScopeResolver.scope()` is the local agent's single scope. The browse API
  (`/api/archives/**`) and the shared `ArchiveBrowser` view exist.
- `SharedApiDescriptionTest` requires the local app's set of `/api/archives/**` paths to equal
  the core's, so a local-only path under `/api/archives` would fail the boundary check.
- The agent's packaged runtime has no JavaFX; `java.desktop` (AWT) is present. The session,
  Host and `Sec-Fetch-Site` checks of `RequestFilters` already apply to every `/api/*` path.
- Explored alternative: the PoC branch (`poc/executable-web-companion-application`) opened an
  AWT `FileDialog` from the agent. It has no owner window, so on Windows it can open behind the
  browser; it cannot choose folders on Windows/Linux and filters only on Windows.

## Goals / Non-Goals

**Goals:**
- One chooser implementation that behaves the same on Windows, macOS and Linux and is fully
  testable in CI (backend tests, Vitest, no manual dialog interaction).
- Reusable chooser API in the frontend so later workflows (create archive, export, upload, Access
  file) only pass a mode and extensions.

**Non-Goals:**
- Native OS dialogs, drag-and-drop from the OS (browsers withhold the path), recent files,
  file operations (create folder, rename, delete).

## Decisions

### D1 In-app chooser backed by an agent file API (not a native dialog)
The frontend renders the chooser (MUI `Dialog`); the agent lists folders. Rejected: native dialog
from the agent (window ordering, per-OS code for folders and filters, blocking HTTP request,
untestable in CI) and the browser's file input (no absolute path).

### D2 Endpoints and packages
New package `ch.admin.bar.siardsuite.localwebapp.files`:
- `FilesResource` at `/api/files`: `GET /places`, `GET ?path&extension*&foldersOnly&hidden`,
  `GET /entry?path`, `POST /reveal`.
- `ArchiveFilesResource` at `/api/archive-files` (`POST`), calling `ArchiveRegistry.register`
  with `LocalScopeResolver.scope()` and a no-op `onRelease` (the agent does not own the file).
  Kept outside `/api/archives` (see Context) and outside backend-core, because the remote app
  will obtain files by upload. `IOException` from `register` becomes 422; `ProblemException(429)`
  propagates.
- Status codes, one rule for all endpoints: 400 for a path that is empty, relative, contains NUL
  or cannot be parsed (`InvalidPathException`), and for an existing path of the wrong kind (a file
  where a folder is expected, a folder where a file is expected); 404 only when nothing exists at
  the path; 403 for `AccessDeniedException`. Listing only: 504 when the listing did not finish
  within 10 s (the location is not responding), 503 when all listing workers are busy. Clients
  decide by status code only; detail texts are for humans and may change freely. No
  `Retry-After` header: the agent cannot predict when a stuck OS call returns. Problem details never contain more than the path the
  client sent.
- Paths are taken from query parameters for `GET` (the frontend never puts them in the browser
  address bar) and from JSON bodies for `POST`. Paths are normalised with
  `Path.of(p).toAbsolutePath().normalize()` after checking `isAbsolute()`; symbolic links are
  followed when listing.

### D3 Listing
`Files.newDirectoryStream`, attributes via `Files.readAttributes(..., BasicFileAttributes)`;
entries whose attributes cannot be read are left out. `hidden` uses `Files.isHidden` (dot files
on Unix/macOS, the hidden attribute on Windows). Filtering happens in the agent (smaller
responses for big folders), then sorting (folders first, `String.CASE_INSENSITIVE_ORDER`), then
the 10000-entry cap with `truncated`. Rejected: client-side filtering (ships every entry of large
folders to the browser).

### D4 Places per OS (`Places`)
- home: `user.home`.
- Desktop/Documents/Downloads: Windows reads `HKCU\...\Explorer\User Shell Folders` via
  `reg query` (same approach as `BrowserLauncher`; handles OneDrive redirection), expanding
  `%USERPROFILE%`; Linux reads `$XDG_CONFIG_HOME/user-dirs.dirs` (`XDG_DESKTOP_DIR`, ...);
  macOS and every fallback use `home/Desktop`, `home/Documents`, `home/Downloads`. Only existing
  folders are returned.
- roots: Windows `File.listRoots()` (drive letters); macOS `/` plus the folders in `/Volumes`
  except the link to `/`; Linux `/` plus folders in `/media/$USER`, `/run/media/$USER` and
  `/mnt`.
- The OS is injected (enum `Os` from `os.name`) so all three branches are unit-tested on any
  machine with a fake environment and a temp folder.

### D5 Reveal per OS (`FileManagerLauncher`)
`ProcessBuilder` with separate arguments, never a shell; only existing absolute paths reach it.
- Windows: `explorer.exe /select,<file>`; folder: `explorer.exe <folder>`. Explorer's exit code
  is meaningless, so success means "process started".
- macOS: `open -R <file>`; folder: `open <folder>`.
- Linux: `gdbus call --session --dest org.freedesktop.FileManager1 --object-path
  /org/freedesktop/FileManager1 --method org.freedesktop.FileManager1.ShowItems "['<file URI>']" ""`
  (selects the file in Nautilus, Dolphin, Nemo, Caja, ...); if `gdbus` is missing or exits
  non-zero within 5 s, `xdg-open <parent folder>`; folder: `xdg-open <folder>`.
- No process could be started → 503. The process runner is injected so tests assert the
  exact command lines per OS without starting anything.

### D6 Frontend chooser (`local/frontend/src/files/`)
- `FileChooserDialog` props: `mode: "open" | "save" | "folder"`, `extensions: string[]`,
  `initialName?` (save), `onClose(path | null)`. Lives in the local app, not `frontend-shared`
  (the remote app uploads instead; module boundary).
- Layout: places list on the left, folder listing (MUI `List`, keyboard navigable; Enter or
  double-click opens a folder or confirms a file), toolbar with parent folder, hidden toggle,
  "Show in file browser", path field on top, name field (save mode), Cancel/confirm buttons.
  On narrow viewports the places collapse into a select.
- Data via the generated `$api` client and React Query, keyed by path and filters.
- `normalisePastedPath(input)`: trims, strips one pair of surrounding double quotes, converts a
  `file://` URI (incl. `file:///C:/...` and `file://server/share/...`) to a path with
  percent-decoding; returns null for empty or relative input (absolute = starts with `/`, a drive
  letter `X:\`/`X:/`, or `\\`). Pure function, unit-tested.
- Save mode: the name gets the first extension appended when it has none of the extensions;
  existence and writability come from `GET /api/files/entry` for the target, then for the
  folder; replacing asks a confirmation dialog.
- `useFileChooser()` hook returns `choose(options): Promise<string | null>` so workflows do not
  manage dialog state themselves.

### D7 Inspect workflow
Start page Open → `useFileChooser().choose({mode: "open", extensions: [".siard"]})` →
`POST /api/archive-files` → navigate to `/archives/:archiveId`. `ArchiveInspectView` renders the
shared `ArchiveBrowser` with a Close button (`DELETE`, then `/`). A 404 from the archive API
(closed, evicted, after an agent restart) shows a localized message with a link to `/`. Leaving
the page without Close relies on the registry's idle timeout.

### D8 Test replaced on the start page (coverage equivalence)
`StartView.test.tsx` "query text is ignored and pointer and keyboard activation are inert" loops
over Open, Upload and Export. Open leaves that loop:

| Removed assertion | Replacement |
|---|---|
| Clicking Open keeps route `/`, opens no dialog/picker | "Open opens the chooser in open mode with .siard" (spec: Activation opens the chooser) and "cancel keeps route /" (spec: Cancel returns to the start page) |
| Enter/Space on Open is inert | same tests, activated by keyboard |
| Open activation discloses no local info | "Open without a session" + the inspect "No path in the address bar" test |

Upload and Export keep their inert assertions unchanged.

## Risks / Trade-offs

- [Not the native look; no OS favourites, cloud places or search] → "Show in file browser" and
  the path field bridge to the system file manager (decided with the user).
- [Windows `ProcessBuilder` quoting of `/select,<path with spaces>`] → unit test of the command
  line plus a manual check on Windows (task 7).
- [Linux desktops without `org.freedesktop.FileManager1`] → fallback opens the parent folder
  without selection.
- [Slow or unavailable drives (network share, empty card reader) block a listing; Java cannot
  cancel a blocking file-system call: `Thread.interrupt()` does not abort `opendir`/`FindFirstFile`
  or attribute reads, so the worker stays blocked until the OS call returns (SMB timeouts on
  Windows; a hard NFS mount on Linux can block indefinitely)] → the request waits at most 10 s
  and answers 504, but the worker is abandoned, not stopped. Listings therefore run on a
  dedicated pool of 8 daemon threads with no queue: when all are blocked, further listings answer
  503 at once instead of piling up threads, and blocked daemon threads do not keep the agent from
  exiting. A worker is reusable once its OS call returns. Places do not probe roots. The chooser
  tells the two cases apart by status code (D2).
- **Known limitation:** repeated clicks on one dead network drive can block all 8 workers; until
  their OS calls return (Windows SMB timeout, possibly much longer on a hard NFS mount), every
  listing, also of reachable folders, answers 503 and the chooser asks to try again in a minute.
  The pool-full check runs before the timeout, so in that state no listing reaches the 504 path.
  A per-drive/share guard was considered and rejected as too much machinery for a rare case.
- [Entry lookup, reveal and archive opening also touch the file system and could stall on a dead
  share] → not guarded in this change; they are single user actions, not repeated listing
  clicks. Revisit if it shows up in practice.
- [Listing exposes the file system to the session] → same reach as opening an archive by path;
  `/api/*` is guarded by session cookie, Host and `Sec-Fetch-Site` checks.

## Migration Plan

Additive; no data migration. `openapi.yaml` of the local app is regenerated with
`openApiUpdate` and committed.
