package ch.admin.bar.siardsuite.presenter.export;

import ch.admin.bar.siardsuite.view.RootStage;
import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.presenter.DialogPresenter;
import ch.admin.bar.siardsuite.util.I18n;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

public class ExportSuccessDialogPresenter extends DialogPresenter {

    @FXML
    public Label title;
    @FXML
    public MFXButton closeButton;
    @FXML
    public Text message;

    @Override
    public void init(Controller controller, Model model, RootStage stage) {
        this.model = model;
        this.controller = controller;
        this.stage = stage;

        this.title.textProperty().bind(I18n.createStringBinding("export.success.title"));
        this.message.textProperty().bind(I18n.createStringBinding("export.success.message"));

        this.closeButton.setOnAction(event -> this.stage.closeDialog());
    }
}
