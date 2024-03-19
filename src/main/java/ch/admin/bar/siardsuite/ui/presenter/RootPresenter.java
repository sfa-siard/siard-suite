package ch.admin.bar.siardsuite.ui.presenter;

import ch.admin.bar.siardsuite.framework.dialogs.Dialogs;
import ch.admin.bar.siardsuite.framework.errors.ErrorHandler;
import ch.admin.bar.siardsuite.framework.i18n.DisplayableText;
import ch.admin.bar.siardsuite.framework.i18n.keys.I18nKey;
import ch.admin.bar.siardsuite.framework.i18n.keys.I18nKeyArg;
import ch.admin.bar.siardsuite.framework.view.FXMLLoadHelper;
import ch.admin.bar.siardsuite.framework.view.LoadedView;
import ch.admin.bar.siardsuite.service.FilesService;
import ch.admin.bar.siardsuite.service.InstallationService;
import ch.admin.bar.siardsuite.service.LogService;
import ch.admin.bar.siardsuite.ui.View;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.util.OS;
import ch.enterag.utils.ProgramInfo;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import lombok.val;

import java.io.IOException;
import java.util.Locale;

public class RootPresenter {

    private static final I18nKeyArg<String> WINDOW_TITLE = I18nKeyArg.of("window.title");

    private static final I18nKey LANGUAGE = I18nKey.of("menu.item.language");
    private static final I18nKey OPTIONS = I18nKey.of("menu.item.options");
    private static final I18nKey SHOW_LOG_FILE = I18nKey.of("menu.item.showLogFile");
    private static final I18nKey INFO = I18nKey.of("menu.item.info");
    private static final I18nKey INSTALL = I18nKey.of("menu.item.install");
    private static final I18nKey CLOSE = I18nKey.of("menu.item.close");


    @FXML
    public Menu menuItemLanguage;
    @FXML
    public MenuItem menuItemOptions, menuItemInfo, menuItemInstall, menuItemClose, menuItemShowLogFile;

    @FXML
    public HBox windowHeader;
    @FXML
    public MenuBar menuBar;
    @FXML
    public Menu menuContainer;
    @FXML
    public MFXButton helpButton;

    @FXML
    public Label applicationLabel;

    private Dialogs dialogs;
    private ErrorHandler errorHandler;
    private InstallationService installationService;
    private FilesService filesService;
    private LogService logService;
    private Runnable appCloser;

    public void init(
            final Dialogs dialogs,
            final InstallationService installationService,
            final FilesService filesService,
            final LogService logService,
            final ErrorHandler errorHandler,
            final Runnable appCloser
    ) {
        this.dialogs = dialogs;
        this.installationService = installationService;
        this.errorHandler = errorHandler;
        this.filesService = filesService;
        this.logService = logService;
        this.appCloser = appCloser;

        applicationLabel.textProperty().bind(DisplayableText.of(WINDOW_TITLE, ProgramInfo.getProgramInfo().getVersion()).bindable());

        initMenu();
    }

    private void initMenu() {
        this.menuBar.setOnMouseClicked(event -> this.menuContainer.show());

        menuItemLanguage.textProperty().bind(DisplayableText.of(LANGUAGE).bindable());
        menuItemOptions.textProperty().bind(DisplayableText.of(OPTIONS).bindable());
        menuItemInfo.textProperty().bind(DisplayableText.of(INFO).bindable());
        menuItemShowLogFile.textProperty().bind(DisplayableText.of(SHOW_LOG_FILE).bindable());
        menuItemInstall.textProperty().bind(DisplayableText.of(INSTALL).bindable());
        menuItemInstall.setVisible(OS.IS_WINDOWS);
        menuItemClose.textProperty().bind(DisplayableText.of(CLOSE).bindable());

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
            });
            item.setToggleGroup(items);
        });

        this.menuItemClose.setOnAction(event -> appCloser.run());
        this.menuItemInfo.setOnAction(event -> this.dialogs.open(View.INFO_DIALOG));
        this.menuItemOptions.setOnAction(event -> this.dialogs.open(View.OPTION_DIALOG));
        this.menuItemShowLogFile.setOnAction(event -> {
            val logFile = logService.getLogFile();
            filesService.openInFileBrowser(logFile);
        });
        this.menuItemInstall.setOnAction(event -> installationService.installToDesktop());
        this.helpButton.setOnAction(event -> filesService.openUserManual());
    }

    public static LoadedView<RootPresenter> load(
            final Dialogs dialogs,
            final InstallationService installationService,
            final FilesService filesService,
            final LogService logService,
            final ErrorHandler errorHandler,
            final Runnable appCloser
    ) {
        val loaded = FXMLLoadHelper.<RootPresenter>load("fxml/root.fxml");
        loaded.getController().init(
                dialogs,
                installationService,
                filesService,
                logService,
                errorHandler,
                appCloser
        );

        return loaded;
    }
}
