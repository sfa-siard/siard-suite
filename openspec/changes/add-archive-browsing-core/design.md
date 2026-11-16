# Design

## Context

See `proposal.md` (Why) and `restructure-into-siard-web` for the module layout: this change adds code to `siard-web/backend-core` (depends on `siard-api` and Quarkus only, never `siard-cmd`/JDBC) and `siard-web/frontend-shared` (source-only npm package, no shell). Facts about `siard-api` verified for this design:

- `Archive.open(File)` takes a `java.io.File`, so an archive must exist as a file on disk. The implementation calls `new Zip64File(file)`, not the `(File, boolean readOnly)` constructor, and that opens the file in mode `rw` (`DiskFile(File)`), so a write-protected archive cannot be opened at all. A read-only open is therefore needed (archives on a server are typically write-protected originals). `Zip64File` and `DiskFile` already have a read-only mode; whether `ArchiveImpl` works through it (no write during open, `validate()`, reading or `close()`) is **unverified** (spike S2).
- Rows are read through `Table.openTableRecords()` returning a forward-only `TableRecordDispenser` (`get()`, `skip(long)`, `getPosition()`, `close()`). There is no seek. The total comes from `MetaTable.getRows()`. The desktop app (`RowsOverviewForm.TableRecordDataSource`) opens a new dispenser and calls `skip(startIndex)` for every page.
- `Search` (`find`, `findNext`, `getFoundRow`...) keeps its position inside the object, so it cannot serve independent clients.
- `Table.sort(...)` mutates archive state; this change does not expose it.
- Views carry metadata and a defining query, no row data. LOB content is either inline (`cell.getBytes()`), a separate file in the zip, a file in an external LOB folder on disk (`MetaColumn.getAbsoluteLobFolder()` + `cell.getFilename()`; a relative LOB folder must start with `../` and is resolved against the directory containing the `.siard` file, an absolute one is used as is), or a data link URL that `siard-api` opens with `URL.openStream()` (`ValueImpl.getInputStreamFromLobFile`); the desktop app detects binary columns by `getPreType()` being `BINARY`, `VARBINARY` or `BLOB`.
- The tracked fixtures are `sample.siard` (1 schema, tables `TSIMPLE` 4 rows and `TCOMPLEX` 2 rows with LOBs, views `VIEW1`/`VIEW2`) and `sfdbsakila.siard` (tables up to 16049 rows: `payment`, `rental`), both in `siard-api/src/test/resources/testfiles/`.
- The setup change deferred a `ProblemException(status, detail)` to "the first change with an intentional client error" (setup design D10). This is that change.

## Goals / Non-Goals

**Goals:**
- One read-only API and one set of browse views that both apps use unchanged.
- Safe on a shared server: isolation between scopes, bounded resources, no cost that grows with other users' behaviour, no filesystem paths in the API.
- Decisions that the remote app will need (scope seam, limits, eviction) made here, so the local app does not have to be restructured later.

**Non-Goals:**
- Opening, uploading or locating archives (app-specific; follow-up changes).
- Authentication and the meaning of a scope in the remote app.
- Sorting, filtering, exporting, editing, routines/types/users/privileges/triggers views, metadata search.
- Random access performance beyond forward paging (deferred, see D4).

## Decisions

### D1: The registry is a scoped, locked holder; apps supply the scope

```
 app-specific                         backend-core
 +------------------+   register(scope, file, onRelease)  +-------------------+
 | local: picker    |------------------------------------>|  ArchiveRegistry  |
 | remote: store/   |<------------ archiveId -------------|  id -> Entry      |
 |         upload   |                                     |  (scope, Archive, |
 +------------------+                                     |   lock, lastUsed, |
 | ScopeResolver    |---- resolve(request) -------------->|   cursors)        |
 +------------------+                                     +---------+---------+
                                                                    | acquire(scope,id)
                                                          +---------v---------+
                                                          | resources (REST)  |
                                                          +-------------------+
```

- `ArchiveRegistry.register(ArchiveScope scope, Path file, Runnable onRelease)` opens the archive with `siard-api`, stores it and returns an `ArchiveId`. On any failure it calls `onRelease` once and throws; nothing is retained. `onRelease` is how an app deletes an uploaded temp file or releases a store lock, exactly once, on every path (explicit close, eviction, failed registration, shutdown).
- `ScopeResolver` is a CDI interface the **app** must implement (`Optional<ArchiveScope> resolve(ContainerRequestContext)`). The core has no default, so an app that forgets it fails at build time (unsatisfied bean) instead of serving archives to everyone. A JAX-RS request filter on `/api/archives/**` calls it and answers `401` when it is empty. The core never reads cookies or sessions.
- Lookups are by `(scope, id)`: a foreign id and an unknown id take the same code path and produce the same body, so existence does not leak.
- `ArchiveId`: 16 bytes from `SecureRandom`, base64url without padding (22 characters).
- *Alternative:* passing a filesystem path in the API (what the PoC did): unshareable with a server and an arbitrary-file-open primitive. Rejected for good, not just for now.

