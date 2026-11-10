plugins {
    application
    alias(libs.plugins.quarkus)
    alias(libs.plugins.beryx.runtime)
}

description = "SIARD Suite Local Web App Backend"

base {
    archivesName.set("siard-suite-local-web-app-backend")
}

val xercesSaxParserFactory = extra["xercesSaxParserFactory"] as String

// JVM flags siard-api needs (design D2), for tests, the launcher and the packaged runtime
val siardJvmArgs = listOf(
    xercesSaxParserFactory,
    "--add-opens=java.xml/com.sun.org.apache.xerces.internal.jaxp=ALL-UNNAMED",
    "--add-opens=java.xml/com.sun.org.apache.xalan.internal.xsltc.trax=ALL-UNNAMED",
    "--add-opens=java.base/java.lang=ALL-UNNAMED"
)

// differs from the desktop app's "SIARD-Suite" so both can be installed side by side
val appName = "SIARD-Suite-Web"

application {
    mainClass.set("io.quarkus.bootstrap.runner.QuarkusEntryPoint")
    applicationName = appName
}

dependencies {
    // advisory (not enforced) platform: siard-api needs javax JAXB internals
    // (com.sun.xml.bind) that the BOM's jakarta-aligned glassfish artifacts lack;
    // the strictly() pins below must be able to win over the BOM alignment
    implementation(platform(libs.quarkus.bom))
    implementation("io.quarkus:quarkus-rest")
    implementation("io.quarkus:quarkus-rest-jackson")
    implementation("io.quarkus:quarkus-smallrye-health")
    implementation("io.quarkus:quarkus-smallrye-openapi")
    implementation("io.quarkus:quarkus-arc")

    // logback would double-bind SLF4J alongside Quarkus' JBoss log manager
    implementation(project(":siard-cmd")) {
        exclude(group = "ch.qos.logback")
    }
    implementation(project(":siard-api"))
    // the built frontend, served from META-INF/resources
    implementation(project(":siard-suite-local-web-app:frontend"))

    // siard-api uses javax JAXB internals (com.sun.xml.bind); the Quarkus BOM would
    // otherwise align the glassfish JAXB artifacts to the jakarta-based 4.x line
    implementation("org.glassfish.jaxb:jaxb-runtime") { version { strictly("2.3.9") } }
    implementation("org.glassfish.jaxb:jaxb-core") { version { strictly("2.3.0.1") } }
    implementation("org.glassfish.jaxb:txw2") { version { strictly("2.3.9") } }
    // JAF in its javax.activation.* packaging, required by the javax JAXB RI;
    // jakarta.activation-api only carries javax.* packages up to 1.2.x
    implementation("jakarta.activation:jakarta.activation-api") { version { strictly("1.2.2") } }

    // ship exactly the JDBC driver versions the siard-cmd integration tests use; the
    // advisory Quarkus BOM would otherwise align these drivers to its own versions
    implementation(libs.postgresql) { version { strictly(libs.versions.postgresql.get()) } }
    implementation(libs.mysql.connector) { version { strictly(libs.versions.mysql.get()) } }
    implementation(libs.mssql.jdbc) { version { strictly(libs.versions.mssql.get()) } }

    testImplementation("io.quarkus:quarkus-junit5")
    testImplementation("io.rest-assured:rest-assured")
    testRuntimeOnly(libs.junit.platform.launcher)
}

tasks.test {
    useJUnitPlatform()
    jvmArgs(siardJvmArgs)
}

// src/integrationTest (source set and quarkusIntTest task come from the Quarkus plugin) starts the
// packaged app as a separate process, for spec scenarios that need LaunchMode.NORMAL
tasks.named("check") {
    dependsOn("quarkusIntTest")
}

// OpenAPI description (design D11): Quarkus writes it to build/openapi/ during quarkusBuild; the
// committed copy ../openapi.yaml is what the frontend generates its types from
val generatedOpenApi = layout.buildDirectory.file("openapi/openapi.yaml")
val committedOpenApi = layout.projectDirectory.file("../openapi.yaml")

val openApiCheck = tasks.register("openApiCheck") {
    description = "Fails if the generated OpenAPI description differs from the committed openapi.yaml."
    group = "verification"
    dependsOn("quarkusBuild")
    inputs.files(generatedOpenApi, committedOpenApi)
    doLast {
        val generated = generatedOpenApi.get().asFile
        val committed = committedOpenApi.asFile
        if (!generated.isFile) {
            throw GradleException("$generated was not written by quarkusBuild")
        }
        // line endings may differ after a Windows checkout
        fun normalized(file: File) = file.readText().replace("\r\n", "\n")
        if (!committed.isFile || normalized(generated) != normalized(committed)) {
            throw GradleException(
                "The API changed: ${committed.canonicalPath} is out of date. Run " +
                    "./gradlew :siard-suite-local-web-app:backend:openApiUpdate, then commit openapi.yaml."
            )
        }
    }
}

