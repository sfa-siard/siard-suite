package ch.admin.bar.siardsuite.presenter.connection;

import ch.admin.bar.siardsuite.component.IconButton;
import ch.admin.bar.siardsuite.database.model.DbmsConnectionData;
import ch.admin.bar.siardsuite.database.model.ServerBasedDbms;
import ch.admin.bar.siardsuite.database.model.ServerBasedDbmsConnectionProperties;
import ch.admin.bar.siardsuite.presenter.connection.fields.StringFormField;
import ch.admin.bar.siardsuite.util.Validator;
import ch.admin.bar.siardsuite.util.i18n.DisplayableText;
import ch.admin.bar.siardsuite.util.i18n.TranslatableText;
import ch.admin.bar.siardsuite.util.i18n.keys.I18nKey;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import lombok.NonNull;
import lombok.val;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ServerBasedDbmsConnectionPropertiesForm extends ConnectionPropertiesForm {

    private static final double FORM_FIELD_WITH = 578D;
    private static final double PORT_FIELD_WITH = 120D;

    private static final I18nKey LEFT_INFO_TITLE = I18nKey.of("connection.view.subtitleLeft");
    private static final I18nKey LEFT_INFO_TEXT = I18nKey.of("connection.view.textLeft");
    private static final I18nKey RIGHT_INFO_TITLE = I18nKey.of("connection.view.subtitleRight");
    private static final I18nKey RIGHT_INFO_TEXT = I18nKey.of("connection.view.textRight");

    private static final I18nKey DB_SERVER_LABEL = I18nKey.of("connection.view.dbServer.label");
    private static final I18nKey DB_PORT_LABEL = I18nKey.of("connection.view.port.label");
    private static final I18nKey DB_NAME_LABEL = I18nKey.of("connection.view.databaseName.label");
    private static final I18nKey USERNAME_LABEL = I18nKey.of("connection.view.username.label");
    private static final I18nKey PASSWORD_LABEL = I18nKey.of("connection.view.password.label");

    private static final I18nKey JDBC_URL_LABEL = I18nKey.of("connection.view.url.label");

    private final Supplier<ServerBasedDbmsConnectionProperties> connectionPropertiesSupplier;
    private final ServerBasedDbms serverBasedDbms;

    public ServerBasedDbmsConnectionPropertiesForm(
            @NonNull final ServerBasedDbms dbms,
            @NonNull final Optional<ServerBasedDbmsConnectionProperties> initialValue
    ) {
        this.serverBasedDbms = dbms;

        val leftInfo = createInfo(DisplayableText.of(LEFT_INFO_TITLE), DisplayableText.of(LEFT_INFO_TEXT), IconButton.Icon.SERVER);
        val rightInfo = createInfo(DisplayableText.of(RIGHT_INFO_TITLE), DisplayableText.of(RIGHT_INFO_TEXT), IconButton.Icon.USER);
        HBox.setMargin(leftInfo, new Insets(25));
        HBox.setMargin(rightInfo, new Insets(25));

        val infos = new HBox(leftInfo, rightInfo);

        val host = StringFormField.builder()
                .title(TranslatableText.of(DB_SERVER_LABEL))
                .initialValue(initialValue
                        .map(ServerBasedDbmsConnectionProperties::getHost)
                        .orElse(""))
                .prompt(DisplayableText.of(dbms.getExampleHost()))
                .prefWidth(FORM_FIELD_WITH - PORT_FIELD_WITH - 10)
                .validator(Validator.IS_NOT_EMPTY_STRING_VALIDATOR)
                .build();

        val port = StringFormField.builder()
                .title(TranslatableText.of(DB_PORT_LABEL))
                .initialValue(initialValue
                        .map(ServerBasedDbmsConnectionProperties::getPort)
                        .orElse(dbms.getExamplePort()))
                .prompt(DisplayableText.of(dbms.getExamplePort()))
                .prefWidth(PORT_FIELD_WITH)
                .validator(Validator.IS_NOT_EMPTY_STRING_VALIDATOR)
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
                .validator(Validator.IS_NOT_EMPTY_STRING_VALIDATOR)
                .build();

        val username = StringFormField.builder()
                .title(TranslatableText.of(USERNAME_LABEL))
                .hint(TranslatableText.of(RIGHT_INFO_TEXT))
                .initialValue(initialValue
                        .map(ServerBasedDbmsConnectionProperties::getUser)
                        .orElse(""))
                .prefWidth(FORM_FIELD_WITH)
                .validator(Validator.IS_NOT_EMPTY_STRING_VALIDATOR)
                .build();

        val password = StringFormField.builder()
                .title(TranslatableText.of(PASSWORD_LABEL))
                .initialValue(initialValue
                        .map(ServerBasedDbmsConnectionProperties::getPassword)
                        .orElse(""))
                .prefWidth(FORM_FIELD_WITH)
                .validator(Validator.IS_NOT_EMPTY_STRING_VALIDATOR)
                .build();

        HBox.setMargin(urlAndPortHBox, new Insets(25));
        HBox.setMargin(username, new Insets(25));
        val firstLineHBox = new HBox(urlAndPortHBox, username);

        HBox.setMargin(dbName, new Insets(25));
        HBox.setMargin(password, new Insets(25));
        val secondLineHBox = new HBox(dbName, password);

        val jdbcUrl = StringFormField.builder()
                .title(TranslatableText.of(JDBC_URL_LABEL))
                .initialValue(initialValue
                        .map(dbms.getJdbcConnectionStringEncoder())
                        .orElse(""))
                .prefWidth(FORM_FIELD_WITH * 2)
                .validator(Validator.IS_NOT_EMPTY_STRING_VALIDATOR)
                .build();
        HBox.setMargin(jdbcUrl, new Insets(25));
        val thirdLineHBox = new HBox(jdbcUrl);

        this.getChildren().addAll(
                infos,
                firstLineHBox,
                secondLineHBox,
                thirdLineHBox
        );

        formFields.add(host);
        formFields.add(port);
        formFields.add(dbName);
        formFields.add(username);
        formFields.add(password);
        formFields.add(jdbcUrl);

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
        if (isValid()) {
            return Optional.of(new DbmsConnectionData(serverBasedDbms, connectionPropertiesSupplier.get()));
        }

        return Optional.empty();

    }

    private static VBox createInfo(
            final DisplayableText title,
            final DisplayableText text,
            final IconButton.Icon icon
    ) {
        val titleNode = new Label();
        titleNode.getStyleClass().addAll("h3", "label-icon");
        titleNode.textProperty().bind(title.bindable());
        titleNode.setGraphic(icon.toImageView());

        val textNode = new Text();
        textNode.getStyleClass().addAll("view-text");
        textNode.textProperty().bind(text.bindable());

        val box = new VBox(titleNode, textNode);
        box.setPrefWidth(FORM_FIELD_WITH);

        return box;
    }
}
