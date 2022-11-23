package ch.admin.bar.siardsuite.presenter.info;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.component.CloseDialogButton;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.presenter.DialogPresenter;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.view.RootStage;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class InfoDialogPresenter extends DialogPresenter {
    @FXML
    protected Label title;
    @FXML
    protected Text text;
    @FXML
    protected MFXButton closeButton;

    @FXML
    protected HBox buttonBox;

    @Override
    public void init(Controller controller, Model model, RootStage stage) {
        this.model = model;
        this.controller = controller;
        this.stage = stage;

        I18n.bind(title.textProperty(), "info.dialog.title");
        I18n.bind(text.textProperty(), "info.dialog.text");
        closeButton.setOnAction(event -> stage.closeDialog());
        buttonBox.getChildren().add(new CloseDialogButton(this.stage));
    }
}
