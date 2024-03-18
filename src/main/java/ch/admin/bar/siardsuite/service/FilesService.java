package ch.admin.bar.siardsuite.service;

import ch.admin.bar.siardsuite.SiardApplication;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.util.OS;
import ch.admin.bar.siardsuite.util.ResourcesResolver;
import com.sun.deploy.uitoolkit.impl.fx.HostServicesFactory;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * Service for file-related operations.
 */
@Slf4j
public class FilesService {

    /**
     * Opens the user manual.
     *
     * @throws IOException If an I/O error occurs.
     */
    public void openUserManual() throws IOException {
        val userManualFile = getOrCreateTemporaryFile("user-manual.pdf");
        InputStream is = ResourcesResolver.loadResource("ch/admin/bar/siardsuite/doc/" + I18n.getLocale().toLanguageTag() + "/user-manual.pdf");

        Files.copy(is, userManualFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        // separate non-FX thread, otherwise JavaFX-Thread is blocked and the file never opens
        new Thread(() -> open(userManualFile)).start();
    }

    public void openInFileBrowser(final File fileOrDir) throws IOException {
        val file = fileOrDir.isDirectory() ? fileOrDir.getParentFile() : fileOrDir;

        if (OS.UNSUPPORTED) throw new UnsupportedOperationException("Open file browser is not supported on your OS");
        if (OS.IS_WINDOWS) Runtime.getRuntime().exec("explorer /select, " + file.getAbsolutePath());
        if (OS.IS_UNIX)
            Runtime.getRuntime().exec("xdg-open " + file.getParentFile().getAbsolutePath()); // for linux: pass a directory to show, not the file itself
        if (OS.IS_MAC) Runtime.getRuntime().exec("open -R " + file.getAbsolutePath());
    }

    private void open(final File file) {
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().open(file);
                return;
            } catch (IOException e) {
                log.warn(String.format("Failed to open file %s with associated application", file), e);
            }
        }

        val hostServices = HostServicesFactory.getInstance(new SiardApplication());
        hostServices.showDocument(file.toURI().toString());
    }

    /**
     * Gets or creates a temporary file with the specified name.
     *
     * @param filename The name of the temporary file.
     * @return The temporary file.
     * @throws IOException If an I/O error occurs.
     */
    private File getOrCreateTemporaryFile(String filename) {
        val tempFile = new File(getSystemDirectoryForTemporaryFiles(), filename);
        tempFile.deleteOnExit();

        return tempFile;
    }

    private File getSystemDirectoryForTemporaryFiles() {
        try {
            return new File(System.getProperty("java.io.tmpdir"));
        } catch (Exception ex) {
            log.error("Failed to read directory for temporary files", ex);
            return new File("./");
        }
    }
}
