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
  file) only pass a mode and its extensions (a list to open, one to save).

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
  non-zero within 5 s, `xdg-open <parent folder>`; folder: the same call with
  `org.freedesktop.FileManager1.ShowFolders "['<folder URI>']"`, then `xdg-open <folder>` if that
  fails. (First `xdg-open <folder>` only: in the manual run on GNOME it exited 0 without showing
  a window, although Nautilus is the default for folders, while `ShowItems` worked.)
- No process could be started → 503. The process runner is injected so tests assert the
  exact command lines per OS without starting anything.

### D6 Frontend chooser (`local/frontend/src/files/`)
- `FileChooserDialog` props: one option type per mode, plus `onClose(path | null)`:
  `{mode: "open", extensions?: string[]}` (files offered; none means any file),
  `{mode: "save", extension: string, initialName?: string}` (exactly the format the workflow
  writes) and `{mode: "folder"}`. Save does not take a list: deriving its extension from a list
  would depend on the list's order and let another listed extension through (`db.zip` with
  SIARD contents). A workflow that can write several formats would let the user choose the
  format ("Save as type"), not pass a list. Lives in the local app, not `frontend-shared`
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
- Save mode: the name gets the save extension appended unless it already ends with it (in any
  letter case; `db.zip` becomes `db.zip.siard`);
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
- The chooser is one `FileChooserDialog` component for all three modes; `useFileChooser` returns
  `{ choose, chooser }`, so a workflow renders `{chooser}` and awaits `choose(options)` without a
  context provider. Errors are mapped by status code only (401 session, 403 not readable,
  404 not found, 503 busy, 504 unreachable). In save mode, a name that matches an existing
  folder as typed opens that folder with an empty name instead of returning it, as native save
  dialogs do (spec scenario "Save with the name of an existing folder"). If only the name with the
  extension appended is a folder (`Archives` → folder `Archives.siard`), the chooser shows an
  error instead, because the user did not type that folder's name (spec scenario "Save where the
  name with the extension is a folder").
- `ArchiveInspectView` reads `GET /api/archives/{archiveId}` with the same query key as the shared
  `ArchiveBrowser`, so it adds no request; a 404 there shows `archive.notOpen` with a link back to
  `/`, a 401 shows `session.missing`. Close sends `DELETE` and goes to `/` whatever the answer:
  an archive that is already gone needs no closing.
- The start page opens the archive itself (`POST /api/archive-files`) and shows a failed open
  above the overview (422 cannot open, 429 too many, else open failed, 401 session as a warning),
  so the user stays where they can try again.
- Keyboard activation of Open: Open is a native `<button>`, which browsers activate with Enter and
  Space; jsdom does not, so `StartView.test.tsx` asserts the native button and the click path,
  and the keyboard itself is checked in the manual run (6.2). New tests share a small fetch router
  (`src/test/routes.ts`).
- DATALINK cells (found in the manual run with `test.siard`, task 5.4): `CellRenderer` read every
  type other than BINARY, VARBINARY and BLOB with the text path for long text, so `siard-api`
  decoded `test.siard`'s DATALINK file (`lob25/record0.bin`, a 47189-byte JPEG) as characters,
  rejected the length (46450 characters) and the whole page of rows answered 500. DATALINK
  cells are now shown as the desktop app shows them (`RowsOverviewForm.extractText`): the text
  of `getString()`, or empty when that throws, per cell (`CellRenderer.dataLinkText`). Checked
  by running that call on both archives: `sample-datalink-2-2.siard` shows its files' text (a
  `file://localhost/…/circle.jpg` path stored as ASCII), `test.siard` shows every DATALINK cell
  empty. The download answers the same text and search finds it.
- Content outside the archive is never opened: a cell with `dlurlpathonly` is shown empty and
  never read, whatever its type and also when it has a stored file (`CellRenderer
  .pointsOutsideTheArchive`, asked before every content access). To read the attribute,
  `siard-api` gets one accessor, `Value.getDataLinkUrl()` (a minimal addition next to
  `getFilename()`). This is the one deliberate deviation from the desktop app; see Follow-up
  Changes.
- The tests use `sample-datalink-2-2.siard` from the shared fixtures of `siard-api` (the same
  `TSIMPLE` with `COLUMN_DATALINK`), so the core keeps one fixture location and reads nothing of
  `siard-cmd`. Its DATALINK files are ASCII and readable, so the failure seen with `test.siard`
  is reproduced by a `CraftedArchive` with non-UTF-8 content. The URL case uses the `CBLOB`
  column, crafted (new `remove(entry)`) to link to a file in the test's temp folder, once
  without and once with the stored file; it fails when the URL check is left out. `siard-api`
  validates rows against the archive's own table schema and its `table.xsd` does not allow
  `dlurlpathonly`, so archives it writes never hold such a cell; the crafted archive also
  extends its `table0.xsd`.
- Structure tree links (found in the manual run, task 5.5): the tree's links were relative
  (`schemas/0/tables/1`). Under the splat route `archives/:archiveId/*`, react-router resolves a
  relative link against the whole current address, so each click appended to it and the views
  matched nothing (no request, no error). `ArchiveBrowser` now derives its mount path once (the
  address without the splat part) and the tree links absolutely from it; the app still decides
  where the browser is mounted. The earlier test only clicked from the mount point, where both
  resolve the same.
- `StructureTree` renders the schema name with `ListSubheader component="div"`: by default it is
  an `<li>`, which may not sit inside the schema's own `<li>` (a React warning in the manual run).

## Follow-up Changes

Recorded here, not part of this change.

- **Content outside the archive (deliberate deviation from the desktop app, to be investigated
  later).** A cell with `dlurlpathonly` (a URL instead of, or next to, a stored file, as
  `metadata.xsd` defines it) is shown empty and never read. The desktop app reads such cells:
  `getInputStream()` and `getReader()` of `siard-api` open that URL before a stored file, so
  the desktop's `getBytes()` (BLOB) and `getString()` (DATALINK, CLOB with a stored file) follow
  it. The reason for deviating is security: an archive could make the app open
  `file://server/share/…` (on Windows this can send the user's credentials to that server) or
  send `http(s)` requests to hosts it chooses. This also narrows D14 of the archive browsing
  design (external LOBs served as `siard-api` resolves them) for such cells, since D14 relied on
  "behaves like the desktop app". To investigate: whether such cells should show something (for
  example the URL as plain text, never as a link and never opened, or a cell kind such as `lob`
  with `external: true` and a label "stored outside the archive"), how external LOB folders
  (absolute paths of the producing machine) should be treated alike, and the remote app's
  confinement policy (D14's open question).
- **How DATALINK content is shown (to be investigated later).** It is shown as the desktop app
  shows it, which hides archived content that is not text: `test.siard`'s DATALINK files are
  JPEGs stored in the archive and appear empty in both apps. Tried during this change and set
  aside for parity: stored DATALINK content as a downloadable `lob` (archived data read only from
  inside the archive). To investigate: what DATALINK columns hold in real archives (the files
  themselves, or a path as text as in `sample-datalink-2-2.siard`), whether to offer a download,
  and that `getString()` reads a DATALINK file as a whole (the desktop does the same), which
  costs memory for large files.
- **One unreadable cell fails its whole page.** Other than DATALINK cells (now empty when they
  cannot be read), a cell that cannot be read still makes its page of rows answer 500. Showing
  such a cell as unreadable and the rest of the page normally needs a new cell type or flag in
  the API and spec.
