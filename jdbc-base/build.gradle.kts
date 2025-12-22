import java.text.DateFormat
import java.util.*

plugins {
    `java-library`
    `java-test-fixtures`
    `maven-publish`
    id("pl.allegro.tech.build.axion-release") version "1.14.3"
    id("io.freefair.lombok") version "6.5.0"
}

group = "ch.admin.bar"
version = scmVersion.version

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("ch.admin.bar:enterutilities:v2.2.6")
    implementation("ch.admin.bar:SqlParser:v2.2.5")

    testFixturesImplementation("ch.admin.bar:enterutilities:v2.2.6")
    testFixturesImplementation("ch.admin.bar:SqlParser:v2.2.5")
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


