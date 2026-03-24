import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

plugins {
    java
    alias(libs.plugins.lombok)
}

description = "SIARD API"

repositories {
    flatDir {
        dirs("lib")
    }
}

// Define directories similar to Ant properties
val dirSrc = "src/main/java"
val dirRes = "src/main/resources/res"
val dirGenerated = "$dirSrc/ch/admin/bar/siard2/api/generated"
val dirTmp = "tmp"

// used for xjc tasks
val xjcConfiguration = configurations.create("xjc")

// Define dependencies similar to Ant classpath definitions
dependencies {
    implementation(project(":siard-utilities"))
    implementation(project(":sql-parser"))
    implementation(project(":zip64-file"))

    implementation(libs.antlr4.runtime)

    implementation(libs.jaxb.activation)
    implementation(libs.jaxb.api)
    implementation(libs.jaxb.core)
    implementation(libs.jaxb.impl)

    implementation(libs.woodstox.stax2)
    implementation(libs.woodstox.core)

    implementation(libs.msv.core)
    implementation(libs.xsdlib)

    implementation(libs.commons.text)
    implementation(libs.jsoup)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.vintage.engine)
    testRuntimeOnly(libs.junit.platform.launcher)
    testImplementation(libs.mockito.junit.jupiter)

    xjcConfiguration(libs.jaxb.xjc)
    xjcConfiguration(libs.jaxb.runtime)
}

tasks.test {
    useJUnitPlatform()
}

// Create necessary directories
tasks.register("createDirs") {
    dependsOn("clean")
    group = "build"
    description = "Initialize project directories"

    doLast {
        mkdir("$dirGenerated/old10")
        mkdir("$dirGenerated/old21")
        mkdir("$dirGenerated/table")
        mkdir(dirTmp)
        mkdir("$dirTmp/lobs")
    }
}

// Task to generate JAXB classes from XSD files
tasks.register<JavaExec>("generateJaxb") {
    group       = "build"
    description = "Generate JAXB classes from XSD files"

    classpath   = xjcConfiguration
    mainClass.set("com.sun.tools.xjc.XJCFacade")

    // first schema
    args("-encoding", "UTF-8", "-npa", "-d", dirSrc,
        "-p", "ch.admin.bar.siard2.api.generated",
        "$dirRes/metadata.xsd")

    // run three more times for the other packages
    doLast {
        fun runXjc(pkg: String, xsd: String) = exec {
            commandLine = listOf(
                "java", "-cp", xjcConfiguration.asPath,
                "com.sun.tools.xjc.XJCFacade",
                "-encoding", "UTF-8", "-npa", "-d", dirSrc,
                "-p", pkg, xsd
            )
        }
        runXjc("ch.admin.bar.siard2.api.generated.old10", "$dirRes/old10/metadata.xsd")
        runXjc("ch.admin.bar.siard2.api.generated.old21", "$dirRes/old21/metadata.xsd")
        runXjc("ch.admin.bar.siard2.api.generated.table",  "$dirRes/table.xsd")
    }
}


// Configure Java compilation
tasks.compileJava {
    dependsOn("generateJaxb")
    options.encoding = "UTF-8"
}

// Configure resources processing
tasks.processResources {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    dependsOn("generateJaxb")
    // Ensure resources are copied to the output directory
    from("src/main/resources") {
        include("**/*.*")
    }
}

// Update the manifest with version and build date
tasks.jar {
    manifest {
        attributes(
            "Implementation-Title" to "SIARD API",
            "Implementation-Version" to project.version,
            "Built-Date" to LocalDate.now().format(DateTimeFormatter.ofPattern("dd. MMM yyyy", Locale.ENGLISH))
        )
    }
}


// Clean task
tasks.clean {
    delete(dirTmp)
    delete(dirGenerated)
}
