import org.gradle.api.tasks.application.CreateStartScripts

plugins {
    application
    id("org.beryx.runtime") version "1.12.7"
    id("org.asciidoctor.jvm.convert") version "3.3.2"
    id("org.asciidoctor.jvm.pdf") version "3.3.2"
    id("io.freefair.lombok") version "6.5.0"
    id("com.cosminpolifronie.gradle.plantuml") version "1.6.0"
}

description = "SIARD Suite Application"

val mainClassName = "ch.admin.bar.siardsuite.Launcher"
val applicationName = "SIARD-Suite"

application {
    mainClass.set(mainClassName)
}

tasks.jar {
    doFirst {
        val manifestClasspath = configurations.compileClasspath.get().files.joinToString(" ") { it.name }
        manifest {
            attributes(
                "Implementation-Version" to "1.0",
                "Main-Class" to "ch.admin.bar.siardsuite.SiardApplication",
                "Class-Path" to manifestClasspath
            )
        }
    }
}

dependencies {
    implementation(project(":siard-cmd"))

    implementation("com.github.vatbub:mslinks:1.0.6.2")
    implementation("org.apache.tika:tika-core:2.9.1")

    implementation("org.glavo.materialfx:materialfx:11.13.5")
    implementation("ch.qos.logback:logback-classic:1.5.18")
    implementation("org.jetbrains:annotations:24.1.0")

    val javaFxVersion = "17.0.7"

    implementation(group = "org.openjfx", name = "javafx-base", version = javaFxVersion, classifier = "win")
    implementation(group = "org.openjfx", name = "javafx-base", version = javaFxVersion, classifier = "mac")
    implementation(group = "org.openjfx", name = "javafx-base", version = javaFxVersion, classifier = "linux")
    implementation(group = "org.openjfx", name = "javafx-controls", version = javaFxVersion, classifier = "win")
    implementation(group = "org.openjfx", name = "javafx-controls", version = javaFxVersion, classifier = "mac")
    implementation(group = "org.openjfx", name = "javafx-controls", version = javaFxVersion, classifier = "linux")
    implementation(group = "org.openjfx", name = "javafx-fxml", version = javaFxVersion, classifier = "win")
    implementation(group = "org.openjfx", name = "javafx-fxml", version = javaFxVersion, classifier = "mac")
    implementation(group = "org.openjfx", name = "javafx-fxml", version = javaFxVersion, classifier = "linux")
    implementation(group = "org.openjfx", name = "javafx-graphics", version = javaFxVersion, classifier = "win")
    implementation(group = "org.openjfx", name = "javafx-graphics", version = javaFxVersion, classifier = "mac")
    implementation(group = "org.openjfx", name = "javafx-graphics", version = javaFxVersion, classifier = "linux")

    testImplementation("org.junit.jupiter:junit-jupiter:5.9.0")
    testImplementation("org.testfx:testfx-core:4.0.16-alpha")
    testImplementation("org.testfx:testfx-junit5:4.0.16-alpha")
    testImplementation("org.testfx:openjfx-monocle:jdk-12.0.1+2")
    testImplementation("org.mockito:mockito-core:2.1.0")

    implementation(files("lib/jfxrt.jar"))
}

tasks.withType<JavaExec> {
    jvmArgs(
        "-Djavax.xml.parsers.SAXParserFactory=com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl",
        "--add-opens=java.xml/com.sun.org.apache.xerces.internal.jaxp=ALL-UNNAMED",
        "--add-opens=java.xml/com.sun.org.apache.xalan.internal.xsltc.trax=ALL-UNNAMED",
        "--add-opens=javafx.graphics/com.sun.javafx.application=ALL-UNNAMED",
        "--add-opens=javafx.graphics/com.sun.glass.ui=ALL-UNNAMED",
        "--add-opens=java.base/java.lang=ALL-UNNAMED"
    )
}

runtime {
    options.set(listOf("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages"))

    launcher {
        noConsole = false
        jvmArgs = listOf(
            "-Djavax.xml.parsers.SAXParserFactory=com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl",
            "--add-opens=java.xml/com.sun.org.apache.xerces.internal.jaxp=ALL-UNNAMED",
            "--add-opens=java.xml/com.sun.org.apache.xalan.internal.xsltc.trax=ALL-UNNAMED",
            "--add-opens=javafx.graphics/com.sun.javafx.application=ALL-UNNAMED",
            "--add-opens=javafx.graphics/com.sun.glass.ui=ALL-UNNAMED"
        )
    }

    additive = true
    modules = listOf(
        "java.logging",
        "java.sql",
        "java.management",
        "java.naming",
        "java.security.jgss"
    )

    imageZip.set(file("${layout.buildDirectory.get()}/image-zip/SIARD-Suite.${version}.zip"))

    val currentOs = org.gradle.internal.os.OperatingSystem.current()
    if (currentOs.isWindows) {
        imageZip.set(file("${layout.buildDirectory.get()}/image-zip/SIARD-Suite.${version}-win.zip"))
        imageDir.set(file("${layout.buildDirectory.get()}/SIARD-Suite.${version}-win"))
    } else if (currentOs.isLinux) {
        imageZip.set(file("${layout.buildDirectory.get()}/image-zip/SIARD-Suite.${version}-linux.zip"))
        imageDir.set(file("${layout.buildDirectory.get()}/SIARD-Suite.${version}-linux"))
    } else if (currentOs.isMacOsX) {
        imageZip.set(file("${layout.buildDirectory.get()}/image-zip/SIARD-Suite.${version}-mac.zip"))
        imageDir.set(file("${layout.buildDirectory.get()}/SIARD-Suite.${version}-mac"))
    }

    jpackage {
        imageName = "SIARD-Suite"
        installerName = "SIARD-Suite"
        appVersion = version.toString()

        skipInstaller = false

        val imgType = if (currentOs.isWindows) "ico" else if (currentOs.isMacOsX) "icns" else "png"
        imageOptions.add("--icon")
        imageOptions.add("src/main/resources/ch/admin/bar/siardsuite/icons/archive_red.$imgType")

        installerOptions.add("--resource-dir")
        installerOptions.add("src/main/resources")
        installerOptions.add("--vendor")
        installerOptions.add("BAR")
        
        if (currentOs.isWindows) {
            installerOptions.addAll(
                listOf(
                    "--win-per-user-install",
                    "--win-dir-chooser",
                    "--win-menu",
                    "--win-menu-group", "SIARD Suite",
                    "--win-shortcut"
                )
            )
        } else if (currentOs.isLinux) {
            installerType = "deb"
            installerOptions.addAll(listOf("--linux-package-name", "siard-suite", "--linux-shortcut"))
        } else if (currentOs.isMacOsX) {
            installerOptions.addAll(listOf("--mac-package-name", "siard-suite"))
        }
    }
}

