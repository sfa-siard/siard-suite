import java.text.DateFormat
import java.util.*

plugins {
    `java-library`
    alias(libs.plugins.lombok)
}

description = "MS SQL Server JDBC Wrapper"

repositories {
    flatDir {
        dirs("lib")
    }
}

dependencies {
    implementation(fileTree("lib") { include("*.jar") })

    implementation(libs.antlr4.runtime)
    implementation(project(":siard-utilities"))
    implementation(project(":sql-parser"))
    implementation(project(":jdbc-base"))

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.vintage.engine)
    testImplementation(libs.junit4)
    testImplementation(libs.testcontainers)
    testImplementation(libs.testcontainers.mssql)
    testImplementation(libs.hamcrest.core)
    testImplementation(testFixtures(project(":jdbc-base")))
    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.junit.platform.launcher)
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType(Jar::class) {
    manifest {
        attributes["Manifest-Version"] = "1.0"
        attributes["Created-By"] = "Hartwig Thomas, Enter AG, Rüti ZH, Switzerland; Puzzle ITC AG, Switzerland"
        attributes["Specification-Title"] = "JdbcMsSql"
        attributes["Specification-Vendor"] = "Swiss Federal Archives, Berne, Switzerland"
        attributes["Implementation-Title"] = "MsSql JDBC Wrapper"
        attributes["Implementation-Version"] = archiveVersion
        attributes["Implementation-Vendor"] = "Swiss Federal Archives, Berne, Switzerland"
        attributes["Built-Date"] = DateFormat.getDateInstance().format(Date())
    }
}
