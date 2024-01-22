package ch.admin.bar.siardsuite.presenter.connection;

import ch.admin.bar.siardsuite.database.model.DbmsConnectionData;
import ch.admin.bar.siardsuite.presenter.connection.fields.FormField;
import javafx.scene.layout.VBox;
import lombok.val;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class ConnectionPropertiesForm extends VBox {

    protected final List<FormField> formFields = new ArrayList<>();

    public boolean isValid() {
        val invalidFields = formFields.stream()
                .map(FormField::hasInvalidValue)
                .collect(Collectors.toList());

        return invalidFields.isEmpty();
    }

    public abstract Optional<DbmsConnectionData> tryGetValidConnectionData();

}
