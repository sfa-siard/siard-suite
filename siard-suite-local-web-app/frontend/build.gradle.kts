import com.github.gradle.node.npm.task.NpmTask

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

val sources = listOf("src", "index.html", "package.json", "package-lock.json", "tsconfig.json", "vite.config.ts")

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

// the built frontend is served by the backend from META-INF/resources
tasks.jar {
    dependsOn(frontendBuild)
    from("dist") {
        into("META-INF/resources")
    }
}
