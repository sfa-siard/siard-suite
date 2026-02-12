import java.text.DateFormat
import java.util.*

plugins {
    `java-library`
    `java-test-fixtures`
    `maven-publish`
    id("io.freefair.lombok") version "6.5.0"
}

description = "Base JDBC Wrapper"

dependencies {
    implementation(project(":siard-utilities"))
    implementation(project(":sql-parser"))

    testFixturesImplementation(project(":siard-utilities"))
    testFixturesImplementation(project(":sql-parser"))
    testFixturesImplementation("org.junit.jupiter:junit-jupiter-api:5.10.1")
    testFixturesImplementation("org.junit.vintage:junit-vintage-engine")
    testFixturesRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.1")
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
        attributes["Implementation-Title"] = "Base JDBC Wrapper"
        attributes["Implementation-Version"] = archiveVersion
        attributes["Implementation-Vendor"] = "Swiss Federal Archives, Berne, Switzerland"
        attributes["Built-Date"] = DateFormat.getDateInstance().format(Date())

    }
}


