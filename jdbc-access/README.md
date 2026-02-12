# jdbc-access

JDBC interface for MS Access databases using Jackcess library.

## Module Overview

This module provides MS Access-specific JDBC implementations for:
- Database metadata extraction from .accdb and .mdb files
- Type mapping (Access → SIARD)
- File-based database access (no server required)
- Query execution and result handling

## Building and Testing

**Build this module:**
```shell
./gradlew :jdbc-access:build
```

**Run tests:**
```shell
./gradlew :jdbc-access:test
```

## Module-Specific Notes

- Uses [Jackcess](http://jackcess.sourceforge.net/) library for Access file parsing
- No running database server required (file-based access)
- Test files (.accdb) are provided in `src/test/resources/testfiles/`
- Supports both .accdb (Access 2007+) and .mdb (Access 97-2003) formats 

