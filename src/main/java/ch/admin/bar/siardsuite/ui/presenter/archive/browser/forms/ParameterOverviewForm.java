package ch.admin.bar.siardsuite.ui.presenter.archive.browser.forms;

import ch.admin.bar.siard2.api.MetaParameter;
import ch.admin.bar.siardsuite.ui.component.rendering.model.ReadOnlyStringProperty;
import ch.admin.bar.siardsuite.ui.component.rendering.model.ReadWriteStringProperty;
import ch.admin.bar.siardsuite.ui.component.rendering.model.RenderableForm;
import ch.admin.bar.siardsuite.ui.component.rendering.model.RenderableFormGroup;
import ch.admin.bar.siardsuite.framework.i18n.keys.I18nKey;
import ch.admin.bar.siardsuite.ui.presenter.archive.browser.forms.utils.Converter;
import lombok.NonNull;

public class ParameterOverviewForm {

    private static final I18nKey NAME = I18nKey.of("details.routine.parameter.name");
    private static final I18nKey POSITION = I18nKey.of("details.routine.parameter.position");
    private static final I18nKey MODE = I18nKey.of("details.routine.parameter.mode");
    private static final I18nKey TYPE = I18nKey.of("details.routine.parameter.type");
    private static final I18nKey TYPE_SCHEMA = I18nKey.of("details.routine.parameter.typeSchema");
    private static final I18nKey TYPE_NAME = I18nKey.of("details.routine.parameter.typeName");
    private static final I18nKey TYPE_ORIGINAL = I18nKey.of("details.routine.parameter.typeOriginal");
    private static final I18nKey CARDINALITY = I18nKey.of("details.routine.parameter.cardinality");
    private static final I18nKey DESCRIPTION = I18nKey.of("details.routine.parameter.description");

    public static RenderableForm create(@NonNull final MetaParameter metaParameter) {
        return RenderableForm.<MetaParameter>builder()
                .dataSupplier(() -> metaParameter)
                .group(RenderableFormGroup.<MetaParameter>builder()
                        .property(new ReadOnlyStringProperty<>(
                                NAME,
                                MetaParameter::getName
                        ))
                        .property(new ReadOnlyStringProperty<>(
                                POSITION,
                                Converter.intToString(MetaParameter::getPosition)
                        ))
                        .property(new ReadOnlyStringProperty<>(
                                MODE,
                                MetaParameter::getMode
                        ))
                        .property(new ReadOnlyStringProperty<>(
                                TYPE,
                                MetaParameter::getType
                        ))
                        .property(new ReadOnlyStringProperty<>(
                                TYPE_SCHEMA,
                                MetaParameter::getTypeSchema
                        ))
                        .property(new ReadOnlyStringProperty<>(
                                TYPE_NAME,
                                MetaParameter::getTypeName
                        ))
                        .property(new ReadOnlyStringProperty<>(
                                TYPE_ORIGINAL,
                                MetaParameter::getTypeOriginal
                        ))
                        .property(new ReadOnlyStringProperty<>(
                                CARDINALITY,
                                Converter.intToString(MetaParameter::getCardinality)
                        ))
                        .property(new ReadWriteStringProperty<>(
                                DESCRIPTION,
                                MetaParameter::getDescription,
                                MetaParameter::setDescription
                        ))
                        .build())
                .build();
    }
}
