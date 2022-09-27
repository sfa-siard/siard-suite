package ch.admin.bar.siardsuite.presenter;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.model.View;
import ch.admin.bar.siardsuite.util.I18n;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ArchiveAbortDialogPresenter extends DialogPresenter {

    @FXML
    public Text dialogText;
    @FXML
    public Text dialogTitle;
    @FXML
    public HBox buttonsBox;
    @FXML
    private Button close;

    @FXML
    private MFXButton proceedButton;
    @FXML
    private MFXButton cancelButton;

    private Stage dialog;

    @Override
    public void init(Controller controller, Model model, Stage stage) {
        this.model = model;
        this.controller = controller;
        this.stage = stage;
    }

    @Override
    public void init(Controller controller, Model model, Stage stage, Stage dialog) {
        this.init(controller, model,stage);
        this.dialog = dialog;

        this.dialogTitle.textProperty().bind(I18n.createStringBinding("archiveAbortDialog.title"));
        this.dialogText.textProperty().bind(I18n.createStringBinding("archiveAbortDialog.text"));

        this.proceedButton = new MFXButton();
        this.proceedButton.textProperty().bind(I18n.createStringBinding("button.proceed"));
        this.proceedButton.getStyleClass().setAll("button", "secondary");
        this.proceedButton.setManaged(true);
        this.cancelButton = new MFXButton();
        this.cancelButton.textProperty().bind(I18n.createStringBinding("button.cancelArchive"));
        this.cancelButton.getStyleClass().setAll("button", "primary");
        this.cancelButton.setManaged(true);

        this.buttonsBox.getChildren().addAll(this.proceedButton, this.cancelButton);

        this.close.setOnAction(event -> this.dialog.close());
        this.cancelButton.setOnAction(event -> {
            this.dialog.close();
            this.navigate(View.START.getName());
        });
        this.proceedButton.setOnAction(event -> this.dialog.close());
    }
}
