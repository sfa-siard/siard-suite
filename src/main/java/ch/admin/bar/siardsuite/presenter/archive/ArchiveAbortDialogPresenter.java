package ch.admin.bar.siardsuite.presenter.archive;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.model.View;
import ch.admin.bar.siardsuite.presenter.DialogPresenter;
import ch.admin.bar.siardsuite.component.CloseDialogButton;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.view.RootStage;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class ArchiveAbortDialogPresenter extends DialogPresenter {

    @FXML
    protected Label title;
    @FXML
    protected Text text;
    @FXML
    protected MFXButton closeButton; // seems redundant
    @FXML
    protected HBox buttonBox;

    @Override
    public void init(Controller controller, Model model, RootStage stage) {
        this.model = model;
        this.controller = controller;
        this.stage = stage;

        I18n.bind(title.textProperty(), "archiveAbortDialog.title");
        I18n.bind(text.textProperty(), "archiveAbortDialog.text");

        final MFXButton cancelArchiveButton = new MFXButton();
        I18n.bind(cancelArchiveButton.textProperty(), "button.cancel.archive");
        cancelArchiveButton.getStyleClass().setAll("button", "primary");
        cancelArchiveButton.setManaged(true);

        cancelArchiveButton.setOnAction(event -> {
            controller.cancelDownload();
            stage.closeDialog();
            stage.navigate(View.START);
        });

        closeButton.setOnAction(event -> stage.closeDialog());

        final MFXButton proceedArchiveButton = new CloseDialogButton(this.stage);
        I18n.bind(proceedArchiveButton.textProperty(), "button.proceed.archive");

        buttonBox.getChildren().addAll(proceedArchiveButton, cancelArchiveButton);
    }
}
