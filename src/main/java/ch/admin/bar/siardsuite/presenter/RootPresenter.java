package ch.admin.bar.siardsuite.presenter;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.SiardApplication;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.model.View;
import ch.admin.bar.siardsuite.util.FileUtils;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.util.OS;
import ch.admin.bar.siardsuite.util.SiardEvent;
import ch.admin.bar.siardsuite.view.RootStage;
import ch.enterag.utils.io.SpecialFolder;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import mslinks.ShellLink;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

public class RootPresenter extends Presenter {

    @FXML
    public Menu menuItemLanguage;
    @FXML
    public MenuItem menuItemOptions, menuItemInfo, menuItemInstall, menuItemClose;

    @FXML
    public HBox windowHeader;
    @FXML
    public MenuBar menuBar;
    @FXML
    public Menu menuContainer;
    @FXML
    public MFXButton helpButton;

    public void init(Controller controller, Model model, RootStage stage) {
        this.model = model;
        this.controller = controller;
        this.stage = stage;

        allowStageRepositioning(windowHeader);

        I18n.bind(stage.titleProperty(), "window.title");

        initMenu();
    }

    private void initMenu() {
        this.menuBar.setOnMouseClicked(event -> this.menuContainer.show());

        I18n.bind(menuItemLanguage.textProperty(), "menu.item.language");
        I18n.bind(menuItemOptions.textProperty(), "menu.item.options");
        I18n.bind(menuItemInfo.textProperty(), "menu.item.info");
        I18n.bind(menuItemInstall.textProperty(), "menu.item.install");
        //menuItemInstall.setVisible(OS.IS_WINDOWS);

        I18n.bind(menuItemClose.textProperty(), "menu.item.close");

        // Language
        ToggleGroup items = new ToggleGroup();
        I18n.getSupportedLocales().forEach((locale) -> {
            RadioMenuItem item = new RadioMenuItem();
            item.setId(locale.toString());
            I18n.bind(item.textProperty(), locale.toString());
            this.menuItemLanguage.getItems().add(item);
            item.setOnAction(event -> {
                RadioMenuItem cmi = (RadioMenuItem) event.getSource();
                I18n.setLocale(new Locale(cmi.getId()));
                stage.fireEvent(new SiardEvent(SiardEvent.UPDATE_LANGUAGE_EVENT));
            });
            item.setToggleGroup(items);
        });

        this.menuItemClose.setOnAction(event -> this.stage.close());
        this.menuItemInfo.setOnAction(event -> this.stage.openDialog(View.INFO_DIALOG));
        this.menuItemOptions.setOnAction(event -> this.stage.openDialog(View.OPTION_DIALOG));
        this.menuItemInstall.setOnAction(this::installToDesktop);
        this.helpButton.setOnAction(event -> {
            try {
                File p = new File("user-manual.pdf");
                InputStream is = FileUtils.getFileFromResource(
                        "ch/admin/bar/siardsuite/doc/" + I18n.getLocale().toLanguageTag() + "/user-manual.pdf");

                Files.copy(is, p.toPath(), StandardCopyOption.REPLACE_EXISTING);

                Runnable openFile = FileUtils.getOpenFile(p);
                // otherwise JavaFX-Thread is blocked and the file never opens.
                SwingUtilities.invokeLater(openFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void installToDesktop(ActionEvent actionEvent) throws IOException {
        String java = OS.IS_WINDOWS ? "java.exe" : "java";

        String javaHome = System.getProperty("java.home"); // TODO: what happens if java home is not set?
        File javaExecutable = new File(javaHome + File.separator + "bin" + File.separator + java);
        String applicationFolder = new File(SiardApplication.class.getProtectionDomain()
                                                                  .getCodeSource()
                                                                  .getLocation()
                                                                  .getPath()).getParent();
        System.out.println("got an application folder: " + applicationFolder);

        Properties props = new Properties();
        props.load(RootPresenter.class.getResourceAsStream("version.properties"));
        String version = (String) props.get("version");
        System.out.println("got an application version: " + version);
        List<String> arguments = Arrays.asList(new String[]{
                "-Xmx1024m",
                "-Dsun.awt.disablegrab=true",
                "-jar",
                applicationFolder + File.separator + "lib" + File.separator + "siard-suite-" + version + ".jar"
        });

        File icon = new File(getClass().getResource("icons/archive_red.ico").getPath());
        String description = "SIARD Suite: view and modify archived data from relational databases";

        ShellLink shellLink = ShellLink.createLink(javaExecutable.getAbsolutePath());
        shellLink.setCMDArgs(String.join(" ", arguments)); // TODO: Hartwig used ~20 lines to format the arguments. Why?
        shellLink.setWorkingDir(applicationFolder);
        shellLink.setIconLocation(icon.getAbsolutePath());
        shellLink.setName(description); // TODO: why set the name to description?
        SpecialFolder.getDesktopFolder();
        shellLink.saveTo(SpecialFolder.getDesktopFolder() + File.separator + "SIARD Suite.lnk");
    }
}
