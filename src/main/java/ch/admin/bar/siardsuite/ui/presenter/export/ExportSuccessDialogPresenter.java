package ch.admin.bar.siardsuite.ui.presenter.export;

import ch.admin.bar.siardsuite.framework.DialogCloser;
import ch.admin.bar.siardsuite.framework.ServicesFacade;
import ch.admin.bar.siardsuite.framework.view.FXMLLoadHelper;
import ch.admin.bar.siardsuite.framework.view.LoadedView;
import ch.admin.bar.siardsuite.framework.i18n.DisplayableText;
import ch.admin.bar.siardsuite.framework.i18n.keys.I18nKey;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import lombok.val;

public class ExportSuccessDialogPresenter {

    private static final I18nKey TITLE = I18nKey.of("export.success.title");
    private static final I18nKey MESSAGE = I18nKey.of("export.success.message");

    @FXML
    public Label title;
    @FXML
    public MFXButton closeButton;
    @FXML
    public Text message;

    public void init(DialogCloser dialogCloser) {
        this.title.textProperty().bind(DisplayableText.of(TITLE).bindable());
        this.message.textProperty().bind(DisplayableText.of(MESSAGE).bindable());

        this.closeButton.setOnAction(event -> dialogCloser.closeDialog());
    }

    public static LoadedView<ExportSuccessDialogPresenter> load(final ServicesFacade servicesFacade) {
        val loaded = FXMLLoadHelper.<ExportSuccessDialogPresenter>load("fxml/export/export-success-dialog.fxml");
        loaded.getController().init(servicesFacade.dialogs());

        return loaded;
    }
}