### D2: Limits and eviction

Defaults (configurable): `siard.archives.max-per-scope=5`, `max-total=50`, `idle-timeout=PT30M`, `lock-timeout=PT30S`. Registration beyond a limit throws; it never evicts someone's archive that may be in use. Idle archives are closed by a scheduler (`quarkus-scheduler`, once per minute); `evictIdle()` is also callable directly so tests drive it with an injectable clock (pattern of the setup change's `MutableClock`). A new dependency on one Quarkus extension is the cost of releasing files without a periodic request. Every `acquire` updates `lastUsed`. A closed archive is removed from the registry and answers exactly like an unknown id (`404`, same body), so a closed id does not tell another scope that it ever existed and the registry keeps no record of closed ids. The frontend treats a 404 as "not or no longer open" (bad indexes do not occur through the UI, which builds them from server responses). Application shutdown closes everything via a `ShutdownEvent` observer.

*Alternative:* evict only lazily on registration. Idle archives would then hold file handles and memory for as long as no one registers again.

### D3: One lock per archive, taken for the whole request

`siard-api` objects are not documented as thread-safe, and dispensers share the zip's file position. A request takes the archive's lock (`ReentrantLock`, `tryLock(lock-timeout)`, then `503` with problem details) for its full duration, including streaming a LOB. Different archives never contend. Resource methods are blocking (plain return types run on worker threads in Quarkus REST; checked in S4). Whether a per-archive lock is stricter than needed is S1; it is the safe default until S1 says otherwise.

### D4: Forward paging with a retained cursor; no row index yet

Each entry keeps at most 2 open paging `TableRecordDispenser`s (LRU per archive), each with its table and position, plus one search cursor (D5) that paging never uses. A page request with `offset` reuses the cursor if its position is `<= offset` (skipping the difference) and reopens it otherwise. The next page costs at most `limit` record reads. The previous page and jumps reopen the cursor and cost `O(offset)`; the second cursor only helps when two positions alternate. The registry exposes a counter of records read, which the "forward paging does not rescan" scenario asserts.

A jump on a table with millions of rows is slow and would hold the archive's lock meanwhile. Accepted for v1 and bounded by the lock timeout; the follow-up if it matters is a sparse position index (every N rows) built lazily per cursor, which does not change the API. S3 measures real numbers before the budget is trusted.

*Alternative:* replicate the desktop app (reopen + skip every time). On the server this means every page click rescans from row 0.

### D5: Search is a bounded, stateless scan on the same cursor machinery

`siard-api`'s `Search` is stateful, so each request does its own scan from `afterRow + 1`. The scan uses a dedicated search cursor per archive (one `TableRecordDispenser` with its table and position, separate from the paging cursors of D4, so a search never evicts the user's paging position). A request reuses it when it is on the same table and its position is `<= afterRow + 1` (so a continuation from `nextRow` or after a match reads no row twice) and reopens it otherwise. The scan compares the displayed text of each cell (the same renderer the grid uses, so a visible value is findable and a match is always visible when the view jumps to it): the first 4096 characters of textual and numeric cells, including CLOB, NCLOB and XML, of which only that prefix is read (`getReader()`, not the whole content). Text beyond 4096 characters and binary cells are not searched. This bounds the cost of a request to rows × columns × 4096 characters; a full-text search over whole CLOBs would need a character budget and stream matching (follow-up if needed, see Open Questions). It stops at a configured budget (`siard.archives.search.max-rows=20000`, `max-time=PT5S`) and returns `nextRow` so the client continues with a new request; the lock is released between requests, so a search over a huge table never blocks the archive for long and the client controls the pace. `matchCase` maps to a case-insensitive compare when false.

### D6: Cells: one renderer, DTO as a small union

One `CellRenderer` reproduces what the desktop app shows (`RowsOverviewForm`: `getPreType()` decides; binary types are LOB; everything else is the cell's string form, temporal types formatted as `Value`'s display conversion does). Text is cut at 4096 characters with `truncated`; structured values (arrays, UDTs) are rendered as text and not as nested JSON, to keep the DTO a flat union (`null` | `text` | `lob`). Nested rendering can be added later without breaking the three existing shapes. The DTOs are records; `siard-api` objects are never serialised (setup design D10).

### D7: LOB content streams, with attachment-only headers

