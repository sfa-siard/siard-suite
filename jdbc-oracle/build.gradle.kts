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
    implementation(libs.antlr4.runtime)
    implementation(project(":siard-utilities"))
    implementation(project(":sql-parser"))
    implementation(libs.oracle.xdb)
    implementation(libs.oracle.xmlparser)
    implementation(project(":jdbc-base"))
    implementation(fileTree("lib") { include("*.jar") })

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit4)
    testImplementation(libs.hamcrest.core)
    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.vintage.engine)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testImplementation(libs.testcontainers)
    testImplementation(libs.testcontainers.oracle)
    testImplementation(testFixtures(project(":jdbc-base")))
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
