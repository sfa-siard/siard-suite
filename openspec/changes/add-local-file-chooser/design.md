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
- Desktop/Documents/Downloads: Windows exports `HKCU\...\Explorer\User Shell Folders` with
  `reg export <key> <temp file> /y` (handles OneDrive and folder redirection). `reg export` writes
  UTF-16 with BOM, so the result does not depend on the console code page as `reg query` output
  would; `REG_EXPAND_SZ` values arrive as `hex(2)` UTF-16LE bytes and `%VAR%` is expanded
  case-insensitively. Output goes to the file, not a pipe, so the 5 s limit on the process is real
  and an overrunning `reg` is destroyed. A successful lookup is cached for the agent's lifetime; a
  failed one is retried on the next request. Linux reads `$XDG_CONFIG_HOME/user-dirs.dirs`
  (`XDG_DESKTOP_DIR`, ...); macOS and every fallback use `home/Desktop`, `home/Documents`,
  `home/Downloads`.
- Existence check: network paths (`\\server\share\...`) are returned unchecked, so a redirected
  folder on an unreachable share cannot stall the endpoint; the others are checked in parallel on daemon
  threads with one shared 2 s deadline and kept when the check does not finish (a later listing
  answers 504). At most 4 checks run at once (4 permits, each released only when its check
  returns, so stuck checks cannot pile up threads); a check that would need a fifth worker is not
  run and the folder counts as present. A folder whose check timed out is not checked again for
  1 minute, so reopening the chooser with Documents on a dead mapped drive (`H:\Documents`) does
  not leave another stuck thread each time.
  Places are de-duplicated by normalised path (ignoring case on Windows), keeping the first kind.
- roots: Windows `File.listRoots()` (drive letters); macOS `/` plus the folders in `/Volumes`
  except the link to `/`; Linux `/` plus folders in `/media/$USER`, `/run/media/$USER` and
  `/mnt`.
- The OS is injected (enum `Os` from `os.name`) so all three branches are unit-tested on any
  machine with a fake environment and a temp folder.

### D5 Reveal per OS (`FileManagerLauncher`)
`ProcessBuilder` with separate arguments, never a shell; only existing absolute paths reach it.
- Windows: `<SystemRoot>\explorer.exe /select,<file>`; folder: `<SystemRoot>\explorer.exe <folder>`.
  Explorer is started by its full path (`SystemRoot`, else `windir`, else `C:\Windows`), never
  looked up through `PATH` or the working folder. Its exit code is meaningless (often 1 on
  success), so success means "process started".
- macOS: `open -R <file>`; folder: `open <folder>`.
- `open` and `xdg-open` are given 2 s to exit: a non-zero exit code (e.g. `xdg-open` 3 "no tool
  found", 4 "action failed") counts as failure and leads to 503; an opener still running after
  2 s counts as started, because `xdg-open` without a recognised desktop can stay in the
  foreground for as long as the file manager runs.
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
- [The 10000-entry cap is applied after reading the whole folder, because sorting needs every
  entry] → acceptable for now; a huge folder on a slow share can run into the 10 s limit and
  answer 504 although only 10000 entries would be returned. Revisit (e.g. stop reading after a
  bound and sort what was read) if this shows up in practice.
- **Known limitation:** repeated clicks on one dead network drive can block all 8 workers; until
  their OS calls return (Windows SMB timeout, possibly much longer on a hard NFS mount), every
  listing, also of reachable folders, answers 503 and the chooser asks to try again in a minute.
  The pool-full check runs before the timeout, so in that state no listing reaches the 504 path.
  A per-drive/share guard was considered and rejected as too much machinery for a rare case.
- [macOS privacy (TCC) prompts] → the places endpoint only checks that Desktop, Documents and
  Downloads exist, which does not read their contents; the first *listing* of one of them, or of
  a network or removable volume under `/Volumes`, makes macOS ask once whether the agent's process
  may access that folder. A denied prompt surfaces as 403 in the chooser ("may not be read"); the
  user can allow it later in System Settings > Privacy & Security > Files and Folders. Checked
  manually in task 7.2.
- [Entry lookup, reveal and archive opening also touch the file system and could stall on a dead
  share] → not guarded in this change; they are single user actions, not repeated listing
  clicks. Revisit if it shows up in practice.
- [Listing exposes the file system to the session] → same reach as opening an archive by path;
  `/api/*` is guarded by session cookie, Host and `Sec-Fetch-Site` checks.

## Migration Plan

Additive; no data migration. `openapi.yaml` of the local app is regenerated with
`openApiUpdate` and committed.

## Implementation Notes

- `FolderListings` uses a `Semaphore` plus a cached daemon pool instead of a `ThreadPoolExecutor`
  with a `SynchronousQueue`: the permit is released only when the file-system call returns, which
  makes "all 8 busy → 503" exact and race-free.
- Entry lookup and reveal touch the file system on the request thread (no time limit), as
  accepted in Risks; an entry whose attributes cannot be read is answered 404.
- `Places` and `FileManagerLauncher` take the OS, environment and process starter as
  constructor arguments, so every OS branch is unit-tested on any machine; HTTP tests replace
  them with `QuarkusMock`, so no real file manager starts in CI.
- `FolderLister` unwraps `DirectoryIteratorException`, so a read error during iteration maps to
  403/404 like one when opening the folder. On Windows it reads `DosFileAttributes` once per
  entry (basic attributes plus hidden flag); elsewhere hidden is the name check of
  `Files.isHidden`. The OS decides, not the `dos` view, which Linux also offers via extended
  attributes.
- `Places` parses the `.reg` export itself (continuation lines, escaped `REG_SZ`, `hex(2)`), so
  the non-ASCII handling is unit-tested on any OS.
- i18n: the key-parity test that task 3.2 assumed did not exist (only the type check in
  `i18n/index.ts`, which finds missing but not extra keys); `frontend-shared/src/i18n/keys.test.ts`
  now checks keys and `{{placeholders}}` of de, fr and it against en. The chooser and inspect
  texts live in the shared package like all other texts; "no longer open" and the lost session
  reuse `archive.notOpen` and `session.missing`.
- `normalisePastedPath` returns Windows paths from `file://` URIs with backslashes
  (`file:///C:/x` → `C:\x`, `file://server/share` → `\\server\share`) and leaves typed paths
  as they are; the agent normalises separators anyway.
- `normalisePastedPath` converts `file://` URIs in the frontend, without knowing the agent's OS:
  a URI with a host (`file://server/share`) and a drive URI (`file:///C:/x`) become Windows paths.
  Only Windows produces these shapes; on macOS/Linux the result is not absolute for the agent,
  which answers 400 and the chooser shows its error. Moving the conversion into the agent
  (`Path.of(URI)`, OS-aware) was considered and kept out, to leave the API taking plain paths only.