The content endpoint copies from the cell's input stream to the response without loading the LOB (single LOBs in `sample.siard` reach 2 MB; real archives hundreds of MB). Because archive content is arbitrary and is served from the app's own origin, it must never be interpreted by the browser: always `application/octet-stream`, `Content-Disposition: attachment` with a name built from indexes only (`t<table>-r<row>-c<col>.bin`), `X-Content-Type-Options: nosniff`. No type detection (the desktop app uses Tika; its result would only matter if content were rendered inline, which it is not).

The streamed bytes are written after the resource method has returned, so the archive lock (D3) is released inside the streaming writer, in a `finally`, when writing finishes, not when the method returns. Otherwise the lock would be released while the download is still reading from the archive. The same `finally` releases it when the download fails or is aborted (e.g. the user closes the tab mid-download); if that path forgot to release, the archive would stay locked until eviction and every further request on it would fail.

### D8: Addressing by zero-based index

`/schemas/{s}/tables/{t}/...` uses indexes; names are returned in bodies. SIARD names are arbitrary SQL identifiers (dots, slashes, unicode, quoting), which makes them poor path segments, and the archive is read-only so indexes are stable for its lifetime. Out-of-range or negative indexes are `404`.

### D9: Errors and `ProblemException`

Add `ProblemException(status, detail)` to the core: its `detail` is passed through to the RFC 9457 body and nothing else is (framework exception messages stay hidden, as before). All new client errors (`400` bad paging/search parameters, `404`, `429`, `503`) use it. All keep `type` `about:blank`; a stable `type` URI is added only when the frontend needs to branch on one.

### D10: OpenAPI for a library

The core has no Quarkus application of its own, so the build-time generation of setup D11 does not apply. Chosen to try first: generate the core's description with the SmallRye OpenAPI Gradle plugin from the core's classes, commit it as `siard-web/backend-core/openapi.yaml`, with `openApiCheck`/`openApiUpdate` tasks mirroring the app's. `frontend-shared` generates `core-schema.d.ts` from that file (not committed, as today). Each app's description is still generated by Quarkus and committed, and a JUnit test in each app reads both files and asserts every `/api/archives/**` path and the schemas it references are equal. Fallback if the plugin does not handle the sealed `CellDto` union or Jakarta REST annotations: generate the shared description from the local app's spec by path prefix; that couples the shared types to the local app and is worse. Verified in S4.

### D11: Frontend: views mount under the app's route, state lives in the URL

`frontend-shared` exports `ArchiveBrowser` (props: `archiveId`, `onUnauthorized`), which uses relative routes: `schemas/:s/tables/:t?page=`, `schemas/:s/views/:v`. An app mounts it at `archives/:archiveId/*`. State in the URL means a reload or a link keeps the position, as both backends keep the archive open. The package imports `react-router` as a peer dependency but owns no `BrowserRouter`. Data comes from `createApiClient<paths>()` (restructure R7) with TanStack Query; the app provides the `QueryClientProvider`. The texts reuse the desktop keys where one exists (`tableContainer.*`-style keys from the migrated JSON) and add the rest to all four language files.

### D12: Grid: MUI X Data Grid Community

