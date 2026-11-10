plugins {
    alias(libs.plugins.axion.release)
    alias(libs.plugins.dependency.analysis)
    alias(libs.plugins.sonarqube)
}

sonarqube {
    properties {
        property("sonar.organization", "siard-suite")
        property("sonar.projectKey", "siard-suite_siard-suite")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.exclusions", "**/generated/**")
    }
}

group = "ch.admin.bar"
version = scmVersion.version

// lombok is auto-added to compileOnly/annotationProcessor by the io.freefair.lombok
// plugin, so any advice about it is a false positive
dependencyAnalysis {
    issues {
        all {
            onAny { exclude("org.projectlombok:lombok") }
        }
    }
}

subprojects {
    group = rootProject.group
    version = rootProject.version

    repositories {
        mavenCentral()
    }

    extra["xercesSaxParserFactory"] = "-Djavax.xml.parsers.SAXParserFactory=com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl"

    plugins.withType<JavaPlugin> {
        configure<JavaPluginExtension> {
            toolchain {
                languageVersion.set(JavaLanguageVersion.of(21))
            }
        }

        // sonar analyzes compiled classes; make sure they exist before it runs
        val compileTasks = listOf(tasks.named("classes"), tasks.named("testClasses"))
        rootProject.tasks.named("sonar") {
            dependsOn(compileTasks)
        }
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
    
    // Apply dependency-analysis to all modules except sql-parser (ANTLR issues)
    if (project.name != "sql-parser") {
        pluginManager.apply("com.autonomousapps.dependency-analysis")
    }
}
