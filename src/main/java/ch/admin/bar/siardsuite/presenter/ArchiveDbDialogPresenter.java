package ch.admin.bar.siardsuite.presenter;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.model.View;
import ch.admin.bar.siardsuite.util.I18n;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ArchiveDbDialogPresenter extends Presenter {

    @FXML
    public Text dialogText;
    @FXML
    public Text dialogTitle;
    @FXML
    private Button close;

    @FXML
    private Button cancel;

    @FXML
    private Button createNewConnection;

    private Stage dialog;

    @Override
    public void init(Controller controller, Model model, Stage stage) {
        this.model = model;
        this.controller = controller;
        this.stage = stage;
    }

    public void init(Controller controller, Model model, Stage stage, Stage dialog) {
        this.init(controller, model,stage);
        this.dialog = dialog;

        this.dialogTitle.textProperty().bind(I18n.createStringBinding("archiveDbDialog.title"));
        this.dialogText.textProperty().bind(I18n.createStringBinding("archiveDbDialog.text"));
        this.createNewConnection.textProperty().bind((I18n.createStringBinding("archiveDbDialog.btnNewConnection")));
        this.cancel.textProperty().bind(I18n.createStringBinding("button.cancel"));

        this.close.setOnAction(event -> this.dialog.close());
        this.cancel.setOnAction(event -> this.dialog.close());
        this.createNewConnection.setOnAction(event -> {
            this.dialog.close();
            this.navigate(View.ARCHIVE_STEPPER.getName());
        });
    }
}
