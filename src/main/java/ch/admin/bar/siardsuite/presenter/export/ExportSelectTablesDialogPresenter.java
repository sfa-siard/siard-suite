package ch.admin.bar.siardsuite.presenter.export;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.presenter.DialogPresenter;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.view.RootStage;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class ExportSelectTablesDialogPresenter extends DialogPresenter {
    @FXML
    public Label title;

    @FXML
    public MFXButton closeButton;

    @FXML
    public Text text;

    @FXML
    public HBox buttonBox;

    @Override
    public void init(Controller controller, Model model, RootStage stage) {
        this.model = model;
        this.controller = controller;
        this.stage = stage;

        this.title.textProperty().bind(I18n.createStringBinding("export.select-tables.dialog.title"));
        this.text.textProperty().bind(I18n.createStringBinding("export.select-tables.dialog.text"));

        this.closeButton.setOnAction(event -> {
            this.stage.closeDialog();
        });

        this.buttonBox.getChildren().add(getCancelButton());
    }
}


