package ch.admin.bar.siardsuite.ui.presenter;

import ch.admin.bar.siardsuite.framework.dialogs.Dialogs;
import ch.admin.bar.siardsuite.framework.ServicesFacade;
import ch.admin.bar.siardsuite.model.View;
import ch.admin.bar.siardsuite.service.FilesService;
import ch.admin.bar.siardsuite.service.InstallationService;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.util.OS;
import ch.admin.bar.siardsuite.framework.view.FXMLLoadHelper;
import ch.admin.bar.siardsuite.framework.view.LoadedView;
import ch.admin.bar.siardsuite.framework.ErrorHandler;
import ch.admin.bar.siardsuite.ui.RootStage;
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
import javafx.stage.WindowEvent;
import lombok.val;

import java.util.Locale;

public class RootPresenter {

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

    @FXML
    public Label applicationLabel;

    private Dialogs dialogs;
    private ErrorHandler errorHandler;
    private InstallationService installationService;
    private FilesService filesService;
    private RootStage stage;

    public void init(
            final Dialogs dialogs,
            final InstallationService installationService,
            final FilesService filesService,
            final ErrorHandler errorHandler,
            final RootStage stage
    ) {
        this.dialogs = dialogs;
        this.installationService = installationService;
        this.errorHandler = errorHandler;
        this.filesService = filesService;
        this.stage = stage;

        I18n.bind(stage.titleProperty(), "window.title", ProgramInfo.getProgramInfo().getVersion());
        I18n.bind(applicationLabel, "window.title", ProgramInfo.getProgramInfo().getVersion());

        initMenu();
    }

    private void initMenu() {
        this.menuBar.setOnMouseClicked(event -> this.menuContainer.show());

        I18n.bind(menuItemLanguage.textProperty(), "menu.item.language");
        I18n.bind(menuItemOptions.textProperty(), "menu.item.options");
        I18n.bind(menuItemInfo.textProperty(), "menu.item.info");
        I18n.bind(menuItemInstall.textProperty(), "menu.item.install");
        menuItemInstall.setVisible(OS.IS_WINDOWS);

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
            });
            item.setToggleGroup(items);
        });

        this.menuItemClose.setOnAction(event -> {
            this.stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
        });
        this.menuItemInfo.setOnAction(event -> this.dialogs.open(View.INFO_DIALOG));
        this.menuItemOptions.setOnAction(event -> this.dialogs.open(View.OPTION_DIALOG));
        this.menuItemInstall.setOnAction(event -> errorHandler.wrap(installationService::installToDesktop));
        this.helpButton.setOnAction(event -> filesService.openUserManual());
    }

    public static LoadedView<RootPresenter> load(
            final ServicesFacade servicesFacade,
            final RootStage stage
    ) {
        val loaded = FXMLLoadHelper.<RootPresenter>load("fxml/root.fxml");
        loaded.getController().init(
                servicesFacade.dialogs(),
                servicesFacade.getService(InstallationService.class),
                servicesFacade.getService(FilesService.class),
                servicesFacade.errorHandler(),
                stage
        );

        return loaded;
    }
}
