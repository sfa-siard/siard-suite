# Proposal

## Why

The connection details page of the local web app hides the JDBC URL the archive run will use, while the desktop app shows it and lets users paste one. Users who get a JDBC URL from a DBA cannot paste it, and cannot check the URL their inputs produce (issue #225).

## What Changes

- The connection details page shows a JDBC URL input, derived from server, port, database name and options of the selected DBMS.
- Editing server, port, database name or options updates the URL immediately.
- Editing the URL updates server, port, database name and options when the URL is valid for the page's DBMS; an invalid URL shows an inline error, leaves the other inputs unchanged and blocks saving.
- The URL is derived on the page only: it is not added to the saved profile, the API or the SQLite store, and it never contains the user name or password.
- The URL input is placed after the existing additional fields (options) and before the divider, spanning the form width.

Out of scope: file-based DBMS (MS Access), the MS SQL `trustServerCertificate` checkbox of the desktop app, user/password parameters inside the URL, and IPv6 host literals (the existing decoders split on `:` and do not support them either).

## Capabilities

### New Capabilities

_None._

### Modified Capabilities

- `local-web-connections`: the "Connection details page" requirement gains the JDBC URL input in its layout and stacking order; a new requirement defines the two-way sync between the URL and the connection inputs.

## Impact

- `siard-web/local/frontend`: `ConnectionDetailsView.tsx` and its tests; a new JDBC URL encode/decode module with unit tests; the e2e archive wizard spec.
- `siard-web/frontend-shared/src/i18n/{de,fr,it,en}.json`: label and error texts for the URL input.
- No backend, API (`openapi.yaml`) or persistence change. No new dependency.
