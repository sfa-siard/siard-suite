import java.text.DateFormat
import java.util.*

plugins {
    `java-library`
    `java-test-fixtures`
    alias(libs.plugins.lombok)
}

description = "DB2 JDBC Wrapper"

dependencies {
    api(project(":sql-parser"))
    api(project(":jdbc-base"))

    implementation(fileTree(mapOf("dir" to "lib", "include" to listOf("*.jar"))))
    implementation(project(":siard-utilities"))

    runtimeOnly(libs.db2.license)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit4)
    testImplementation(testFixtures(project(":jdbc-base")))

    testRuntimeOnly(libs.junit.vintage.engine)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.junit.platform.launcher)
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
