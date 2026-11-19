plugins {
    `java-library`
    // only for @QuarkusTest, which needs the plugin's application model (design R4); the core is
    // not an application, so its packaging task is disabled below
    alias(libs.plugins.quarkus)
}

description = "SIARD Suite Web Backend Core"

// JVM flags siard-api needs, defined once in siard-web/build.gradle.kts for all web modules
@Suppress("UNCHECKED_CAST")
val siardJvmArgs = extra["siardApiJvmArgs"] as List<String>

base {
    archivesName.set("siard-web-backend-core")
}

// Shared backend library of the SIARD web apps (design R2): depends on siard-api and Quarkus only,
// never on siard-cmd or a JDBC driver (design R3). Its CDI beans and JAX-RS providers are
// discovered by the apps through the empty META-INF/beans.xml (design R4).
dependencies {
    // each app applies the Quarkus BOM itself (advisory, see the local backend); the core needs it
    // only to resolve the versions of the Quarkus artifacts below
    implementation(platform(libs.quarkus.bom))
    api("io.quarkus:quarkus-rest")
    api("io.quarkus:quarkus-rest-jackson")
    api("io.quarkus:quarkus-smallrye-openapi")
    // idle eviction of open archives (design D2)
    api("io.quarkus:quarkus-scheduler")

    api(project(":siard-api"))

    // siard-api uses javax JAXB internals (com.sun.xml.bind); the Quarkus BOM would otherwise
    // align the glassfish JAXB artifacts to the jakarta-based 4.x line. These transitive pins do not
    // win against the BOM inside an app (design R2): every app must declare the same pins itself;
    // without them its resolution fails with a conflict naming these modules.
    api("org.glassfish.jaxb:jaxb-runtime") { version { strictly("2.3.9") } }
    api("org.glassfish.jaxb:jaxb-core") { version { strictly("2.3.0.1") } }
    api("org.glassfish.jaxb:txw2") { version { strictly("2.3.9") } }
    // JAF in its javax.activation.* packaging, required by the javax JAXB RI;
    // jakarta.activation-api only carries javax.* packages up to 1.2.x
    api("jakarta.activation:jakarta.activation-api") { version { strictly("1.2.2") } }
}

dependencies {
    testImplementation(platform(libs.quarkus.bom))
    testImplementation("io.quarkus:quarkus-junit5")
    testImplementation("io.rest-assured:rest-assured")
    testRuntimeOnly(libs.junit.platform.launcher)
}

tasks.test {
    useJUnitPlatform()
    // siard-api needs these flags in every JVM that uses it (siard-web/build.gradle.kts, design R2)
    jvmArgs(siardJvmArgs)
}

// OpenAPI description of the shared API (design D10): the core is a library consumed as a plain
// jar, so quarkusBuild runs only to let Quarkus write the description to build/openapi/; nothing
// uses or publishes its build/quarkus-app. Set here, not in an application.properties, which
// would ship in the jar to every app. No build-dependent values, so the check only sees API changes.
quarkus {
    set("smallrye-openapi.store-schema-directory", "build/openapi")
    set("smallrye-openapi.info-title", "SIARD Suite web backend core API")
    set("smallrye-openapi.info-version", "1")
    set("smallrye-openapi.auto-add-server", "false")
}

// the committed copy openapi.yaml is what frontend-shared generates its types from
val generatedOpenApi = layout.buildDirectory.file("openapi/openapi.yaml")
val committedOpenApi = layout.projectDirectory.file("openapi.yaml")

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
                    "./gradlew :siard-web:backend-core:openApiUpdate :siard-web:local:backend:openApiUpdate, " +
                    "then commit both openapi.yaml files."
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

// Boundary of the core (design R3): a server built on the core must not be able to open database
// connections, and must not inherit the local agent's code. Fails with the offending coordinates.
val forbiddenGroups = setOf(
    "org.postgresql", "com.mysql", "com.microsoft.sqlserver", "org.mariadb.jdbc", "com.ibm.db2",
    "com.mysitex.sx.lib", // DB2 license jar
    "com.healthmarketscience.jackcess" // MS Access
)
// the DB2 and Oracle drivers are local jar files in jdbc-db2/lib and jdbc-oracle/lib, not modules
val forbiddenJarNames = Regex("^(ojdbc|db2jcc).*\\.jar$")

val verifyCoreBoundary = tasks.register("verifyCoreBoundary") {
    description = "Fails if siard-cmd, a jdbc-* module, a web app or a JDBC driver is on the core's runtime classpath."
    group = "verification"
    val runtimeClasspath = configurations.runtimeClasspath
    inputs.files(runtimeClasspath)
    doLast {
        val offending = mutableListOf<String>()
        runtimeClasspath.get().incoming.resolutionResult.allComponents.forEach { component ->
            when (val id = component.id) {
                is ProjectComponentIdentifier -> {
                    val path = id.projectPath
                    if (path == ":siard-cmd" || path.startsWith(":jdbc-") || path.startsWith(":siard-web:local")) {
                        offending += "project $path"
                    }
                }
                is ModuleComponentIdentifier -> {
                    if (id.group in forbiddenGroups || id.group.startsWith("com.oracle")) {
                        offending += id.displayName
                    }
                }
            }
        }
        runtimeClasspath.get().files.filter { forbiddenJarNames.matches(it.name) }
            .forEach { offending += "jar ${it.name}" }
        if (offending.isNotEmpty()) {
            throw GradleException(
                "siard-web:backend-core must not depend on siard-cmd, a jdbc-* module, a web app or a " +
                    "JDBC driver (restructure design R3). Found:\n  " + offending.distinct().joinToString("\n  ")
            )
        }
    }
}

tasks.named("check") {
    dependsOn(verifyCoreBoundary, openApiCheck)
}

tasks.test {
    // the compiled main classes, scanned by CoreBoundaryTest for references to the local app
    systemProperty("webcore.classesDirs", sourceSets.main.get().output.classesDirs.asPath)
    // the archive fixtures are read in place, never copied (design D13)
    systemProperty("siard.testfiles", project(":siard-api").file("src/test/resources/testfiles").absolutePath)
}
