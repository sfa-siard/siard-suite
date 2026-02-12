import java.text.DateFormat
import java.util.*

plugins {
    `java-library`
    `java-test-fixtures`
    id("io.freefair.lombok") version "6.5.0"
}

description = "MS Access JDBC Wrapper"

dependencies {
    implementation(project(":siard-utilities"))
    implementation(project(":sql-parser"))
    implementation(project(":jdbc-base"))

    implementation("org.antlr:antlr4-runtime:4.5.2")

    implementation("commons-lang:commons-lang:2.6")
    implementation("commons-logging:commons-logging:1.1.3")
    implementation("com.healthmarketscience.jackcess:jackcess:2.1.12")

    implementation("com.googlecode.json-simple:json-simple:1.1.1")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.1")
    testImplementation("org.junit.vintage:junit-vintage-engine")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.1")

    testImplementation(testFixtures(project(":jdbc-base")))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType(Jar::class) {
    manifest {
        attributes["Manifest-Version"] = "1.0"
        attributes["Created-By"] = "Hartwig Thomas, Enter AG, Rüti ZH, Switzerland; Puzzle ITC GmbH, Switzerland"
        attributes["Specification-Title"] = "JDBC"
        attributes["Specification-Vendor"] = "Swiss Federal Archives, Berne, Switzerland"
        attributes["Implementation-Title"] = "MS Access JDBC Wrapper"
        attributes["Implementation-Version"] = archiveVersion
        attributes["Implementation-Vendor"] = "Swiss Federal Archives, Berne, Switzerland"
        attributes["Built-Date"] = DateFormat.getDateInstance().format(Date())
    }
}
