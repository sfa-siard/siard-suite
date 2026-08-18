package ch.admin.bar.siardsuite;

import ch.admin.bar.siardsuite.ui.RootStage;
import ch.enterag.utils.ProgramInfo;
import io.github.palexdev.materialfx.theming.JavaFXThemes;
import io.github.palexdev.materialfx.theming.MaterialFXStylesheets;
import io.github.palexdev.materialfx.theming.UserAgentBuilder;
import javafx.application.Application;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

@Slf4j
public class SiardApplication extends Application {

    private static SiardApplication instance;

    public SiardApplication() {
        instance = this;
    }

    public static SiardApplication getInstance() {
        return instance;
    }

    @Override
    public void start(Stage stage) throws IOException {
        log.info("Application started");

        // trial to fix the bad font rendering issue from javafx
        System.setProperty("prism.lcdtext", "false");
        System.setProperty("prism.text", "t2k");

        // MaterialFX 11.17.0+ dropped per-control user-agent stylesheets in favor of this global theming API
        UserAgentBuilder.builder()
                        .themes(JavaFXThemes.MODENA)
                        .themes(MaterialFXStylesheets.forAssemble(true))
                        .setDeploy(true)
                        .setResolveAssets(true)
                        .build()
                        .setGlobal();
        Properties props = new Properties();
        props.load(SiardApplication.class.getResourceAsStream("version.properties"));
        String version = (String) props.get("version");

        // needed for the api *eyes rolling*
        ProgramInfo.getProgramInfo(
                "SIARD Suite", getClass().getPackage()
                                         .getImplementationVersion(),
                "SIARD Suite", version,
                "Program to download, view, upload database content and database edit meta data in a .siard file",
                "Swiss Federal Archives, Berne, Switzerland, 2007-2023");

        if (SystemTray.isSupported()) {
            URL url = SiardApplication.class.getResource("icons/archive_red.png");
            Image image = Toolkit.getDefaultToolkit()
                                 .getImage(url);
            final TrayIcon trayIcon = new TrayIcon(image);
            final SystemTray tray = SystemTray.getSystemTray();
            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                System.out.println("TrayIcon could not be added.");
            }
        }

        new RootStage();
    }

    public static void main(String[] args) {
        launch();
    }

}
