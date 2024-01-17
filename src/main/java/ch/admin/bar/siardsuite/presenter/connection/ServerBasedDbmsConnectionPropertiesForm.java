package ch.admin.bar.siardsuite.presenter.connection;

import ch.admin.bar.siardsuite.database.model.DbmsConnectionData;
import ch.admin.bar.siardsuite.database.model.ServerBasedDbms;
import ch.admin.bar.siardsuite.database.model.ServerBasedDbmsConnectionProperties;
import ch.admin.bar.siardsuite.presenter.connection.fields.StringFormField;
import ch.admin.bar.siardsuite.util.i18n.DisplayableText;
import ch.admin.bar.siardsuite.util.i18n.TranslatableText;
import ch.admin.bar.siardsuite.util.i18n.keys.I18nKey;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.val;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Supplier;

public class ServerBasedDbmsConnectionPropertiesForm extends ConnectionPropertiesForm {

    private static final double FORM_FIELD_WITH = 578D;
    private static final double PORT_FIELD_WITH = 120D;

    private static final I18nKey DB_SERVER_LABEL = I18nKey.of("connection.view.dbServer.label");
    private static final I18nKey DB_PORT_LABEL = I18nKey.of("connection.view.port.label");
    private static final I18nKey DB_NAME_LABEL = I18nKey.of("connection.view.databaseName.label");
    private static final I18nKey USERNAME_LABEL = I18nKey.of("connection.view.username.label");
    private static final I18nKey PASSWORD_LABEL = I18nKey.of("connection.view.password.label");

    private final Collection<StringFormField> formFields;

    private final Supplier<ServerBasedDbmsConnectionProperties> connectionPropertiesSupplier;
    private final ServerBasedDbms serverBasedDbms;

    public ServerBasedDbmsConnectionPropertiesForm(
            @NonNull final ServerBasedDbms dbms,
            @NonNull final Optional<ServerBasedDbmsConnectionProperties> initialValue
    ) {
        this.serverBasedDbms = dbms;

        val host = StringFormField.builder()
                .title(TranslatableText.of(DB_SERVER_LABEL))
                .initialValue(initialValue
                        .map(ServerBasedDbmsConnectionProperties::getHost)
                        .orElse(""))
                .prompt(DisplayableText.of(dbms.getExampleHost()))
                .prefWidth(FORM_FIELD_WITH - PORT_FIELD_WITH - 10)
                .build();

        val port = StringFormField.builder()
                .title(TranslatableText.of(DB_PORT_LABEL))
                .initialValue(initialValue
                        .map(ServerBasedDbmsConnectionProperties::getPort)
                        .orElse(dbms.getExamplePort()))
                .prompt(DisplayableText.of(dbms.getExamplePort()))
                .prefWidth(PORT_FIELD_WITH)
                .build();

        val urlAndPortHBox = new HBox(host, port);
        HBox.setMargin(host, new Insets(0, 10, 0, 0));

        val dbName = StringFormField.builder()
                .title(TranslatableText.of(DB_NAME_LABEL))
                .initialValue(initialValue
                        .map(ServerBasedDbmsConnectionProperties::getDbName)
                        .orElse(""))
                .prompt(DisplayableText.of(dbms.getExampleDbName()))
                .prefWidth(FORM_FIELD_WITH)
                .build();

        val username = StringFormField.builder()
                .title(TranslatableText.of(USERNAME_LABEL))
                .initialValue(initialValue
                        .map(ServerBasedDbmsConnectionProperties::getUser)
                        .orElse(""))
                .prefWidth(FORM_FIELD_WITH)
                .build();

        val password = StringFormField.builder()
                .title(TranslatableText.of(PASSWORD_LABEL))
                .initialValue(initialValue
                        .map(ServerBasedDbmsConnectionProperties::getPassword)
                        .orElse(""))
                .prefWidth(FORM_FIELD_WITH)
                .build();

        HBox.setMargin(urlAndPortHBox, new Insets(25));
        HBox.setMargin(username, new Insets(25));
        val firstLineHBox = new HBox(urlAndPortHBox, username);

        HBox.setMargin(dbName, new Insets(25));
        HBox.setMargin(password, new Insets(25));
        val secondLineHBox = new HBox(dbName, password);

        this.getChildren().addAll(
                firstLineHBox,
                secondLineHBox);

        formFields = Arrays.asList(
                host,
                port,
                dbName,
                username,
                password
        );

        connectionPropertiesSupplier = () -> ServerBasedDbmsConnectionProperties.builder()
                .host(host.getValue())
                .port(port.getValue())
                .dbName(dbName.getValue())
                .user(username.getValue())
                .password(password.getValue())
                .build();
    }

    @Override
    public Optional<DbmsConnectionData> tryGetValidConnectionData() {
        if (formFields.stream()
                .allMatch(StringFormField::hasValidValue)) {
            return Optional.of(new DbmsConnectionData(serverBasedDbms, connectionPropertiesSupplier.get()));
        }
        return Optional.empty();
    }
}
