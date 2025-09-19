import java.text.DateFormat
import java.util.*

plugins {
    `java-library`
    `java-test-fixtures`
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

val versions = mapOf(
    "jdbc-base" to "v2.2.11",
)

dependencies {
    implementation("ch.admin.bar:enterutilities:v2.2.5")
    implementation("ch.admin.bar:SqlParser:v2.2.4")
    implementation("ch.admin.bar:jdbc-base:${versions["jdbc-base"]}")

    implementation("org.antlr:antlr4-runtime:4.5.2")

    implementation("commons-lang:commons-lang:2.6")
    implementation("commons-logging:commons-logging:1.1.3")
    implementation("com.healthmarketscience.jackcess:jackcess:2.1.12")

    implementation("com.googlecode.json-simple:json-simple:1.1.1")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.1")
    testImplementation("org.junit.vintage:junit-vintage-engine")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.1")

    testImplementation(testFixtures("ch.admin.bar:jdbc-base:${versions["jdbc-base"]}"))
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
