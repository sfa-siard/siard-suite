package ch.admin.bar.siardsuite.service;

import ch.admin.bar.siardsuite.SiardApplication;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.util.ResourcesResolver;
import com.sun.deploy.uitoolkit.impl.fx.HostServicesFactory;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

@Slf4j
public class FilesService {

    @SneakyThrows
    public void openUserManual() {
        val userManualFile = getOrCreateTemporaryFile("user-manual.pdf");
        InputStream is = ResourcesResolver.loadResource("ch/admin/bar/siardsuite/doc/" + I18n.getLocale().toLanguageTag() + "/user-manual.pdf");

        Files.copy(is, userManualFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        // separate non-FX thread, otherwise JavaFX-Thread is blocked and the file never opens
        new Thread(() -> open(userManualFile)).start();
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
