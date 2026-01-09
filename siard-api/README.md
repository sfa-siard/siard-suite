# siard-api

Core API for reading and writing SIARD Format 2.2 archives.

## Module Overview

This module provides the fundamental API for working with SIARD archives, including:
- Reading and writing SIARD 2.2 format files
- Metadata management (schemas, tables, columns, types)
- Data access and manipulation
- Archive validation and conversion

## Building and Testing

**Build this module:**
```shell
./gradlew :siard-api:build
```

**Run tests:**
```shell
./gradlew :siard-api:test
```

**Run specific test:**
```shell
./gradlew :siard-api:test --tests "ClassName.testMethod"
```

## Module-Specific Notes

- Generated sources are created in `src/main/java/ch/admin/bar/siard2/api/generated/` during build
- JAXB is used for XML schema binding
- Supports SIARD format versions 1.0, 2.1, and 2.2 






