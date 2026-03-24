plugins {
    `java-library`
}

description = "MySQL JDBC Wrapper"

dependencies {
    implementation(libs.antlr4.runtime)
    implementation(libs.mysql.connector)
    implementation(libs.jts.core)
    implementation(project(":jdbc-base"))
    implementation(project(":siard-utilities"))
    implementation(project(":sql-parser"))

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit4)
    testImplementation(libs.testcontainers)
    testImplementation(libs.testcontainers.mysql)
    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.vintage.engine)
    testImplementation(libs.hamcrest.core)
    testImplementation(testFixtures(project(":jdbc-base")))

    testRuntimeOnly(libs.junit.jupiter.engine)
}

tasks.test {
    useJUnitPlatform()
}