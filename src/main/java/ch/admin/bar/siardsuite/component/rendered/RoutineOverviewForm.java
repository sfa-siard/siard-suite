package ch.admin.bar.siardsuite.component.rendered;

import ch.admin.bar.siard2.api.MetaParameter;
import ch.admin.bar.siardsuite.component.rendering.model.ReadOnlyStringProperty;
import ch.admin.bar.siardsuite.component.rendering.model.ReadWriteStringProperty;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableForm;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableFormGroup;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableTable;
import ch.admin.bar.siardsuite.model.database.Routine;
import ch.admin.bar.siardsuite.util.I18nKey;

public class RoutineOverviewForm {
    public static RenderableForm create(final Routine routine) {
        return RenderableForm.<Routine>builder()
                .dataExtractor(controller -> routine)
                .saveAction((controller, edited) -> edited.write())
                .group(RenderableFormGroup.<Routine>builder()
                        .property(new ReadOnlyStringProperty<>(
                                I18nKey.of("details.routine.name"),
                                Routine::name
                        ))
                        .property(new ReadOnlyStringProperty<>(
                                I18nKey.of("details.routine.specificName"),
                                Routine::specificName
                        ))
                        .property(new ReadWriteStringProperty<>(
                                I18nKey.of("details.routine.source"),
                                Routine::getSource,
                                Routine::setSource
                        ))
                        .property(new ReadWriteStringProperty<>(
                                I18nKey.of("details.routine.body"),
                                Routine::getBody,
                                Routine::setBody
                        ))
                        .property(new ReadOnlyStringProperty<>(
                                I18nKey.of("details.routine.characteristics"),
                                Routine::characteristics
                        ))
                        .property(new ReadOnlyStringProperty<>(
                                I18nKey.of("details.routine.returnType"),
                                Routine::returnType
                        ))
                        .property(new ReadWriteStringProperty<>(
                                I18nKey.of("details.routine.description"),
                                Routine::getDescription,
                                Routine::setDescription
                        ))
                        .property(RenderableTable.<Routine, MetaParameter>builder()
                                .dataExtractor(Routine::parameters)
                                .property(new ReadOnlyStringProperty<>(
                                        I18nKey.of("tableContainer.table.header.position"),
                                        metaParameter -> String.valueOf(metaParameter.getPosition())
                                ))
                                .property(new ReadOnlyStringProperty<>(
                                        I18nKey.of("tableContainer.parameter.header.parameterName"),
                                        MetaParameter::getName
                                ))
                                .property(new ReadOnlyStringProperty<>(
                                        I18nKey.of("tableContainer.parameter.header.parameterMode"),
                                        MetaParameter::getMode
                                ))
                                .property(new ReadOnlyStringProperty<>(
                                        I18nKey.of("tableContainer.parameter.header.parameterType"),
                                        MetaParameter::getType
                                ))
                                .property(new ReadOnlyStringProperty<>(
                                        I18nKey.of("tableContainer.parameter.header.cardinality"),
                                        metaParameter -> String.valueOf(metaParameter.getCardinality())
                                ))
                                .build())
                        .build())
                .build();
    }
}
