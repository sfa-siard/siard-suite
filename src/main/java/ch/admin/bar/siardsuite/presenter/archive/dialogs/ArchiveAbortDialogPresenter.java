package ch.admin.bar.siardsuite.presenter.archive.dialogs;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.component.CloseDialogButton;
import ch.admin.bar.siardsuite.framework.dialogs.Dialogs;
import ch.admin.bar.siardsuite.framework.general.ServicesFacade;
import ch.admin.bar.siardsuite.framework.navigation.Navigator;
import ch.admin.bar.siardsuite.model.View;
import ch.admin.bar.siardsuite.presenter.DialogPresenter;
import ch.admin.bar.siardsuite.service.DbInteractionService;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.view.RootStage;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import lombok.val;

import static ch.admin.bar.siardsuite.util.I18n.bind;

public class ArchiveAbortDialogPresenter extends DialogPresenter {

    @FXML
    protected Label title;
    @FXML
    protected Text text;
    @FXML
    protected MFXButton closeButton;
    @FXML
    protected HBox buttonBox;

    private Dialogs dialogs;
    private Navigator navigator;
    private DbInteractionService dbInteractionService;

    @Override
    public void init(Controller controller, RootStage stage) {
        this.controller = controller;
        this.stage = stage;

        val services = ServicesFacade.INSTANCE; // TODO
        this.dialogs = services.dialogs();
        this.navigator = services.navigator();
        this.dbInteractionService = services.dbInteractionService();


        bindLabels();
        addDialogButtons();
    }

    private void bindLabels() {
        bind(title, "archiveAbortDialog.title");
        I18n.bind(text, "archiveAbortDialog.text");
    }

    private void addDialogButtons() {
        final MFXButton cancelArchiveButton = new MFXButton();
        bind(cancelArchiveButton, "button.cancel.archive");
        cancelArchiveButton.getStyleClass().setAll("button", "primary");
        cancelArchiveButton.setManaged(true);

        cancelArchiveButton.setOnAction(event -> {
            dbInteractionService.cancelRunning();
            dialogs.closeDialog();
            navigator.navigate(View.START);
        });

        closeButton.setOnAction(event -> dialogs.closeDialog());

        final MFXButton proceedArchiveButton = new CloseDialogButton(this.stage);
        bind(proceedArchiveButton, "button.proceed.archive");
        buttonBox.getChildren().addAll(proceedArchiveButton, cancelArchiveButton);
    }

}
