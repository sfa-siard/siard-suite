package ch.admin.bar.siardsuite.presenter;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.model.View;
import ch.admin.bar.siardsuite.util.FileUtils;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.util.SiardEvent;
import ch.admin.bar.siardsuite.view.RootStage;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Locale;

public class RootPresenter extends Presenter {

    @FXML
    public Menu menuItemLanguage;
    @FXML
    public MenuItem menuItemOptions, menuItemInfo, menuItemClose;

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
}
