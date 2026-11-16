import com.github.gradle.node.npm.task.NpmTask
import com.github.gradle.node.npm.task.NpxTask

plugins {
    java
    alias(libs.plugins.node.gradle)
}

description = "SIARD Suite Local Web App Frontend"

base {
    archivesName.set("siard-suite-local-web-app-frontend")
}

// Node is downloaded by the build, so no local Node installation is required.
// The version comes from .nvmrc, the single place to change it (mise.toml mirrors it).
node {
    version.set(file(".nvmrc").readText().trim())
    download.set(true)
    npmInstallCommand.set("ci")
}

// ../openapi.yaml: the committed API description the TypeScript types are generated from
val sources = listOf("src", "index.html", "package.json", "package-lock.json", "tsconfig.json", "tsconfig.app.json", "tsconfig.node.json", "vite.config.ts", "playwright.config.ts", "e2e", "../openapi.yaml")

val frontendBuild = tasks.register<NpmTask>("frontendBuild") {
    description = "Builds the frontend into dist/."
    dependsOn(tasks.npmInstall)
    args.set(listOf("run", "build"))
    inputs.files(sources)
    outputs.dir("dist")
}

val frontendTypecheck = tasks.register<NpmTask>("frontendTypecheck") {
    description = "Type-checks the frontend with tsc."
    dependsOn(tasks.npmInstall)
    args.set(listOf("run", "typecheck"))
    inputs.files(sources)
    outputs.upToDateWhen { true }
}

val frontendLint = tasks.register<NpmTask>("frontendLint") {
    description = "Runs ESLint and the Prettier check."
    dependsOn(tasks.npmInstall)
    args.set(listOf("run", "lint"))
    inputs.files(sources + listOf("eslint.config.js", ".prettierrc.json", ".prettierignore"))
    outputs.upToDateWhen { true }
}

val frontendTest = tasks.register<NpmTask>("frontendTest") {
    description = "Runs the Vitest tests."
    dependsOn(tasks.npmInstall)
    args.set(listOf("run", "test"))
    inputs.files(sources)
    outputs.upToDateWhen { true }
}

tasks.named("check") {
    dependsOn(frontendTypecheck, frontendLint, frontendTest)
}

// the Playwright smoke test against the packaged agent (design D12); not part of check, CI runs it
// as its own step. Playwright downloads its pinned Chromium build on the first run.
val playwrightInstall = tasks.register<NpxTask>("playwrightInstall") {
    description = "Downloads the Chromium build Playwright needs, unless it is already there."
    dependsOn(tasks.npmInstall)
    command.set("playwright")
    args.set(listOf("install", "chromium"))
}

tasks.register<NpmTask>("e2eTest") {
    description = "Runs the Playwright smoke test against the packaged agent."
    group = "verification"
    dependsOn(playwrightInstall, ":siard-suite-local-web-app:backend:quarkusBuild")
    args.set(listOf("run", "e2e"))
    val backend = project(":siard-suite-local-web-app:backend")
    environment.putAll(mapOf(
        "E2E_JAVA" to File(System.getProperty("java.home"), "bin/java").path,
        "E2E_AGENT_JAR" to backend.layout.buildDirectory.file("quarkus-app/quarkus-run.jar").get().asFile.path,
        "E2E_APP_VERSION" to project.version.toString()
    ))
    // never up to date: it tests the running system, not files
    outputs.upToDateWhen { false }
}

// the built frontend is served by the backend from META-INF/resources
tasks.jar {
    dependsOn(frontendBuild)
    from("dist") {
        into("META-INF/resources")
    }
}
