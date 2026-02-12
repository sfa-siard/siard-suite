plugins {
    id("pl.allegro.tech.build.axion-release") version "1.14.3"
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
