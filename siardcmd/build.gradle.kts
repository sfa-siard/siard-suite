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

    implementation("ch.admin.bar:siard-api:v2.2.134")
    implementation("ch.admin.bar:SqlParser:v2.2.4")
    implementation("ch.admin.bar:Zip64File:v2.2.5")
    implementation("ch.admin.bar:enterutilities:v2.2.5")
    implementation("ch.admin.bar:jdbc-base:v2.2.11")
    implementation("ch.admin.bar:JdbcPostgres:v2.2.4")
    implementation("ch.admin.bar:jdbcoracle:v2.2.8")
    implementation("ch.admin.bar:jdbcmssql:v2.2.5")
    implementation("ch.admin.bar:jdbc-mysql:v2.2.5")
    implementation("ch.admin.bar:jdbc-access:v2.2.4")
    implementation("ch.admin.bar:jdbc-db2:v2.2.4")

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.assertj:assertj-core:3.24.2")
    testImplementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.15.2")
    testImplementation("com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.15.2")
    testImplementation("org.mockito:mockito-core:5.19.0")

    // testcontainers
    testImplementation("org.testcontainers:testcontainers:1.19.0")
    testImplementation("org.testcontainers:mssqlserver:1.19.0")
    testImplementation("org.testcontainers:postgresql:1.19.0")
    testImplementation("org.testcontainers:mysql:1.19.0")
    testImplementation("org.testcontainers:mariadb:1.19.6")
    testImplementation("org.mariadb.jdbc:mariadb-java-client:2.7.4") // Used by mariadb testcontainer
    testImplementation("org.testcontainers:oracle-xe:1.19.0")
    testImplementation("org.testcontainers:db2:1.19.0")

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

task<Test>("integrationTest") {
    description = "Runs the integration tests"
    group = "verification"
    testClassesDirs = sourceSets["integrationTest"].output.classesDirs
    classpath = sourceSets["integrationTest"].runtimeClasspath
    mustRunAfter(tasks["test"])
    useJUnitPlatform()
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