# jdbc-mssql

JDBC wrapper for Microsoft SQL Server database connectivity in SIARD Suite.

## Module Overview

This module provides MS SQL Server-specific JDBC implementations for:
- Database metadata extraction
- Type mapping (SQL Server → SIARD)
- Connection management
- Query execution and result handling

## Building and Testing

**Build this module:**
```shell
./gradlew :jdbc-mssql:build
```

**Run tests (requires Docker):**
```shell
docker compose up -d
./gradlew :jdbc-mssql:test
```

## Module-Specific Notes

- Tests use docker-compose with custom Dockerfile for SQL Server
- Supports SQL Server-specific types (hierarchyid, geography, etc.)
- Connection configuration in `docker-compose.yml` 
