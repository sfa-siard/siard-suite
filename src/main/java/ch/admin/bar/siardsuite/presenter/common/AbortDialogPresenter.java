package ch.admin.bar.siardsuite.presenter.common;

import ch.admin.bar.siardsuite.framework.DialogCloser;
import ch.admin.bar.siardsuite.framework.ServicesFacade;
import ch.admin.bar.siardsuite.framework.navigation.Navigator;
import ch.admin.bar.siardsuite.model.View;
import ch.admin.bar.siardsuite.framework.view.FXMLLoadHelper;
import ch.admin.bar.siardsuite.framework.view.LoadedView;
import ch.admin.bar.siardsuite.framework.i18n.DisplayableText;
import ch.admin.bar.siardsuite.framework.i18n.keys.I18nKey;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import lombok.val;

public class AbortDialogPresenter {

    private static final I18nKey UPLOAD_TITLE = I18nKey.of("uploadAbortDialog.title");
    private static final I18nKey UPLOAD_TEXT = I18nKey.of("uploadAbortDialog.text");
    private static final I18nKey UPLOAD_CANCEL = I18nKey.of("uploadAbortDialog.cancel");
    private static final I18nKey UPLOAD_CONFIRM = I18nKey.of("uploadAbortDialog.confirm");

    private static final I18nKey ARCHIVE_TITLE = I18nKey.of("archiveAbortDialog.title");
    private static final I18nKey ARCHIVE_TEXT = I18nKey.of("archiveAbortDialog.text");
    private static final I18nKey ARCHIVE_CANCEL = I18nKey.of("button.proceed.archive");
    private static final I18nKey ARCHIVE_CONFIRM = I18nKey.of("button.cancel.archive");

    @FXML
    protected Label title;
    @FXML
    protected Text text;
    @FXML
    protected MFXButton closeButton; // x-button
    @FXML
    protected HBox buttonBox;
    @FXML
    public MFXButton cancel; // do not abort process
    @FXML
    public MFXButton confirm; // confirm abortion

    public void init(
            DisplayableText titleText,
            DisplayableText textText,
            DisplayableText cancelDialogText,
            DisplayableText confirmDialogText,
            DialogCloser dialogCloser,
            Navigator navigator
    ) {
        title.textProperty().bind(titleText.bindable());
        text.textProperty().bind(textText.bindable());
        cancel.textProperty().bind(cancelDialogText.bindable());
        confirm.textProperty().bind(confirmDialogText.bindable());

        confirm.setOnAction(event -> {
            dialogCloser.closeDialog();
            navigator.navigate(View.START);
        });

        closeButton.setOnAction(event -> dialogCloser.closeDialog());
        cancel.setOnAction(event -> dialogCloser.closeDialog());
    }

    public static LoadedView<AbortDialogPresenter> loadForUpload(
            final ServicesFacade servicesFacade
    ) {
        val loaded = FXMLLoadHelper.<AbortDialogPresenter>load("fxml/upload/upload-abort-dialog.fxml");
        loaded.getController().init(
                DisplayableText.of(UPLOAD_TITLE),
                DisplayableText.of(UPLOAD_TEXT),
                DisplayableText.of(UPLOAD_CANCEL),
                DisplayableText.of(UPLOAD_CONFIRM),
                servicesFacade.dialogs(),
                servicesFacade.navigator()
        );

        return loaded;
    }

    public static LoadedView<AbortDialogPresenter> loadForArchive(
            final ServicesFacade servicesFacade
    ) {
        val loaded = FXMLLoadHelper.<AbortDialogPresenter>load("fxml/upload/upload-abort-dialog.fxml");
        loaded.getController().init(
                DisplayableText.of(ARCHIVE_TITLE),
                DisplayableText.of(ARCHIVE_TEXT),
                DisplayableText.of(ARCHIVE_CANCEL),
                DisplayableText.of(ARCHIVE_CONFIRM),
                servicesFacade.dialogs(),
                servicesFacade.navigator()
        );

        return loaded;
    }
}
