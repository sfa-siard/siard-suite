import java.nio.file.Files
import java.util.*

group = "ch.admin.bar"
version = scmVersion.version
val siardVersion = "2.2"
val versionedProjectName = "${project.name}-${scmVersion.version}"
val xercesSaxParserFactory = extra["xercesSaxParserFactory"] as String

val generatedResourcesDir =
    Files.createDirectories(layout.buildDirectory.dir("generated/resources").get().asFile.toPath())

plugins {
    application
    `java-library`
    alias(libs.plugins.axion.release)
    alias(libs.plugins.lombok)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
}


sourceSets {
    create("integrationTest") {
        java.srcDir("src/integrationTest/java")
        compileClasspath += sourceSets["main"].output + sourceSets["test"].compileClasspath
        runtimeClasspath += output + compileClasspath + sourceSets["test"].runtimeClasspath
    }
}

dependencies {
    implementation(libs.tika.core)
    implementation(libs.slf4j.api)


    implementation(project(":siard-api"))
    implementation(project(":sql-parser"))
    implementation(project(":siard-utilities"))
    implementation(project(":jdbc-base"))
    implementation(project(":jdbc-postgres"))
    implementation(project(":jdbc-oracle"))
    implementation(project(":jdbc-mssql"))
    implementation(project(":jdbc-mysql"))
    implementation(project(":jdbc-access"))
    implementation(project(":jdbc-db2"))

    runtimeOnly(libs.logback.classic)
    runtimeOnly(libs.mysql.connector)
    runtimeOnly(libs.jaxb.runtime)

    testImplementation(libs.junit4)
    testImplementation(libs.assertj.core)
    testImplementation(libs.mockito.core)
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.bouncycastle.bcpkix)

    testImplementation(libs.jackson.dataformat.xml)
    testImplementation(libs.jackson.datatype.jdk8)
    testImplementation(libs.testcontainers)
    testImplementation(libs.testcontainers.mssql)
    testImplementation(libs.testcontainers.postgresql)
    testImplementation(libs.testcontainers.mysql)
    testImplementation(libs.testcontainers.mariadb)
    testImplementation(libs.testcontainers.oracle)
    testImplementation(libs.testcontainers.db2)

    testRuntimeOnly(libs.mariadb.client)
    testRuntimeOnly(libs.junit.platform.launcher)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.junit.vintage.engine)
    testRuntimeOnly(libs.bouncycastle.bcprov)
}

tasks.withType(JavaExec::class) {
    jvmArgs = listOf(xercesSaxParserFactory)
}

tasks.withType<Test> {
    jvmArgs = listOf(xercesSaxParserFactory)
}

// Helper function to create database-specific integration test tasks
fun createDbIntegrationTestTask(dbName: String, packagePattern: String): TaskProvider<Test> {
    return tasks.register<Test>("integrationTest${dbName.replaceFirstChar { it.uppercase() }}") {
        description = "Runs the $dbName integration tests"
        group = "verification"
        testClassesDirs = sourceSets["integrationTest"].output.classesDirs
        classpath = sourceSets["integrationTest"].runtimeClasspath
        mustRunAfter(tasks["test"])
        useJUnitPlatform {
            includeEngines("junit-jupiter", "junit-vintage")
        }

        filter {
            includeTestsMatching("ch.admin.bar.siard2.cmd.$packagePattern.*")
        }

        maxParallelForks = 2

        testLogging {
            events("passed", "skipped", "failed")
            showStandardStreams = true
        }

        // Clean up Docker resources after each test class to prevent disk space exhaustion
        addTestListener(object : TestListener {
            override fun beforeSuite(suite: TestDescriptor) {}
            override fun afterSuite(suite: TestDescriptor, result: TestResult) {
                if (suite.className != null) {
                    try {
                        project.providers.exec {
                            commandLine("docker", "container", "prune", "-f")
                        }.result.get()
                    } catch (e: Exception) {
                        logger.warn("Failed to clean up Docker containers: ${e.message}")
                    }
                }
            }
            override fun beforeTest(testDescriptor: TestDescriptor) {}
            override fun afterTest(testDescriptor: TestDescriptor, result: TestResult) {}
        })
    }
}

// Create database-specific test tasks
val integrationTestPostgres = createDbIntegrationTestTask("postgres", "postgres")
val integrationTestMysql = createDbIntegrationTestTask("mysql", "mysql")
val integrationTestMariadb = createDbIntegrationTestTask("mariadb", "mariadb")
val integrationTestMssql = createDbIntegrationTestTask("mssql", "mssql")
val integrationTestOracle = createDbIntegrationTestTask("oracle", "oracle")
val integrationTestDb2 = createDbIntegrationTestTask("db2", "db2")
val integrationTestMsaccess = createDbIntegrationTestTask("msaccess", "msaccess")
val integrationTestUtils = createDbIntegrationTestTask("utils", "utils")

