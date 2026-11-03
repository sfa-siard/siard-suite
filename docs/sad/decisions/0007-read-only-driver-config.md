# ADR-0007: Read-Only JDBC Driver Configuration

**Status:** Accepted

## Context

`SiardConnection` resolved JDBC-driver class mappings from `etc/jdbcdrivers.properties` next to the installation. On first run it wrote that file when missing. This breaks read-only install locations (a requirement for proper OS packaging) and silently mutates the installation directory — a side effect users and installers do not expect. The bundled default mapping also carried a copy-paste bug (`mysql_title` was "SQL Server").

## Decision

The driver mapping is resolved read-only, in precedence order:

1. `-Dch.admin.bar.siard2.cmd.drivers=<file>` — explicit external override, if set
2. `etc/jdbcdrivers.properties` — if the file exists
3. bundled defaults compiled into `SiardConnection` — always present

`SiardConnection` never writes the properties file. The `mysql_title` default was corrected to "MySQL" (the shipped `etc/` file was already correct).

## Consequences

- The CLI starts correctly from read-only install directories — precondition for the `siard-cli-packaging` change (fat jar / installers).
- Users needing a custom driver mapping supply it explicitly via the `-D` property; no implicit file creation.
- Losing the "self-documenting generated properties file" convenience is mitigated by the `-D` override and bundled defaults covering all supported databases.

## Alternatives

- **Keep first-run write**: rejected — incompatible with read-only installs and packaging standards.
- **External-only config (no bundled defaults)**: rejected — requires every install to ship a correct `etc/` file; bundling defaults removes a failure mode.
