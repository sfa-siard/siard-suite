package ch.admin.bar.siardsuite.component.rendered;

import ch.admin.bar.siardsuite.component.rendering.model.ReadOnlyStringProperty;
import ch.admin.bar.siardsuite.component.rendering.model.ReadWriteStringProperty;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableForm;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableFormGroup;
import ch.admin.bar.siardsuite.model.database.DatabaseColumn;
import ch.admin.bar.siardsuite.util.I18nKey;
import lombok.NonNull;

import java.net.URI;
import java.net.URISyntaxException;

public class ColumnDetailsForm {
    public static final ReadWriteStringProperty.Validator IS_VALID_PATH_TO_LOB_FOLDER_VALIDATOR = ReadWriteStringProperty.Validator.builder()
            .message(I18nKey.of("valueValidation.notAValidLocation"))
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
                                I18nKey.of("columnDetails.name"),
                                DatabaseColumn::getName))
                        .property(new ReadOnlyStringProperty<>(
                                I18nKey.of("columnDetails.position"),
                                DatabaseColumn::getIndex))
                        .property(new ReadWriteStringProperty<>(
                                I18nKey.of("columnDetails.lob"),
                                DatabaseColumn::getLobFolder,
                                DatabaseColumn::setLobFolder,
                                IS_VALID_PATH_TO_LOB_FOLDER_VALIDATOR
                        ))
                        .property(new ReadWriteStringProperty<>(
                                I18nKey.of("columnDetails.mime"),
                                DatabaseColumn::getMimeType,
                                DatabaseColumn::setMimeType
                        ))
                        .property(new ReadOnlyStringProperty<>(
                                I18nKey.of("columnDetails.sql"),
                                DatabaseColumn::getType))
                        .property(new ReadOnlyStringProperty<>(
                                I18nKey.of("columnDetails.udtSchema"),
                                DatabaseColumn::getUserDefinedTypeSchema))
                        .property(new ReadOnlyStringProperty<>(
                                I18nKey.of("columnDetails.udtName"),
                                DatabaseColumn::getUserDefinedTypeName))
                        .property(new ReadOnlyStringProperty<>(
                                I18nKey.of("columnDetails.dataType"),
                                DatabaseColumn::getOriginalType))
                        .property(new ReadOnlyStringProperty<>(
                                I18nKey.of("columnDetails.nullable"),
                                DatabaseColumn::getIsNullable))
                        .property(new ReadOnlyStringProperty<>(
                                I18nKey.of("columnDetails.defaultValue"),
                                DatabaseColumn::getDefaultValue))
                        .property(new ReadOnlyStringProperty<>(
                                I18nKey.of("columnDetails.cardinality"),
                                DatabaseColumn::getCardinality))
                        .property(new ReadWriteStringProperty<>(
                                I18nKey.of("columnDetails.description"),
                                DatabaseColumn::getDescription,
                                DatabaseColumn::setDescription))
                        .build())
                .build();
    }
}
