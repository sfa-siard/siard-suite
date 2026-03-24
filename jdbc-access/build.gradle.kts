import java.text.DateFormat
import java.util.*

plugins {
    `java-library`
    `java-test-fixtures`
    alias(libs.plugins.lombok)
}

description = "MS Access JDBC Wrapper"

dependencies {
    implementation(project(":siard-utilities"))
    implementation(project(":sql-parser"))
    implementation(project(":jdbc-base"))

    implementation(libs.antlr4.runtime)

    implementation(libs.commons.logging)
    implementation(libs.jackcess)

    implementation(libs.json.simple)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.vintage.engine)
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
        attributes["Implementation-Title"] = "MS Access JDBC Wrapper"
        attributes["Implementation-Version"] = archiveVersion
        attributes["Implementation-Vendor"] = "Swiss Federal Archives, Berne, Switzerland"
        attributes["Built-Date"] = DateFormat.getDateInstance().format(Date())
    }
}
