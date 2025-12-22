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

sourceControl {
    gitRepository(URI.create("https://github.com/sfa-siard/JdbcPostgres")) {
        producesModule("ch.admin.bar:JdbcPostgres")
    }

    gitRepository(URI.create("https://github.com/sfa-siard/JdbcOracle")) {
        producesModule("ch.admin.bar:jdbcoracle")
    }

    gitRepository(URI.create("https://github.com/sfa-siard/JdbcMsSql")) {
        producesModule("ch.admin.bar:jdbcmssql")
    }

    gitRepository(URI.create("https://github.com/sfa-siard/JdbcMySql")) {
        producesModule("ch.admin.bar:jdbc-mysql")
    }

    gitRepository(URI.create("https://github.com/sfa-siard/JdbcAccess")) {
        producesModule("ch.admin.bar:jdbc-access")
    }

    gitRepository(URI.create("https://github.com/sfa-siard/JdbcDb2")) {
        producesModule("ch.admin.bar:jdbc-db2")
    }
}
