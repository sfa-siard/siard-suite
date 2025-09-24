# SIARD Suite 2.2
The SIARD (Software-Independent Archival of Relational Databases) standard defines a format for the long-term archival of relational database contents. To facilitate this process, the "Siard Suite" application offers a user-friendly graphical interface for archiving, restoring, searching, and exporting SIARD archives. With its intuitive design Siard Suite makes it easy to preserve valuable data from relational databases in a way that ensures its longevity and accessibility.

## Getting started (for developers)
### Prerequisites

Java 17 with Java FX - you can get it from here: https://www.azul.com/downloads/?version=java-17-lts&package=jdk-fx#zulu

For [asdf](https://asdf-vm.com/) users:
```shell
asdf install
```

### CLI
Run the application from the command line:
```shell
./gradlew run
```

### Build the project
```shell
./gradlew clean build
```

The build task creates a distribution in `build/distributions` that contains an archive with the necessary executable scripts.

You can also create platform-specific runtimes for the application:
```shell
./gradlew jpackageImage
```

_NOTE:_ You can only create images for the OS you are running the task on. The image is then available at `./build/jpackage/siard-suite`

To create a platform-specific installer use:
```shell
./gradlew jpackage
```

Hint: If you are working on ubuntu building the rpm installer may fail - in this case install the necessary packages on your system:
```shell
sudo apt install alien
```

### Versioning, tags, and releases

⚠️ Please check the [release guide](release-guide.md) before creating a new version and pushing it to main! ⚠️ 

Versions and tags are managed with the [Axion Release Plugin](https://github.com/allegro/axion-release-plugin) for Gradle. While the versioning scheme looks like semver it is actually not - the major and minor versions represent the supported SIARD Format version (currently 2.2)

Short overview:
```shell
./gradlew currentVersion  # Shows the current version

./gradlew release         # Creates a new release, adds a tag, and pushes it to remote
```

Once a tag is pushed to remote, GitHub Actions will create and upload deliverables specified in [deliverables.yml](.github/workflows/deliverables.yml).

__NOTE: the official GitHub Release has to be created manually by BAR!__

## Documentation
The documentation is made with  [Asciidoc]( https://asciidoctor.org/). As part of the build process, the documentation is rendered and then bundled with the application artefact.
To create the documentation PDF:
```shell
./gradlew asciidoctorPdf
```

- [User Manual](docs/user-manual/en/user-manual.adoc)
- [Software Architecture Document](docs/sad/sad.adoc)

## Declaration
Contributions to the codebase have been made with the support of Windsurf. Windsurf is AI-powered code completion tool, that is trained exclusively on natural language and source code data with [permissive licenses](https://windsurf.com/blog/copilot-trains-on-gpl-codeium-does-not). 