tasks.test {
    useJUnitPlatform()
    jvmArgs(
        "-Djava.awt.headless=true",
        "-Dtestfx.headless=true",
        "-Djavax.xml.parsers.SAXParserFactory=com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl",
        "--add-opens=java.xml/com.sun.org.apache.xerces.internal.jaxp=ALL-UNNAMED",
        "--add-opens=java.xml/com.sun.org.apache.xalan.internal.xsltc.trax=ALL-UNNAMED",
        "--add-opens=javafx.graphics/com.sun.javafx.application=ALL-UNNAMED",
        "--add-opens=javafx.graphics/com.sun.glass.ui=ALL-UNNAMED",
        "--add-opens=java.base/java.lang=ALL-UNNAMED"
    )
}

tasks.processResources {
    filesMatching("**/version.properties") {
        expand("projectVersion" to project.version)
    }
}

val siardFromDb by tasks.registering(CreateStartScripts::class) {
    applicationName = "siard-from-db"
    mainClass.set("ch.admin.bar.siard2.cmd.SiardFromDb")
    outputDir = file("build/scripts")
    classpath = files(tasks.jar.get().outputs.files, configurations.runtimeClasspath.get())
}

val siardToDb by tasks.registering(CreateStartScripts::class) {
    applicationName = "siard-to-db"
    mainClass.set("ch.admin.bar.siard2.cmd.SiardToDb")
    outputDir = file("build/scripts")
    classpath = files(tasks.jar.get().outputs.files, configurations.runtimeClasspath.get())
}

siardFromDb {
    dependsOn(siardToDb)
}

tasks.distTar {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    into("${applicationName}-${project.version}/licenses") {
        from("licenses")
    }
    dependsOn(siardFromDb)
}

tasks.distZip {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    into("${applicationName}-${project.version}/licenses") {
        from("licenses")
    }
    dependsOn(siardFromDb)
}

tasks.installDist {
    dependsOn(siardFromDb)
    dependsOn(siardToDb)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.asciidoctor {
    languages("en", "de", "fr", "it")
    baseDirFollowsSourceDir()

    sourceDir("../docs/user-manual/")
    setOutputDir(file("build/docs"))
    sources {
        include("user-manual.adoc")
    }
}

tasks.asciidoctorPdf {
    languages("en", "de", "fr", "it")
    baseDirFollowsSourceDir()

    setOutputDir(file("build/docs/pdf"))
    sourceDir("../docs/user-manual/")

    pdfThemes {
        local("basic") {
            themeDir = file("../docs/theme")
            themeName = "siard-theme"
        }
    }

    asciidoctorj {
        attributes(
            mapOf(
                "media" to "press",
                "styles-dir" to "../docs/theme",
                "stylesheet" to "siard-theme.css",
                "source-highlighter" to "coderay",
                "imagesdir" to "images",
                "toc" to "left"
            )
        )
    }
    dependsOn(tasks.asciidoctor)
}

val copyDocumentation by tasks.registering(Copy::class) {
    from(layout.buildDirectory.dir("docs/pdf"))
    into(layout.projectDirectory.dir("./src/main/resources/ch/admin/bar/siardsuite/doc"))
}

tasks.named("asciidoctorPdf") { 
    finalizedBy(copyDocumentation)
}

tasks.named("build") {
    dependsOn(tasks.asciidoctorPdf)
}

tasks.named("processResources") { 
    dependsOn(copyDocumentation)
}

tasks.named("jpackageImage") {
    dependsOn(tasks.named("runtime"))
}

tasks.withType<CreateStartScripts>().configureEach {
    doLast {
        val jvmArgsLine = listOf(
            "-Djavax.xml.parsers.SAXParserFactory=com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl",
            "--add-opens=java.xml/com.sun.org.apache.xerces.internal.jaxp=ALL-UNNAMED",
            "--add-opens=java.xml/com.sun.org.apache.xalan.internal.xsltc.trax=ALL-UNNAMED",
            "--add-opens=javafx.graphics/com.sun.javafx.application=ALL-UNNAMED",
            "--add-opens=javafx.graphics/com.sun.glass.ui=ALL-UNNAMED",
            "--add-opens=java.base/java.lang=ALL-UNNAMED"
        ).joinToString(" ")

        unixScript.writeText(
            unixScript.readText().replace(
                "DEFAULT_JVM_OPTS=\"\"",
                "DEFAULT_JVM_OPTS=\"$jvmArgsLine\""
            )
        )

        windowsScript.writeText(
            windowsScript.readText().replace(
                "set DEFAULT_JVM_OPTS=",
                "set DEFAULT_JVM_OPTS=$jvmArgsLine"
            )
        )
    }
}
