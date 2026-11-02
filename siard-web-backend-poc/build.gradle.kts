plugins {
    application
    alias(libs.plugins.quarkus)
    alias(libs.plugins.beryx.runtime)
}

description = "SIARD Suite Web Companion Backend"

val xercesSaxParserFactory = extra["xercesSaxParserFactory"] as String

application {
    mainClass.set("io.quarkus.bootstrap.runner.QuarkusEntryPoint")
    applicationName = "SIARD-Web"
}

dependencies {
    // advisory (not enforced) platform: siard-api needs javax JAXB internals
    // (com.sun.xml.bind) that the BOM's jakarta-aligned glassfish artifacts lack;
    // the strictly() pins below must be able to win over the BOM alignment
    implementation(platform(libs.quarkus.bom))
    implementation("io.quarkus:quarkus-rest")
    implementation("io.quarkus:quarkus-rest-jackson")
    implementation("io.quarkus:quarkus-smallrye-health")
    implementation("io.quarkus:quarkus-arc")

    // logback would double-bind SLF4J alongside Quarkus' JBoss log manager
    implementation(project(":siard-cmd")) {
        exclude(group = "ch.qos.logback")
    }
    implementation(project(":siard-api"))
    implementation(project(":siard-web-frontend-poc"))

    // siard-api uses javax JAXB internals (com.sun.xml.bind); the Quarkus BOM would
    // otherwise align the glassfish JAXB artifacts to the jakarta-based 4.x line
    implementation("org.glassfish.jaxb:jaxb-runtime") { version { strictly("2.3.9") } }
    implementation("org.glassfish.jaxb:jaxb-core") { version { strictly("2.3.0.1") } }
    implementation("org.glassfish.jaxb:txw2") { version { strictly("2.3.9") } }
    // JAF in its javax.activation.* packaging — required by the javax JAXB RI, dropped
    // from the JDK in Java 11; jakarta.activation-api only carries javax.* packages up to
    // 1.2.x (2.x renamed them to jakarta.*)
    implementation("jakarta.activation:jakarta.activation-api") { version { strictly("1.2.2") } }

    testImplementation("io.quarkus:quarkus-junit5")
    testImplementation("io.rest-assured:rest-assured")
    testRuntimeOnly(libs.junit.platform.launcher)
}

// the application distribution must carry the Quarkus fast-jar layout
// (quarkus-run.jar + quarkus/, app/, lib/ siblings): the thin jar + loose
// dependency layout breaks QuarkusEntryPoint, which resolves
// quarkus/quarkus-application.dat relative to the run jar's parent dir
tasks.named("installDist") {
    dependsOn("quarkusBuild")
    doLast {
        val libDir = layout.buildDirectory.dir("install/SIARD-Web/lib").get().asFile
        libDir.listFiles()?.forEach { it.delete() }
        copy {
            from(layout.buildDirectory.dir("quarkus-app")) {
                rename("quarkus-run.jar", "${project.name}-${project.version}.jar")
            }
            into(libDir)
        }
    }
}

// jpackage puts every jar in the app image on app.classpath, which loads the
// application and dependency classes through the system classloader and breaks
// Quarkus' RunnerClassLoader isolation — keep only the run jar on the classpath
val patchAppImageCfg = tasks.register("patchAppImageCfg") {
    dependsOn("jpackageImage")
    doLast {
        val cfg = layout.buildDirectory.file("jpackage/SIARD-Web/lib/app/SIARD-Web.cfg").get().asFile
        val runJarLine = "app.classpath=\$APPDIR/${project.name}-${project.version}.jar"
        val kept = cfg.readLines().filter { line ->
            !line.startsWith("app.classpath=") || line == runJarLine
        }
        if (runJarLine !in kept) {
            throw GradleException("run jar classpath entry not found in $cfg")
        }
        cfg.writeText(kept.joinToString("\n") + "\n")
    }
}

tasks.named("jpackage") {
    dependsOn(patchAppImageCfg)
}

tasks.test {
    useJUnitPlatform()
    jvmArgs(
        xercesSaxParserFactory,
        "--add-opens=java.xml/com.sun.org.apache.xerces.internal.jaxp=ALL-UNNAMED",
        "--add-opens=java.xml/com.sun.org.apache.xalan.internal.xsltc.trax=ALL-UNNAMED",
        "--add-opens=java.base/java.lang=ALL-UNNAMED"
    )
}

runtime {
    options.set(listOf("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages"))

    launcher {
        noConsole = false
        jvmArgs = listOf(
            xercesSaxParserFactory,
            "--add-opens=java.xml/com.sun.org.apache.xerces.internal.jaxp=ALL-UNNAMED",
            "--add-opens=java.xml/com.sun.org.apache.xalan.internal.xsltc.trax=ALL-UNNAMED",
            "--add-opens=java.base/java.lang=ALL-UNNAMED"
        )
    }

    additive = true
    modules = listOf(
        "java.desktop",
        "java.logging",
        "java.sql",
        "java.management",
        "java.naming",
        "java.security.jgss",
        "jdk.crypto.ec",
        "jdk.unsupported",
        "jdk.zipfs"
    )

    jpackage {
        imageName = "SIARD-Web"
        installerName = "SIARD-Web"
        appVersion = version.toString()
        skipInstaller = false

        installerOptions.add("--vendor")
        installerOptions.add("BAR")

        val currentOs = org.gradle.internal.os.OperatingSystem.current()
        if (currentOs.isWindows) {
            installerOptions.addAll(
                listOf(
                    "--win-per-user-install",
                    "--win-menu",
                    "--win-menu-group", "SIARD Suite",
                    "--win-shortcut"
                )
            )
        } else if (currentOs.isLinux) {
            installerType = "deb"
            installerOptions.addAll(listOf("--linux-package-name", "siard-web", "--linux-shortcut"))
        } else if (currentOs.isMacOsX) {
            installerOptions.addAll(listOf("--mac-package-name", "siard-web"))
        }
    }
}
