# jdbc-mysql

JDBC wrapper for MySQL database connectivity in SIARD Suite.

## Module Overview

This module provides MySQL-specific JDBC implementations for:
- Database metadata extraction
- Type mapping (MySQL → SIARD)
- Connection management
- Query execution and result handling

## Building and Testing

**Build this module:**
```shell
./gradlew :jdbc-mysql:build
```

**Run tests (requires Docker):**
```shell
docker-compose up -d
./gradlew :jdbc-mysql:test
```

## Module-Specific Notes

- Tests use docker-compose to start MySQL 5 instance
- Supports MySQL-specific types and storage engines
- Connection configuration in `docker-compose.yml` 

