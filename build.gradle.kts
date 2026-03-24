plugins {
    alias(libs.plugins.axion.release)
}

group = "ch.admin.bar"
version = scmVersion.version

subprojects {
    apply(plugin = "java")
    
    group = rootProject.group
    version = rootProject.version
    
    repositories {
        mavenCentral()
    }
    
    configure<JavaPluginExtension> {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }
    
    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
}
