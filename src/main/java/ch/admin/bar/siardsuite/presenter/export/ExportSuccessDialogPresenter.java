package ch.admin.bar.siardsuite.presenter.export;

import ch.admin.bar.siardsuite.framework.general.ServicesFacade;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.util.fxml.FXMLLoadHelper;
import ch.admin.bar.siardsuite.util.fxml.LoadedFxml;
import ch.admin.bar.siardsuite.view.DialogCloser;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import lombok.val;

public class ExportSuccessDialogPresenter {

    @FXML
    public Label title;
    @FXML
    public MFXButton closeButton;
    @FXML
    public Text message;

    public void init(DialogCloser dialogCloser) {
        this.title.textProperty().bind(I18n.createStringBinding("export.success.title"));
        this.message.textProperty().bind(I18n.createStringBinding("export.success.message"));

        this.closeButton.setOnAction(event -> dialogCloser.closeDialog());
    }

    public static LoadedFxml<ExportSuccessDialogPresenter> load(final ServicesFacade servicesFacade) {
        val loaded = FXMLLoadHelper.<ExportSuccessDialogPresenter>load("fxml/export/export-success-dialog.fxml");
        loaded.getController().init(servicesFacade.dialogs());

        return loaded;
    }
}
