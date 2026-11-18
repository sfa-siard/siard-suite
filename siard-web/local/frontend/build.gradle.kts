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

// The npm workspace root is siard-web/ (design R5): Node, the lockfile and the single `npm ci` live in
// the :siard-web project. This project reuses its Node download and runs its scripts after it.
val workspaceRoot = project(":siard-web")
node {
    version.set(workspaceRoot.file(".nvmrc").readText().trim())
    download.set(true)
    workDir.set(workspaceRoot.layout.projectDirectory.dir(".gradle/nodejs"))
    npmWorkDir.set(workspaceRoot.layout.projectDirectory.dir(".gradle/npm"))
}

// only the workspace root installs; the Node download is done once there as well (its npmInstall
// depends on its nodeSetup, and every npm task here depends on that npmInstall)
tasks.npmInstall { enabled = false }
tasks.nodeSetup { enabled = false }
tasks.npmSetup { enabled = false }
val workspaceInstall = ":siard-web:npmInstall"

// ../openapi.yaml: the committed API description the TypeScript types are generated from; the
// workspace's lockfile, shared tool configs and the shared package (compiled as source with this
// app, design R6) are inputs as well
val sources = listOf("src", "index.html", "package.json", "../../package-lock.json", "tsconfig.json", "tsconfig.app.json", "tsconfig.node.json", "../../tsconfig.base.json", "vite.config.ts", "playwright.config.ts", "e2e", "../openapi.yaml", "../../frontend-shared/src", "../../frontend-shared/package.json")

val frontendBuild = tasks.register<NpmTask>("frontendBuild") {
    description = "Builds the frontend into dist/."
    dependsOn(workspaceInstall)
    args.set(listOf("run", "build"))
    inputs.files(sources)
    outputs.dir("dist")
}

val frontendTypecheck = tasks.register<NpmTask>("frontendTypecheck") {
    description = "Type-checks the frontend with tsc."
    dependsOn(workspaceInstall)
    args.set(listOf("run", "typecheck"))
    inputs.files(sources)
    outputs.upToDateWhen { true }
}

val frontendLint = tasks.register<NpmTask>("frontendLint") {
    description = "Runs ESLint and the Prettier check."
    dependsOn(workspaceInstall)
    args.set(listOf("run", "lint"))
    inputs.files(sources + listOf("../../eslint.config.js", "../../.prettierrc.json", ".prettierignore"))
    outputs.upToDateWhen { true }
}

val frontendTest = tasks.register<NpmTask>("frontendTest") {
    description = "Runs the Vitest tests."
    dependsOn(workspaceInstall)
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
    dependsOn(workspaceInstall)
    command.set("playwright")
    args.set(listOf("install", "chromium"))
}

tasks.register<NpmTask>("e2eTest") {
    description = "Runs the Playwright smoke test against the packaged agent."
    group = "verification"
    dependsOn(playwrightInstall, ":siard-web:local:backend:quarkusBuild")
    args.set(listOf("run", "e2e"))
    val backend = project(":siard-web:local:backend")
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
