import java.text.DateFormat
import java.util.*

plugins {
    `java-library`
    id("antlr")
}

description = "SQL Parser"

dependencies {
    antlr("org.antlr:antlr4:4.5.2-1")
    antlr("org.antlr:antlr4-runtime:4.5.2-1")
    antlr("org.antlr:antlr4-master:4.5.2-1")
    implementation("com.microsoft.sqlserver:mssql-jdbc:12.2.0.jre8")
    implementation(project(":siard-utilities"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.1")
    testImplementation("org.junit.vintage:junit-vintage-engine")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.1")
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