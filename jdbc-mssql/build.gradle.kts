import java.text.DateFormat
import java.util.*

plugins {
    `java-library`
    id("io.freefair.lombok") version "6.5.0"
}

description = "MS SQL Server JDBC Wrapper"

repositories {
    flatDir {
        dirs("lib")
    }
}

dependencies {
    implementation(fileTree("lib") { include("*.jar") })

    implementation("org.antlr:antlr4-runtime:4.5.2")
    implementation(project(":enter-utilities"))
    implementation(project(":sql-parser"))
    implementation(project(":jdbc-base"))

    //test dependencies
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testImplementation("org.junit.vintage:junit-vintage-engine")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.testcontainers:testcontainers:1.21.4")
    testImplementation("org.testcontainers:mssqlserver:1.21.4")
    testImplementation("org.hamcrest:hamcrest-core:1.3")
    testImplementation(testFixtures(project(":jdbc-base")))
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
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
