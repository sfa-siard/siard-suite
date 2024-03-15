package ch.admin.bar.siardsuite.presenter.connection;

import ch.admin.bar.siardsuite.service.database.model.DbmsConnectionData;
import ch.admin.bar.siardsuite.service.database.model.FileBasedDbms;
import ch.admin.bar.siardsuite.service.database.model.FileBasedDbmsConnectionProperties;
import ch.admin.bar.siardsuite.presenter.connection.fields.FileChooserFormField;
import ch.admin.bar.siardsuite.presenter.connection.fields.StringFormField;
import ch.admin.bar.siardsuite.util.Validator;
import ch.admin.bar.siardsuite.framework.i18n.DisplayableText;
import ch.admin.bar.siardsuite.framework.i18n.TranslatableText;
import ch.admin.bar.siardsuite.framework.i18n.keys.I18nKey;
import javafx.geometry.Insets;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import lombok.val;

import java.util.Optional;
import java.util.function.Supplier;

public class FileBasedDbmsConnectionPropertiesForm extends ConnectionPropertiesForm {

    private static final I18nKey MS_ACCESS_FILE_TITLE = I18nKey.of("connection.view.msaccess.title");
    private static final I18nKey MS_ACCESS_FILE_CHOOSER_TITLE = I18nKey.of("connection.view.msaccess.filechooser.title");
    private static final I18nKey JDBC_URL_LABEL = I18nKey.of("connection.view.url.label");

    private final Supplier<FileBasedDbmsConnectionProperties> connectionPropertiesSupplier;
    private final FileBasedDbms dbms;

    private final FileChooserFormField fileChooserField;
    private final StringFormField jdbcUrl;

    public FileBasedDbmsConnectionPropertiesForm(
            final FileBasedDbms dbms,
            final Optional<FileBasedDbmsConnectionProperties> initialValue
    ) {
        this.dbms = dbms;

        fileChooserField = FileChooserFormField.builder()
                .title(TranslatableText.of(MS_ACCESS_FILE_TITLE))
                .fileChooserTitle(TranslatableText.of(MS_ACCESS_FILE_CHOOSER_TITLE))
                .initialValue(initialValue.map(FileBasedDbmsConnectionProperties::getFile)
                        .orElse(null)) // FIXME
                .prompt(DisplayableText.of(dbms.getExampleFile().getAbsolutePath()))
                .fileChooserExtensionFilter(new FileChooser.ExtensionFilter("Microsoft Access Files", "*.mdb", "*.accdb"))
                .validator(Validator.IS_EXISTING_FILE_VALIDATOR)
                .onNewUserInput(newFile -> handleJdbcUrl())
                .build();
        VBox.setMargin(fileChooserField, new Insets(25));

        jdbcUrl = StringFormField.builder()
                .title(TranslatableText.of(JDBC_URL_LABEL))
                .initialValue(initialValue
                        .map(dbms.getJdbcConnectionStringEncoder())
                        .orElse(""))
                .prompt(DisplayableText.of(dbms.getJdbcConnectionStringEncoder().apply(FileBasedDbmsConnectionProperties.builder()
                        .file(dbms.getExampleFile())
                        .build())))
                .prefWidth(FORM_FIELD_WITH * 2)
                .validator(Validator.IS_NOT_EMPTY_STRING_VALIDATOR)
                .validator(validJdbcUrlValidator())
                .onNewUserInput(newValue -> {
                    try {
                        val decoded = dbms.getJdbcConnectionStringDecoder().apply(newValue);
                        fileChooserField.setValue(decoded.getFile());
                    } catch (Exception e) {
                        // should not be thrown, because of validator
                    }
                })
                .build();
        VBox.setMargin(jdbcUrl, new Insets(25));

        this.getChildren().addAll(fileChooserField, jdbcUrl);

        formFields.add(fileChooserField);
        formFields.add(jdbcUrl);

        connectionPropertiesSupplier = () -> new FileBasedDbmsConnectionProperties(fileChooserField.getValue());
    }

    @Override
    public DbmsConnectionData getConnectionData() {
        return new DbmsConnectionData(dbms, connectionPropertiesSupplier.get());
    }

    private void handleJdbcUrl() {
        if (fileChooserField.hasValidValue()) {
            val currentProperties = connectionPropertiesSupplier.get();

            val currentJdbcUrl = dbms.getJdbcConnectionStringEncoder().apply(currentProperties);
            jdbcUrl.setValue(currentJdbcUrl);
        }
    }

    private Validator<String> validJdbcUrlValidator() {
        return Validator.<String>builder()
                .message(DisplayableText.of(INVALID_JDBC_URL_MESSAGE))
                .isValidCheck(nullableValue -> Optional.ofNullable(nullableValue)
                        .filter(value -> {
                            if (!value.startsWith("jdbc:" + dbms.getId())) {
                                return false;
                            }

                            try {
                                dbms.getJdbcConnectionStringDecoder().apply(value);
                                return true;
                            } catch (Exception e) {
                                return false;
                            }
                        })
                        .isPresent())
                .build();
    }
}
