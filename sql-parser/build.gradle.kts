import java.text.DateFormat
import java.util.*

plugins {
    `java-library`
    id("antlr")
}

description = "SQL Parser"

dependencies {
    antlr(libs.antlr4)
    antlr(libs.antlr4.runtime)
    antlr(libs.antlr4.master)
    implementation(libs.mssql.jdbc)
    implementation(project(":siard-utilities"))
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.vintage.engine)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.junit.platform.launcher)
}

tasks.test {
    useJUnitPlatform()
}

tasks.register<JavaExec>("keywordgenerator") {
    mainClass.set("ch.enterag.sqlparser.KeywordGenerator")
    classpath = sourceSets["test"].runtimeClasspath
}

tasks.build {
    dependsOn("keywordgenerator")
}

tasks.generateGrammarSource {
    outputDirectory = File("src/main/java/ch/enterag/sqlparser/generated")
    include("Sql.g4")
    arguments = arguments + listOf(
            "-visitor",
            "-no-listener",
            "-package",
            "ch.enterag.sqlparser.generated",
    )
}

tasks.withType(Jar::class) {
    manifest {
        attributes["Manifest-Version"] = "1.0"
        attributes["Created-By"] = "Hartwig Thomas, Enter AG, Rüti ZH, Switzerland; Puzzle ITC GmbH, Switzerland"
        attributes["Specification-Title"] = "SQL Parser"
        attributes["Specification-Vendor"] = "Swiss Federal Archives, Berne, Switzerland"
        attributes["Implementation-Title"] = "SQL Parser"
        attributes["Implementation-Version"] = archiveVersion
        attributes["Implementation-Vendor"] = "Swiss Federal Archives, Berne, Switzerland"
        attributes["Built-Date"] = DateFormat.getDateInstance().format(Date())
    }
}