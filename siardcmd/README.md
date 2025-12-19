# SiardCmd - Command-line Interface to download and upload relational databases

This package contains the SIARD command-line interface to download and upload relational databases in the SIARD (Software-Independent Archival of Relational Databases) Format 2.2.

## Development

### Prerequisites
- JDK 8
- Docker (Used for integration tests)

### Build application artifacts

Run tests and build the package

```shell
./gradlew build
```

### Integration tests

Runs all integration tests (Note: [TestContainers](https://testcontainers.com/) is used, so Docker is required)

```shell
./gradlew integrationTest
```

### Deliverables


```shell
./gradlew packDeliverables
```

Runs tests, build the artifacts, packages them, and includes all additional required files for execution in a CLI into a single zip file under 'libs/deliverables'.


## Versioning, tags and releases

Versions and tags are managed with the Axion Release Plugin for Gradle (https://github.com/allegro/axion-release-plugin)

Short overview:

```shell
./gradlew currentVersion # show the current version

./gradlew release        # creates a new release adds a tag and pushes it to remote.
```

Run the release task to create a new patch version and push it to remote. The GitHub Actions will create the
deliverables.

