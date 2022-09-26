package ch.admin.bar.siardsuite.presenter;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.model.View;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.view.RootStage;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

public class ArchiveDbDialogPresenter extends Presenter {

    @FXML
    public Text dialogTitle;
    @FXML
    public Text dialogText;
    @FXML
    private Button cancel;
    @FXML
    private Button createNewConnection;

    @Override
    public void init(Controller controller, Model model, RootStage stage) {
        this.model = model;
        this.controller = controller;
        this.stage = stage;

        dialogTitle.textProperty().bind(I18n.createStringBinding("archiveDbDialog.title"));
        dialogText.textProperty().bind(I18n.createStringBinding("archiveDbDialog.text"));
        createNewConnection.textProperty().bind(I18n.createStringBinding("archiveDbDialog.btnNewConnection"));
        cancel.textProperty().bind(I18n.createStringBinding("button.cancel"));

        cancel.setOnAction(event -> stage.closeDialog());
        createNewConnection.setOnAction(event -> {
            stage.closeDialog();
            stage.loadView(View.ARCHIVE_STEPPER.getName());
        });
    }

}
