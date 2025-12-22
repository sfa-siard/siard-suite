import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

plugins {
    java
    id("pl.allegro.tech.build.axion-release") version "1.14.3"
    id("io.freefair.lombok") version "6.5.0"
}

group = "ch.admin.bar"
version = scmVersion.version

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
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
    implementation("ch.admin.bar:enterutilities:v2.2.6")
    implementation("ch.admin.bar:SqlParser:v2.2.5")
    implementation("ch.admin.bar:Zip64File:v2.2.6")

    implementation("org.antlr:antlr4-runtime:4.5.2")

    implementation("javax.activation:activation:1.1.1")
    implementation("javax.xml.bind:jaxb-api:2.3.0")
    implementation("com.sun.xml.bind:jaxb-core:2.3.0")
    implementation("com.sun.xml.bind:jaxb-impl:2.3.0")

    implementation("org.codehaus.woodstox:stax2-api:3.1.1")
    implementation("org.codehaus.woodstox:woodstox-core-lgpl:4.1.2")

    implementation("net.java.dev.msv:msv-core:2010.2")
    implementation("net.java.dev.msv:xsdlib:2013.2.2")

    implementation("org.apache.commons:commons-text:1.14.0")
    implementation("org.jsoup:jsoup:1.21.2") // html pretty printing in html export

    testImplementation(platform("org.junit:junit-bom:5.13.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.junit.vintage:junit-vintage-engine")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.mockito:mockito-junit-jupiter:5.19.0")

    // 4.x uses jakarta.* packages.  For a javax‑based project stick to 2.3.*.
    xjcConfiguration("org.glassfish.jaxb:jaxb-xjc:2.3.2")
    xjcConfiguration("org.glassfish.jaxb:jaxb-runtime:2.3.2")  // needed by the compiler itself
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
