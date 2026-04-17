# CHANGELOG.md

## unreleased

* Maintenance:
  * Upgrade to MSSQL JDBC Driver 13.4.0.jre11
  
* Fix: 
  * Issues with encrypted secure connections to MSSQL Server:
    * https://github.com/sfa-siard/siard-suite/issues/153
    * https://github.com/sfa-siard/siard-suite/issues/150

* Documentation:
  * New section on secure connections for MSSQL Server in the user manual

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
