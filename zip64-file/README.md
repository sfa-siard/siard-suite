# Zip64File - SIARD 2.2 ZIP 64 handler
This package contains a ZIP 64 handler. Its main purpose is reading and writing ZIP files which conform to the 64 bit extension of the ZIP standard. It also includes a command-line tool for reading and writing ZIP files in ZIP 64 format.

## Getting started (for developers)
For building the binaries, Java JDK 17 must be installed. 

### Run all tests
```shell
./gradlew check
```

### Build the project
```shell
./gradlew clean build
```

### Versioning, tags, and releases
Versions and tags are managed with the [Axion Release Plugin](https://github.com/allegro/axion-release-plugin) for Gradle.

Short overview:
```shell
./gradlew currentVersion  # Shows the current version

./gradlew release         # Creates a new release, adds a tag, and pushes it to remote
```

## Documentation
- [User Manual](https://github.com/sfa-siard/siard-suite/blob/main/docs/user-manual/en/user-manual.adoc)
- [Software Architecture Document](https://github.com/sfa-siard/siard-suite/blob/main/docs/sad/sad.adoc)

## Declaration
Contributions to the codebase have been made with the support of Windsurf. Windsurf is AI-powered code completion tool, that is trained exclusively on natural language and source code data with [permissive licenses](https://windsurf.com/blog/copilot-trains-on-gpl-codeium-does-not). 

