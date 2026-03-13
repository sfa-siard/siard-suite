package ch.admin.bar.siardsuite.ui.presenter.archive.browser.dialogues;

import ch.admin.bar.siardsuite.framework.ServicesFacade;
import ch.admin.bar.siardsuite.framework.dialogs.DialogCloser;
import ch.admin.bar.siardsuite.framework.i18n.DisplayableText;
import ch.admin.bar.siardsuite.framework.i18n.keys.I18nKey;
import ch.admin.bar.siardsuite.framework.view.FXMLLoadHelper;
import ch.admin.bar.siardsuite.framework.view.LoadedView;
import ch.admin.bar.siardsuite.ui.component.CloseDialogButton;
import ch.admin.bar.siardsuite.util.I18n;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import lombok.val;

import java.util.function.Consumer;

public class UnsavedChangesDialogPresenter {

    private static final I18nKey TITLE = I18nKey.of("storage.unsavedChanges.title");
    private static final I18nKey TEXT = I18nKey.of("storage.unsavedChanges.text");

    private static final I18nKey DROP_CHANGES = I18nKey.of("storage.unsavedChanges.action.dropChanges");
    private static final I18nKey SAVE_CHANGES = I18nKey.of("storage.unsavedChanges.action.saveChanges");

    @FXML
    private Label title;
    @FXML
    private Text text;
    @FXML
    private MFXButton closeButton;
    @FXML
    private HBox buttonBox;

    public void init(final DialogCloser dialogCloser, final Consumer<UnsavedChangesDialogPresenter.Result> resultCallback) {
        title.setText(DisplayableText.of(TITLE)
                                     .getText());
        text.setText(DisplayableText.of(TEXT)
                                    .getText());

        closeButton.setOnAction(event -> {
            dialogCloser.closeDialog();
            resultCallback.accept(Result.CANCEL);
        });

        val cancelButton = new CloseDialogButton(dialogCloser);

        val saveChangesButton = new MFXButton();
        saveChangesButton.getStyleClass()
                         .addAll("button", "primary");
        saveChangesButton.setOnAction(event -> {
            dialogCloser.closeDialog();
            resultCallback.accept(Result.SAVE_CHANGES);
        });
        I18n.bind(saveChangesButton.textProperty(), DisplayableText.of(SAVE_CHANGES));

        val dropChangesButton = new MFXButton();
        dropChangesButton.getStyleClass()
                         .addAll("button", "secondary");
        dropChangesButton.setOnAction(event -> {
            dialogCloser.closeDialog();
            resultCallback.accept(Result.DROP_CHANGES);
        });
        I18n.bind(dropChangesButton.textProperty(), DisplayableText.of(DROP_CHANGES));

        buttonBox.getChildren()
                 .addAll(cancelButton, saveChangesButton, dropChangesButton);
    }

    public static LoadedView<UnsavedChangesDialogPresenter> load(
            final Consumer<Result> resultCallback,
            final ServicesFacade servicesFacade
    ) {
        val loaded = FXMLLoadHelper.<UnsavedChangesDialogPresenter>load("fxml/tree/unsaved-changes-dialog.fxml");
        loaded.getController()
              .init(servicesFacade.dialogs(), resultCallback);

        return loaded;
    }

    public enum Result {
        CANCEL,
        DROP_CHANGES,
        SAVE_CHANGES
    }
}
