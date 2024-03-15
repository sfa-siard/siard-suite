package ch.admin.bar.siardsuite.service;

import ch.admin.bar.siardsuite.SiardApplication;
import ch.admin.bar.siardsuite.util.OS;
import ch.admin.bar.siardsuite.util.ResourcesResolver;
import ch.enterag.utils.io.SpecialFolder;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import mslinks.ShellLink;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Optional;
import java.util.Properties;

@Slf4j
public class InstallationService {

    public void installToDesktop() throws IOException {
        log.info("Install SIARD-Suite to desktop");

        File javaExecutable = findJavaExecutable();

        val applicationFolder = findAppFile().getParent();

        val version = findAppVersion();

        val arguments = Arrays.asList(
                "-Xmx1024m",
                "-Dsun.awt.disablegrab=true",
                "-jar",
                applicationFolder + File.separator + "siard-suite-" + version + ".jar"
        );

        try (InputStream inputStream = ResourcesResolver.loadResource("ch/admin/bar/siardsuite/icons/archive_red.ico")) {
            Files.copy(inputStream,
                    Paths.get(applicationFolder + File.separator + "archive_red.ico"),
                    StandardCopyOption.REPLACE_EXISTING);
        }

        val shellLink = ShellLink.createLink(javaExecutable.getAbsolutePath());
        shellLink.setCMDArgs(String.join(" ",
                arguments));
        shellLink.setWorkingDir(applicationFolder);
        shellLink.setIconLocation(applicationFolder + File.separator + "archive_red.ico");
        shellLink.setName("SIARD Suite: view and modify archived data from relational databases");
        shellLink.saveTo(SpecialFolder.getDesktopFolder() + File.separator + "SIARD Suite-" + version + ".lnk");

        log.info("Installation successful");
    }

    private static File findAppFile() {
        val appFile = new File(SiardApplication.class.getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .getPath());

        log.info("Location of application: {}", appFile.getAbsolutePath());

        return appFile;
    }

    private static String findAppVersion() throws IOException {
        val props = new Properties();
        props.load(ResourcesLoader.loadResource("ch/admin/bar/siardsuite/version.properties"));

        val version = Optional.ofNullable(props.get("version"))
                .map(Object::toString)
                .orElseThrow(() -> new IllegalStateException("Failed to read version from version.properties"));

        log.info("Got an application version: {}", version);

        return version;
    }

    private static File findJavaExecutable() {
        val java = OS.IS_WINDOWS ? "javaw.exe" : "java";

        return Optional.ofNullable(System.getProperty("java.home"))
                .map(javaHome -> {
                    val javaExecutable = new File(javaHome + File.separator + "bin" + File.separator + java);
                    log.info("Java executable found at {}", javaExecutable.getAbsolutePath());
                    return javaExecutable;
                })
                .orElseThrow(() -> new IllegalStateException("Can not find java executable"));
    }
}
