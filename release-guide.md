# SIARD Suite Release Testing Guide

## Motivation

The development of SIARD Suite is financed by the Swiss Federal Archives (SFA), which are one of the main stakeholders of the project. The SFA are also one of the main users of the application. SFA staff workstations have the following restrictions:

- No Java is installed on the workstations.
- Inability to download and run any form of executable binary or script (e.g. `SIARD-suite.bat`, image zips).
- Only installers packaged by BIT (the federal IT service provider) are deployed to workstations.
- BIT deployment policy forbids JavaFX in the `siard` CLI bundle, which is why the CLI is distributed as its own standalone product — with its own native installers and runtime-bundled image zips — rather than inside the SIARD Suite installer.

That's why it is recommended to perform the following steps, to make sure that the packaged installers and images really work for SFA staff and for users of different distributions.

## Test script

Run a build and create native installers and packaged distributions for both the SIARD Suite application and the `siard` CLI:

```bash
./gradlew clean jpackage
```

On Linux this produces the default installer type per module (`deb`). A second installer type is produced with:

```bash
./gradlew :siard-cmd:jpackage -PinstallerType=rpm
```

Start some databases:

```bash
cd docker
docker compose up -d --build
```

## Smoke tests for the packaged SIARD Suite application

Switch to the installed or unpacked application (the jpackage app image mirrors what the native installers deploy):

```bash
cd siard-suite-app/build/jpackage/SIARD-Suite/
```

Run the application:

```bash
./SIARD-Suite(.exe)
```

In the application, please carry out the following steps to archive data sources:

* Configure the data source for archiving: Select "Archive" and "Use new connection".
* Fill out metadata.
* Save the archive and select "View archive" at the end of the wizard.

### Microsoft SQL Server
  * Connection URL: jdbc:sqlserver://localhost:1433;databaseName=siard
  * Username: sa
  * Password: Yukon900 (test password from `docker/docker-compose.yaml` — local test environment only, do not reuse elsewhere)

### Oracle
   * Connection URL: jdbc:oracle:thin:@localhost:1521/siard
   * Username: siard
   * Password: siard

### MS Access
  * MS Access files: 
    * `/home/<username>/<your-directory>/siard-suite/docker/msaccess/Northwind.accdb`
    * `/home/<username>/<your-directory>/siard-suite/docker/msaccess/nations.accdb`


## Smoke tests for the `siard` CLI

The CLI ships as native installers (`.msi`/`.exe`, `.deb`/`.rpm`, `.dmg`) and runtime-bundled image zips per OS. Unpack the image zip or install the native installer, then run the launcher **from the installed location** (launchers are not added to `PATH`):

- Linux `.deb`/`.rpm`: `/opt/siard-cli/bin/siard`
- Windows `.msi`/`.exe` (system-wide install): `siard-cli\siard.exe` under the chosen/program-files install dir
- macOS `.dmg`: inside `siard.app`; the image zip (`bin/siard`) is the ergonomic path for terminal users
- Image zip: `<unpacked>/bin/siard`

Then verify:

```bash
bin/siard --version          # prints program name, app version and SIARD format version; exit 0
bin/siard archive --help     # usage help on stdout; exit 0
bin/siard frobnicate         # error naming the unknown subcommand on stderr; exit 2
bin/java --list-modules      # no module starting with javafx. (BIT requirement)
```

Optionally run a real archive against one of the docker databases:

```bash
bin/siard archive --jdbc-url=jdbc:postgresql://localhost:5432/siard --user=siard --password=siard --siard-file=/tmp/test.siard --overwrite   # exit 0
```

On Windows, also verify that running the `.msi`/`.exe` without administrator privileges reports that elevated privileges are required and installs no files, and that an installer built for a different OS is rejected.

If everything works, push changes to the main branch and run the release task according to the readme.


## Considerations

Testing the release manually is tedious and time-consuming. Be aware that you should only test two things manually:
1. Does the application start?
2. Is it possible to archive a simple database and open the archive?

The above tests are only valuable if they are actually run at least once. We should find ways to automate this step and add it to the build pipeline.

Testing special cases and complex databases should be done in the SiardCmd repository using the integration test framework!

