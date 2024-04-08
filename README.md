# SIARD Suite 2.2.x

The SIARD (Software-Independent Archival of Relational Databases) standard defines a format for the long-term archival
of relational database contents. To facilitate this process, the "Siard Suite" application offers a user-friendly
graphical interface for archiving, restoring, searching, and exporting SIARD archives. With its intuitive design Siard
Suite makes it easy to preserve valuable data from relational databases in a way that ensures its longevity and
accessibility.

## Development

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

### Build application artifacts

Run tests and build the package

```shell
./gradlew build
```

the build task creates a distribution in `build/distributions` that contains an archive with the necessary executable
scripts.

You can also create platform specific runtimes for the application:

```shell
./gradlew jpackageImage
```

_NOTE:_ You can only create images for the OS you are running the task on.

The image is then available at `./build/jpackage/siard-suite`

To create a platform specific installer use:

```shell
./gradlew jpackage
```

Hint: If you are working on ubuntu building the rpm installer may fail - in this case install the necessary packages on
your system:

```shell
sudo apt install alien
```

## Versioning, tags and releases

⚠️ Please check the [release guide](release-guide.md) before creating a new version and pushing it to main! ⚠️ 

Versions and tags are managed with the Axion Release Plugin for Gradle (https://github.com/allegro/axion-release-plugin)

Short overview:

```shell
./gradlew currentVersion # show the current version

./gradlew release        # creates a new release adds a tag and pushes it to remote.
```

Run the release task to create a new patch version and push it to remote. The GitHub Actions will create the
deliverables.

__NOTE: the official GitHub Release has to be created manually by BUAR!__

While the versioning scheme looks like it's semver it is actually not! The major and minor version represent the
supported SIARD Format version (currently 2.2)

## Documentation

The documentation is made with  [Asciidoc]( https://asciidoctor.org/).

Create the documentation PDF:

```shell
./gradlew asciidoctorPdf
```

As part of the build process, the documentation is rendered and then bundled with the application artefact.


