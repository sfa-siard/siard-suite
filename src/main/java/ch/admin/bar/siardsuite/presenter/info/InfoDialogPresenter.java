package ch.admin.bar.siardsuite.presenter.info;

import ch.admin.bar.siardsuite.component.CloseDialogButton;
import ch.admin.bar.siardsuite.framework.general.ServicesFacade;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.util.fxml.FXMLLoadHelper;
import ch.admin.bar.siardsuite.util.fxml.LoadedFxml;
import ch.admin.bar.siardsuite.view.DialogCloser;
import ch.enterag.utils.ProgramInfo;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import lombok.val;

public class InfoDialogPresenter {
    @FXML
    protected Label title;
    @FXML
    protected Text text;
    @FXML
    protected MFXButton closeButton;
    @FXML
    protected HBox buttonBox;

    public void init(DialogCloser dialogCloser) {
        I18n.bind(title.textProperty(), "info.dialog.title", ProgramInfo.getProgramInfo().getVersion());
        I18n.bind(text.textProperty(), "info.dialog.text");
        closeButton.setOnAction(event -> dialogCloser.closeDialog());
        buttonBox.getChildren().add(new CloseDialogButton(dialogCloser));
    }

    public static LoadedFxml<InfoDialogPresenter> load(final ServicesFacade servicesFacade) {
        val loaded = FXMLLoadHelper.<InfoDialogPresenter>load("fxml/info/info-dialog.fxml");
        loaded.getController().init(servicesFacade.dialogs());

        return loaded;
    }
}
