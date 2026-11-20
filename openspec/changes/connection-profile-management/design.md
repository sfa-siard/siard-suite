# Design

## Context

See proposal.md for motivation. Relevant current state:

- `siard-suite-app` holds the only existing connection model (`service.database.model`, `DbmsRegistry`) and preferences (`RecentDbConnection`, `UserPreferences` on Java Preferences, capped at 3 recents, no passwords). It is a JavaFX module the web backend cannot depend on and is being retired — it stays untouched.
- `siard-web/local/backend` (Quarkus) already has `siard-cmd` and all six JDBC drivers transitively; it serves the built frontend and enforces the agent's session/Host/`Sec-Fetch-Site` checks on `/api/*`.
- `siard-web/backend-core` forbids siard-cmd, jdbc-* modules and JDBC drivers on its classpath, so the model cannot live there yet even though it is driver-free. It moves there when the remote app arrives.
- `SpecialFolder.getUserDataHome()` resolves `%APPDATA%\<app>` on Windows but `~/<data>/<app>` on Unix — not the macOS `~/Library/Application Support` or Linux XDG convention, so it is not used.
- Frontend uses `@tanstack/react-query` + `openapi-react-query` against the generated typed client, MUI components, and i18next with resources in `frontend-shared/src/i18n/{de,fr,it,en}.json`.

## Goals / Non-Goals

**Goals:**

- Working `/archive` picker and `/archive/connection` details pages, localized, responsive.
- DBMS catalog and saved-connection CRUD API behind the existing session checks.
- SQLite persistence of profiles (no passwords) in the per-OS app-data dir.
- Port of the DBMS/connection model into the local backend, modern style, driver-free.

**Non-Goals:**

- Opening JDBC connections, testing connectivity, schema/table browsing, or archiving.
- MS Access / file-based DBMS in the UI (model keeps `FileBasedDbms`; the `file` column is reserved).
- Renaming or otherwise managing profiles beyond delete.
- Sharing stored connections with the desktop app; they use different stores by design.
- Moving the model to `backend-core` (deferred to the remote-app change).
- A generic settings/kv store — no consumer exists yet.

## Decisions

### D1: Model ported into `localwebapp.database`, not shared yet

`Dbms`, `DbmsConnectionProperties`, `ServerBasedDbms(ConnectionProperties)`, `FileBasedDbms(ConnectionProperties)`, `DbmsConnectionData` and a `DbmsRegistry` equivalent are ported to `ch.admin.bar.siardsuite.localwebapp.database` in modern style: plain final classes/records, no Lombok, no `CastHelper`/`OptionalHelper` (replaced by `instanceof` patterns and `Optional` chains), SLF4J where needed. The registry keeps all six DBMS so `FileBasedDbms` survives for the future Access flow; the catalog API filters to the five server-based entries.

*Alternative considered:* putting the model in `siard-cmd` or `backend-core` now — rejected per the README rule (share only when app-agnostic today) and because `RecentDbConnection`/`UserPreferences` rightly stay in the desktop app.

### D2. `SavedConnection` record + `ConnectionStore` interface in `localwebapp.connections`

A flat record (`id`, `name`, `dbmsId`, `host`, `port`, `dbName`, `user`, `options`, `file`, `createdAt`, `updatedAt`) — no password field exists, so none can leak. `ConnectionStore` is a small interface (`list`, `create`, `update`, `delete`) with a SQLite implementation; tests can substitute an in-memory implementation. This keeps the persistence detail swappable when the remote app arrives.

*Alternative considered:* reusing `DbmsConnectionData` as the stored shape — rejected because it carries a password supplier and JDBC-URL semantics that don't belong in a profile record.

### D3. Plain JDBC + sqlite-jdbc, hand-written SQL

One `connections` table, no ORM. sqlite-jdbc is Apache-2.0 and bundles natives for Windows/macOS/Linux. Version pinned in `libs.versions.toml`. Store initializes schema on startup/first use; single-writer local app needs no pooling — one lazily opened connection is enough.

*Alternative considered:* Quarkus Hibernate/Panache — rejected as disproportionate for one table.

### D4. DB path resolved per OS, overridable by config

`siard.connections.db` property (default: per-OS app-data dir + `siard-suite-web/siard-suite.db`):

