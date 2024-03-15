package ch.admin.bar.siardsuite.presenter;

import ch.admin.bar.siardsuite.framework.view.FXMLLoadHelper;
import ch.admin.bar.siardsuite.framework.view.LoadedView;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import lombok.val;

public class DialogPresenter {

    @FXML
    protected HBox windowHeader;

    public static LoadedView<DialogPresenter> load() {
        val loaded = FXMLLoadHelper.<DialogPresenter>load("fxml/dialog.fxml");

        return loaded;
    }
}
