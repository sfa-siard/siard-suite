# ADR-0001: Web Companion Deployment Model

## Status

Accepted

## Context

SFA staff cannot run Java runtimes or install executables on their managed
laptops, but they still need to archive relational databases. The existing JavaFX desktop
application and command-line tool cannot be deployed to these users. Databases might
only be reachable from inside restricted networks making a setup with a centralized server quite difficult or useless.

We needed a delivery model that:

- Requires only a browser on the end-user device.
- Can be centrally deployed and updated by IT.
- Still supports databases that are not reachable from a central server.

The Web Companion will initially be delivered through the **Local Agent**, which can run in restricted networks. The **Central Hub** is the intended primary delivery model for SFA staff, but it will be implemented and released after the Local Agent is proven.

## Decision

Introduce a **Web Companion** with two runtime roles:

1. **Central Hub** — the primary delivery model for SFA staff. A server
   deployed and managed by IT, accessed through a browser, with authentication,
   persistent job state, and per-user file isolation.
2. **Local Agent** — a secondary delivery model for databases or users that
   cannot use the hub. Runs on the user's machine or a nearby server, no
   authentication, binds to localhost only, uses an embedded SQLite database for
   job history.

The existing desktop and CLI applications remain unchanged but maintenance may be dropped after a certain period. The Web Companion is additive.

The `siard-server` module is built into two artifacts from one codebase from the start:

- `siard-agent` — minimal, localhost-only, no auth.
- `siard-hub` — full server with auth, persistence, and multi-user isolation.

Both artifacts are produced from the same `siard-server` module. The hub artifact may initially contain only stub or minimal functionality while the central hub features are implemented, but the module and build split are in place from the beginning.

## Implementation

- **Build layout**: `siard-server` uses Gradle source sets: `src/main` for shared code, `src/agent` for agent-only code, and `src/hub` for hub-only code. Two `Jar` tasks produce the `siard-agent` and `siard-hub` artifacts.
- **Frontend**: A separate `siard-web` module builds the shared frontend and produces a JAR that is included in both artifacts. The same UI bundle runs on the hub and the local agent.
- **Local agent packaging**: Delivered as a platform-specific `.zip` archive for Windows and Linux. The archive contains a bundled JRE and a start script (`start.bat` for Windows, `start.sh` for Linux). The user downloads, extracts, and runs the script. Native installers may be added later. The initial release bundles a full JRE; a `jlink`-stripped runtime is targeted after the POC.
- **Local agent runtime**: Binds to localhost only. The configured server address is resolved at startup; if any resolved IP is not a loopback address, the agent refuses to start. The SQLite job history defaults to `%LOCALAPPDATA%/siard-agent/jobs.db` on Windows and `$XDG_DATA_HOME/siard-agent/jobs.db` (or `~/.local/share/siard-agent/jobs.db`) on Linux, and is configurable.

## Consequences

- **Positive**: SFA users need only a browser; IT controls deployment and
  updates; local agent covers unreachable databases.
- **Positive**: Building the local agent first reduces risk and proves the architecture before investing in the central hub.
- **Negative**: Two runtime roles increase build and testing complexity.
- **Negative**: The initial local agent archive is larger because it ships a full JRE.
- **Neutral**: Desktop/CLI continues to exist; coexistence is supported
  indefinitely.

## Open Questions

- **Per-user file isolation on the hub**: The customer has not yet specified the requirements for how per-user files are isolated. The mechanism must be revisited once the requirements are known.
- **Local agent security**: The local agent has no authentication and runs on localhost. The risk of other local processes or malicious web pages interacting with it is acknowledged and must be mitigated in a separate security decision (e.g., same-origin checks, per-launch tokens).

## Alternatives

- **Browser-only central hub** — rejected because it cannot reach databases
  inside user-only or isolated networks.
- **IT-managed desktop app pushed to SFA laptops** — rejected because bundling and distributing of these packages cannot hold up to the desired speed of developement.
