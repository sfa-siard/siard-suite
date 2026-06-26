# siard-utilities

Common utility classes and helper functions used across SIARD Suite modules.

## Module Overview

This module provides shared utilities for:
- String manipulation and encoding
- File I/O operations
- Date/time handling
- XML processing
- Logging utilities

## Building and Testing

**Build this module:**
```shell
./gradlew :siard-utilities:build
```

**Run tests:**
```shell
./gradlew :siard-utilities:test
```

## Module-Specific Notes

- Utility library with minimal external dependencies (SLF4J, Apache Tika, Lombok)
- Used by all other SIARD Suite modules
- No database or UI dependencies 
