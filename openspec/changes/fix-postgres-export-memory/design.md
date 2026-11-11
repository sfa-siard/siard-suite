# Design

## Context

See `proposal.md` for motivation. The technical mechanism, verified against pgjdbc 42.7.11 source (`PgStatement.executeInternal`) and reproduced by `PostgresResultSetMemoryIT`:

A server-side cursor is used only when **all** of these hold:

```
fetchSize > 0  &&  !wantsScrollableResultSet()
&& (!autoCommit || transactionState == OPEN)
&& !wantsHoldableResultSet()
```

`PrimaryDataTransfer.openTable()` violates two conditions: it never sets a fetch size, and it passes `ResultSet.HOLD_CURSORS_OVER_COMMIT` — which `wantsHoldableResultSet()` treats as holdable, so the cursor flag can never be set. With `QUERY_FORWARD_CURSOR` absent, the driver reads the whole result set into the client-side row list during `executeQuery`. `openTable()` also calls `_conn.setHoldability(HOLD_CURSORS_OVER_COMMIT)`, so the connection default is holdable too. The `if (sm == null) / else` branches assign the same holdability in both arms — dead code introduced in `a2319967` ("Sync with dev/enter").

Measured on testcontainers Postgres 16: a ~98 MiB `bytea` table retains ~209 MB of heap after `executeQuery` returns (≈2× amplification: tuples plus per-cell `byte[]`). With `CLOSE_CURSORS_AT_COMMIT` + `fetchSize=10`, heap stays bounded and all rows remain readable.

The export loop (`PrimaryDataFromDb`) runs inside one transaction for the whole archive (autocommit off), so `CLOSE_CURSORS_AT_COMMIT` never actually closes anything mid-run. Interleaved work on the same connection while a portal is suspended — `PostgresResultSetMetaData` catalog queries (`getColumns`), `PostgresBlob`/`PostgresClob` large-object fastpath calls — must keep working; portals coexist with other commands in the wire protocol, and the regression test exercises the basic case.

## Goals / Non-Goals

**Goals:**

- Bound heap usage of Postgres archive export to O(fetchSize × row size) instead of O(table size)
- Eliminate the per-cell `pg_catalog` query in `PostgresResultSetMetaData`
- Eliminate per-cell `Cell` retention in `MimeTypeHandler`
- Release the large-object fd opened for every `Blob`/`Clob` cell handed to Tika
- Zero behavior change for non-Postgres databases

**Non-Goals:**

- The import/upload path (`PrimaryDataToDb`'s `CONCUR_UPDATABLE` result set accumulates inserted rows in pgjdbc — a separate driver limitation; see `fix-postgres-import-memory`)
- Changing the SIARD archive format or the export row pipeline (already streams via temp files and 8 KiB buffers)
- Holdability semantics for callers that genuinely need holdable cursors (none exist in the codebase today)

## Decisions

### D1: Neutralize `HOLD_CURSORS_OVER_COMMIT` inside `PostgresConnection`, not in `openTable`

`openTable()` is shared by all six database drivers. Changing it to `CLOSE_CURSORS_AT_COMMIT` would alter behavior for every database — and `HOLD_CURSORS_OVER_COMMIT` there is likely vestigial anyway, but that is not this change's risk to take.

Instead, `PostgresConnection.createStatement(3-arg)` maps `HOLD_CURSORS_AT_COMMIT` → `CLOSE_CURSORS_AT_COMMIT` before delegating, and `setHoldability(HOLD_CURSORS_AT_COMMIT)` stores `CLOSE_CURSORS_AT_COMMIT` as the connection default. `openTable()` keeps requesting holdable; the Postgres wrapper silently downgrades it. Rationale: the wrapper owns Postgres-specific driver quirks, and no code path in this codebase depends on Postgres cursors surviving a commit (the export never commits mid-table; `PostgresConnection` itself toggles autocommit during construction before any statements exist).

Alternative considered: fix `openTable()` to pass `CLOSE_CURSORS_AT_COMMIT` for all drivers. Rejected — cross-database behavior change for a Postgres-specific problem.

### D2: Default `fetchSize` in the Postgres statement wrappers, not per-call-site

Set a conservative default `fetchSize` (candidate: 100 rows) inside `PostgresStatement`/`PostgresPreparedStatement` construction — e.g. `setFetchSize(DEFAULT_FETCH_SIZE)` on the wrapped statement — so every SELECT benefits, including future callers. Callers can still override via `setFetchSize`.

Why not `openTable`? Same reasoning as D1 — shared code. Why not a per-row byte budget? Rows can be 100 MB+ PDFs, so any fixed row count can still buffer gigabytes in pathological cases, but 100 rows is small enough that the worst case is bounded and the round-trip cost is amortized over large payloads. (A byte-aware fetch size is impossible — JDBC fetchSize counts rows.) If 100 proves too aggressive for wide-row tables in practice, the constant is a one-line change.

Alternative considered: set fetchSize only in `openTable` via a driver-name check. Rejected — scattered driver sniffing; the wrapper is the right layer.

### D3: Cache `PostgresResultSetMetaData` per column

`getColumnType()`/`getColumnTypeName()` currently call `pdmd.getColumns(...)` — a catalog query — on every invocation. Cache resolved type/name per column index on first use (arrays sized to column count, lazily filled). Result-set metadata is immutable for the life of the `ResultSet`, so this is a pure memoization with no invalidation concern.

### D4: Make `MimeTypeHandler`'s per-cell map transient

`individualMimeTypes` (`HashMap<Value,String>`) is populated in `add()` and consumed immediately after by `applyMimeType()` — entries are never needed beyond that. Either remove entries after application or replace the map with a per-cell return value; simplest correct fix is `individualMimeTypes.remove(cell)` (or restructure so the map is not retained). Keys are `Cell` objects that keep the record DOM reachable, so retention is the leak.

### D5: Close the stream handed to Tika

`mimeTypeHandler.add(value, blob/clob)` calls `tika.detect(stream)` without closing. `PostgresBlob.getBinaryStream()` opens a `LargeObject`; its `BlobInputStream.close()` issues `lo_close`. Wrap the detect call so the stream is closed in `finally` (or use try-with-resources at the call site). Applies to both `Blob` and `Clob` branches.

## Risks / Trade-offs

- [Interleaved catalog/LO queries while a portal is suspended could break cursor fetching] → pgjdbc supports multiple active portals/commands per connection; verified mechanism is covered by `PostgresResultSetMemoryIT` plus existing `PostgresUploadDownloadSiardProjectIT`; if a specific interleaving fails, the fix can fall back to holding the metadata/LO calls outside the fetch loop
- [Fixed `fetchSize=100` still buffers 100 × largest-row bytes] → acceptable bound (vs. whole table); rows larger than ~50 MB are rare; constant is trivially tunable
- [Down-mapping `HOLD_CURSORS_OVER_COMMIT` silently could surprise a future caller that commits mid-iteration] → documented in `PostgresConnection` Javadoc; no such caller exists today; `CLOSE_CURSORS_AT_COMMIT` is the JDBC default anyway
- [Metadata caching holds type names slightly longer] → negligible; bounded by column count per ResultSet
- [Heap-delta assertions in `PostgresResultSetMemoryIT` may be flaky under different GC/heap settings] → thresholds (60/40 MiB for a ~98 MiB payload) are wide; CI runs with default heap; can be widened further if needed

## Migration Plan

No migration — behavior-only change inside the JDBC wrapper and export path. Rollback is a revert; archives already produced are unaffected (SIARD format unchanged).
