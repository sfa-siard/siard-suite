import java.text.DateFormat
import java.util.Date

plugins {
    `java-library`
    id("io.freefair.lombok") version "6.5.0"
}

description = "Oracle JDBC Wrapper"

repositories {
    flatDir {
        dirs("lib")
    }
}

dependencies {
    implementation("org.antlr:antlr4-runtime:4.5.2")
    implementation(project(":enter-utilities"))
    implementation(project(":sql-parser"))
    implementation("com.oracle.ojdbc:xdb:19.3.0.0")
    implementation("com.oracle.ojdbc:xmlparserv2:19.3.0.0")
    implementation(project(":jdbc-base"))
    implementation(fileTree("lib") { include("*.jar") })

    // test dependencies
    testImplementation("junit:junit:4.13.1")
    testImplementation("org.hamcrest:hamcrest-core:1.3")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testImplementation("org.junit.vintage:junit-vintage-engine")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    testImplementation("org.testcontainers:testcontainers:1.19.0")
    testImplementation("org.testcontainers:oracle-xe:1.19.0")
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
