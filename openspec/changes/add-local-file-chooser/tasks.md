# Tasks

## 1. Agent file API

- [ ] 1.1 Add `localwebapp.files` with path parsing/validation (D2) and `GET /api/files` listing with filters, sorting, 10000-entry cap and the 10 s limit on a pool of 8 daemon threads without queue (D3, Risks); verify a test with an injected blocking lister gets 504 after the limit and 503 at once when all 8 workers are blocked, both without `Retry-After`, and with `FilesResourceTest` covering every scenario of "Folder listing", "Listing filters" and "Large folders" (temp folders, incl. a hidden entry and an unreadable folder on POSIX; 401 via `Sessions`): `./gradlew :siard-web:local:backend:test --tests '*FilesResourceTest'`
- [ ] 1.2 Add `GET /api/files/entry` (incl. `writable`); verify the "Entry lookup" scenarios in `FilesResourceTest`
- [ ] 1.3 Add `Places` with the per-OS rules of D4 and `GET /api/files/places`; verify `PlacesTest` for Windows (fake `reg query` output, OneDrive path), macOS (`/Volumes` fake) and Linux (`user-dirs.dirs`, fallback) plus the "Places" scenarios in `FilesResourceTest`
- [ ] 1.4 Add `FileManagerLauncher` with the per-OS commands of D5 and `POST /api/files/reveal`; verify `FileManagerLauncherTest` asserts the exact argument lists for file/folder on all three OS, the Linux fallback and 503, and `FilesResourceTest` covers the "Show in file browser" scenarios (400/404 start no process, 401)
- [ ] 1.5 Regenerate and commit `siard-web/local/openapi.yaml` (`./gradlew :siard-web:local:backend:openApiUpdate`) and document the `/api/files` endpoints in `siard-web/local/README.md`; verify `./gradlew :siard-web:local:backend:check` passes (incl. `SharedApiDescriptionTest`, `openApiCheck`)
