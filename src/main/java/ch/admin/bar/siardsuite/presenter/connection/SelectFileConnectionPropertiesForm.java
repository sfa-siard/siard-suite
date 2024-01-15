package ch.admin.bar.siardsuite.presenter.connection;

import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import lombok.val;

public class SelectFileConnectionPropertiesForm extends VBox {

    public SelectFileConnectionPropertiesForm() {
        val fileChooser = new FileChooser();
    }
}
