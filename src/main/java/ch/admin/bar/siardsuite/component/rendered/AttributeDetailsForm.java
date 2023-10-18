package ch.admin.bar.siardsuite.component.rendered;

import ch.admin.bar.siard2.api.MetaAttribute;
import ch.admin.bar.siardsuite.component.rendered.utils.Converter;
import ch.admin.bar.siardsuite.component.rendering.model.ReadOnlyStringProperty;
import ch.admin.bar.siardsuite.component.rendering.model.ReadWriteStringProperty;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableForm;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableFormGroup;
import ch.admin.bar.siardsuite.util.i18n.keys.I18nKey;
import lombok.NonNull;

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

    public static RenderableForm create(@NonNull final MetaAttribute attribute) {

        return RenderableForm.<MetaAttribute>builder()
                .dataSupplier(() -> attribute)
                .group(RenderableFormGroup.<MetaAttribute>builder()
                        .property(new ReadOnlyStringProperty<>(
                                NAME,
                                MetaAttribute::getName))
                        .property(new ReadOnlyStringProperty<>(
                                POSITION,
                                it -> String.valueOf(it.getPosition())
                        ))
                        .property(new ReadOnlyStringProperty<>(
                                SQL_TYPE,
                                MetaAttribute::getType
                        ))
                        .property(new ReadOnlyStringProperty<>(
                                UDT_SCHEMA,
                                MetaAttribute::getTypeSchema
                        ))
                        .property(new ReadOnlyStringProperty<>(
                                UDT_NAME,
                                MetaAttribute::getTypeName
                        ))
                        .property(new ReadOnlyStringProperty<>(
                                ORIGINAL_DATA_TYPE,
                                MetaAttribute::getTypeOriginal))
                        .property(new ReadOnlyStringProperty<>(
                                ALLOWS_NULL,
                                it -> String.valueOf(it.isNullable())
                        ))
                        .property(new ReadOnlyStringProperty<>(
                                DEFAULT_VALUE,
                                MetaAttribute::getDefaultValue
                        ))
                        .property(new ReadOnlyStringProperty<>(
                                ARRAY_CARDINALITY,
                                Converter.cardinalityToString(MetaAttribute::getCardinality)
                        ))
                        .property(new ReadWriteStringProperty<>(
                                DESCRIPTION,
                                MetaAttribute::getDescription,
                                MetaAttribute::setDescription
                        ))
                        .build())
                .build();
    }
}