tasks.register("openApiUpdate") {
    description = "Copies the generated OpenAPI description to the committed openapi.yaml."
    group = "build"
    dependsOn("quarkusBuild")
    doLast {
        generatedOpenApi.get().asFile.copyTo(committedOpenApi.asFile, overwrite = true)
    }
}

tasks.named("check") {
    dependsOn(openApiCheck)
}

tasks.named<Test>("quarkusIntTest") {
    // the version the packaged agent must report in /api/system/info
    systemProperty("siard.expected-app-version", project.version.toString())
}

// --- Packaging: bundled runtime without JavaFX, Windows/Linux installers, macOS zip ---

// the distribution must carry the Quarkus fast-jar layout (quarkus-run.jar next to quarkus/, app/,
// lib/): QuarkusEntryPoint resolves quarkus/quarkus-application.dat relative to the run jar, so
// the thin jar plus loose dependencies of a plain installDist does not start. The run jar takes
// the project jar's name, which is the one the generated start scripts put on the classpath.
val runJarName = tasks.jar.flatMap { it.archiveFileName }

tasks.named<Sync>("installDist") {
    dependsOn("quarkusBuild")
    doLast {
        val libDir = destinationDir.resolve("lib")
        libDir.listFiles()?.forEach { it.deleteRecursively() }
        copy {
            from(layout.buildDirectory.dir("quarkus-app")) {
                rename("quarkus-run.jar", runJarName.get())
            }
            into(libDir)
        }
    }
}

// jpackage puts every jar of the app image on app.classpath, which loads the application through
// the system class loader and breaks Quarkus' RunnerClassLoader: keep only the run jar
val patchAppImageCfg = tasks.register("patchAppImageCfg") {
    dependsOn("jpackageImage")
    doLast {
        // the app image layout differs per OS: linux lib/app/, windows app/, macOS .app/Contents/app/
        val cfg = layout.buildDirectory.dir("jpackage/$appName").get().asFile
            .walkTopDown().first { it.name == "$appName.cfg" }
        val jarName = runJarName.get()
        val lines = cfg.readLines()
        if (lines.none { it.startsWith("app.classpath=") && it.endsWith(jarName) }) {
            throw GradleException("run jar classpath entry not found in $cfg:\n" + lines.joinToString("\n"))
        }
        cfg.writeText(lines.filter { !it.startsWith("app.classpath=") || it.endsWith(jarName) }
            .joinToString("\n") + "\n")
    }
}

// jpackageImage reads the runtime image written by the runtime task (build/jre) without declaring
// it; make the dependency explicit so both can run in one build
tasks.named("jpackageImage") {
    dependsOn(tasks.named("runtime"))
}

tasks.named("jpackage") {
    dependsOn(patchAppImageCfg)
}

runtime {
    options.set(listOf("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages"))
    // modules found by jdeps plus these, which are only reached reflectively or via services
    additive.set(true)
    modules.set(listOf(
        "java.desktop", // AWT Desktop.browse for the browser launch (design D9)
        "java.logging",
        "java.sql",
        "java.management",
        "java.naming",
        "java.security.jgss",
        "jdk.crypto.ec",
        "jdk.unsupported",
        "jdk.zipfs"
    ))
    launcher {
        noConsole = false
        jvmArgs = siardJvmArgs
    }

    val currentOs = org.gradle.internal.os.OperatingSystem.current()
    val platform = when {
        currentOs.isWindows -> "win"
        currentOs.isMacOsX -> "mac"
        else -> "linux"
    }
    // runtimeZip: the macOS deliverable (no installer), a zip with the runtime and a start script
    imageDir.set(layout.buildDirectory.dir("$appName.$version-$platform"))
    imageZip.set(layout.buildDirectory.file("image-zip/$appName.$version-$platform.zip"))

    jpackage {
        imageName = appName
        installerName = appName
        // jpackage needs a numeric version (MSI ProductVersion): strip -branch/-SNAPSHOT suffixes
        appVersion = version.toString().replaceFirst(Regex("[^0-9.].*$"), "").removeSuffix(".")
        skipInstaller = false

        val iconType = if (currentOs.isWindows) "ico" else if (currentOs.isMacOsX) "icns" else "png"
        imageOptions.addAll(listOf(
            "--icon",
            file("src/packaging/icons/archive_red.$iconType").path
        ))
        installerOptions.addAll(listOf("--vendor", "BAR"))
        if (currentOs.isWindows) {
            installerOptions.addAll(listOf(
                "--win-per-user-install",
                "--win-menu",
                "--win-menu-group", "SIARD Suite",
                "--win-shortcut"
            ))
        } else if (currentOs.isLinux) {
            installerType = "deb"
            installerOptions.addAll(listOf("--linux-package-name", "siard-suite-web", "--linux-shortcut"))
        }
    }
}
