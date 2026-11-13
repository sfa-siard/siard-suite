# Tasks

Precondition: `restructure-into-siard-web` is applied. Test scope: one test per spec scenario, nothing beyond; ask before adding any other test (as in the setup change, design D15). Fixtures are read from `siard-api/src/test/resources/testfiles/` and never copied.

## 1. Spikes (results recorded in design.md, "Spikes")

- [ ] 1.1 S2 read-only open: open `sample.siard` from a read-only file in a read-only directory through `ArchiveImpl.open(File)` and record whether it succeeds, the file's size and mtime before and after, and any new files; if it needs write access, add a minimal read-only open path to `siard-api` (modern style, legacy code otherwise untouched) with a test that opens a read-only copy; verify with that test
- [ ] 1.2 S1 thread-safety: in a throw-away test, issue 8 concurrent page reads of `payment` in `sfdbsakila.siard` against one archive with no lock and compare with sequential results; record the outcome and whether D3's lock may be relaxed; verify the finding is written into design.md (the throw-away test is not committed)
- [ ] 1.3 S3 cost: on `sfdbsakila.siard` measure open time, heap per open archive, `skip(n)` cost at 0, 5000 and 16000, and `Value.getInputStream()` vs `getBytes()` for an external LOB of `sample.siard`; verify the numbers are in design.md and that the default limits (5/50, 30 min) are confirmed or changed accordingly
- [ ] 1.4 S4 build: prove in `backend-core` that a JAX-RS resource and a CDI bean are discovered by the local app, that a `@QuarkusTest` runs in the core (else plan the tests in the local backend, design D13), that resource methods run on worker threads, that `quarkus-scheduler` is available, and that the SmallRye OpenAPI Gradle plugin generates a description with a sealed `CellDto` union; verify each result is recorded in design.md D10/D13

## 2. Archive registry

- [ ] 2.1 Add `ProblemException(status, detail, type)` to the core's error mapper with the pass-through of `detail` and the `archive-closed` type constant (design D9); verify a unit test shows `detail` is returned and a framework exception message is still hidden
- [ ] 2.2 Implement `ArchiveRegistry` (id generation, register/acquire/close, per-archive lock with timeout, scope check, `onRelease` exactly once on every path) and `ArchiveScope`/`ScopeResolver` with the JAX-RS filter on `/api/archives/**`; verify the spec scenarios of "Archives are referenced by opaque ids", "Archives are isolated by scope" and "Archives can be closed explicitly" pass in a `@QuarkusTest` with a test `ScopeResolver` (two scopes) and a test-only registration resource
- [ ] 2.3 Add limits, idle eviction (injectable clock, scheduler every minute, configuration properties of design D2) and shutdown release; verify the scenarios of "Open archives are limited per scope", "Idle archives are closed" and "Open archives are released on shutdown" pass
- [ ] 2.4 Verify the scenarios of "Concurrent requests on one archive are safe" (8 concurrent pages equal their sequential results; a slow request on archive X does not block archive Y), after the paging endpoint of 3.2 exists; document the registry contract for app authors (what to implement: `ScopeResolver`, when to call `register`, what `onRelease` must do) in the module README and verify the README example matches the test resource

## 3. Archive API

- [ ] 3.1 Implement `GET /api/archives/{id}`, `/schemas/{s}`, `/schemas/{s}/tables/{t}` and `/schemas/{s}/views/{v}` with record DTOs mapped from `siard-api`; verify the scenarios of "Archive metadata" and "Schema, table and view structure" pass against `sample.siard`, including 404 for bad indexes, 405 for write methods with the file unchanged, and a name containing `../` returned verbatim (small crafted archive built in the test)
- [ ] 3.2 Implement `rows?offset=&limit=` with the retained-cursor strategy of design D4 and the records-read counter; verify the scenarios of "Paged table rows" pass on `payment` (page, last partial page, offset beyond end, 400s, forward paging reads at most 100 records)
- [ ] 3.3 Implement `CellRenderer` and the cell DTO union (design D6); verify the scenarios of "Cell values" pass (NULL, text equal to the desktop display, 4096 truncation, LOB cells carry no content and the page stays under 20000 bytes) and compare the rendered text of every `TSIMPLE` cell with `Value`'s display conversion
- [ ] 3.4 Implement the LOB/text content endpoint with streaming and the attachment-only headers (design D7); verify the scenarios of "Large object content" pass, including the byte-for-byte comparison with the zip entry and the `<script>` content case
- [ ] 3.5 Implement the bounded search (design D5) with the budget properties; verify the scenarios of "Bounded search in a table" pass, with the budget set to 5000 rows for the exhaustion case, and that a table scan releases the archive lock between requests

## 4. OpenAPI

- [ ] 4.1 Add the core's generated `openapi.yaml`, `openApiCheck`/`openApiUpdate` tasks wired into the core's `check` (per the S4 result), and regenerate the local app's `openapi.yaml`; verify the scenarios of "The archive API is described and stays consistent across apps" (core description current, app contains the shared paths unchanged, drift fails the build naming the command)
- [ ] 4.2 Add the test in the local backend that compares the `/api/archives/**` paths and schemas of both descriptions; verify it fails when a field is changed in only one file and passes after regeneration

## 5. Shared frontend views

- [ ] 5.1 Generate `core-schema.d.ts` from the core description into `frontend-shared` (`gen:api`, not committed, run by `typecheck` and `build`), add `@mui/x-data-grid` and a `react-router` peer dependency, and create the `ArchiveBrowser` skeleton with relative routes (design D11); verify `./gradlew :siard-web:frontend-shared:check` passes and the local frontend still builds
- [ ] 5.2 Implement the structure tree and the table/view detail views with the data grid (server paging, one page size, sorting and filtering off, LOB download cells); verify the scenarios "Browse a table" and "Paging requests only the needed page" pass with a mocked `fetch`
- [ ] 5.3 Implement the search field and "next" with continuation on `complete: false`, match marking and page jump; verify the scenario "Search jumps to the match" passes
- [ ] 5.4 Implement the error states (`archive-closed`, 401 through `onUnauthorized`, load errors) and add all texts to the de/fr/it/en files, reusing desktop keys where they exist; verify the scenarios "Archive no longer open", "Session lost" and "Language" pass and that the typed-key check finds no key missing in any language

## 6. Integration and documentation

- [ ] 6.1 Run `./gradlew :siard-web:check :siard-web:local:e2eTest` and verify it is green with no change to the existing local-app test counts other than the new tests; verify `siard-web:backend-core` still passes `verifyCoreBoundary`
- [ ] 6.2 Update `AGENTS.md` only where the module descriptions changed (the core now serves `/api/archives`), and the README of `siard-web` with the archive API overview, the limits and their properties, and a note that no app exposes "open archive" yet; verify every property named in the README exists in `application.properties` of the core's test or the apps

## Workflow follow-up

- Archive after review. Next changes: `add-local-archive-picker` (native dialog, `POST /api/archives/pick`, local open view and route) and `add-remote-web-app` (server shell, scope from the authenticated user, store and upload).
