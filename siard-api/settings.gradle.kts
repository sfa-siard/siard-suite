import java.net.URI

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention").version("1.0.0")
}


rootProject.name = "siard-api"

sourceControl {
    gitRepository(URI.create("https://github.com/sfa-siard/EnterUtilities.git")) {
        producesModule("ch.admin.bar:enterutilities")
    }

    gitRepository(URI.create("https://github.com/sfa-siard/SqlParser.git")) {
        producesModule("ch.admin.bar:SqlParser")
    }

    gitRepository(URI.create("https://github.com/sfa-siard/Zip64File.git")) {
        producesModule("ch.admin.bar:Zip64File")
    }
}