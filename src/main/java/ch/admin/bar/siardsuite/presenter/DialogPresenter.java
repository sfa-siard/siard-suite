package ch.admin.bar.siardsuite.presenter;

import ch.admin.bar.siardsuite.util.fxml.FXMLLoadHelper;
import ch.admin.bar.siardsuite.util.fxml.LoadedFxml;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import lombok.val;

public class DialogPresenter {

    @FXML
    protected HBox windowHeader;

    public static LoadedFxml<DialogPresenter> load() {
        val loaded = FXMLLoadHelper.<DialogPresenter>load("fxml/dialog.fxml");

        return loaded;
    }
}
