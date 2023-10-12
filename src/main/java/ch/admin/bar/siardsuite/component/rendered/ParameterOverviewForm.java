package ch.admin.bar.siardsuite.component.rendered;

import ch.admin.bar.siard2.api.MetaParameter;
import ch.admin.bar.siardsuite.component.rendering.model.ReadOnlyStringProperty;
import ch.admin.bar.siardsuite.component.rendering.model.ReadWriteStringProperty;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableForm;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableFormGroup;
import ch.admin.bar.siardsuite.util.I18nKey;
import lombok.NonNull;

import static ch.admin.bar.siardsuite.component.rendered.utils.Converter.intToString;

public class ParameterOverviewForm {

    public static RenderableForm create(@NonNull final MetaParameter metaParameter) {
        return RenderableForm.<MetaParameter>builder()
                .dataSupplier(() -> metaParameter)
                .group(RenderableFormGroup.<MetaParameter>builder()
                        .property(new ReadOnlyStringProperty<>(
                                I18nKey.of("details.routine.parameter.name"),
                                MetaParameter::getName
                        ))
                        .property(new ReadOnlyStringProperty<>(
                                I18nKey.of("details.routine.parameter.position"),
                                intToString(MetaParameter::getPosition)
                        ))
                        .property(new ReadOnlyStringProperty<>(
                                I18nKey.of("details.routine.parameter.mode"),
                                MetaParameter::getMode
                        ))
                        .property(new ReadOnlyStringProperty<>(
                                I18nKey.of("details.routine.parameter.type"),
                                MetaParameter::getType
                        ))
                        .property(new ReadOnlyStringProperty<>(
                                I18nKey.of("details.routine.parameter.typeSchema"),
                                MetaParameter::getTypeSchema
                        ))
                        .property(new ReadOnlyStringProperty<>(
                                I18nKey.of("details.routine.parameter.typeName"),
                                MetaParameter::getTypeName
                        ))
                        .property(new ReadOnlyStringProperty<>(
                                I18nKey.of("details.routine.parameter.typeOriginal"),
                                MetaParameter::getTypeOriginal
                        ))
                        .property(new ReadOnlyStringProperty<>(
                                I18nKey.of("details.routine.parameter.cardinality"),
                                intToString(MetaParameter::getCardinality)
                        ))
                        .property(new ReadWriteStringProperty<>(
                                I18nKey.of("details.routine.parameter.name"),
                                MetaParameter::getDescription,
                                MetaParameter::setDescription
                        ))
                        .build())
                .build();
    }
}
