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
