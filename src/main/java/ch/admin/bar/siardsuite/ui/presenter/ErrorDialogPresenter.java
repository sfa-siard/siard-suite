package ch.admin.bar.siardsuite.ui.presenter;

import ch.admin.bar.siardsuite.framework.DialogCloser;
import ch.admin.bar.siardsuite.framework.ServicesFacade;
import ch.admin.bar.siardsuite.framework.i18n.DisplayableText;
import ch.admin.bar.siardsuite.framework.i18n.keys.I18nKey;
import ch.admin.bar.siardsuite.framework.view.FXMLLoadHelper;
import ch.admin.bar.siardsuite.framework.view.LoadedView;
import ch.admin.bar.siardsuite.model.Failure;
import ch.admin.bar.siardsuite.ui.component.IconButton;
import ch.admin.bar.siardsuite.ui.model.WarningDefinition;
import ch.admin.bar.siardsuite.util.OptionalHelper;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import lombok.Builder;
import lombok.Value;
import lombok.val;

import java.util.Optional;

public class ErrorDialogPresenter {

    private static final I18nKey TITLE = I18nKey.of("error.title");

    @FXML
    public Label title;
    @FXML
    public MFXButton closeButton;
    @FXML
    public Text message;

    @FXML
    public TextArea stacktraceTextArea;

    @FXML
    public TitledPane detailsPane;

    @FXML
    public ImageView iconImageView;

    public void init(final Failure failure, final DialogCloser dialogCloser) {

        iconImageView.setImage(IconButton.Icon.CIRCLE_WARN.toImage());

        detailsPane.setText("Details"); // TODO

        title.textProperty().bind(DisplayableText.of(TITLE).bindable());
        message.textProperty().setValue(failure.message());
        stacktraceTextArea.setText(failure.stacktrace());
        closeButton.setOnAction(event -> dialogCloser.closeDialog());
    }

    public void init(
            final DisplayableText titleText,
            final DisplayableText messageText,
            final IconButton.Icon icon,
            final Optional<Failure> optionalFailure,
            final DialogCloser dialogCloser
    ) {
        iconImageView.setImage(icon.toImage());

        detailsPane.setText("Details"); // TODO

        title.textProperty().bind(titleText.bindable());
        message.textProperty().bind(messageText.bindable());

        OptionalHelper.when(optionalFailure)
                .isPresent(failure -> stacktraceTextArea.setText(failure.stacktrace()))
                .orElse(() -> detailsPane.setVisible(false));
        closeButton.setOnAction(event -> dialogCloser.closeDialog());
    }

    public static LoadedView<ErrorDialogPresenter> load(final Failure failure, final ServicesFacade servicesFacade) {
        val loaded = FXMLLoadHelper.<ErrorDialogPresenter>load("fxml/error-dialog.fxml");
        loaded.getController().init(failure, servicesFacade.dialogs());

        return loaded;
    }

    public static LoadedView<ErrorDialogPresenter> load(
            final WarningDefinition data,
            final ServicesFacade servicesFacade
    ) {
        val loaded = FXMLLoadHelper.<ErrorDialogPresenter>load("fxml/error-dialog.fxml");
        loaded.getController().init(
                data.getTitle(),
                data.getMessage(),
                IconButton.Icon.CIRCLE_WARN,
                Optional.empty(),
                servicesFacade.dialogs()
        );

        return loaded;
    }
}
