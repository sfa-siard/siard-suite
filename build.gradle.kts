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
    
    // Standardize testcontainers version across all modules
    configurations.all {
        resolutionStrategy {
            force("org.testcontainers:testcontainers:1.20.4")
            force("org.testcontainers:mssqlserver:1.20.4")
            force("org.testcontainers:postgresql:1.20.4")
            force("org.testcontainers:mysql:1.20.4")
            force("org.testcontainers:mariadb:1.20.4")
            force("org.testcontainers:oracle-xe:1.20.4")
            force("org.testcontainers:db2:1.20.4")
            force("org.testcontainers:junit-jupiter:1.20.4")
        }
    }
    
    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
}
