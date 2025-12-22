import java.net.URI

plugins {
    // Apply the foojay-resolver plugin to allow automatic download of JDKs
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "jdbc-access"

sourceControl {
    gitRepository(URI.create("https://github.com/sfa-siard/EnterUtilities.git")) {
        producesModule("ch.admin.bar:enterutilities")
    }
    gitRepository(URI.create("https://github.com/sfa-siard/SqlParser.git")) {
        producesModule("ch.admin.bar:SqlParser")
    }
    gitRepository(URI.create("https://github.com/sfa-siard/JdbcBase.git")) {
        producesModule("ch.admin.bar:jdbc-base")
    }
}

