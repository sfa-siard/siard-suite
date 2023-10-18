package ch.admin.bar.siardsuite.component.rendered;

import ch.admin.bar.siard2.api.MetaSchema;
import ch.admin.bar.siard2.api.MetaView;
import ch.admin.bar.siardsuite.component.rendered.utils.Converter;
import ch.admin.bar.siardsuite.component.rendering.model.ReadOnlyStringProperty;
import ch.admin.bar.siardsuite.component.rendering.model.ReadWriteStringProperty;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableForm;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableFormGroup;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableTable;
import ch.admin.bar.siardsuite.util.i18n.keys.I18nKey;
import lombok.NonNull;

import java.util.List;

public class ViewsOverviewForm {

    private static final I18nKey LABEL_SCHEMA = I18nKey.of("tableContainer.labelSchema");
    private static final I18nKey LABEL_DESC_SCHEMA = I18nKey.of("tableContainer.labelDescSchema");

    private static final I18nKey ROW = I18nKey.of("tableContainer.table.header.row");
    private static final I18nKey VIEW_NAME = I18nKey.of("tableContainer.table.header.viewName");
    private static final I18nKey NUMBER_OF_COLUMNS = I18nKey.of("tableContainer.table.header.numberOfColumns");
    private static final I18nKey NUMBER_OF_ROWS = I18nKey.of("tableContainer.table.header.numberOfRows");

    public static RenderableForm create(@NonNull final MetaSchema schema, @NonNull final List<MetaView> views) {

        return RenderableForm.<MetaSchema>builder()
                .dataSupplier(() -> schema)
                .group(RenderableFormGroup.<MetaSchema>builder()
                        .property(new ReadOnlyStringProperty<>(
                                LABEL_SCHEMA,
                                MetaSchema::getName))
                        .property(new ReadWriteStringProperty<>(
                                LABEL_DESC_SCHEMA,
                                MetaSchema::getDescription,
                                MetaSchema::setDescription
                        ))
                        .property(RenderableTable.<MetaSchema, MetaView>builder()
                                .dataExtractor(metaSchema -> views)
                                .property(new ReadOnlyStringProperty<>(
                                        ROW,
                                        databaseView -> String.valueOf(views.indexOf(databaseView) + 1)
                                ))
                                .property(new ReadOnlyStringProperty<>(
                                        VIEW_NAME,
                                        MetaView::getName
                                ))
                                .property(new ReadOnlyStringProperty<>(
                                        NUMBER_OF_COLUMNS,
                                        Converter.intToString(MetaView::getMetaColumns)
                                ))
                                .property(new ReadOnlyStringProperty<>(
                                        NUMBER_OF_ROWS,
                                        Converter.longToString(MetaView::getRows)
                                ))
                                .build())
                        .build())
                .build();
    }
}
