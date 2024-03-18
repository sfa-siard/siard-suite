package ch.admin.bar.siardsuite.ui.presenter.info;

import ch.admin.bar.siardsuite.framework.dialogs.DialogCloser;
import ch.admin.bar.siardsuite.framework.ServicesFacade;
import ch.admin.bar.siardsuite.framework.i18n.DisplayableText;
import ch.admin.bar.siardsuite.framework.i18n.keys.I18nKey;
import ch.admin.bar.siardsuite.framework.i18n.keys.I18nKeyArg;
import ch.admin.bar.siardsuite.framework.view.FXMLLoadHelper;
import ch.admin.bar.siardsuite.framework.view.LoadedView;
import ch.admin.bar.siardsuite.ui.component.CloseDialogButton;
import ch.enterag.utils.ProgramInfo;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import lombok.val;

public class InfoDialogPresenter {

    private static final I18nKeyArg<String> TITLE = I18nKeyArg.of("info.dialog.title");
    private static final I18nKey TEXT = I18nKey.of("info.dialog.text");

    @FXML
    protected Label title;
    @FXML
    protected Text text;
    @FXML
    protected MFXButton closeButton;
    @FXML
    protected HBox buttonBox;

    public void init(DialogCloser dialogCloser) {
        val appVersion = ProgramInfo.getProgramInfo()
                .getVersion();

        title.textProperty().bind(DisplayableText.of(TITLE, appVersion).bindable());
        text.textProperty().bind(DisplayableText.of(TEXT).bindable());

        closeButton.setOnAction(event -> dialogCloser.closeDialog());
        buttonBox.getChildren().add(new CloseDialogButton(dialogCloser));
    }

    public static LoadedView<InfoDialogPresenter> load(final ServicesFacade servicesFacade) {
        val loaded = FXMLLoadHelper.<InfoDialogPresenter>load("fxml/info/info-dialog.fxml");
        loaded.getController().init(servicesFacade.dialogs());

        return loaded;
    }
}
