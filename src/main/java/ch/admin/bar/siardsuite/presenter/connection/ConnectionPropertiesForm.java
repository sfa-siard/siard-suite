package ch.admin.bar.siardsuite.presenter.connection;

import ch.admin.bar.siardsuite.database.model.DbmsConnectionData;
import ch.admin.bar.siardsuite.presenter.connection.fields.FormField;
import ch.admin.bar.siardsuite.util.i18n.keys.I18nKey;
import javafx.scene.layout.VBox;
import lombok.val;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class ConnectionPropertiesForm extends VBox {

    protected static final double FORM_FIELD_WITH = 578D;

    protected static final I18nKey JDBC_URL_LABEL = I18nKey.of("connection.view.url.label");
    protected static final I18nKey INVALID_JDBC_URL_MESSAGE = I18nKey.of("connection.view.url.invalid");

    protected final List<FormField> formFields = new ArrayList<>();

    public boolean isValid() {
        val invalidFields = formFields.stream()
                .filter(FormField::hasInvalidValueAndIfSoShowValidationMessage)
                .collect(Collectors.toList());

        return invalidFields.isEmpty();
    }

    public abstract Optional<DbmsConnectionData> tryGetValidConnectionData();

}