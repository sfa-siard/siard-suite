package ch.admin.bar.siardsuite.presenter.connection;

import ch.admin.bar.siardsuite.database.model.DbmsConnectionData;
import javafx.scene.layout.VBox;

import java.util.Optional;

public abstract class ConnectionPropertiesForm extends VBox {
    public abstract Optional<DbmsConnectionData> tryGetValidConnectionData();
}
