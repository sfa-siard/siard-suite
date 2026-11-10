plugins {
    java
    alias(libs.plugins.quarkus)
}

description = "SIARD Suite Local Web App Backend"

base {
    archivesName.set("siard-suite-local-web-app-backend")
}

val xercesSaxParserFactory = extra["xercesSaxParserFactory"] as String

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
    jvmArgs(
        xercesSaxParserFactory,
        "--add-opens=java.xml/com.sun.org.apache.xerces.internal.jaxp=ALL-UNNAMED",
        "--add-opens=java.xml/com.sun.org.apache.xalan.internal.xsltc.trax=ALL-UNNAMED",
        "--add-opens=java.base/java.lang=ALL-UNNAMED"
    )
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
