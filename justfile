[doc('List all available recipes')]
default:
    just --list

alias ar := app-run
[doc('Run the SIARD Suite Desktop App')]
[group('SIARD Suite Desktop Application')]
app-run:
    ./gradlew :siard-suite-app:run


alias cb := cli-build
[doc("Build the SIARD Suite CLI (dev distribution, requires Java 21)")]
[group('SIARD Suite CLI')]
cli-build:
    ./gradlew :siard-cmd:installDist


alias ci := cli-image
[doc("Build the runtime-bundled CLI image zip (no Java needed to run)")]
[group('SIARD Suite CLI')]
cli-image:
    ./gradlew :siard-cmd:runtimeZip


alias cp := cli-package
[doc("Build the native CLI installer for the current OS")]
[group('SIARD Suite CLI')]
cli-package:
    ./gradlew :siard-cmd:jpackage


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


alias um := user-manual-pdf
[doc("Create User Manual PDF")]
[group('Documentation')]
user-manual-pdf:
    ./gradlew :siard-suite-app:asciidoctorPdf
