import java.net.URI

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "siard-suite"

include("siard-suite-app")
include("siardcmd")
include("enter-utilities")
include("zip64-file")
include("sql-parser")
include("siard-api")
include("jdbc-base")
include("jdbc-postgres")
include("jdbc-mysql")
include("jdbc-mssql")
include("jdbc-oracle")
include("jdbc-access")
include("jdbc-db2")