tasks.register<Test>("integrationTest") {
    description = "Runs all integration tests"
    group = "verification"
    testClassesDirs = sourceSets["integrationTest"].output.classesDirs
    classpath = sourceSets["integrationTest"].runtimeClasspath
    mustRunAfter(tasks["test"])
    useJUnitPlatform()

    maxParallelForks = 2

    testLogging {
        events("passed", "skipped", "failed")
        showStandardStreams = true
    }

    // Clean up Docker resources after each test class to prevent disk space exhaustion
    addTestListener(object : TestListener {
        override fun beforeSuite(suite: TestDescriptor) {}
        override fun afterSuite(suite: TestDescriptor, result: TestResult) {}
        override fun beforeTest(testDescriptor: TestDescriptor) {}
        override fun afterTest(testDescriptor: TestDescriptor, result: TestResult) {
            if (testDescriptor.parent == null) {
                try {
                    project.providers.exec {
                        commandLine("docker", "container", "prune", "-f")
                    }.result.get()
                } catch (e: Exception) {
                    logger.warn("Failed to clean up Docker containers: ${e.message}")
                }
            }
        }
    })

    // Depend on all database-specific test tasks
    dependsOn(
        integrationTestPostgres,
        integrationTestMysql,
        integrationTestMariadb,
        integrationTestMssql,
        integrationTestOracle,
        integrationTestDb2,
        integrationTestMsaccess,
        integrationTestUtils
    )

    // Don't run tests directly, just aggregate results
    filter {
        excludeTestsMatching("*")
    }
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

tasks.register("createVersionsPropertiesFile") {
    description = "Creates a properties file which contains all needed versions information"
    group = "build"

    doLast {
        val file = generatedResourcesDir.resolve("versions.properties").toFile()
        if (!file.exists()) {
            file.parentFile.mkdirs()
            file.createNewFile()
            logger.info("$file successfully created")
        }

        file.writer().use { writer ->
            val properties = Properties()
            properties["SIARD-Version"] = siardVersion
            properties["App-Version"] = "${project.version}"
            properties.store(writer, null)

            logger.info("$file successfully generated (SIARD: $siardVersion, App: ${project.version})")
        }
    }
}

tasks {
    compileJava {
        dependsOn("createVersionsPropertiesFile")
    }

    processResources {
        from(generatedResourcesDir)
        logger.info("$generatedResourcesDir added to processed resources")
    }
}

distributions {
    main {
        contents {

            from(layout.projectDirectory) {
                into("")
                include(
                    "LICENSE.txt",
                    "RELEASE.txt"
                )
            }

            from(layout.projectDirectory.dir("testfiles/siardarchives")) {
                into("testfiles")
                include("sample.siard")
            }

            from(layout.projectDirectory.dir("etc")) {
                into("etc")
            }

            from(layout.projectDirectory.dir("doc")) {
                into("doc")
                exclude("/manual/developer")
            }

            duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        }
    }
}

val createSiardToDbStartScript = tasks.register<CreateStartScripts>("createSiardToDbStartScript") {
    mainClass.set("ch.admin.bar.siard2.cmd.SiardToDb")
    description = "Creates a start script for siard-to-db"
    applicationName = "siard-to-db"
    outputDir = layout.buildDirectory.dir("scripts").get().asFile
    classpath = files(tasks.named<Jar>("jar").get().archiveFile, configurations.runtimeClasspath.get())
}

tasks.withType<CreateStartScripts>().configureEach {
    doLast {
        val jvmArgsLine = listOf(xercesSaxParserFactory).joinToString(" ")

        // UNIX script
        unixScript.writeText(
            unixScript.readText().replace(
                "DEFAULT_JVM_OPTS=\"\"",
                "DEFAULT_JVM_OPTS=\"$jvmArgsLine\""
            )
        )

        // Windows script
        windowsScript.writeText(
            windowsScript.readText().replace(
                "set DEFAULT_JVM_OPTS=",
                "set DEFAULT_JVM_OPTS=$jvmArgsLine"
            )
        )
    }
}


tasks.named<CreateStartScripts>("startScripts") {
    mainClass.set("ch.admin.bar.siard2.cmd.SiardFromDb")
    applicationName = "siard-from-db"
    dependsOn(createSiardToDbStartScript)
}

tasks.named<Sync>("installDist") {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.named<Zip>("distZip") {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.named<Tar>("distTar") {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}