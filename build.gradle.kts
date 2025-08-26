import java.text.DateFormat
import java.util.Date

plugins {
    `java-library`
    id("pl.allegro.tech.build.axion-release") version "1.14.3"
    id("io.freefair.lombok") version "6.5.0"
}


java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

group = "ch.admin.bar"
version = scmVersion.version
val versions = mapOf(
    "jdbc-base" to "v2.2.11",
)

repositories {
    mavenCentral()
    flatDir {
        dirs("lib")
    }
}

dependencies {
    implementation("org.antlr:antlr4-runtime:4.5.2")
    implementation("ch.admin.bar:enterutilities:v2.2.5")
    implementation("ch.admin.bar:SqlParser:v2.2.4")
    implementation("com.oracle.database.xml:xdb6:18.3.0.0")
    implementation("com.oracle.ojdbc:xdb:19.3.0.0")
    implementation("com.oracle.database.jdbc:ojdbc6:11.2.0.4")
    implementation("com.oracle.ojdbc:ojdbc8:19.3.0.0")
    implementation("com.oracle.ojdbc:xmlparserv2:19.3.0.0")
    implementation("ch.admin.bar:jdbc-base:${versions["jdbc-base"]}")
    implementation(fileTree("lib") { include("*.jar") })

    // test dependencies
    testImplementation("junit:junit:4.13.1")
    testImplementation("org.hamcrest:hamcrest-core:1.3")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testImplementation("org.junit.vintage:junit-vintage-engine")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    testImplementation("org.testcontainers:testcontainers:1.19.0")
    testImplementation("org.testcontainers:oracle-xe:1.19.0")
    testImplementation(testFixtures("ch.admin.bar:jdbc-base:${versions["jdbc-base"]}"))
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
