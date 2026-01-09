# siard-cmd

Command-line interface for downloading and uploading relational databases in SIARD Format 2.2.

## Module Overview

This module provides CLI tools for:
- Downloading databases to SIARD archives
- Uploading SIARD archives to databases
- Database-to-database migration
- Metadata extraction and validation

Supports: PostgreSQL, MySQL, MariaDB, MS SQL Server, Oracle, DB2, and MS Access.

## Building and Testing

**Build this module:**
```shell
./gradlew :siard-cmd:build
```

**Run unit tests:**
```shell
./gradlew :siard-cmd:test
```

**Run integration tests:**
```shell
./gradlew :siard-cmd:integrationTest                    # All databases
./gradlew :siard-cmd:integrationTestPostgres            # PostgreSQL only
./gradlew :siard-cmd:integrationTestMysql               # MySQL only
./gradlew :siard-cmd:integrationTestMssql               # MS SQL Server only
./gradlew :siard-cmd:integrationTestOracle              # Oracle only
./gradlew :siard-cmd:integrationTestDb2                 # DB2 only
./gradlew :siard-cmd:integrationTestMsaccess            # MS Access only
```

**Create distribution archives:**
```shell
./gradlew :siard-cmd:assembleDist
```
Creates `.tar` and `.zip` distributions in `build/distributions/`.

## Module-Specific Notes

- Integration tests use [Testcontainers](https://testcontainers.com/) and require Docker
- Distribution includes all dependencies and launch scripts
- Configuration files are located in `src/main/resources/`

