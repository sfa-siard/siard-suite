import com.github.gradle.node.npm.task.NpmTask

plugins {
    base
    alias(libs.plugins.node.gradle)
}

description = "SIARD Suite Web Frontend Shared"

// Source-only npm package (design R6): nothing is built here, the apps compile it with their own
// code. Like the apps, it uses the Node download and the single `npm ci` of :siard-web (design R5).
val workspaceRoot = project(":siard-web")
node {
    version.set(workspaceRoot.file(".nvmrc").readText().trim())
    download.set(true)
    workDir.set(workspaceRoot.layout.projectDirectory.dir(".gradle/nodejs"))
    npmWorkDir.set(workspaceRoot.layout.projectDirectory.dir(".gradle/npm"))
}

tasks.npmInstall { enabled = false }
tasks.nodeSetup { enabled = false }
tasks.npmSetup { enabled = false }
val workspaceInstall = ":siard-web:npmInstall"

// ../backend-core/openapi.yaml: the shared API description src/api/core-schema.d.ts is generated
// from by the typecheck script (archive design D10), so a changed description re-runs the checks
val sources = listOf("src", "package.json", "tsconfig.json", "vitest.config.ts", "../tsconfig.base.json", "../package-lock.json", "../backend-core/openapi.yaml")

val sharedTypecheck = tasks.register<NpmTask>("sharedTypecheck") {
    description = "Type-checks the shared frontend package with tsc."
    dependsOn(workspaceInstall)
    args.set(listOf("run", "typecheck"))
    inputs.files(sources)
    outputs.upToDateWhen { true }
}

val sharedLint = tasks.register<NpmTask>("sharedLint") {
    description = "Runs ESLint (including the no-app-imports rule, design R8) and the Prettier check."
    dependsOn(workspaceInstall)
    args.set(listOf("run", "lint"))
    inputs.files(sources + listOf("../eslint.config.js", "../.prettierrc.json", ".prettierignore"))
    outputs.upToDateWhen { true }
}

val sharedTest = tasks.register<NpmTask>("sharedTest") {
    description = "Runs the Vitest tests of the shared frontend package."
    dependsOn(workspaceInstall)
    args.set(listOf("run", "test"))
    inputs.files(sources)
    outputs.upToDateWhen { true }
}

tasks.named("check") {
    dependsOn(sharedTypecheck, sharedLint, sharedTest)
}
