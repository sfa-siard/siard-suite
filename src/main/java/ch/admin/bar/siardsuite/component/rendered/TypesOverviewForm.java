package ch.admin.bar.siardsuite.component.rendered;

import ch.admin.bar.siardsuite.component.rendering.model.ReadOnlyStringProperty;
import ch.admin.bar.siardsuite.component.rendering.model.ReadWriteStringProperty;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableForm;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableFormGroup;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableTable;
import ch.admin.bar.siardsuite.model.database.DatabaseSchema;
import ch.admin.bar.siardsuite.model.database.DatabaseType;
import ch.admin.bar.siardsuite.util.I18nKey;
import lombok.NonNull;

public class TypesOverviewForm {
    public static RenderableForm<DatabaseSchema> create(@NonNull final DatabaseSchema schema) {

        return RenderableForm.<DatabaseSchema>builder()
                .dataSupplier(() -> schema)
                .afterSaveAction(DatabaseSchema::write)
                .group(RenderableFormGroup.<DatabaseSchema>builder()
                        .property(new ReadOnlyStringProperty<>(
                                I18nKey.of("tableContainer.labelSchema"),
                                DatabaseSchema::name))
                        .property(new ReadWriteStringProperty<>(
                                I18nKey.of("tableContainer.labelDescSchema"),
                                DatabaseSchema::getDescription,
                                DatabaseSchema::setDescription
                        ))
                        .property(RenderableTable.<DatabaseSchema, DatabaseType>builder()
                                .dataExtractor(DatabaseSchema::getTypes)
                                .property(new ReadOnlyStringProperty<>(
                                        I18nKey.of("tableContainer.types.header.name"),
                                        DatabaseType::getName
                                ))
                                .property(new ReadOnlyStringProperty<>(
                                        I18nKey.of("tableContainer.types.header.category"),
                                        DatabaseType::getCategory
                                ))
                                .property(new ReadOnlyStringProperty<>(
                                        I18nKey.of("tableContainer.types.header.instantiable"),
                                        databaseType -> Boolean.valueOf(databaseType.isInstantiable()).toString()
                                ))
                                .property(new ReadOnlyStringProperty<>(
                                        I18nKey.of("tableContainer.types.header.final"),
                                        databaseType -> Boolean.valueOf(databaseType.isFinal()).toString()
                                ))
                                .property(new ReadOnlyStringProperty<>(
                                        I18nKey.of("tableContainer.types.header.base"),
                                        DatabaseType::getBase
                                ))
                                .property(new ReadOnlyStringProperty<>(
                                        I18nKey.of("tableContainer.types.header.description"),
                                        DatabaseType::getDescription
                                ))
                                .build())
                        .build())
                .build();
    }
}
