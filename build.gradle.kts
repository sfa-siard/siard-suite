plugins {
    // Apply the java-library plugin for API and implementation separation.
    `java-library`
    id("pl.allegro.tech.build.axion-release") version "1.14.3"
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
}

dependencies {
    implementation("org.antlr:antlr4-runtime:4.5.2-1")
    implementation("com.mysql:mysql-connector-j:8.3.0")
    implementation("com.vividsolutions:jts-core:1.14.0")
    implementation("ch.admin.bar:jdbc-base:${versions["jdbc-base"]}")
    implementation("ch.admin.bar:enterutilities:v2.2.5")
    implementation("ch.admin.bar:SqlParser:v2.2.4")

    // test dependencies
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testImplementation("org.junit.vintage:junit-vintage-engine")
    testImplementation("org.hamcrest:hamcrest-core:1.3")
    testImplementation(testFixtures("ch.admin.bar:jdbc-base:${versions["jdbc-base"]}"))

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

tasks.test {
    useJUnitPlatform()
}