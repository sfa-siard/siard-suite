# ADR-0001: Web Companion Deployment Model

## Status

Accepted

## Context

SFA staff cannot run Java runtimes or install executables on their managed
laptops, but they still need to archive relational databases. Some databases are
only reachable from inside restricted networks. The existing JavaFX desktop
application and command-line tool cannot be deployed to these users.

We needed a delivery model that:

- Requires only a browser on the end-user device.
- Can be centrally deployed and updated by IT.
- Still supports databases that are not reachable from a central server.

## Decision

Introduce a **Web Companion** with two runtime roles:

1. **Central Hub** — the primary delivery model for SFA staff. A server
   deployed and managed by IT, accessed through a browser, with authentication,
   persistent job state, and per-user file isolation.
2. **Local Agent** — a secondary delivery model for databases or users that
   cannot use the hub. Runs on the user's machine or a nearby server, no
   authentication, binds to localhost only, uses an embedded SQLite database for
   job history.

The existing desktop and CLI applications remain unchanged. The Web Companion
is additive.

The `siard-server` module is built into two artifacts from one codebase:

- `siard-agent` — minimal, localhost-only, no auth.
- `siard-hub` — full server with auth, persistence, and multi-user isolation.

During the spike, a single artifact with an explicit `--server.mode=local|hub`
flag is acceptable. The split into two artifacts must happen before the first
production release.

## Consequences

- **Positive**: SFA users need only a browser; IT controls deployment and
  updates; local agent covers unreachable databases.
- **Negative**: Two runtime roles increase build and testing complexity.
- **Neutral**: Desktop/CLI continues to exist; coexistence is supported
  indefinitely.

## Alternatives

- **Replace the desktop app entirely** — rejected because external users and
  restricted-network scenarios still need a local executable or agent.
- **Browser-only central hub** — rejected because it cannot reach databases
  inside user-only or isolated networks.
- **IT-managed desktop app pushed to SFA laptops** — rejected because SFA
  policy forbids installing executables on managed laptops.
