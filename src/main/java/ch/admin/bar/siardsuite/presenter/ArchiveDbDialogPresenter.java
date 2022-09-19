package ch.admin.bar.siardsuite.presenter;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.util.View;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class ArchiveDbDialogPresenter extends Presenter {

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

        this.close.setOnAction(event -> this.dialog.close());
        this.cancel.setOnAction(event -> this.dialog.close());
        this.createNewConnection.setOnAction(event -> {
            this.dialog.close();
            this.navigate(View.ARCHIVE.getName());
        });
    }
}
