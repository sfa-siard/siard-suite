import java.text.DateFormat
import java.util.*

plugins {
    `java-library`
    alias(libs.plugins.lombok)
}

description = "Postgres JDBC Wrapper"

dependencies {
    implementation(libs.antlr4.runtime)
    implementation(libs.json.simple)
    implementation(libs.postgresql)
    implementation(project(":siard-utilities"))
    implementation(project(":sql-parser"))
    implementation(project(":jdbc-base"))
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.vintage.engine)
    testImplementation(libs.junit4)
    testImplementation(libs.testcontainers)
    testImplementation(libs.testcontainers.postgresql)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.junit.platform.launcher)
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
        attributes["Implementation-Title"] = "Postgres JDBC Wrapper"
        attributes["Implementation-Version"] = archiveVersion
        attributes["Implementation-Vendor"] = "Swiss Federal Archives, Berne, Switzerland"
        attributes["Built-Date"] = DateFormat.getDateInstance().format(Date())

    }
}
