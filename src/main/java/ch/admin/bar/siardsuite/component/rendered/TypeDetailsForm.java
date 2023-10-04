package ch.admin.bar.siardsuite.component.rendered;

import ch.admin.bar.siardsuite.component.rendering.model.ReadOnlyStringProperty;
import ch.admin.bar.siardsuite.component.rendering.model.ReadWriteStringProperty;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableForm;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableFormGroup;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableTable;
import ch.admin.bar.siardsuite.model.database.DatabaseAttribute;
import ch.admin.bar.siardsuite.model.database.DatabaseType;
import ch.admin.bar.siardsuite.util.I18nKey;

public class TypeDetailsForm {
    public static RenderableForm create(final DatabaseType type) {
        return RenderableForm.<DatabaseType>builder()
                .dataExtractor(controller -> type)
                .saveAction((controller, edited) -> edited.write())
                .group(RenderableFormGroup.<DatabaseType>builder()
                        .property(new ReadOnlyStringProperty<>(
                                I18nKey.of("type.name.label"),
                                DatabaseType::getName))
                        .property(new ReadOnlyStringProperty<>(
                                I18nKey.of("type.category.label"),
                                DatabaseType::getCategory))
                        .property(new ReadOnlyStringProperty<>(
                                I18nKey.of("type.isInstantiable.label"),
                                it -> Boolean.valueOf(it.isInstantiable()).toString()))
                        .property(new ReadOnlyStringProperty<>(
                                I18nKey.of("type.isFinal.label"),
                                it -> Boolean.valueOf(it.isFinal()).toString()))
                        .property(new ReadOnlyStringProperty<>(
                                I18nKey.of("type.base.category.label"),
                                DatabaseType::getCategory))
                        .property(new ReadWriteStringProperty<>(
                                I18nKey.of("type.description.label"),
                                DatabaseType::getDescription,
                                DatabaseType::setDescription
                        ))
                        .property(RenderableTable.<DatabaseType, DatabaseAttribute>builder()
                                .dataExtractor(DatabaseType::getDatabaseAttributes)
                                .property(new ReadOnlyStringProperty<>(
                                        I18nKey.of("attribute.name"),
                                        DatabaseAttribute::name
                                ))
                                .property(new ReadOnlyStringProperty<>(
                                        I18nKey.of("attribute.type"),
                                        DatabaseAttribute::type
                                ))
                                .property(new ReadOnlyStringProperty<>(
                                        I18nKey.of("attribute.cardinality"),
                                        DatabaseAttribute::cardinality
                                ))
                                .build())
                        .build())
                .build();
    }
}
