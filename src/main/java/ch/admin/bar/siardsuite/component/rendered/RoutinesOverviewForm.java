package ch.admin.bar.siardsuite.component.rendered;

import ch.admin.bar.siardsuite.component.rendered.utils.Converter;
import ch.admin.bar.siardsuite.component.rendering.model.ReadOnlyStringProperty;
import ch.admin.bar.siardsuite.component.rendering.model.ReadWriteStringProperty;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableForm;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableFormGroup;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableTable;
import ch.admin.bar.siardsuite.model.database.DatabaseSchema;
import ch.admin.bar.siardsuite.model.database.Routine;
import ch.admin.bar.siardsuite.util.i18n.keys.I18nKey;
import lombok.NonNull;

public class RoutinesOverviewForm {

    private static final I18nKey LABEL_SCHEMA = I18nKey.of("tableContainer.labelSchema");
    private static final I18nKey LABEL_DESC_SCHEMA = I18nKey.of("tableContainer.labelDescSchema");

    private static final I18nKey ROW = I18nKey.of("tableContainer.table.header.row");
    private static final I18nKey NAME = I18nKey.of("tableContainer.routine.header.name");
    private static final I18nKey SPECIFIC_NAME = I18nKey.of("tableContainer.routine.header.specificName");
    private static final I18nKey CHARACTERISTICS = I18nKey.of("tableContainer.routine.header.characteristics");
    private static final I18nKey RETURN_TYPE = I18nKey.of("tableContainer.routine.header.returnType");
    private static final I18nKey NUMBER_OF_PARAMETERS = I18nKey.of("tableContainer.routine.header.numberOfParameters");

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
                        .property(RenderableTable.<DatabaseSchema, Routine>builder()
                                .dataExtractor(DatabaseSchema::getRoutines)
                                .property(new ReadOnlyStringProperty<>(
                                        ROW,
                                        routine -> String.valueOf(schema.getRoutines().indexOf(routine) + 1)
                                ))
                                .property(new ReadOnlyStringProperty<>(
                                        NAME,
                                        Routine::getName
                                ))
                                .property(new ReadOnlyStringProperty<>(
                                        SPECIFIC_NAME,
                                        Routine::getSpecificName
                                ))
                                .property(new ReadOnlyStringProperty<>(
                                        CHARACTERISTICS,
                                        Routine::getCharacteristics
                                ))
                                .property(new ReadOnlyStringProperty<>(
                                        RETURN_TYPE,
                                        Routine::getReturnType
                                ))
                                .property(new ReadOnlyStringProperty<>(
                                        NUMBER_OF_PARAMETERS,
                                        Converter.intToString(routine -> routine.getParameters().size())
                                ))
                                .build())
                        .build())
                .build();
    }
}
