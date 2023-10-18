package ch.admin.bar.siardsuite.component.rendered;

import ch.admin.bar.siard2.api.MetaAttribute;
import ch.admin.bar.siard2.api.MetaType;
import ch.admin.bar.siardsuite.component.rendered.utils.Converter;
import ch.admin.bar.siardsuite.component.rendering.model.ReadOnlyStringProperty;
import ch.admin.bar.siardsuite.component.rendering.model.ReadWriteStringProperty;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableForm;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableFormGroup;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableTable;
import ch.admin.bar.siardsuite.util.i18n.keys.I18nKey;
import lombok.NonNull;

import java.util.List;

import static ch.admin.bar.siardsuite.component.rendered.utils.Converter.cardinalityToString;

public class TypeDetailsForm {

    private static final I18nKey NAME = I18nKey.of("type.name.label");
    private static final I18nKey CATEGORY = I18nKey.of("type.category.label");
    private static final I18nKey IS_INSTANTIABLE = I18nKey.of("type.isInstantiable.label");
    private static final I18nKey IS_FINAL = I18nKey.of("type.isFinal.label");
    private static final I18nKey BASE_CATEGORY = I18nKey.of("type.base.category.label");
    private static final I18nKey DESCRIPTION = I18nKey.of("type.description.label");

    private static final I18nKey ATTRIBUTE_NAME = I18nKey.of("attribute.name");
    private static final I18nKey ATTRIBUTE_TYPE = I18nKey.of("attribute.type");
    private static final I18nKey ATTRIBUTE_CARDINALITY = I18nKey.of("attribute.cardinality");

    public static RenderableForm create(@NonNull final MetaType type, @NonNull final List<MetaAttribute> attributes) {
        return RenderableForm.<MetaType>builder()
                .dataSupplier(() -> type)
                .group(RenderableFormGroup.<MetaType>builder()
                        .property(new ReadOnlyStringProperty<>(
                                NAME,
                                MetaType::getName))
                        .property(new ReadOnlyStringProperty<>(
                                CATEGORY,
                                MetaType::getCategory))
                        .property(new ReadOnlyStringProperty<>(
                                IS_INSTANTIABLE,
                                it -> Boolean.valueOf(it.isInstantiable()).toString()))
                        .property(new ReadOnlyStringProperty<>(
                                IS_FINAL,
                                it -> Boolean.valueOf(it.isFinal()).toString()))
                        .property(new ReadOnlyStringProperty<>(
                                BASE_CATEGORY,
                                MetaType::getCategory))
                        .property(new ReadWriteStringProperty<>(
                                DESCRIPTION,
                                MetaType::getDescription,
                                MetaType::setDescription
                        ))
                        .property(RenderableTable.<MetaType, MetaAttribute>builder()
                                .dataExtractor(metaType -> attributes)
                                .property(new ReadOnlyStringProperty<>(
                                        ATTRIBUTE_NAME,
                                        MetaAttribute::getName
                                ))
                                .property(new ReadOnlyStringProperty<>(
                                        ATTRIBUTE_TYPE,
                                        MetaAttribute::getType
                                ))
                                .property(new ReadOnlyStringProperty<>(
                                        ATTRIBUTE_CARDINALITY,
                                        cardinalityToString(MetaAttribute::getCardinality)
                                ))
                                .build())
                        .build())
                .build();
    }
}
