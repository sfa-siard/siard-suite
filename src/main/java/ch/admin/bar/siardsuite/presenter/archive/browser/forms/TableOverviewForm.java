package ch.admin.bar.siardsuite.presenter.archive.browser.forms;

import ch.admin.bar.siardsuite.component.rendering.model.ReadOnlyStringProperty;
import ch.admin.bar.siardsuite.component.rendering.model.ReadWriteStringProperty;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableForm;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableFormGroup;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableTable;
import ch.admin.bar.siardsuite.component.rendering.model.TableColumnProperty;
import ch.admin.bar.siardsuite.model.database.DatabaseColumn;
import ch.admin.bar.siardsuite.model.database.DatabaseTable;
import ch.admin.bar.siardsuite.util.i18n.keys.I18nKey;
import lombok.NonNull;

import static ch.admin.bar.siardsuite.presenter.archive.browser.forms.utils.Converter.intToString;
import static ch.admin.bar.siardsuite.presenter.archive.browser.forms.utils.Converter.longToString;

public class TableOverviewForm {

    private static final I18nKey LABEL_TABLE = I18nKey.of("tableContainer.labelTable");
    private static final I18nKey LABEL_NUMBER_OF_ROWS = I18nKey.of("tableContainer.labelNumberOfRows");
    private static final I18nKey LABEL_DESC_TABLE = I18nKey.of("tableContainer.labelDescTable");

    private static final I18nKey POSITION = I18nKey.of("tableContainer.table.header.position");
    private static final I18nKey COLUMN_NAME = I18nKey.of("tableContainer.table.header.columnName");
    private static final I18nKey COLUMN_TYPE = I18nKey.of("tableContainer.table.header.columnType");

    public static RenderableForm<DatabaseTable> create(@NonNull final DatabaseTable table) {
        return RenderableForm.<DatabaseTable>builder()
                .dataSupplier(() -> table)
                .afterSaveAction(DatabaseTable::write)
                .group(RenderableFormGroup.<DatabaseTable>builder()
                        .property(new ReadOnlyStringProperty<>(
                                LABEL_TABLE,
                                DatabaseTable::getName
                        ))
                        .property(new ReadOnlyStringProperty<>(
                                LABEL_NUMBER_OF_ROWS,
                                longToString(DatabaseTable::getNumberOfRows)
                        ))
                        .property(new ReadWriteStringProperty<>(
                                LABEL_DESC_TABLE,
                                DatabaseTable::getDescription,
                                DatabaseTable::setDescription
                        ))
                        .property(RenderableTable.<DatabaseTable, DatabaseColumn>builder()
                                .dataExtractor(DatabaseTable::getColumns)
                                .property(new TableColumnProperty<>(
                                        POSITION,
                                        intToString(DatabaseColumn::getIndex)
                                ))
                                .property(new TableColumnProperty<>(
                                        COLUMN_NAME,
                                        DatabaseColumn::getName
                                ))
                                .property(new TableColumnProperty<>(
                                        COLUMN_TYPE,
                                        DatabaseColumn::getType
                                ))
                                .build())
                        .build())
                .build();
    }
}
