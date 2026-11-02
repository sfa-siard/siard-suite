plugins {
    java
    alias(libs.plugins.node.gradle)
}

description = "SIARD Suite Web Companion Frontend"

node {
    version.set("24.19.0")
    download.set(true)
}

val frontendBuild = tasks.register("frontendBuild", com.github.gradle.node.npm.task.NpmTask::class) {
    dependsOn(tasks.named("npmInstall"))
    args.set(listOf("run", "build"))
    inputs.files("package.json", "package-lock.json", "vite.config.ts", "tsconfig.json", "index.html")
    inputs.dir("src")
    outputs.dir("dist")
}

val frontendTest = tasks.register("frontendTest", com.github.gradle.node.npm.task.NpmTask::class) {
    dependsOn(tasks.named("npmInstall"))
    args.set(listOf("test"))
    inputs.files("package.json", "package-lock.json")
    inputs.dir("src")
}

tasks.named("check") {
    dependsOn(frontendTest)
}

tasks.jar {
    dependsOn(frontendBuild)
    from("dist") {
        into("META-INF/resources")
    }
}
