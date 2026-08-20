import java.text.DateFormat
import java.util.*

plugins {
    `java-library`
    `java-test-fixtures`
    alias(libs.plugins.lombok)
}

description = "MS Access JDBC Wrapper"

dependencies {
    api(project(":sql-parser"))
    api(project(":jdbc-base"))
    api(libs.jackcess)

    implementation(project(":siard-utilities"))
    implementation(libs.commons.lang)
    implementation(libs.commons.logging)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit4)
    testImplementation(testFixtures(project(":jdbc-base")))

    testRuntimeOnly(libs.junit.vintage.engine)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.junit.platform.launcher)

    annotationProcessor(libs.lombok)

    testAnnotationProcessor(libs.lombok)
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
