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
    alias(libs.plugins.beryx.runtime)
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
        compileClasspath += sourceSets["main"].output + sourceSets["test"].output + sourceSets["test"].compileClasspath
        runtimeClasspath += output + compileClasspath + sourceSets["test"].runtimeClasspath
    }
}

// integrationTest inherits the unit-test dependencies through the configuration
// graph (not just the source set classpath), so that dependency-analysis can
// attribute usages in src/integrationTest correctly.
configurations.named("integrationTestImplementation") {
    extendsFrom(configurations.testImplementation.get())
}
configurations.named("integrationTestRuntimeOnly") {
    extendsFrom(configurations.testRuntimeOnly.get())
}

dependencies {
    implementation(libs.tika.core)
    implementation(libs.slf4j.api)
    implementation(libs.picocli)


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

    testImplementation(libs.assertj.core)
    testImplementation(libs.logback.classic)
    testImplementation(libs.mockito.core)
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter.api)

    testRuntimeOnly(libs.junit.platform.launcher)
    testRuntimeOnly(libs.junit.jupiter.engine)

    "integrationTestImplementation"(libs.bouncycastle.bcpkix)
    "integrationTestImplementation"(libs.bouncycastle.bcprov)

    "integrationTestImplementation"(libs.jackson.annotations)
    "integrationTestImplementation"(libs.jackson.core)
    "integrationTestImplementation"(libs.jackson.databind)
    "integrationTestImplementation"(libs.jackson.dataformat.xml)
    "integrationTestImplementation"(libs.jackson.datatype.jdk8)
    "integrationTestImplementation"(libs.testcontainers)
    "integrationTestImplementation"(libs.testcontainers.jdbc)
    "integrationTestImplementation"(libs.testcontainers.mssql)
    "integrationTestImplementation"(libs.testcontainers.postgresql)
    "integrationTestImplementation"(libs.testcontainers.mysql)
    "integrationTestImplementation"(libs.testcontainers.mariadb)
    "integrationTestImplementation"(libs.testcontainers.oracle)
    "integrationTestImplementation"(libs.testcontainers.db2)
    "integrationTestImplementation"(libs.testcontainers.junit)

    "integrationTestImplementation"(libs.mysql.connector)

    "integrationTestRuntimeOnly"(libs.mariadb.client)
}

tasks.withType(JavaExec::class) {
    jvmArgs = listOf(xercesSaxParserFactory)
}

tasks.withType<Test> {
    jvmArgs = listOf(xercesSaxParserFactory)
}

