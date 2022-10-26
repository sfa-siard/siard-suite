package ch.admin.bar.siardsuite.presenter;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.model.View;
import ch.admin.bar.siardsuite.view.RootStage;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

import static ch.admin.bar.siardsuite.Workflow.*;
import static ch.admin.bar.siardsuite.util.I18n.createStringBinding;

public class StartPresenter extends Presenter {

    @FXML
    private Button archive;
    @FXML
    private Button upload;
    @FXML
    private Button export;
    @FXML
    private Button open;
    @FXML
    private Text openHint;
    @FXML
    private Text archiveHint;
    @FXML
    private Text uploadHint;

    public void init(Controller controller, Model model, RootStage stage) {
        this.model = model;
        this.controller = controller;
        this.stage = stage;

        this.archive.setOnAction(event -> {
            this.controller.setWorkflow(ARCHIVE);
            stage.openDialog(View.ARCHIVE_DB_DIALOG.getName());
        });
        this.upload.setOnAction(event -> {
            this.controller.setWorkflow(UPLOAD);
            System.out.println("uplaod button pressed");
        });
        this.export.setOnAction(event -> {
            this.controller.setWorkflow(EXPORT);
            stage.openDialog(View.OPEN_SIARD_ARCHIVE_DIALOG.getName());

        });
        this.open.setOnAction(event -> {
            this.controller.setWorkflow(OPEN);
            stage.openDialog(View.OPEN_SIARD_ARCHIVE_DIALOG.getName());
        });

        this.archive.textProperty().bind(createStringBinding("button.archive"));
        this.upload.textProperty().bind(createStringBinding("button.upload"));
        this.export.textProperty().bind(createStringBinding("button.export"));
        this.open.textProperty().bind(createStringBinding("button.open"));

        this.openHint.textProperty().bind(createStringBinding("text.open.hint"));
        this.archiveHint.textProperty().bind(createStringBinding("text.archive.hint"));
        this.uploadHint.textProperty().bind(createStringBinding("text.upload.hint"));
    }

}
