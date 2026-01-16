plugins {
    `java-library`
}

description = "MySQL JDBC Wrapper"

dependencies {
    implementation("org.antlr:antlr4-runtime:4.5.2-1")
    implementation("com.mysql:mysql-connector-j:8.3.0")
    implementation("com.vividsolutions:jts-core:1.14.0")
    implementation(project(":jdbc-base"))
    implementation(project(":enter-utilities"))
    implementation(project(":sql-parser"))

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.testcontainers:testcontainers:1.21.4")
    testImplementation("org.testcontainers:mysql:1.21.4")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testImplementation("org.junit.vintage:junit-vintage-engine")
    testImplementation("org.hamcrest:hamcrest-core:1.3")
    testImplementation(testFixtures(project(":jdbc-base")))

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

tasks.test {
    useJUnitPlatform()
}