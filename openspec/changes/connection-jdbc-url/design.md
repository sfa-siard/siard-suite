# Design

## Context

- `ConnectionDetailsView.tsx` keeps all inputs in one `values` state keyed by the catalog field keys (`host`, `port`, `dbName`, `user`, `password`, `options`). The catalog (`GET /api/dbms`) deliberately does not expose the JDBC encoders (`DbmsCatalogEntry`, design D5 of the connection-profile change).
- The URL formats live in the backend's `DbmsRegistry` (a port of the frozen `siard-suite-app` copy):
  - DB/2, MySQL, PostgreSQL: `jdbc:<id>://host:port/db[?options]`
  - Oracle: `jdbc:oracle:thin:@host:port/db[?options]`
  - MS SQL Server: `jdbc:sqlserver://host:port;databaseName=db[;options]`
- The desktop form (`ServerBasedDbmsConnectionPropertiesForm`) syncs the same way: field edits re-encode the URL, URL edits decode into host/port/database/options.

## Goals / Non-Goals

**Goals:**
- Instant two-way sync on every keystroke, without a request per keystroke.

**Non-Goals:**
- Changing the backend codec, the catalog, the API or the saved profile.
- Sharing one codec between Java and TypeScript.

## Decisions

### D1. Encode/decode in the frontend

A small module `siard-web/local/frontend/src/views/jdbcUrl.ts` exports `encodeJdbcUrl(dbmsId, values)` and `decodeJdbcUrl(dbmsId, url)` for the five server DBMS ids. `decodeJdbcUrl` returns `undefined` for anything it cannot parse instead of throwing.

Alternatives: a backend encode/decode endpoint keeps one codec, but needs a round trip per keystroke (debounce, stale responses, loading state) plus API and OpenAPI changes; exposing a URL template in the catalog covers encoding only, not decoding. The URL here is display/input help only: the archive run will still build its URL in the backend from the saved fields, so a drift in the frontend copy cannot change what gets archived.

### D2. Strict decoding

Decoding uses one regular expression per format, anchored at both ends, requiring the exact prefix of the page's DBMS (`jdbc:postgresql://`, not just `jdbc:postgresql`), a non-empty host, a port of digits and the database part. MS SQL requires a `databaseName=` part; the remaining `;`-separated parts become options, as in the backend decoder. Stricter than the Java decoders (which throw on index errors) so a half-typed URL is reported invalid rather than half-applied.

### D3. Form state

The URL text is its own state (`jdbcUrl`) next to `values`, plus a `jdbcUrlError` flag.
- A change of host/port/dbName/options sets `values` and re-encodes `jdbcUrl`, clearing the error.
- A URL change stores the raw text; if it decodes, it merges host/port/dbName/options into `values` (user, password, name untouched) and clears the error; otherwise it sets the error and leaves `values` unchanged.
- `submit` returns early with the URL error shown while `jdbcUrlError` is set. The URL is never put into the request body (the body is built from `entry.fields`, which do not include it).
- Initial `jdbcUrl` is encoded from the initial `values`, so new and saved connections show it on load.

### D4. Placement and texts

The URL `TextField` is rendered after the additional fields and before the divider, `gridColumn: "1 / -1"`, with the encoded catalog examples as placeholder. New i18n keys `connections.fields.jdbcUrl` and `connections.invalidJdbcUrl` in de/fr/it/en of `frontend-shared`.

## Risks / Trade-offs

- [Frontend codec drifts from `DbmsRegistry`] → unit tests use the same example URLs as the backend registry's formats; the archive run never uses the frontend URL.
- [Options containing credentials, e.g. `?password=x`, are saved as options] → existing behavior of the options field, unchanged by this change; the URL itself never adds user or password.
- [IPv6 hosts are not decodable] → same limit as the existing decoders; out of scope.

## Platforms

Frontend-only; browser tests already run on Linux, macOS and Windows in CI, no platform-specific code.
