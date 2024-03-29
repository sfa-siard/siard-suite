package ch.admin.bar.siardsuite.ui.presenter.archive.browser.forms;

import ch.admin.bar.siard2.api.MetaColumn;
import ch.admin.bar.siard2.api.MetaValue;
import ch.admin.bar.siard2.api.MetaView;
import ch.admin.bar.siardsuite.ui.component.rendering.model.ReadOnlyStringProperty;
import ch.admin.bar.siardsuite.ui.component.rendering.model.ReadWriteStringProperty;
import ch.admin.bar.siardsuite.ui.component.rendering.model.RenderableForm;
import ch.admin.bar.siardsuite.ui.component.rendering.model.RenderableFormGroup;
import ch.admin.bar.siardsuite.ui.component.rendering.model.RenderableTable;
import ch.admin.bar.siardsuite.ui.presenter.archive.browser.forms.utils.ListAssembler;
import ch.admin.bar.siardsuite.framework.i18n.keys.I18nKey;
import ch.admin.bar.siardsuite.ui.presenter.archive.browser.forms.utils.Converter;
import lombok.NonNull;

import static ch.admin.bar.siardsuite.ui.presenter.archive.browser.forms.utils.Converter.catchExceptions;

public class ViewOverviewForm {

    private static final I18nKey NAME = I18nKey.of("details.view.name");
    private static final I18nKey QUERY_ORIGINAL = I18nKey.of("details.view.queryOriginal");
    private static final I18nKey QUERY = I18nKey.of("details.view.query");
    private static final I18nKey NR_RECORDS = I18nKey.of("details.view.nrRecords");
    private static final I18nKey DESCRIPTION = I18nKey.of("details.view.description");

    private static final I18nKey POSITION = I18nKey.of("tableContainer.table.header.position");
    private static final I18nKey COLUMN_NAME = I18nKey.of("tableContainer.table.header.columnName");
    private static final I18nKey COLUMN_TYPE = I18nKey.of("tableContainer.table.header.columnType");
    private static final I18nKey CARDINALITY = I18nKey.of("tableContainer.view.header.cardinality");

    public static RenderableForm create(@NonNull final MetaView view) {

        return RenderableForm.<MetaView>builder()
                .dataSupplier(() -> view)
                .group(RenderableFormGroup.<MetaView>builder()
                        .property(new ReadOnlyStringProperty<>(
                                NAME,
                                MetaView::getName
                        ))
                        .property(new ReadWriteStringProperty<>(
                                QUERY_ORIGINAL,
                                MetaView::getQueryOriginal,
                                MetaView::setQueryOriginal
                        ))
                        .property(new ReadWriteStringProperty<>(
                                QUERY,
                                MetaView::getQuery,
                                MetaView::setQuery
                        ))
                        .property(new ReadOnlyStringProperty<>(
                                NR_RECORDS,
                                Converter.longToString(MetaView::getRows)
                        ))
                        .property(new ReadWriteStringProperty<>(
                                DESCRIPTION,
                                MetaView::getDescription,
                                MetaView::setDescription
                        ))
                        .property(RenderableTable.<MetaView, MetaColumn>builder()
                                .dataExtractor(metaView -> new ListAssembler<>(
                                        metaView::getMetaColumns,
                                        metaView::getMetaColumn
                                ).assemble())
                                .property(new ReadOnlyStringProperty<>(
                                        POSITION,
                                        Converter.intToString(MetaValue::getPosition)
                                ))
                                .property(new ReadOnlyStringProperty<>(
                                        COLUMN_NAME,
                                        MetaColumn::getName
                                ))
                                .property(new ReadOnlyStringProperty<>(
                                        COLUMN_TYPE,
                                        Converter.catchExceptions(MetaValue::getType, "")
                                ))
                                .property(new ReadOnlyStringProperty<>(
                                        CARDINALITY,
                                        Converter.intToString(Converter.catchExceptions(MetaValue::getCardinality, 0))
                                ))
                                .build())
                        .build())
                .build();
    }
}
