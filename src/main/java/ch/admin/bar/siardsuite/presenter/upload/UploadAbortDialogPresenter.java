package ch.admin.bar.siardsuite.presenter.upload;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.model.View;
import ch.admin.bar.siardsuite.presenter.DialogPresenter;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.view.RootStage;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class UploadAbortDialogPresenter extends DialogPresenter {


    @FXML
    protected Label title;
    @FXML
    protected Text text;
    @FXML
    protected MFXButton closeButton; // x-button
    @FXML
    protected HBox buttonBox;
    @FXML
    public MFXButton cancel; // do not abort process
    @FXML
    public MFXButton confirm; // confirm abortion

    @Override
    public void init(Controller controller, RootStage stage) {
        this.controller = controller;
        this.stage = stage;

        I18n.bind(title.textProperty(), "uploadAbortDialog.title");
        I18n.bind(text.textProperty(), "uploadAbortDialog.text");

        I18n.bind(cancel.textProperty(), "uploadAbortDialog.cancel");
        I18n.bind(confirm.textProperty(), "uploadAbortDialog.confirm");

        confirm.setOnAction(event -> {
            controller.cancelRunning();
            stage.closeDialog();
            stage.navigate(View.START);
        });

        closeButton.setOnAction(event -> stage.closeDialog());
        cancel.setOnAction(event -> stage.closeDialog());
    }
}
