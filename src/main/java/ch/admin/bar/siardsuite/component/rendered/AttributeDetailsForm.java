package ch.admin.bar.siardsuite.component.rendered;

import ch.admin.bar.siardsuite.component.rendering.model.ReadOnlyStringProperty;
import ch.admin.bar.siardsuite.component.rendering.model.ReadWriteStringProperty;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableForm;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableFormGroup;
import ch.admin.bar.siardsuite.model.database.DatabaseAttribute;
import ch.admin.bar.siardsuite.util.I18nKey;
import lombok.NonNull;

public class AttributeDetailsForm {
    public static RenderableForm create(@NonNull final DatabaseAttribute attribute) {

        return RenderableForm.<DatabaseAttribute>builder()
                .dataSupplier(() -> attribute)
                .afterSaveAction(DatabaseAttribute::write)
                .group(RenderableFormGroup.<DatabaseAttribute>builder()
                        .property(new ReadOnlyStringProperty<>(
                                I18nKey.of("attribute.name"),
                                DatabaseAttribute::getName))
                        .property(new ReadOnlyStringProperty<>(
                                I18nKey.of("attribute.position"),
                                it -> String.valueOf(it.getPosition())))
                        .property(new ReadOnlyStringProperty<>(
                                I18nKey.of("attribute.sqlType"),
                                DatabaseAttribute::getType))
                        .property(new ReadOnlyStringProperty<>(
                                I18nKey.of("attribute.udtSchema"),
                                DatabaseAttribute::getTypeSchema))
                        .property(new ReadOnlyStringProperty<>(
                                I18nKey.of("attribute.udtName"),
                                DatabaseAttribute::getTypeName))
                        .property(new ReadOnlyStringProperty<>(
                                I18nKey.of("attribute.originalDataType"),
                                DatabaseAttribute::getTypeOriginal))
                        .property(new ReadOnlyStringProperty<>(
                                I18nKey.of("attribute.allowsNull"),
                                it -> String.valueOf(it.isNullable())))
                        .property(new ReadOnlyStringProperty<>(
                                I18nKey.of("attribute.defaultValue"),
                                DatabaseAttribute::getDefaultValue))
                        .property(new ReadOnlyStringProperty<>(
                                I18nKey.of("attribute.arrayCardinality"),
                                it -> String.valueOf(it.getCardinality())))
                        .property(new ReadWriteStringProperty<>(
                                I18nKey.of("attribute.description"),
                                DatabaseAttribute::getDescription,
                                DatabaseAttribute::setDescription
                        ))
                        .build())
                .build();
    }
}
