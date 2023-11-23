package ch.admin.bar.siardsuite.presenter.archive.browser.forms;

import ch.admin.bar.siardsuite.component.rendering.model.*;
import ch.admin.bar.siardsuite.model.database.DatabaseSchema;
import ch.admin.bar.siardsuite.model.database.DatabaseTable;
import ch.admin.bar.siardsuite.util.i18n.keys.I18nKey;
import lombok.NonNull;

import static ch.admin.bar.siardsuite.presenter.archive.browser.forms.utils.Converter.intToString;
import static ch.admin.bar.siardsuite.presenter.archive.browser.forms.utils.Converter.longToString;

public class SchemaOverviewForm {

    private static final I18nKey LABEL_SCHEMA = I18nKey.of("tableContainer.labelSchema");
    private static final I18nKey LABEL_DESC_SCHEMA = I18nKey.of("tableContainer.labelDescSchema");

    private static final I18nKey ROW = I18nKey.of("tableContainer.table.header.row");
    private static final I18nKey TABLE_NAME = I18nKey.of("tableContainer.table.header.tableName");
    private static final I18nKey NUMBER_OF_COLUMNS = I18nKey.of("tableContainer.table.header.numberOfColumns");
    private static final I18nKey NUMBER_OF_ROWS = I18nKey.of("tableContainer.table.header.numberOfRows");

    public static RenderableForm<DatabaseSchema> create(@NonNull final DatabaseSchema schema) {
        return RenderableForm.<DatabaseSchema>builder()
                .dataSupplier(() -> schema)
                .afterSaveAction(DatabaseSchema::write)
                .group(RenderableFormGroup.<DatabaseSchema>builder()
                        .property(new ReadOnlyStringProperty<>(
                                LABEL_SCHEMA,
                                DatabaseSchema::getName
                        ))
                        .property(new ReadWriteStringProperty<>(
                                LABEL_DESC_SCHEMA,
                                DatabaseSchema::getDescription,
                                DatabaseSchema::setDescription
                        ))
                        .property(RenderableTable.<DatabaseSchema, DatabaseTable>builder()
                                .dataExtractor(DatabaseSchema::getTables)
                                .property(new TableColumnProperty<>(
                                        ROW,
                                        databaseTable -> (schema.getTables().indexOf(databaseTable) + 1) + ""
                                ))
                                .property(new TableColumnProperty<>(
                                        TABLE_NAME,
                                        DatabaseTable::getName
                                ))
                                .property(new TableColumnProperty<>(
                                        NUMBER_OF_COLUMNS,
                                        intToString(databaseTable -> databaseTable.getColumns().size())
                                ))
                                .property(new TableColumnProperty<>(
                                        NUMBER_OF_ROWS,
                                        longToString(DatabaseTable::getNumberOfRows)
                                ))
                                .build())
                        .build())
                .build();
    }
}
