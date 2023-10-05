package ch.admin.bar.siardsuite.component.rendered;

import ch.admin.bar.siard2.api.MetaColumn;
import ch.admin.bar.siard2.api.MetaValue;
import ch.admin.bar.siard2.api.MetaView;
import ch.admin.bar.siardsuite.component.rendered.utils.Converter;
import ch.admin.bar.siardsuite.component.rendered.utils.ListAssembler;
import ch.admin.bar.siardsuite.component.rendering.model.ReadOnlyStringProperty;
import ch.admin.bar.siardsuite.component.rendering.model.ReadWriteStringProperty;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableForm;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableFormGroup;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableTable;
import ch.admin.bar.siardsuite.util.I18nKey;

import static ch.admin.bar.siardsuite.component.rendered.utils.Converter.catchExceptions;
import static ch.admin.bar.siardsuite.component.rendered.utils.Converter.intToString;

public class ViewOverviewForm {
    public static RenderableForm create(final MetaView view) {

        return RenderableForm.<MetaView>builder()
                .dataExtractor(controller -> view)
                .group(RenderableFormGroup.<MetaView>builder()
                        .property(new ReadOnlyStringProperty<>(
                                I18nKey.of("details.view.name"),
                                MetaView::getName
                        ))
                        .property(new ReadWriteStringProperty<>(
                                I18nKey.of("details.view.queryOriginal"),
                                MetaView::getQueryOriginal,
                                MetaView::setQueryOriginal
                        ))
                        .property(new ReadWriteStringProperty<>(
                                I18nKey.of("details.view.query"),
                                MetaView::getQuery,
                                MetaView::setQuery
                        ))
                        .property(new ReadOnlyStringProperty<>(
                                I18nKey.of("details.view.nrRecords"),
                                Converter.longToString(MetaView::getRows)
                        ))
                        .property(new ReadWriteStringProperty<>(
                                I18nKey.of("details.view.description"),
                                MetaView::getDescription,
                                MetaView::setDescription
                        ))
                        .property(RenderableTable.<MetaView, MetaColumn>builder()
                                .dataExtractor(metaView -> new ListAssembler<>(
                                        metaView::getMetaColumns,
                                        metaView::getMetaColumn
                                ).assemble())
                                .property(new ReadOnlyStringProperty<>(
                                        I18nKey.of("tableContainer.table.header.position"),
                                        intToString(MetaValue::getPosition)
                                ))
                                .property(new ReadOnlyStringProperty<>(
                                        I18nKey.of("tableContainer.table.header.columnName"),
                                        MetaColumn::getName
                                ))
                                .property(new ReadOnlyStringProperty<>(
                                        I18nKey.of("tableContainer.table.header.columnType"),
                                        catchExceptions(MetaValue::getType, "")
                                ))
                                .property(new ReadOnlyStringProperty<>(
                                        I18nKey.of("tableContainer.view.header.cardinality"),
                                        intToString(catchExceptions(MetaValue::getCardinality, 0))
                                ))
                                .build())
                        .build())
                .build();
    }
}
