package ch.admin.bar.siardsuite.presenter.info;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.component.CloseDialogButton;
import ch.admin.bar.siardsuite.presenter.DialogPresenter;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.view.RootStage;
import ch.enterag.utils.ProgramInfo;
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
    public void init(Controller controller, RootStage stage) {
        this.controller = controller;
        this.stage = stage;

        I18n.bind(title.textProperty(), "info.dialog.title", ProgramInfo.getProgramInfo().getVersion());
        I18n.bind(text.textProperty(), "info.dialog.text");
        closeButton.setOnAction(event -> stage.closeDialog());
        buttonBox.getChildren().add(new CloseDialogButton(this.stage));
    }
}
