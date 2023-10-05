package ch.admin.bar.siardsuite.component.rendered;

import ch.admin.bar.siardsuite.component.rendering.model.ReadOnlyStringProperty;
import ch.admin.bar.siardsuite.component.rendering.model.ReadWriteStringProperty;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableForm;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableFormGroup;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableTable;
import ch.admin.bar.siardsuite.model.database.DatabaseSchema;
import ch.admin.bar.siardsuite.model.database.DatabaseView;
import ch.admin.bar.siardsuite.util.I18nKey;

public class ViewsOverviewForm {
    public static RenderableForm create(final DatabaseSchema schema) {

        return RenderableForm.<DatabaseSchema>builder()
                .dataExtractor(controller -> schema)
                .saveAction((controller, editedSchema) -> editedSchema.write())
                .group(RenderableFormGroup.<DatabaseSchema>builder()
                        .property(new ReadOnlyStringProperty<>(
                                I18nKey.of("tableContainer.labelSchema"),
                                DatabaseSchema::name))
                        .property(new ReadWriteStringProperty<>(
                                I18nKey.of("tableContainer.labelDescSchema"),
                                DatabaseSchema::getDescription,
                                DatabaseSchema::setDescription
                        ))
                        .property(RenderableTable.<DatabaseSchema, DatabaseView>builder()
                                .dataExtractor(DatabaseSchema::getViews)
                                .property(new ReadOnlyStringProperty<>(
                                        I18nKey.of("tableContainer.table.header.row"),
                                        databaseView -> String.valueOf(schema.getViews().indexOf(databaseView) + 1)
                                ))
                                .property(new ReadOnlyStringProperty<>(
                                        I18nKey.of("tableContainer.table.header.viewName"),
                                        DatabaseView::name
                                ))
                                .property(new ReadOnlyStringProperty<>(
                                        I18nKey.of("tableContainer.table.header.numberOfColumns"),
                                        DatabaseView::getNumberOfColumns
                                ))
                                .property(new ReadOnlyStringProperty<>(
                                        I18nKey.of("tableContainer.table.header.numberOfRows"),
                                        DatabaseView::getNumberOfRows
                                ))
                                .build())
                        .build())
                .build();
    }
}
