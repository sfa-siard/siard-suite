package ch.admin.bar.siardsuite.ui.presenter.archive.browser.forms;

import ch.admin.bar.siardsuite.ui.component.rendering.model.ReadOnlyStringProperty;
import ch.admin.bar.siardsuite.ui.component.rendering.model.ReadWriteStringProperty;
import ch.admin.bar.siardsuite.ui.component.rendering.model.RenderableForm;
import ch.admin.bar.siardsuite.ui.component.rendering.model.RenderableFormGroup;
import ch.admin.bar.siardsuite.model.database.DatabaseColumn;
import ch.admin.bar.siardsuite.ui.presenter.archive.browser.forms.utils.Converter;
import ch.admin.bar.siardsuite.ui.common.Validator;
import ch.admin.bar.siardsuite.framework.i18n.DisplayableText;
import ch.admin.bar.siardsuite.framework.i18n.keys.I18nKey;
import lombok.NonNull;

import java.net.URI;
import java.net.URISyntaxException;

import static ch.admin.bar.siardsuite.ui.presenter.archive.browser.forms.utils.Converter.catchExceptions;

public class ColumnDetailsForm {

    private static final I18nKey NAME = I18nKey.of("columnDetails.name");
    private static final I18nKey POSITION = I18nKey.of("columnDetails.position");
    private static final I18nKey LOB = I18nKey.of("columnDetails.lob");
    private static final I18nKey MIME = I18nKey.of("columnDetails.mime");
    private static final I18nKey SQL = I18nKey.of("columnDetails.sql");
    private static final I18nKey UDT_SCHEMA = I18nKey.of("columnDetails.udtSchema");
    private static final I18nKey UDT_NAME = I18nKey.of("columnDetails.udtName");
    private static final I18nKey DATA_TYPE = I18nKey.of("columnDetails.dataType");
    private static final I18nKey NULLABLE = I18nKey.of("columnDetails.nullable");
    private static final I18nKey DEFAULT_VALUE = I18nKey.of("columnDetails.defaultValue");
    private static final I18nKey CARDINALITY = I18nKey.of("columnDetails.cardinality");
    private static final I18nKey DESCRIPTION = I18nKey.of("columnDetails.description");

    private static final I18nKey NOT_A_VALID_LOCATION = I18nKey.of("valueValidation.notAValidLocation");

    public static final Validator<String> IS_VALID_PATH_TO_LOB_FOLDER_VALIDATOR = Validator.<String>builder()
            .message(DisplayableText.of(NOT_A_VALID_LOCATION))
            .isValidCheck(nullableValue -> {
                if (nullableValue == null || nullableValue.isEmpty()) {
                    return true;
                }

                try {
                    new URI(nullableValue);
                    return true;
                } catch (URISyntaxException e) {
                    return false;
                }
            })
            .build();

    public static RenderableForm<DatabaseColumn> create(@NonNull final DatabaseColumn column) {
        return RenderableForm.<DatabaseColumn>builder()
                .dataSupplier(() -> column)
                .afterSaveAction(DatabaseColumn::write)
                .group(RenderableFormGroup.<DatabaseColumn>builder()
                        .property(new ReadOnlyStringProperty<>(
                                NAME,
                                DatabaseColumn::getName
                        ))
                        .property(new ReadOnlyStringProperty<>(
                                POSITION,
                                Converter.intToString(DatabaseColumn::getIndex)
                        ))
                        .property(new ReadOnlyStringProperty<>(
                                LOB,
                                DatabaseColumn::getLobFolder
                        ))
                        .property(new ReadWriteStringProperty<>(
                                MIME,
                                DatabaseColumn::getMimeType,
                                DatabaseColumn::setMimeType
                        ))
                        .property(new ReadOnlyStringProperty<>(
                                SQL,
                                Converter.catchExceptions(DatabaseColumn::getType)
                        ))
                        .property(new ReadOnlyStringProperty<>(
                                UDT_SCHEMA,
                                DatabaseColumn::getUserDefinedTypeSchema
                        ))
                        .property(new ReadOnlyStringProperty<>(
                                UDT_NAME,
                                DatabaseColumn::getUserDefinedTypeName
                        ))
                        .property(new ReadOnlyStringProperty<>(
                                DATA_TYPE,
                                DatabaseColumn::getOriginalType
                        ))
                        .property(new ReadOnlyStringProperty<>(
                                NULLABLE,
                                Converter.booleanToString(DatabaseColumn::isNullable)
                        ))
                        .property(new ReadOnlyStringProperty<>(
                                DEFAULT_VALUE,
                                DatabaseColumn::getDefaultValue
                        ))
                        .property(new ReadOnlyStringProperty<>(
                                CARDINALITY,
                                Converter.cardinalityToString(DatabaseColumn::getCardinality)
                        ))
                        .property(new ReadWriteStringProperty<>(
                                DESCRIPTION,
                                DatabaseColumn::getDescription,
                                DatabaseColumn::setDescription
                        ))
                        .build())
                .build();
    }
}
