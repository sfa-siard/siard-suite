plugins {
    `java-library`
}

description = "MySQL JDBC Wrapper"

dependencies {
    api(project(":jdbc-base"))
    api(project(":sql-parser"))

    implementation(libs.mysql.connector)
    implementation(libs.jts.core)
    implementation(project(":siard-utilities"))

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit4)
    testImplementation(libs.testcontainers)
    testImplementation(libs.testcontainers.mysql)
    testImplementation(testFixtures(project(":jdbc-base")))

    testRuntimeOnly(libs.junit.vintage.engine)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.junit.platform.launcher)
}

tasks.test {
    useJUnitPlatform()
}