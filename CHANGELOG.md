# CHANGELOG.md

## 2.2.167 (2026-08-17)

* Feature;
  * Allow users to select database/ schema for postgresql connections
  * Add schema selection parameter to SiardCmd
  * Add checkbox to enable/ disable certificate validation for MSSQL Server connections
  
* Maintenance:
  * Use gradle version catalog to manage dependencies 
  * Upgrade to PostgreSQL JDBC Driver 42.7.11
  * Upgrade to MSSQL JDBC Driver 13.4.0.jre11
  * Dependency library updates, see https://github.com/sfa-siard/siard-suite/commit/60966331b5ff9c771677a1ca486072eae0b46395
  * Apply standard code style to all java files
  * Consistent usage of SLF4J logging with logback, replacing old logging mechanism. Calls to system.out and system.err are not yet replaced everywhere.
  
* Fix: 
  * Issues with encrypted secure connections to MSSQL Server:
    * https://github.com/sfa-siard/siard-suite/issues/153
    * https://github.com/sfa-siard/siard-suite/issues/150
  * Include module "jdk.crypto.ec" in bundled JRE. **this is the actual fix for ssl/ tls related issues after the java 17 upgrade**
  * Fixes for: https://github.com/sfa-siard/siard-suite/issues/151
    * MS Access: skip views with no resolvable columns to prevent XML schema validation errors
    * MS Access: prevent IndexOutOfBoundsException when parsing `Nz` or `IIf` functions with fewer arguments than expected

* Documentation:
  * New section on secure connections for MSSQL Server in the user manual
  * Add new AGENTS.md file
  * Datatype mappings for all supported databases, see  https://github.com/sfa-siard/siard-suite/commit/ab536bc33599b670632c0c6052f63281bb50884e

## 2.2.161 (2025-12-11)

* Features:
  * Allow users to select schema to export (for oracle database) in the application
  * Add schema selection parameter to SiardCmd
  * support overloaded functions and packages (for oracle database)
  * Include table description metadata in export

* Maintenance:
    * Upgrade to Java 17

* Fixes:
  * Improve precision of typeOriginal during metadata extraction (Oracle, Postgres, MySQL, MSSQL)
  * Handling underscores _ in schema and table names when importing/exporting to MsSql and MySQL
  * Handling of BIN-Files in 7zip
  * Handling empty txt files with ZIP64
  * Editing metadata
