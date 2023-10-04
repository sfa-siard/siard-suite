package ch.admin.bar.siardsuite.component.rendered;

import ch.admin.bar.siardsuite.component.rendering.model.ReadOnlyStringProperty;
import ch.admin.bar.siardsuite.component.rendering.model.ReadWriteStringProperty;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableForm;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableFormGroup;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableTable;
import ch.admin.bar.siardsuite.model.database.DatabaseSchema;
import ch.admin.bar.siardsuite.model.database.Routine;
import ch.admin.bar.siardsuite.util.I18nKey;

public class RoutinesOverviewForm {
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
                        .property(RenderableTable.<DatabaseSchema, Routine>builder()
                                .dataExtractor(DatabaseSchema::getRoutines)
                                .property(new ReadOnlyStringProperty<>(
                                        I18nKey.of("tableContainer.table.header.row"),
                                        routine -> String.valueOf(schema.getRoutines().indexOf(routine) + 1)
                                ))
                                .property(new ReadOnlyStringProperty<>(
                                        I18nKey.of("tableContainer.routine.header.name"),
                                        Routine::name
                                ))
                                .property(new ReadOnlyStringProperty<>(
                                        I18nKey.of("tableContainer.routine.header.specificName"),
                                        Routine::specificName
                                ))
                                .property(new ReadOnlyStringProperty<>(
                                        I18nKey.of("tableContainer.routine.header.characteristics"),
                                        Routine::characteristics
                                ))
                                .property(new ReadOnlyStringProperty<>(
                                        I18nKey.of("tableContainer.routine.header.returnType"),
                                        Routine::returnType
                                ))
                                .property(new ReadOnlyStringProperty<>(
                                        I18nKey.of("tableContainer.routine.header.numberOfParameters"),
                                        Routine::numberOfParameters
                                ))
                                .build())
                        .build())
                .build();
    }
}
