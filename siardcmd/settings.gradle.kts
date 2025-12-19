import java.net.URI

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention").version("1.0.0")
}

rootProject.name = "siardcmd"

sourceControl {
    gitRepository(URI.create("https://github.com/sfa-siard/Zip64File.git")) {
        producesModule("ch.admin.bar:Zip64File")
    }

    gitRepository(URI.create("https://github.com/sfa-siard/EnterUtilities")) {
        producesModule("ch.admin.bar:enterutilities")
    }

    gitRepository(URI.create("https://github.com/sfa-siard/SqlParser.git")) {
        producesModule("ch.admin.bar:SqlParser")
    }

    gitRepository(URI.create("https://github.com/sfa-siard/JdbcPostgres")) {
        producesModule("ch.admin.bar:JdbcPostgres")
    }

    gitRepository(URI.create("https://github.com/sfa-siard/JdbcOracle")) {
        producesModule("ch.admin.bar:jdbcoracle")
    }

    gitRepository(URI.create("https://github.com/sfa-siard/JdbcMsSql")) {
        producesModule("ch.admin.bar:jdbcmssql")
    }

    gitRepository(URI.create("https://github.com/sfa-siard/SiardApi")) {
        producesModule("ch.admin.bar:siard-api")
    }

    gitRepository(URI.create("https://github.com/sfa-siard/JdbcMySql")) {
        producesModule("ch.admin.bar:jdbc-mysql")
    }

    gitRepository(URI.create("https://github.com/sfa-siard/JdbcBase")) {
        producesModule("ch.admin.bar:jdbc-base")
    }

    gitRepository(URI.create("https://github.com/sfa-siard/JdbcAccess")) {
        producesModule("ch.admin.bar:jdbc-access")
    }

    gitRepository(URI.create("https://github.com/sfa-siard/JdbcDb2")) {
        producesModule("ch.admin.bar:jdbc-db2")
    }
}