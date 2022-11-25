package ch.admin.bar.siardsuite.util;

import ch.admin.bar.siardsuite.SiardApplication;
import com.sun.deploy.uitoolkit.impl.fx.HostServicesFactory;
import com.sun.javafx.application.HostServicesDelegate;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {

    // works everywhere, IDEA, unit test and JAR file.
    public static InputStream getFileFromResource(String fileName) {

        ClassLoader classLoader = FileUtils.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(fileName);

        if (inputStream == null) {
            throw new IllegalArgumentException("file not found! " + fileName);
        } else {
            return inputStream;
        }
    }


    public static Runnable getOpenFile(java.io.File p) {
        Runnable runnable = new Runnable() {
            public void run() {
                if (Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().open(p);
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                        openInBrowser();
                    }
                } else {
                    openInBrowser();
                }
            }

            private void openInBrowser() {
                HostServicesDelegate hostServices = HostServicesFactory.getInstance(new SiardApplication());
                hostServices.showDocument(p.toURI().toString());
            }
        };
        return runnable;
    }
}
