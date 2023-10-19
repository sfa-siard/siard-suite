package ch.admin.bar.siardsuite.component.rendered;

import ch.admin.bar.siard2.api.MetaParameter;
import ch.admin.bar.siardsuite.component.rendering.model.ReadOnlyStringProperty;
import ch.admin.bar.siardsuite.component.rendering.model.ReadWriteStringProperty;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableForm;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableFormGroup;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableTable;
import ch.admin.bar.siardsuite.model.database.Routine;
import ch.admin.bar.siardsuite.util.i18n.keys.I18nKey;
import lombok.NonNull;

import static ch.admin.bar.siardsuite.component.rendered.utils.Converter.cardinalityToString;
import static ch.admin.bar.siardsuite.component.rendered.utils.Converter.intToString;

public class RoutineOverviewForm {

    private static final I18nKey NAME = I18nKey.of("details.routine.name");
    private static final I18nKey SPECIFIC_NAME = I18nKey.of("details.routine.specificName");
    private static final I18nKey SOURCE = I18nKey.of("details.routine.source");
    private static final I18nKey BODY = I18nKey.of("details.routine.body");
    private static final I18nKey CHARACTERISTICS = I18nKey.of("details.routine.characteristics");
    private static final I18nKey RETURN_TYPE = I18nKey.of("details.routine.returnType");
    private static final I18nKey DESCRIPTION = I18nKey.of("details.routine.description");

    private static final I18nKey POSITION = I18nKey.of("tableContainer.table.header.position");
    private static final I18nKey PARAMETER_NAME = I18nKey.of("tableContainer.parameter.header.parameterName");
    private static final I18nKey PARAMETER_MODE = I18nKey.of("tableContainer.parameter.header.parameterMode");
    private static final I18nKey PARAMETER_TYPE = I18nKey.of("tableContainer.parameter.header.parameterType");
    private static final I18nKey CARDINALITY = I18nKey.of("tableContainer.parameter.header.cardinality");

    public static RenderableForm create(@NonNull final Routine routine) {
        return RenderableForm.<Routine>builder()
                .dataSupplier(() -> routine)
                .afterSaveAction(Routine::write)
                .group(RenderableFormGroup.<Routine>builder()
                        .property(new ReadOnlyStringProperty<>(
                                NAME,
                                Routine::getName
                        ))
                        .property(new ReadOnlyStringProperty<>(
                                SPECIFIC_NAME,
                                Routine::getSpecificName
                        ))
                        .property(new ReadWriteStringProperty<>(
                                SOURCE,
                                Routine::getSource,
                                Routine::setSource
                        ))
                        .property(new ReadWriteStringProperty<>(
                                BODY,
                                Routine::getBody,
                                Routine::setBody
                        ))
                        .property(new ReadOnlyStringProperty<>(
                                CHARACTERISTICS,
                                Routine::getCharacteristics
                        ))
                        .property(new ReadOnlyStringProperty<>(
                                RETURN_TYPE,
                                Routine::getReturnType
                        ))
                        .property(new ReadWriteStringProperty<>(
                                DESCRIPTION,
                                Routine::getDescription,
                                Routine::setDescription
                        ))
                        .property(RenderableTable.<Routine, MetaParameter>builder()
                                .dataExtractor(Routine::getParameters)
                                .property(new ReadOnlyStringProperty<>(
                                        POSITION,
                                        intToString(MetaParameter::getPosition)
                                ))
                                .property(new ReadOnlyStringProperty<>(
                                        PARAMETER_NAME,
                                        MetaParameter::getName
                                ))
                                .property(new ReadOnlyStringProperty<>(
                                        PARAMETER_MODE,
                                        MetaParameter::getMode
                                ))
                                .property(new ReadOnlyStringProperty<>(
                                        PARAMETER_TYPE,
                                        MetaParameter::getType
                                ))
                                .property(new ReadOnlyStringProperty<>(
                                        CARDINALITY,
                                        cardinalityToString(MetaParameter::getCardinality)
                                ))
                                .build())
                        .build())
                .build();
    }
}
