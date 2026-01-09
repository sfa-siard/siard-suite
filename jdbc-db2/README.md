# jdbc-db2

JDBC wrapper for IBM DB2 database connectivity in SIARD Suite.

## Module Overview

This module provides DB2-specific JDBC implementations for:
- Database metadata extraction
- Type mapping (DB2 → SIARD)
- Connection management
- Query execution and result handling

## Building and Testing

**Build this module:**
```shell
./gradlew :jdbc-db2:build
```

**Run tests (requires Docker):**
```shell
docker compose up -d
./gradlew :jdbc-db2:test
```

## Module-Specific Notes

- Tests use docker-compose to start DB2 instance
- Supports DB2-specific types and features
- Connection configuration in `docker-compose.yml` 
