package ch.admin.bar.siardsuite.presenter;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.util.SiardEvent;
import ch.admin.bar.siardsuite.view.RootStage;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;

public class ErrorDialogPresenter extends DialogPresenter {

    @FXML
    public Label title;
    @FXML
    public MFXButton closeButton;
    @FXML
    public Text message;
    @FXML
    public TextArea stacktrace;

    @Override
    public void init(Controller controller, Model model, RootStage stage) {
        this.model = model;
        this.controller = controller;
        this.stage = stage;

        EventHandler<SiardEvent> databaseDownloadFailedHandler = event -> {
            this.title.textProperty().bind(I18n.createStringBinding("error.title"));
            this.message.textProperty().setValue(this.controller.getErrorMessage());
            this.stacktrace.textProperty().setValue(this.controller.getStacktrace());
        };

        this.stage.addEventHandler(SiardEvent.DATABASE_DOWNLOAD_FAILED, databaseDownloadFailedHandler);

        this.closeButton.setOnAction(event -> this.stage.closeDialog());
    }
}
