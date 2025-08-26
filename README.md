# JdbcOracle - SIARD 2.2 Oracle JDBC Wrapper

This package contains the JDBC Wrapper for Oracle DBMS for SIARD 2.2.

## Getting started (for developers)

For building the binaries, Java JDK (17 or higher) must be installed.

A running Oracle DB instance is needed before running the tests:
```shell
docker compose up -d
```

### Build application artifacts

Run tests and build the package:
```shell
./gradlew build
```

## Versioning, tags and releases

Versions and tags are managed with the [Axion Release Plugin](https://github.com/allegro/axion-release-plugin) for Gradle.

Short overview:
```shell
./gradlew currentVersion # Show the current version

./gradlew release        # Creates a new release, adds a tag, and pushes it to remote
```

## Documentation

[./doc/manual/user/index.html](./doc/manual/user/index.html) contains the manual for using the binaries.
[./doc/manual/developer/index.html](./doc/manual/user/index.html) is the manual for developers wishing
build the binaries or work on the code.

More information about the build process can be found in
[./doc/manual/developer/build.html](./doc/manual/developer/build.html)