# jdbc-postgres

JDBC wrapper for PostgreSQL database connectivity in SIARD Suite.

## Module Overview

This module provides PostgreSQL-specific JDBC implementations for:
- Database metadata extraction
- Type mapping (PostgreSQL → SIARD)
- Connection management
- Query execution and result handling

## Building and Testing

**Build this module:**
```shell
./gradlew :jdbc-postgres:build
```

**Run tests:**
```shell
./gradlew :jdbc-postgres:test
```

## Module-Specific Notes

- Tests use GitHub Actions service containers for PostgreSQL
- Supports PostgreSQL-specific types (arrays, JSON, UUID, etc.)
- Connection configuration in test resources 

