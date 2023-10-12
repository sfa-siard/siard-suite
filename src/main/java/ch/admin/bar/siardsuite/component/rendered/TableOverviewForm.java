package ch.admin.bar.siardsuite.component.rendered;

import ch.admin.bar.siardsuite.component.rendering.model.ReadOnlyStringProperty;
import ch.admin.bar.siardsuite.component.rendering.model.ReadWriteStringProperty;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableForm;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableFormGroup;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableTable;
import ch.admin.bar.siardsuite.model.database.DatabaseColumn;
import ch.admin.bar.siardsuite.model.database.DatabaseTable;
import ch.admin.bar.siardsuite.util.I18nKey;
import lombok.NonNull;

public class TableOverviewForm {
    public static RenderableForm<DatabaseTable> create(@NonNull final DatabaseTable table) {
        return RenderableForm.<DatabaseTable>builder()
                .dataSupplier(() -> table)
                .afterSaveAction(DatabaseTable::write)
                .group(RenderableFormGroup.<DatabaseTable>builder()
                        .property(new ReadOnlyStringProperty<>(
                                I18nKey.of("tableContainer.labelTable"),
                                DatabaseTable::name))
                        .property(new ReadOnlyStringProperty<>(
                                I18nKey.of("tableContainer.labelNumberOfRows"),
                                DatabaseTable::getNumberOfRows))
                        .property(new ReadWriteStringProperty<>(
                                I18nKey.of("tableContainer.labelDescTable"),
                                DatabaseTable::getDescription,
                                DatabaseTable::setDescription
                        ))
                        .property(RenderableTable.<DatabaseTable, DatabaseColumn>builder()
                                .dataExtractor(DatabaseTable::getColumns)
                                .property(new ReadOnlyStringProperty<>(
                                        I18nKey.of("tableContainer.table.header.position"),
                                        DatabaseColumn::index
                                ))
                                .property(new ReadOnlyStringProperty<>(
                                        I18nKey.of("tableContainer.table.header.columnName"),
                                        DatabaseColumn::name
                                ))
                                .property(new ReadOnlyStringProperty<>(
                                        I18nKey.of("tableContainer.table.header.columnType"),
                                        DatabaseColumn::type
                                ))
                                .build())
                        .build())
                .build();
    }
}
