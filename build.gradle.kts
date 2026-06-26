plugins {
    alias(libs.plugins.axion.release)
    alias(libs.plugins.dependency.analysis)
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

    extra["xercesSaxParserFactory"] = "-Djavax.xml.parsers.SAXParserFactory=com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl"

    configure<JavaPluginExtension> {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }
    
    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
    
    // Apply dependency-analysis to all modules except sql-parser (ANTLR issues)
    if (project.name != "sql-parser") {
        apply(plugin = "com.autonomousapps.dependency-analysis")
    }
}
