package ch.admin.bar.siardsuite.presenter.connection;

import ch.admin.bar.siardsuite.database.model.DbmsConnectionData;
import ch.admin.bar.siardsuite.database.model.FileBasedDbms;
import ch.admin.bar.siardsuite.database.model.FileBasedDbmsConnectionProperties;
import ch.admin.bar.siardsuite.presenter.connection.fields.FileChooserFormField;
import ch.admin.bar.siardsuite.presenter.connection.fields.StringFormField;
import ch.admin.bar.siardsuite.util.Validator;
import ch.admin.bar.siardsuite.util.i18n.DisplayableText;
import ch.admin.bar.siardsuite.util.i18n.TranslatableText;
import ch.admin.bar.siardsuite.util.i18n.keys.I18nKey;
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

    public FileBasedDbmsConnectionPropertiesForm(
            final FileBasedDbms dbms,
            final Optional<FileBasedDbmsConnectionProperties> initialValue
    ) {
        this.dbms = dbms;

        val fileChooserField = FileChooserFormField.builder()
                .title(TranslatableText.of(MS_ACCESS_FILE_TITLE))
                .fileChooserTitle(TranslatableText.of(MS_ACCESS_FILE_CHOOSER_TITLE))
                .initialValue(initialValue.map(FileBasedDbmsConnectionProperties::getFile)
                        .orElse(null)) // FIXME
                .prompt(DisplayableText.of(dbms.getExampleFile().getAbsolutePath()))
                .fileChooserExtensionFilter(new FileChooser.ExtensionFilter("Microsoft Access Files", "*.mdb", "*.accdb"))
                .validator(Validator.IS_EXISTING_FILE_VALIDATOR)
                .build();
        VBox.setMargin(fileChooserField, new Insets(25));

        val jdbcUrl = StringFormField.builder()
                .title(TranslatableText.of(JDBC_URL_LABEL))
                .initialValue(initialValue
                        .map(dbms.getJdbcConnectionStringEncoder())
                        .orElse(""))
                .validator(Validator.IS_NOT_EMPTY_STRING_VALIDATOR)
                .build();
        VBox.setMargin(jdbcUrl, new Insets(25));

        this.getChildren().addAll(fileChooserField, jdbcUrl);

        formFields.add(fileChooserField);
        formFields.add(jdbcUrl);

        connectionPropertiesSupplier = () -> new FileBasedDbmsConnectionProperties(fileChooserField.getValue());
    }

    @Override
    public Optional<DbmsConnectionData> tryGetValidConnectionData() {
        if (isValid()) {
            return Optional.of(new DbmsConnectionData(dbms, connectionPropertiesSupplier.get()));
        }

        return Optional.empty();
    }
}
