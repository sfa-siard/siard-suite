package ch.admin.bar.siardsuite.presenter.connection;

import ch.admin.bar.siardsuite.database.model.Dbms;
import ch.admin.bar.siardsuite.database.model.DbmsConnectionData;
import ch.admin.bar.siardsuite.database.model.DbmsConnectionProperties;
import ch.admin.bar.siardsuite.database.model.FileBasedDbms;
import ch.admin.bar.siardsuite.database.model.FileBasedDbmsConnectionProperties;
import ch.admin.bar.siardsuite.database.model.ServerBasedDbms;
import ch.admin.bar.siardsuite.database.model.ServerBasedDbmsConnectionProperties;
import ch.admin.bar.siardsuite.presenter.connection.fields.StringFormField;
import ch.admin.bar.siardsuite.util.CastHelper;
import ch.admin.bar.siardsuite.util.Validator;
import ch.admin.bar.siardsuite.util.i18n.DisplayableText;
import ch.admin.bar.siardsuite.util.i18n.keys.I18nKey;
import ch.admin.bar.siardsuite.util.preferences.RecentDbConnection;
import ch.admin.bar.siardsuite.util.preferences.UserPreferences;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import lombok.val;

import java.util.Optional;

public class ConnectionForm extends VBox {

    private static final I18nKey TITLE = I18nKey.of("connection.view.title");

    private static final I18nKey TEXT_0 = I18nKey.of("connection.view.text0");
    private static final I18nKey TEXT_1 = I18nKey.of("connection.view.text1");
    private static final I18nKey TEXT_2 = I18nKey.of("connection.view.text2");
    private static final I18nKey TEXT_3 = I18nKey.of("connection.view.text3");
    private static final I18nKey TEXT_4 = I18nKey.of("connection.view.text4");

    private static final I18nKey TOGGLE_SAVE_INFO = I18nKey.of("archiveConnection.view.tooltip");
    private static final I18nKey CONNECTION_NAME = I18nKey.of("archiveConnection.view.connectionName.label");

    private final VBox titleBox;
    private final VBox formBox;
    private ConnectionPropertiesForm connectionPropertiesForm;
    private StringFormField connectionNameField;

    public ConnectionForm() {
        val title = new Text();
        title.textProperty().bind(DisplayableText.of(TITLE).bindable());
        title.setStrokeType(StrokeType.OUTSIDE);
        title.setStrokeWidth(0);
        title.getStyleClass().add("h2");

        val textflow = new TextFlow(
                createText(TEXT_0),
                createBoldText(TEXT_1),
                createText(TEXT_2),
                createBoldText(TEXT_3),
                createText(TEXT_4)
        );
        textflow.setLineSpacing(8);

        VBox.setMargin(title, new Insets(0,25,0,25));
        VBox.setMargin(textflow, new Insets(15,25,24,25));
        titleBox = new VBox(title, textflow);

        formBox = new VBox();
        formBox.getStyleClass().add("form-container");

        this.getChildren().addAll(titleBox, formBox);
    }

    public <T extends DbmsConnectionProperties> void show(final Dbms<T> dbms) {
        show(dbms, Optional.empty(), "");
    }

    public <T extends DbmsConnectionProperties> void show(
            final Dbms<T> dbms,
            final DbmsConnectionProperties<T> initialValue,
            final String connectionName
    ) {
        show(dbms, Optional.of(initialValue), connectionName);
    }

    private <T extends DbmsConnectionProperties> void show(
            final Dbms<T> dbms,
            final Optional<DbmsConnectionProperties<T>> initialValue,
            final String connectionName
    ) {
        connectionPropertiesForm = this.getConnectionPropertiesForm(dbms, initialValue);
        HBox.setHgrow(connectionPropertiesForm, Priority.ALWAYS);

        connectionNameField = StringFormField.builder()
                .title(DisplayableText.of(CONNECTION_NAME))
                .validator(Validator.IS_NOT_EMPTY_STRING_VALIDATOR)
                .validator(Validator.SLASH_NOT_ALLOWED_VALIDATOR)
                .hint(DisplayableText.of(TOGGLE_SAVE_INFO))
                .initialValue(connectionName)
                .deactivable(true)
                .build();
        VBox.setMargin(connectionNameField, new Insets(25));
        VBox.setVgrow(connectionNameField, Priority.ALWAYS);

        this.formBox.getChildren().clear();
        this.formBox.getChildren().addAll(connectionPropertiesForm, connectionNameField);
    }

    public boolean isValid() {
        return connectionPropertiesForm.isValid() & !connectionNameField.hasInvalidValueAndIfSoShowValidationMessage();
    }

    public DbmsConnectionData getConnectionData() {
        val connectionData = connectionPropertiesForm.getConnectionData();

        if (connectionNameField.isActivated()) {
            UserPreferences.INSTANCE.push(RecentDbConnection.from(
                    connectionData,
                    connectionNameField.getValue()
            ));
        }

        return connectionData;
    }

    public Optional<DbmsConnectionData> tryGetValidConnectionData() {
        if (!isValid()) {
            return Optional.empty();
        }

        val connectionData = connectionPropertiesForm.getConnectionData();

        if (connectionNameField.isActivated()) {
            UserPreferences.INSTANCE.push(RecentDbConnection.from(
                    connectionData,
                    connectionNameField.getValue()
            ));
        }

        return Optional.of(connectionData);
    }

    private <T extends DbmsConnectionProperties> ConnectionPropertiesForm getConnectionPropertiesForm(
            final Dbms<T> dbms,
            final Optional<DbmsConnectionProperties<T>> initialValue
    ) {
        if (dbms instanceof ServerBasedDbms) {
            return new ServerBasedDbmsConnectionPropertiesForm(
                    (ServerBasedDbms) dbms,
                    initialValue.flatMap(CastHelper.tryCast(ServerBasedDbmsConnectionProperties.class)));
        }

        if (dbms instanceof FileBasedDbms) {
            return new FileBasedDbmsConnectionPropertiesForm(
                    (FileBasedDbms) dbms,
                    initialValue.flatMap(CastHelper.tryCast(FileBasedDbmsConnectionProperties.class)));
        }

        throw new IllegalArgumentException("Unsupported DBMS: " + dbms);
    }

    private static Text createBoldText(I18nKey textKey) {
        val text = createText(textKey);
        text.getStyleClass().add("bold");

        return text;
    }

    private static Text createText(I18nKey textKey) {
        val text = new Text();
        text.textProperty().bind(DisplayableText.of(textKey).bindable());
        text.getStyleClass().add("view-text");

        return text;
    }
}
