import java.text.DateFormat
import java.util.*

plugins {
    `java-library`
    `java-test-fixtures`
    alias(libs.plugins.lombok)
}

description = "DB2 JDBC Wrapper"

dependencies {

    implementation(fileTree(mapOf("dir" to "lib", "include" to listOf("*.jar"))))

    implementation(libs.antlr4.runtime)
    implementation(libs.json.simple)
    implementation(libs.postgresql)
    implementation(project(":siard-utilities"))
    implementation(project(":sql-parser"))
    implementation(project(":jdbc-base"))
    implementation(libs.db2.license)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.vintage.engine)
    testImplementation(testFixtures(project(":jdbc-base")))

    testRuntimeOnly(libs.junit.jupiter.engine)
    testImplementation(libs.testcontainers.junit)
    testImplementation(libs.testcontainers.db2)
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
        attributes["Implementation-Title"] = "DB2 JDBC Wrapper"
        attributes["Implementation-Version"] = archiveVersion
        attributes["Implementation-Vendor"] = "Swiss Federal Archives, Berne, Switzerland"
        attributes["Built-Date"] = DateFormat.getDateInstance().format(Date())
    }
}
