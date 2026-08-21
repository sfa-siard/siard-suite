# ADR-0004: Web Companion External LOB Handling

## Status

Accepted

## Context

SIARD archives can store large objects (LOBs) outside the `.siard` file in an
external LOB folder. The SIARD metadata references that folder with relative
URIs such as `../externalLobs/`. This keeps the `.siard` file small but creates
a two-part artifact that must always stay together.

Browsers cannot directly download folders and cannot easily select a local
folder for upload. In the Local Agent, the agent can read and write both parts
directly on the local filesystem. In the Central Hub, the server must package and
deliver both parts in a way a browser can handle.

## Decision

- **Local Agent mode**: keep existing behavior. The agent reads and writes the
  `.siard` file and external LOB folder directly on the user's filesystem.
- **Hub mode**: support external LOBs through **ZIP package downloads and
  uploads**. The ZIP contains the `.siard` file and the external LOB folder in
  the correct relative layout.

The server validates that:

- The external LOB folder referenced in the SIARD metadata exists.
- Relative LOB paths do not escape the archive directory (path traversal
  prevention).
- Uploaded ZIP packages contain both parts in the expected relative layout.

## Consequences

- **Positive**: archive fidelity is preserved; the hub can handle large objects
  without rewriting the SIARD format.
- **Negative**: users must understand that they are handling a package, not a
  single file; UI must guide them clearly.
- **Neutral**: re-internalizing LOBs remains a future option if users prefer a
  single-file workflow.

## Alternatives

- **Re-internalize LOBs on download** — rejected because it changes the archive
  format and may create very large downloads.
- **Disable external LOBs in hub mode** — rejected because it changes archive
  size and behavior compared to the desktop application.
- **Server-managed object storage with rewritten references** — rejected because
  it is non-standard and adds significant complexity.
