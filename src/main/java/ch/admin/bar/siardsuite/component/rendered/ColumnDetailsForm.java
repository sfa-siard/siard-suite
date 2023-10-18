package ch.admin.bar.siardsuite.component.rendered;

import ch.admin.bar.siard2.api.MetaColumn;
import ch.admin.bar.siardsuite.component.rendered.utils.Converter;
import ch.admin.bar.siardsuite.component.rendering.model.ReadOnlyStringProperty;
import ch.admin.bar.siardsuite.component.rendering.model.ReadWriteStringProperty;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableForm;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableFormGroup;
import ch.admin.bar.siardsuite.util.i18n.DisplayableText;
import ch.admin.bar.siardsuite.util.i18n.keys.I18nKey;
import lombok.NonNull;

import java.net.URI;
import java.net.URISyntaxException;

import static ch.admin.bar.siardsuite.component.rendered.utils.Converter.booleanToString;
import static ch.admin.bar.siardsuite.component.rendered.utils.Converter.cardinalityToString;
import static ch.admin.bar.siardsuite.component.rendered.utils.Converter.catchExceptions;
import static ch.admin.bar.siardsuite.component.rendered.utils.Converter.intToString;
import static ch.admin.bar.siardsuite.component.rendered.utils.Converter.uriToString;

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

    public static final ReadWriteStringProperty.Validator IS_VALID_PATH_TO_LOB_FOLDER_VALIDATOR = ReadWriteStringProperty.Validator.builder()
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

    public static RenderableForm create(@NonNull final MetaColumn column) {
        return RenderableForm.<MetaColumn>builder()
                .dataSupplier(() -> column)
                .group(RenderableFormGroup.<MetaColumn>builder()
                        .property(new ReadOnlyStringProperty<>(
                                NAME,
                                MetaColumn::getName))
                        .property(new ReadOnlyStringProperty<>(
                                POSITION,
                                intToString(MetaColumn::getPosition)))
                        .property(new ReadWriteStringProperty<>(
                                LOB,
                                uriToString(MetaColumn::getLobFolder),
                                (valueHolder, value) -> valueHolder.setLobFolder(new URI(value)),
                                IS_VALID_PATH_TO_LOB_FOLDER_VALIDATOR
                        ))
                        .property(new ReadWriteStringProperty<>(
                                MIME,
                                MetaColumn::getMimeType,
                                MetaColumn::setMimeType
                        ))
                        .property(new ReadOnlyStringProperty<>(
                                SQL,
                                catchExceptions(MetaColumn::getType)))
                        .property(new ReadOnlyStringProperty<>(
                                UDT_SCHEMA,
                                MetaColumn::getTypeSchema))
                        .property(new ReadOnlyStringProperty<>(
                                UDT_NAME,
                                MetaColumn::getTypeName))
                        .property(new ReadOnlyStringProperty<>(
                                DATA_TYPE,
                                MetaColumn::getTypeOriginal))
                        .property(new ReadOnlyStringProperty<>(
                                NULLABLE,
                                booleanToString(MetaColumn::isNullable)))
                        .property(new ReadOnlyStringProperty<>(
                                DEFAULT_VALUE,
                                MetaColumn::getDefaultValue))
                        .property(new ReadOnlyStringProperty<>(
                                CARDINALITY,
                                cardinalityToString(MetaColumn::getCardinality)))
                        .property(new ReadWriteStringProperty<>(
                                DESCRIPTION,
                                MetaColumn::getDescription,
                                MetaColumn::setDescription))
                        .build())
                .build();
    }
}
