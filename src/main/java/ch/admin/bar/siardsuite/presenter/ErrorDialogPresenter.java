package ch.admin.bar.siardsuite.presenter;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.model.Failure;
import ch.admin.bar.siardsuite.util.SiardEvent;
import ch.admin.bar.siardsuite.util.fxml.FXMLLoadHelper;
import ch.admin.bar.siardsuite.util.fxml.LoadedFxml;
import ch.admin.bar.siardsuite.util.i18n.DisplayableText;
import ch.admin.bar.siardsuite.util.i18n.keys.I18nKey;
import ch.admin.bar.siardsuite.view.DialogCloser;
import ch.admin.bar.siardsuite.view.RootStage;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import lombok.val;

public class ErrorDialogPresenter extends DialogPresenter {

    private static final I18nKey TITLE = I18nKey.of("error.title");

    @FXML
    public Label title;
    @FXML
    public MFXButton closeButton;
    @FXML
    public Text message;
    @FXML
    public TextArea stacktrace;

    @Override
    public void init(Controller controller, RootStage stage) {
        this.controller = controller;
        this.stage = stage;

        EventHandler<SiardEvent> databaseDownloadFailedHandler = event -> {
            this.title.textProperty().bind(DisplayableText.of(TITLE).bindable());

            this.message.textProperty().setValue(this.controller.errorMessage());
            this.stacktrace.textProperty().setValue(this.controller.errorStackTrace());
        };

        this.stage.addEventHandler(SiardEvent.ERROR_OCCURED, databaseDownloadFailedHandler);
        this.closeButton.setOnAction(event -> this.stage.closeDialog());
    }

    public static LoadedFxml<ErrorDialogPresenter> load(final Failure failure, final DialogCloser dialogCloser) {
        val loaded = FXMLLoadHelper.<ErrorDialogPresenter>load("fxml/error-dialog.fxml");

        val controller = loaded.getController();

        controller.title.textProperty().bind(DisplayableText.of(TITLE).bindable());
        controller.message.textProperty().setValue(failure.message());
        controller.stacktrace.textProperty().setValue(failure.stacktrace());
        controller.closeButton.setOnAction(event -> dialogCloser.closeDialog());

        return loaded;
    }
}
