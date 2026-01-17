import java.nio.file.Files
import java.util.*

group = "ch.admin.bar"
version = scmVersion.version
val siardVersion = "2.2"
val versionedProjectName = "${project.name}-${scmVersion.version}"

val generatedResourcesDir = Files.createDirectories(layout.buildDirectory.dir("generated/resources").get().asFile.toPath())

plugins {
    application
    `java-library`
    id("pl.allegro.tech.build.axion-release") version "1.14.3"
    id("io.freefair.lombok") version "6.5.0"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
}


sourceSets {
    create("integrationTest") {
        java.srcDir("src/integrationTest/java")
        compileClasspath += sourceSets["main"].output + configurations["testRuntimeClasspath"]
        runtimeClasspath += output + compileClasspath + sourceSets["test"].runtimeClasspath
    }
}

dependencies {
    implementation("org.apache.tika:tika-core:2.9.1") // used for getting mime-type from binary data
    implementation("commons-lang:commons-lang:2.6")
    implementation("commons-logging:commons-logging:1.1.3")
    implementation("ch.qos.logback:logback-classic:1.5.18")
    implementation("com.mysql:mysql-connector-j:8.3.0") // transitive dependency from lib/jdbcmysql.jar
    implementation("org.glassfish.jaxb:jaxb-runtime:2.3.9")

    api(project(":siard-api"))
    api(project(":sql-parser"))
    api(project(":zip64-file"))
    api(project(":siard-utilities"))
    api(project(":jdbc-base"))
    api(project(":jdbc-postgres"))
    api(project(":jdbc-oracle"))
    api(project(":jdbc-mssql"))
    api(project(":jdbc-mysql"))
    api(project(":jdbc-access"))
    api(project(":jdbc-db2"))

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.assertj:assertj-core:3.24.2")
    testImplementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.15.2")
    testImplementation("com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.15.2")
    testImplementation("org.mockito:mockito-core:5.19.0")

    // testcontainers
    testImplementation("org.testcontainers:testcontainers:1.21.4")
    testImplementation("org.testcontainers:mssqlserver:1.21.4")
    testImplementation("org.testcontainers:postgresql:1.21.4")
    testImplementation("org.testcontainers:mysql:1.21.4")
    testImplementation("org.testcontainers:mariadb:1.21.4")
    testImplementation("org.mariadb.jdbc:mariadb-java-client:2.7.4") // Used by mariadb testcontainer
    testImplementation("org.testcontainers:oracle-xe:1.21.4")
    testImplementation("org.testcontainers:db2:1.21.4")

    testImplementation(platform("org.junit:junit-bom:5.13.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine:5.13.1")
}

tasks.withType(JavaExec::class) {
    jvmArgs =
        listOf("-Djavax.xml.parsers.SAXParserFactory=com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl")
}

tasks.withType<Test> {
    jvmArgs = listOf(
        "-Djavax.xml.parsers.SAXParserFactory=com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl"
    )
}

// Helper function to create database-specific integration test tasks
fun createDbIntegrationTestTask(dbName: String, packagePattern: String): TaskProvider<Test> {
    return tasks.register<Test>("integrationTest${dbName.capitalize()}") {
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
        afterTest(KotlinClosure2<TestDescriptor, TestResult, Unit>({ descriptor, result ->
            if (descriptor.parent == null) {
                try {
                    exec {
                        commandLine("docker", "container", "prune", "-f")
                        isIgnoreExitValue = true
                    }
                } catch (e: Exception) {
                    logger.warn("Failed to clean up Docker containers: ${e.message}")
                }
            }
        }))
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

task<Test>("integrationTest") {
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
    afterTest(KotlinClosure2<TestDescriptor, TestResult, Unit>({ descriptor, result ->
        if (descriptor.parent == null) {
            try {
                exec {
                    commandLine("docker", "container", "prune", "-f")
                    isIgnoreExitValue = true
                }
            } catch (e: Exception) {
                logger.warn("Failed to clean up Docker containers: ${e.message}")
            }
        }
    }))
    
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

task("createVersionsPropertiesFile") {
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
                exclude("debug.properties")
            }

            from(layout.projectDirectory.dir("doc")) {
                into("doc")
                exclude("/manual/developer")
            }

            duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        }
    }
}

val createSiardToDbStartScript by tasks.registering(CreateStartScripts::class) {
    mainClass.set("ch.admin.bar.siard2.cmd.SiardToDb")
    applicationName = "siard-to-db"
    outputDir = layout.buildDirectory.dir("scripts").get().asFile
    classpath = files(tasks.named<Jar>("jar").get().archiveFile, configurations.runtimeClasspath.get())
}

tasks.withType<CreateStartScripts>().configureEach {
    doLast {
        val jvmArgsLine = listOf(
            "-Djavax.xml.parsers.SAXParserFactory=com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl"
        ).joinToString(" ")

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