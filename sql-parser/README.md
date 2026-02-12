# sql-parser

SQL:2008 parser for parsing, formatting, and evaluating SQL statements.

## Module Overview

This module provides SQL parsing capabilities for:
- Parsing SQL:2008 standard statements
- Formatting SQL queries
- Primitive evaluation of single-table queries
- Converting proprietary SQL to standard SQL

## Building and Testing

**Build this module:**
```shell
./gradlew :sql-parser:build
```

**Run tests:**
```shell
./gradlew :sql-parser:test
```

## Module-Specific Notes

- Uses ANTLR4 for grammar definition and parser generation
- Grammar files (*.g4) are in `src/main/antlr/`
- Generated sources appear in `src/main/java/ch/enterag/sqlparser/generated/`
- Build automatically generates parser from grammar files

