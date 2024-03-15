package ch.admin.bar.siardsuite.service;

import ch.admin.bar.siardsuite.SiardApplication;
import ch.admin.bar.siardsuite.util.OS;
import ch.enterag.utils.io.SpecialFolder;
import lombok.extern.slf4j.Slf4j;
import mslinks.ShellLink;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@Slf4j
public class InstallationService {

    public void installToDesktop() throws IOException {
        log.info("Install SIARD-Suite to desktop");

        String java = OS.IS_WINDOWS ? "javaw.exe" : "java";

        String javaHome = System.getProperty("java.home"); // TODO: what happens if java home is not set?
        File javaExecutable = new File(javaHome + File.separator + "bin" + File.separator + java);
        String applicationFolder = new File(SiardApplication.class.getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .getPath()).getParent();
        log.info("Got an application folder: {}", applicationFolder);

        Properties props = new Properties();
        props.load(SiardApplication.class.getResourceAsStream("version.properties"));
        String version = (String) props.get("version");
        log.info("Got an application version: {}", version);
        List<String> arguments = Arrays.asList(
                "-Xmx1024m",
                "-Dsun.awt.disablegrab=true",
                "-jar",
                applicationFolder + File.separator + "siard-suite-" + version + ".jar"
        );

        InputStream resourceAsStream = SiardApplication.class.getResourceAsStream("icons/archive_red.ico");
        Files.copy(resourceAsStream,
                Paths.get(applicationFolder + File.separator + "archive_red.ico"),
                StandardCopyOption.REPLACE_EXISTING);
        String description = "SIARD Suite: view and modify archived data from relational databases";

        ShellLink shellLink = ShellLink.createLink(javaExecutable.getAbsolutePath());
        shellLink.setCMDArgs(String.join(" ",
                arguments)); // TODO: Hartwig used ~20 lines to format the arguments. Why?
        shellLink.setWorkingDir(applicationFolder);
        shellLink.setIconLocation(applicationFolder + File.separator + "archive_red.ico");
        shellLink.setName(description); // TODO: why set the name to description?
        shellLink.saveTo(SpecialFolder.getDesktopFolder() + File.separator + "SIARD Suite-" + version + ".lnk");

        log.info("Installation successful");
    }
}