`@mui/x-data-grid` (MIT; the free tier the project rules allow). `paginationMode="server"` with `rowCount`, one page size (100, the Community tier's maximum), sorting, filtering and column menu turned off because the server supports none. Virtualization must be disabled in jsdom tests. LOB cells render the size and a download link to the content endpoint.

### D13: Tests

One test per spec scenario (as in the setup change). Backend: `@QuarkusTest` with a test-only registration resource (`/api/test-archives`, in test sources like `ProbeResource`) that registers a fixture for a fixed or per-header scope and a test `ScopeResolver`; fixtures are read from `siard-api`'s test resources through a system property, not copied. Search tests set the time budget (`max-time`) high so that only the row budget can stop a scan, which keeps them independent of machine speed. Frontend: Vitest + Testing Library with a mocked `fetch`, no Playwright. If a `@QuarkusTest` cannot run inside the library module (S4), the same tests run in the local backend's test source set against the core, which is where the extension is already configured.

## Risks / Trade-offs

- **[`ArchiveImpl`/`Zip64File` is not thread-safe, or does not work through a read-only handle]** → S1/S2 before anything else; D3's lock is the safe default. The read-only open path in `siard-api` is expected to be minimal (pass the existing read-only mode through); if S2 shows `ArchiveImpl` writes while reading, the extent of the fix is decided from S2's findings before implementation continues.
- **[Jump to a late row of a large table is slow]** → bounded by the lock timeout; the sparse index of D4 is a compatible follow-up. Measured in S3 so the decision rests on numbers.
- **[Memory per open archive]** → `MetaData` is loaded as a JAXB tree on open and `validate()` runs once; the limits (5 per scope, 50 total) are guesses until S3 measures the largest fixture. Limits are configuration for that reason.
- **[Eviction closes an archive under a reader]** → eviction takes the archive lock; a request in flight finishes first, the next one gets `404`.
- **[Hidden exposure through LOB content]** → D7 headers, plus a scenario with `<script>` content.
- **[Scope seam is untested against a real second app]** → tested with two scopes in `@QuarkusTest`; the remote app's change is its real consumer and may extend the resolver contract, which is why it is one small interface.
- **[Core description generation for a library is unproven]** → D10 and S4; fallback named.
- **[The shared frontend does not look at all like the remote app's eventual design]** → the views take their theme from the app and contain no shell (restructure R7); per-app styling beyond that is a later change.

## Spikes (done first, results recorded in this file)

- **S1 thread-safety:** run concurrent page requests against one `ArchiveImpl` without the lock and see whether results differ; decides whether D3 may be relaxed to a read/write split.
- **S2 read-only open:** open the fixtures (`sample.siard`, `sfdbsakila.siard`, the SIARD 2.1 fixture `sfdbsakila_2_1.siard`) through a read-only `Zip64File` from a write-protected file in a write-protected directory; read all metadata, every row and every LOB, close the archive, and check that the results equal a normal read-write open and that the file is untouched (size, mtime, no new files). If it works, add `Archive.open(File, boolean readOnly)`-style support in `siard-api` with minimal edits to the legacy code; if not, record what writes and decide the fix from that.
- **S3 cost:** on `sfdbsakila.siard` measure open time, heap per open archive, cost of `skip(n)` at 0, 5000 and 16000, a previous-page request at offset 16000 extrapolated linearly to 1M and 10M rows and compared with `lock-timeout` (if it does not stay well below, the sparse position index of D4 is raised for a decision before 3.2 is implemented), and LOB streaming from the zip (`Value.getInputStream()` vs `getBytes()`).
- **S4 build:** prove Jandex/`beans.xml` discovery of core resources, that `@QuarkusTest` runs in the library module, that resource methods run on worker threads, that `quarkus-scheduler` works from the core, and that the SmallRye Gradle plugin produces the description (D10).

## Migration Plan

Additive: new endpoints and views, nothing existing changes except the local app's committed `openapi.yaml` (regenerated). Rollback is reverting the change; no data or config is migrated.

## Open Questions

- Remote-relevant: a slow LOB download holds the archive lock for its whole duration (D3, D7), so the user's other requests on that archive wait and get `503` after `lock-timeout`. The lock is per registry entry, so other users opening the same file are not affected unless an app shares one entry across a group scope. Suggested solution: copy the LOB from the zip into a temp file under the lock (disk speed, not client speed), release the lock, stream the temp file, and delete it when the response ends or is aborted. This requires temp disk space on the host, sized for the largest LOBs times the number of concurrent downloads (with a cap per scope or in total), and cleanup on every path including shutdown.
- Remote-relevant, **must be decided before the remote app serves archives**: LOBs stored outside the archive are a needed feature, but `siard-api` resolves them without restriction: an external LOB folder is read with `FileInputStream` on whatever path the archive's metadata names, and a data link is opened with `URL.openStream()` (any `file:` or `http:` URL). `getByteLength()` falls back to reading the content when the `length` attribute is missing, so even listing rows can open them. On a server, a crafted archive could use this to read server files or reach internal hosts. The local app behaves like the desktop app (the user's own machine and files), so this change serves external LOBs as `siard-api` resolves them. Suggested solution for the remote app: an app-supplied policy next to `ScopeResolver` that the core consults before opening any external LOB, with `length` taken from the `length` attribute only. Local: allow all. Remote:
  - Each archive is stored in its own directory, and its external LOB folders must be stored inside that directory (uploaded or stored together with the archive; how is part of the remote app's storage design). A shared upload directory would let a relative `../folder/` resolve to another user's files.
  - Only relative LOB folders are followed, and only if the resolved path stays inside the archive's directory, checked after resolving symlinks. Absolute LOB folders point to the machine that produced the archive (`C:\…`, `/home/…`) and are never followed on the server.
  - Data links are refused. `siard-api` opens them with `URL.openStream()`, which also fetches `http(s)` URLs, so a crafted archive could make the server send requests into its internal network (SSRF); with a `file:` URL it reads arbitrary server files. Data links are supported by the API only: no code in this repository produces them and no fixture uses them.
- Search behaviour differs from the desktop app, to be confirmed with the users: the desktop app filters only the rows already loaded in the client, but compares their full text including CLOBs. The web apps search the whole table, but only the first 4096 characters of each cell (D5). Whether finding text beyond 4096 characters matters (e.g. long texts archived as CLOB) decides whether a full-text follow-up is needed: whole CLOBs read as a stream with a character budget (`max-chars`) next to the row and time budgets, without changing the API.
- Remote-only: whether the scope also needs a role (read/admin) is decided with the remote app's authorization; the interface leaves room since a scope is an opaque value.
