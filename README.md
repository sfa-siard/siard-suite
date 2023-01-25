# siard-suite

## for developers

Run the application from the command line:

```shell
./gradlew run
```

Run tests and build the package

```shell
./gradlew build
```

the build task creates a distribution in `build/distributions` that contains an archive with the necessary executable scripts.

You can also create platform specific images that include the necessary jre and provides a binary to start the application:

```shell
./gradlew jpackageImage
```

The image is available at `./build/jpackage/siard-suite`

To create a platform specific installer use:

```shell
./gradlew jpackage
```

Hint: If you are working on ubuntu building the rpm installer may fail - in this case install the necessary packages on your system:

```shell
sudo apt install alien
```

## versioning, tags and releases

Versions and tags are managed with the Axion Release Plugin for Gradle (https://github.com/allegro/axion-release-plugin)

Short overview:

```shell
./gradlew currentVersion # show the current version

./gradlew release        # creates a new release adds a tag and pushes it to remote.
```

Run the release task to create a new patch version and push it to remote. The Github Actions will create the deliverables.

__NOTE: the official github release has be created manually by BUAR!__

While the versioning scheme looks like it's semver it is actuall not! The major and minor version represent the supported SIARD Format version (currently 2.2)

## documentation

Siard-Suite documentation is made with  [Asciidoc]( https://asciidoctor.org/).

To build the docs locally use: 

```shell
./gradlew asciidoctorPdf
```

To provide the documentation with the app, the rendering of the documentation is included in the build step of the app.

