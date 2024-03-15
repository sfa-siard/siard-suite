package ch.admin.bar.siardsuite.presenter;

import ch.admin.bar.siardsuite.framework.ServicesFacade;
import ch.admin.bar.siardsuite.model.Failure;
import ch.admin.bar.siardsuite.framework.view.FXMLLoadHelper;
import ch.admin.bar.siardsuite.framework.view.LoadedView;
import ch.admin.bar.siardsuite.framework.i18n.DisplayableText;
import ch.admin.bar.siardsuite.framework.i18n.keys.I18nKey;
import ch.admin.bar.siardsuite.framework.DialogCloser;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import lombok.val;

public class ErrorDialogPresenter {

    private static final I18nKey TITLE = I18nKey.of("error.title");

    @FXML
    public Label title;
    @FXML
    public MFXButton closeButton;
    @FXML
    public Text message;
    @FXML
    public TextArea stacktrace;

    public void init(final Failure failure, final DialogCloser dialogCloser) {
        this.closeButton.setOnAction(event -> dialogCloser.closeDialog());

        title.textProperty().bind(DisplayableText.of(TITLE).bindable());
        message.textProperty().setValue(failure.message());
        stacktrace.textProperty().setValue(failure.stacktrace());
        closeButton.setOnAction(event -> dialogCloser.closeDialog());
    }

    public static LoadedView<ErrorDialogPresenter> load(final Failure failure, final ServicesFacade servicesFacade) {
        val loaded = FXMLLoadHelper.<ErrorDialogPresenter>load("fxml/error-dialog.fxml");
        loaded.getController().init(failure, servicesFacade.dialogs());

        return loaded;
    }
}
