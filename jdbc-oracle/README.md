# jdbc-oracle

JDBC wrapper for Oracle database connectivity in SIARD Suite.

## Module Overview

This module provides Oracle-specific JDBC implementations for:
- Database metadata extraction
- Type mapping (Oracle → SIARD)
- Connection management
- Query execution and result handling

## Building and Testing

**Build this module:**
```shell
./gradlew :jdbc-oracle:build
```

**Run tests (requires Docker):**
```shell
docker compose up -d
./gradlew :jdbc-oracle:test
```

## Module-Specific Notes

- Tests use docker-compose to start Oracle XE instance
- Supports Oracle-specific types (XMLTYPE, CLOB, BLOB, etc.)
- Script `build-oracle-image.sh` helps build Oracle Docker images
- Connection configuration in `docker-compose.yml` 