// Helper function to create database-specific integration test tasks
fun createDbIntegrationTestTask(dbName: String, packagePattern: String, forks: Int = 2): TaskProvider<Test> {
    return tasks.register<Test>("integrationTest${dbName.replaceFirstChar { it.uppercase() }}") {
        description = "Runs the $dbName integration tests"
        group = "verification"
        testClassesDirs = sourceSets["integrationTest"].output.classesDirs
        classpath = sourceSets["integrationTest"].runtimeClasspath
        mustRunAfter(tasks["test"])
        useJUnitPlatform()

        filter {
            includeTestsMatching("ch.admin.bar.siard2.cmd.$packagePattern.*")
        }

        maxParallelForks = forks

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
// Oracle XE cold-start does not tolerate parallel test forks on constrained CI runners
val integrationTestOracle = createDbIntegrationTestTask("oracle", "oracle", forks = 1)
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

application {
    mainClass.set("ch.admin.bar.siard2.cmd.SiardCli")
    applicationName = "siard"
    applicationDefaultJvmArgs = listOf(xercesSaxParserFactory)
}

val packagingOs = org.gradle.internal.os.OperatingSystem.current()
val cliOsName = when {
    packagingOs.isWindows -> "win"
    packagingOs.isLinux -> "linux"
    packagingOs.isMacOsX -> "mac"
    else -> "unknown"
}
// jpackage only accepts dotted-number versions; branch-SNAPSHOT versions like
// "2.2.168-feat-x-SNAPSHOT" break jpackage validation (win/mac) and rpmbuild (linux)
val jpackageVersion = Regex("\\d+(\\.\\d+)*").find(version.toString())?.value ?: "0.0.1"

// jpackage accepts one --type per invocation; a second run passes -PinstallerType=
val cliInstallerType = providers.gradleProperty("installerType").orNull ?: when (cliOsName) {
    "win" -> "msi"
    "linux" -> "deb"
    "mac" -> "dmg"
    else -> null
}

runtime {
    options.set(listOf("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages"))

    launcher {
        noConsole = false
        jvmArgs = listOf(
            xercesSaxParserFactory,
            "--add-opens=java.xml/com.sun.org.apache.xerces.internal.jaxp=ALL-UNNAMED",
            "--add-opens=java.xml/com.sun.org.apache.xalan.internal.xsltc.trax=ALL-UNNAMED"
        )
    }

    additive = true
    // derived via `jdeps --recursive --print-module-deps` on the runtime classpath
    // (java.compiler, java.desktop, java.management, java.net.http, java.prefs,
    // java.rmi, java.security.sasl, java.sql.rowset, jdk.security.auth, jdk.unsupported)
    // plus modules loaded dynamically that jdeps cannot see:
    // jdk.crypto.ec (TLS to databases), java.naming (JNDI lookups via reflection),
    // jdk.charsets (non-UTF-8 database charsets)
    modules = listOf(
        "java.compiler",
        "java.desktop",
        "java.logging",
        "java.management",
        "java.naming",
        "java.net.http",
        "java.prefs",
        "java.rmi",
        "java.security.jgss",
        "java.security.sasl",
        "java.sql",
        "java.sql.rowset",
        "java.xml",
        "jdk.charsets",
        "jdk.crypto.ec",
        "jdk.security.auth",
        "jdk.unsupported"
    )

    val imageSuffix = if (cliOsName != "unknown") "-$cliOsName" else ""
    imageZip.set(file("${layout.buildDirectory.get()}/image-zip/siard-cli-${version}$imageSuffix.zip"))
    imageDir.set(file("${layout.buildDirectory.get()}/siard-cli-${version}$imageSuffix"))

    jpackage {
        imageName = "siard"
        installerName = "siard-cli"
        appVersion = jpackageVersion

        skipInstaller = false

        val imgType = if (packagingOs.isWindows) "ico" else if (packagingOs.isMacOsX) "icns" else "png"
        imageOptions.add("--icon")
        imageOptions.add(layout.projectDirectory.file("packaging/icons/archive_red.$imgType").asFile.absolutePath)
        if (packagingOs.isWindows) {
            // without this, jpackage builds a GUI-subsystem exe that detaches
            // from the console — all stdout/stderr of the CLI is lost
            imageOptions.add("--win-console")
        }

        installerOptions.add("--resource-dir")
        installerOptions.add(layout.projectDirectory.dir("packaging").asFile.absolutePath)
        installerOptions.add("--vendor")
        installerOptions.add("BAR")

        // Windows: system-wide install (no --win-per-user-install) for managed BIT
        // deployments; --win-dir-chooser lets the user pick a different install dir
        cliInstallerType?.let { installerType = it }
        if (packagingOs.isWindows) {
            installerOptions.add("--win-dir-chooser")
        }

        if (packagingOs.isLinux) {
            installerOptions.addAll(listOf("--linux-package-name", "siard-cli"))
        } else if (packagingOs.isMacOsX) {
            installerOptions.addAll(listOf("--mac-package-name", "siard-cli"))
        }
    }
}

// jpackage names installers <name>-<version>.<ext> (or <name>_<version>_<arch>.deb/.rpm);
// the release contract uses siard-cli-<version>-<os>.<ext>. Fail loudly rather than
// silently shipping a misnamed artifact.
tasks.named("jpackage") {
    doLast {
        if (cliInstallerType == null) {
            throw GradleException("no default installerType for OS '$cliOsName' — pass -PinstallerType=")
        }
        val jpackageDir = layout.buildDirectory.dir("jpackage").get().asFile
        // move (not rename in place): a second jpackage run with -PinstallerType=
        // wipes build/jpackage, so finished installers live in build/installers
        val installersDir = layout.buildDirectory.dir("installers").get().asFile
        installersDir.mkdirs()
        val targetName = "siard-cli-${version}-$cliOsName.$cliInstallerType"
        val produced = (jpackageDir.listFiles() ?: emptyArray()).filter {
            it.isFile && it.extension == cliInstallerType && it.name.startsWith("siard-cli")
        }
        val installer = produced.singleOrNull()
            ?: throw GradleException(
                "expected exactly one siard-cli *.$cliInstallerType installer in $jpackageDir, found: ${produced.map { it.name }}"
            )
        if (!installer.renameTo(File(installersDir, targetName))) {
            throw GradleException("failed to move ${installer.name} to $installersDir/$targetName")
        }
    }
}

listOf("jpackageImage", "jpackage").forEach { taskName ->
    tasks.named(taskName) {
        dependsOn(tasks.named("runtime"))
    }
}

val cliImageSuffix = if (cliOsName != "unknown") "-$cliOsName" else ""
tasks.register("verifyRuntimeImage") {
    description = "Verifies the bundled runtime image contains a siard launcher and no JavaFX modules"
    group = "verification"
    dependsOn("runtime")
    doLast {
        val imageBin = layout.buildDirectory.dir("siard-cli-${version}$cliImageSuffix/bin").get().asFile
        // the launcher is a script (siard/siard.bat); java is java/java.exe on Windows
        val launcherNames = if (packagingOs.isWindows) listOf("siard", "siard.bat") else listOf("siard")
        check(launcherNames.any { File(imageBin, it).isFile }) { "no bin/siard launcher in CLI runtime image" }
        val javaBin = File(imageBin, if (packagingOs.isWindows) "java.exe" else "java")
        val modules = ProcessBuilder(javaBin.absolutePath, "--list-modules")
            .redirectErrorStream(true)
            .start()
            .inputStream.bufferedReader().readText()
        check(modules.lineSequence().none { it.startsWith("javafx.") }) {
            "JavaFX module found in siard-cli runtime image (BIT forbids JavaFX in the CLI bundle)"
        }
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
                exclude("/manual/**")
            }

            duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        }
    }
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