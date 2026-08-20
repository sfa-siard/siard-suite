import java.text.DateFormat
import java.util.Date

plugins {
    `java-library`
    alias(libs.plugins.lombok)
}

description = "Oracle JDBC Wrapper"

repositories {
    flatDir {
        dirs("lib")
    }
}

dependencies {
    api(project(":sql-parser"))
    api(project(":jdbc-base"))
    api(files("lib/ojdbc7.jar"))

    implementation(project(":siard-utilities"))

    runtimeOnly(libs.oracle.xdb)
    runtimeOnly(libs.oracle.xmlparser)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit4)
    testImplementation(libs.junit.jupiter.api)

    testImplementation(libs.testcontainers)
    testImplementation(libs.testcontainers.oracle)
    testImplementation(libs.testcontainers.jdbc)
    testImplementation(testFixtures(project(":jdbc-base")))

    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.junit.vintage.engine)
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
        attributes["Created-By"] = "Hartwig Thomas, Enter AG, Rüti ZH, Switzerland; Puzzle ITC AG, Switzerland"
        attributes["Specification-Title"] = "JdbcOracle"
        attributes["Specification-Vendor"] = "Swiss Federal Archives, Berne, Switzerland"
        attributes["Implementation-Title"] = "Oracle JDBC Wrapper"
        attributes["Implementation-Version"] = archiveVersion
        attributes["Implementation-Vendor"] = "Swiss Federal Archives, Berne, Switzerland"
        attributes["Built-Date"] = DateFormat.getDateInstance().format(Date())
    }
}
