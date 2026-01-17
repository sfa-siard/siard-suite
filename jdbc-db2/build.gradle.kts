import java.text.DateFormat
import java.util.*

plugins {
    `java-library`
    `java-test-fixtures`
    id("io.freefair.lombok") version "6.5.0"
}

description = "DB2 JDBC Wrapper"

dependencies {

    implementation(fileTree(mapOf("dir" to "lib", "include" to listOf("*.jar"))))

    implementation("org.antlr:antlr4-runtime:4.5.2")
    implementation("com.googlecode.json-simple:json-simple:1.1.1")
    implementation("org.postgresql:postgresql:42.2.5")
    implementation(project(":siard-utilities"))
    implementation(project(":sql-parser"))
    implementation(project(":jdbc-base"))
    implementation("com.mysitex.sx.lib:db2jcc_license_cu:1.1")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.1")
    testImplementation("org.junit.vintage:junit-vintage-engine")
    testImplementation(testFixtures(project(":jdbc-base")))

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.1")
    testImplementation("org.testcontainers:junit-jupiter:1.21.4")
    testImplementation("org.testcontainers:db2:1.21.4")
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
