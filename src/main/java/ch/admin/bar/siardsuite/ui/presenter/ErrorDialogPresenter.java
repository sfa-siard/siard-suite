package ch.admin.bar.siardsuite.ui.presenter;

import ch.admin.bar.siardsuite.framework.dialogs.DialogCloser;
import ch.admin.bar.siardsuite.framework.ServicesFacade;
import ch.admin.bar.siardsuite.framework.i18n.DisplayableText;
import ch.admin.bar.siardsuite.framework.i18n.keys.I18nKey;
import ch.admin.bar.siardsuite.framework.view.FXMLLoadHelper;
import ch.admin.bar.siardsuite.framework.view.LoadedView;
import ch.admin.bar.siardsuite.model.Failure;
import ch.admin.bar.siardsuite.ui.component.IconButton;
import ch.admin.bar.siardsuite.framework.errors.NewFailure;
import ch.admin.bar.siardsuite.util.OptionalHelper;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import lombok.val;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

public class ErrorDialogPresenter {

    private static final I18nKey SHOW_DETAILS = I18nKey.of("error.button.details.show");
    private static final I18nKey HIDE_DETAILS = I18nKey.of("error.button.details.hide");

    @FXML
    public Label title;

    @FXML
    public MFXButton closeButton;

    @FXML
    public Text message;

    @FXML
    public TextArea stacktraceTextArea;

    @FXML
    public ImageView iconImageView;

    @FXML
    public Label detailsLabel;

    public void init(
            final DisplayableText titleText,
            final DisplayableText messageText,
            final IconButton.Icon icon,
            final Optional<Throwable> optionalThrowable,
            final DialogCloser dialogCloser
    ) {
        detailsLabel.setText(DisplayableText.of(SHOW_DETAILS).getText());
        iconImageView.setImage(icon.toImage());


        title.textProperty().bind(titleText.bindable());
        message.textProperty().bind(messageText.bindable());

        OptionalHelper.when(optionalThrowable.map(throwable -> {
                    val stringWriter = new StringWriter();
                    throwable.printStackTrace(new PrintWriter(stringWriter));
                    return stringWriter.toString();
                }))
                .isPresent(stacktrace -> {
                    detailsLabel.setVisible(true);
                    detailsLabel.setManaged(true);

                    stacktraceTextArea.setText(stacktrace);
                })
                .orElse(() -> {
                    detailsLabel.setVisible(false);
                    detailsLabel.setManaged(false);
                });

        closeButton.setOnAction(event -> dialogCloser.closeDialog());

        detailsLabel.setOnMouseClicked(event -> {
            if (stacktraceTextArea.isVisible()) {
                detailsLabel.setText(DisplayableText.of(SHOW_DETAILS).getText());
                stacktraceTextArea.setVisible(false);
                stacktraceTextArea.setManaged(false);
            } else {
                detailsLabel.setText(DisplayableText.of(HIDE_DETAILS).getText());
                stacktraceTextArea.setVisible(true);
                stacktraceTextArea.setManaged(true);
            }
        });
    }

    public static LoadedView<ErrorDialogPresenter> load(
            final NewFailure data,
            final ServicesFacade servicesFacade
    ) {
        val loaded = FXMLLoadHelper.<ErrorDialogPresenter>load("fxml/error-dialog.fxml");
        loaded.getController().init(
                data.getTitle(),
                data.getMessage(),
                getIcon(data.getType()),
                data.getThrowable(),
                servicesFacade.dialogs()
        );

        return loaded;
    }

    private static IconButton.Icon getIcon(final NewFailure.Type failureType) {
        switch (failureType) {
            case ERROR:
                return IconButton.Icon.CIRCLE_ERROR;
            case WARNING:
                return IconButton.Icon.CIRCLE_WARN;
        }

        throw new IllegalArgumentException("Not supported: " + failureType);
    }
}