- Windows: `%APPDATA%\siard-suite-web\` (fallback `%USERPROFILE%\AppData\Roaming`)
- macOS: `~/Library/Application Support/siard-suite-web/`
- Linux/other: `${XDG_DATA_HOME:-~/.local/share}/siard-suite-web/`

Directory created with user-only permissions where the OS allows (POSIX `700`/`600`; Windows ACLs are user-profile defaults). `SpecialFolder` not used (wrong Unix layout, see Context).

### D5. REST surface mirrors the desktop dialog's information, not its shape

- `GET /api/dbms` → catalog entries `{id, name, kind, fields[], examples}` — enough for the form without exposing encoder functions.
- `GET /api/connections` → profile list ordered by name.
- `POST /api/connections` → 201; 400 on validation failure, 409 on duplicate name (DB `UNIQUE` constraint on `name` mapped to 409).
- `PUT /api/connections/{id}` → 200 with the updated profile; 400 on validation failure, 404 for an unknown id, 409 when the name belongs to another profile. Saving an opened saved profile uses this, so it never re-creates the profile.
- `DELETE /api/connections/{id}` → 204 / 404.
- Ports are validated as whole numbers 1–65535 in both backend and frontend.

All under `/api/` so existing session, Host and `Sec-Fetch-Site` filters apply unchanged; errors via the core's problem-details mapper. `openApiUpdate` regenerates `openapi.yaml`; the frontend client is regenerated from it.

### D6. Frontend: two routes, react-query, shared i18n

- `/archive` → `ArchiveView`: saved-connection list (name + target summary + delete IconButton with MUI confirm `Dialog`) above a DBMS grid (MUI `Card`/`ButtonBase` per catalog entry).
- `/archive/connection?dbms=<id>` | `?connection=<id>` → `ConnectionDetailsView`: fields driven by the catalog entry; saved profiles prefill all but password and keep the DBMS fixed in page state. The heading identifies the DBMS; no disabled or read-only DBMS input is rendered. The optional name field gates the save without a separate toggle; submit returns to `/archive`.
- New forms initialize host to the editable value `localhost` and port to the selected DBMS's default: PostgreSQL `5432`, MySQL `3306`, Microsoft SQL Server `1433`, Oracle `1521`, DB/2 `50000`. These are values, not placeholders. Use the catalog's existing per-DBMS port value rather than maintaining a second frontend port map; catalog example hosts remain placeholders, not initial values. Other fields start empty. Saved profiles take precedence, including empty optional values; defaults are applied only when initializing a new connection, never reapplied over user edits.
- Use a responsive MUI grid: at desktop width, server and compact port share the left half of the first row, with database name on the right; username is on the left of the second row, password on the right. Existing additional fields follow, then the optional connection name spans the form width. At narrow widths, stack server, port, database name, username, password, additional fields and connection name in that order, without horizontal overflow. Keep labels localized and the existing wizard action bar unchanged.
- This layout update does not add schema selection or connection-URL editing from the desktop screenshot.
- Start page Archive button becomes a `Link`/`navigate` to `/archive`; other buttons stay inert.
- New i18n keys under `archive`/`connections` namespaces in `frontend-shared/src/i18n/*.json` (the established single location for resources).
- Password field renders (`type="password"`) but is never included in mutation payloads — held in local component state only.

*Alternative considered:* a stepper/wizard like the desktop — rejected; two flat routes are simpler and the workflow continues in later changes.

## Risks / Trade-offs

- [sqlite-jdbc native binaries per platform] → pinned version, `check` runs on all three OSes in CI; fallback is a pure-Java SQLite mode if natives fail (xerial supports `org.sqlite.SQLiteConfig` pure-java envar, noted if needed).
- [Corrupt or unwritable DB file] → store init failure surfaces as problem+json 500, agent keeps running; no auto-repair in this change (logged for follow-up).
- [Model drift between desktop and web copies] → accepted: desktop is frozen and will be deleted; divergence is bounded. Documented in code comment at the port site.
- [Password field collects but never sends] → interim UX oddity, deliberate: it preserves form parity and gives the connect change a ready field. Spec scenario pins the no-transmission behavior.
- [Catalog JSON shape may need fields the form didn't anticipate] → `fields[]` is descriptor-driven so additions don't break the contract; OpenAPI is the contract of record.
