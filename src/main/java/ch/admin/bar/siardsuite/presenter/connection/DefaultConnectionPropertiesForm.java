package ch.admin.bar.siardsuite.presenter.connection;

import ch.admin.bar.siardsuite.util.i18n.TranslatableText;
import ch.admin.bar.siardsuite.util.i18n.keys.I18nKey;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.val;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Supplier;

public class DefaultConnectionPropertiesForm extends VBox {

    private static final double FORM_FIELD_WITH = 578D;
    private static final double PORT_FIELD_WITH = 120D;

    private static final I18nKey DB_SERVER_LABEL = I18nKey.of("connection.view.dbServer.label");
    private static final I18nKey DB_SERVER_PROMPT = I18nKey.of("connection.view.dbServer.prompt");
    private static final I18nKey DB_PORT_LABEL = I18nKey.of("connection.view.port.label");
    private static final I18nKey DB_NAME_LABEL = I18nKey.of("connection.view.databaseName.label");
    private static final I18nKey USERNAME_LABEL = I18nKey.of("connection.view.username.label");
    private static final I18nKey PASSWORD_LABEL = I18nKey.of("connection.view.password.label");

    private final Collection<ConnectionFormField> formFields;

    private final Supplier<Values> valuesSupplier;

    public DefaultConnectionPropertiesForm() {
        val url = ConnectionFormField.builder()
                .title(TranslatableText.of(DB_SERVER_LABEL))
                .prompt(TranslatableText.of(DB_SERVER_PROMPT))
                .prefWidth(FORM_FIELD_WITH - PORT_FIELD_WITH - 10)
                .build();

        val port = ConnectionFormField.builder()
                .title(TranslatableText.of(DB_PORT_LABEL))
                .prefWidth(PORT_FIELD_WITH)
                .build();

        val urlAndPortHBox = new HBox(url, port);
        HBox.setMargin(url, new Insets(0, 10, 0, 0));

        val dbName = ConnectionFormField.builder()
                .title(TranslatableText.of(DB_NAME_LABEL))
                .prefWidth(FORM_FIELD_WITH)
                .build();

        val username = ConnectionFormField.builder()
                .title(TranslatableText.of(USERNAME_LABEL))
                .prefWidth(FORM_FIELD_WITH)
                .build();

        val password = ConnectionFormField.builder()
                .title(TranslatableText.of(PASSWORD_LABEL))
                .prefWidth(FORM_FIELD_WITH)
                .build();

        HBox.setMargin(urlAndPortHBox, new Insets(25));
        HBox.setMargin(username, new Insets(25));
        val firstLineHBox = new HBox(urlAndPortHBox, username);

        HBox.setMargin(dbName, new Insets(25));
        HBox.setMargin(password, new Insets(25));
        val secondLineHBox = new HBox(dbName, password);

        this.getChildren().addAll(firstLineHBox, secondLineHBox);

        formFields = Arrays.asList(
                url,
                port,
                dbName,
                username,
                password
        );

        valuesSupplier = () -> Values.builder()
                .url(url.getValue())
                .port(port.getValue())
                .dbName(dbName.getValue())
                .username(username.getValue())
                .password(password.getValue())
                .build();
    }

    public Optional<Values> tryGetValidValues() {
        if (formFields.stream()
                .allMatch(ConnectionFormField::hasValidValue)) {
            return Optional.of(valuesSupplier.get());
        }
        return Optional.empty();
    }

    @Value
    @Builder
    public static class Values {
        @NonNull String url;
        @NonNull String port;
        @NonNull String dbName;
        @NonNull String username;
        @NonNull String password;
    }
}
