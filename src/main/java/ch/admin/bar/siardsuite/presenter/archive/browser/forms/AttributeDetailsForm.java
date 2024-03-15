package ch.admin.bar.siardsuite.presenter.archive.browser.forms;

import ch.admin.bar.siardsuite.component.rendering.model.ReadOnlyStringProperty;
import ch.admin.bar.siardsuite.component.rendering.model.ReadWriteStringProperty;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableForm;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableFormGroup;
import ch.admin.bar.siardsuite.model.database.DatabaseAttribute;
import ch.admin.bar.siardsuite.framework.i18n.keys.I18nKey;
import lombok.NonNull;

import static ch.admin.bar.siardsuite.presenter.archive.browser.forms.utils.Converter.*;

public class AttributeDetailsForm {

    private static final I18nKey NAME = I18nKey.of("attribute.name");
    private static final I18nKey POSITION = I18nKey.of("attribute.position");
    private static final I18nKey SQL_TYPE = I18nKey.of("attribute.sqlType");
    private static final I18nKey UDT_SCHEMA = I18nKey.of("attribute.udtSchema");
    private static final I18nKey UDT_NAME = I18nKey.of("attribute.udtName");
    private static final I18nKey ORIGINAL_DATA_TYPE = I18nKey.of("attribute.originalDataType");
    private static final I18nKey ALLOWS_NULL = I18nKey.of("attribute.allowsNull");
    private static final I18nKey DEFAULT_VALUE = I18nKey.of("attribute.defaultValue");
    private static final I18nKey ARRAY_CARDINALITY = I18nKey.of("attribute.arrayCardinality");
    private static final I18nKey DESCRIPTION = I18nKey.of("attribute.description");

    public static RenderableForm create(@NonNull final DatabaseAttribute attribute) {

        return RenderableForm.<DatabaseAttribute>builder()
                             .dataSupplier(() -> attribute)
                             .afterSaveAction(DatabaseAttribute::write)
                             .group(RenderableFormGroup.<DatabaseAttribute>builder()
                                                       .property(new ReadOnlyStringProperty<>(
                                                               NAME,
                                                               DatabaseAttribute::getName
                                                       ))
                                                       .property(new ReadOnlyStringProperty<>(
                                                               POSITION,
                                                               intToString(DatabaseAttribute::getPosition)
                                                       ))
                                                       .property(new ReadOnlyStringProperty<>(
                                                               SQL_TYPE,
                                                               DatabaseAttribute::getType
                                                       ))
                                                       .property(new ReadOnlyStringProperty<>(
                                                               UDT_SCHEMA,
                                                               DatabaseAttribute::getTypeSchema
                                                       ))
                                                       .property(new ReadOnlyStringProperty<>(
                                                               UDT_NAME,
                                                               DatabaseAttribute::getTypeName
                                                       ))
                                                       .property(new ReadOnlyStringProperty<>(
                                                               ORIGINAL_DATA_TYPE,
                                                               DatabaseAttribute::getTypeOriginal
                                                       ))
                                                       .property(new ReadOnlyStringProperty<>(
                                                               ALLOWS_NULL,
                                                               booleanToString(DatabaseAttribute::isNullable)
                                                       ))
                                                       .property(new ReadOnlyStringProperty<>(
                                                               DEFAULT_VALUE,
                                                               DatabaseAttribute::getDefaultValue
                                                       ))
                                                       .property(new ReadOnlyStringProperty<>(
                                                               ARRAY_CARDINALITY,
                                                               cardinalityToString(DatabaseAttribute::getCardinality)
                                                       ))
                                                       .property(new ReadWriteStringProperty<>(
                                                               DESCRIPTION,
                                                               DatabaseAttribute::getDescription,
                                                               DatabaseAttribute::setDescription
                                                       ))
                                                       .build())
                             .build();
    }
}
