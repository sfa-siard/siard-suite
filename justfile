[doc('List all available recipes')]
default:
    just --list

alias ar := app-run
[doc('Run the SIARD Suite Desktop App')]
[group('SIARD Suite Desktop Application')]
app-run:
    ./gradlew :siard-suite-app:run


alias cb := cli-build
[doc("Build the SIARD Suite CLI")]
[group('SIARD Suite CLI')]
cli-build:
    ./gradlew :siard-cmd:installDist


alias ta := test-all
[doc("Run all tests (go get a coffee!)")]
[group('Testing')]
test-all:
    ./gradlew test


alias t := test
[doc("Run tests for a module, e.g. just test siard-api")]
[group('Testing')]
test module:
    ./gradlew :{{module}}:test


alias it := integration-test
[doc("Run integration tests for a database, e.g. just integration-test postgres (requires Docker)")]
[group('Testing')]
integration-test db:
    ./gradlew :siard-cmd:integrationTest{{capitalize(db)}}


alias cv := current-version
[doc("Check current version")]
[group('Releasing')]
current-version:
    ./gradlew currentVersion


[doc("Create a release tag and push to remote (see release guide)")]
[group('Releasing')]
release:
    ./gradlew release
