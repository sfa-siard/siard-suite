package ch.admin.bar.siardsuite.presenter.archive.browser.forms;

import ch.admin.bar.siardsuite.component.rendering.model.ReadOnlyStringProperty;
import ch.admin.bar.siardsuite.component.rendering.model.ReadWriteStringProperty;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableForm;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableFormGroup;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableTable;
import ch.admin.bar.siardsuite.model.database.DatabaseSchema;
import ch.admin.bar.siardsuite.model.database.DatabaseView;
import ch.admin.bar.siardsuite.util.i18n.keys.I18nKey;
import lombok.NonNull;

public class ViewsOverviewForm {

    private static final I18nKey LABEL_SCHEMA = I18nKey.of("tableContainer.labelSchema");
    private static final I18nKey LABEL_DESC_SCHEMA = I18nKey.of("tableContainer.labelDescSchema");

    private static final I18nKey ROW = I18nKey.of("tableContainer.table.header.row");
    private static final I18nKey VIEW_NAME = I18nKey.of("tableContainer.table.header.viewName");
    private static final I18nKey NUMBER_OF_COLUMNS = I18nKey.of("tableContainer.table.header.numberOfColumns");
    private static final I18nKey NUMBER_OF_ROWS = I18nKey.of("tableContainer.table.header.numberOfRows");

    public static RenderableForm create(@NonNull final DatabaseSchema schema) {

        return RenderableForm.<DatabaseSchema>builder()
                .dataSupplier(() -> schema)
                .afterSaveAction(DatabaseSchema::write)
                .group(RenderableFormGroup.<DatabaseSchema>builder()
                        .property(new ReadOnlyStringProperty<>(
                                LABEL_SCHEMA,
                                DatabaseSchema::getName))
                        .property(new ReadWriteStringProperty<>(
                                LABEL_DESC_SCHEMA,
                                DatabaseSchema::getDescription,
                                DatabaseSchema::setDescription
                        ))
                        .property(RenderableTable.<DatabaseSchema, DatabaseView>builder()
                                .dataExtractor(DatabaseSchema::getViews)
                                .property(new ReadOnlyStringProperty<>(
                                        ROW,
                                        databaseView -> String.valueOf(schema.getViews().indexOf(databaseView) + 1)
                                ))
                                .property(new ReadOnlyStringProperty<>(
                                        VIEW_NAME,
                                        DatabaseView::name
                                ))
                                .property(new ReadOnlyStringProperty<>(
                                        NUMBER_OF_COLUMNS,
                                        DatabaseView::getNumberOfColumns
                                ))
                                .property(new ReadOnlyStringProperty<>(
                                        NUMBER_OF_ROWS,
                                        DatabaseView::getNumberOfRows
                                ))
                                .build())
                        .build())
                .build();
    }
}
