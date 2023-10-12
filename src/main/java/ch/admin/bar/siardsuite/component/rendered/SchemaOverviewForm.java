package ch.admin.bar.siardsuite.component.rendered;

import ch.admin.bar.siardsuite.component.rendering.model.ReadOnlyStringProperty;
import ch.admin.bar.siardsuite.component.rendering.model.ReadWriteStringProperty;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableForm;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableFormGroup;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableTable;
import ch.admin.bar.siardsuite.model.database.DatabaseSchema;
import ch.admin.bar.siardsuite.model.database.DatabaseTable;
import ch.admin.bar.siardsuite.util.I18nKey;
import lombok.NonNull;

public class SchemaOverviewForm {
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
                        .property(RenderableTable.<DatabaseSchema, DatabaseTable>builder()
                                .dataExtractor(DatabaseSchema::getTables)
                                .property(new ReadOnlyStringProperty<>(
                                        I18nKey.of("tableContainer.table.header.row"),
                                        databaseTable -> (schema.getTables().indexOf(databaseTable) + 1) + ""
                                ))
                                .property(new ReadOnlyStringProperty<>(
                                        I18nKey.of("tableContainer.table.header.tableName"),
                                        DatabaseTable::getName
                                ))
                                .property(new ReadOnlyStringProperty<>(
                                        I18nKey.of("tableContainer.table.header.numberOfColumns"),
                                        databaseTable -> databaseTable.getColumns().size() + ""
                                ))
                                .property(new ReadOnlyStringProperty<>(
                                        I18nKey.of("tableContainer.table.header.numberOfRows"),
                                        databaseTable -> databaseTable.getRows().size() + ""
                                ))
                                .build())
                        .build())
                .build();
    }
}
