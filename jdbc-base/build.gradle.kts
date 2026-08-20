import java.text.DateFormat
import java.util.*

plugins {
    `java-library`
    `java-test-fixtures`
    `maven-publish`
    alias(libs.plugins.lombok)
}

description = "Base JDBC Wrapper"

dependencies {
    api(project(":sql-parser"))

    implementation(project(":siard-utilities"))

    testFixturesApi(libs.junit4)

    testFixturesImplementation(project(":siard-utilities"))
    testFixturesImplementation(platform(libs.junit.bom))

    testFixturesRuntimeOnly(libs.junit.vintage.engine)
    testFixturesRuntimeOnly(libs.junit.jupiter.engine)
    testFixturesRuntimeOnly(libs.junit.platform.launcher)

    testFixturesAnnotationProcessor(libs.lombok)
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


