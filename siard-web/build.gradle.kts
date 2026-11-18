// :siard-web groups the web modules: the shared backend core and the web apps. It is also the root of
// the npm workspace of the frontends (design R5): one Node version, one lockfile, one `npm ci`.

plugins {
    base
    alias(libs.plugins.node.gradle)
}

// Node is downloaded by the build, so no local Node installation is required. The version comes from
// .nvmrc, the single place to change it (mise.toml mirrors it). The frontend projects share this
// download (their node.workDir points here) and run their npm scripts after this project's npmInstall.
node {
    version.set(file(".nvmrc").readText().trim())
    download.set(true)
    npmInstallCommand.set("ci")
}

tasks.npmInstall {
    // npm ci installs every workspace, so their package.json files are inputs too
    inputs.files(fileTree(".") { include("*/package.json", "*/*/package.json"); exclude("**/node_modules/**") })
}

subprojects {
    // JVM flags siard-api needs at runtime, for the web modules' tests, launchers and packaged
    // runtimes (restructure-into-siard-web design R2). Set here and not in backend-core, because an
    // extra property is only visible to the project that sets it and its subprojects, and the apps
    // are siblings of the core. xercesSaxParserFactory comes from the root build.
    extra["siardApiJvmArgs"] = listOf(
        extra["xercesSaxParserFactory"] as String,
        "--add-opens=java.xml/com.sun.org.apache.xerces.internal.jaxp=ALL-UNNAMED",
        "--add-opens=java.xml/com.sun.org.apache.xalan.internal.xsltc.trax=ALL-UNNAMED",
        "--add-opens=java.base/java.lang=ALL-UNNAMED"
    )
}

// ./gradlew :siard-web:check runs the checks of every web module (design R9)
tasks.named("check") {
    dependsOn(
        ":siard-web:backend-core:check",
        ":siard-web:frontend-shared:check",
        ":siard-web:local:check"
    )
}
