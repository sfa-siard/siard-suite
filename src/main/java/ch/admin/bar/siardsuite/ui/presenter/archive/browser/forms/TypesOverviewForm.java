package ch.admin.bar.siardsuite.ui.presenter.archive.browser.forms;

import ch.admin.bar.siardsuite.ui.component.rendering.model.ReadOnlyStringProperty;
import ch.admin.bar.siardsuite.ui.component.rendering.model.ReadWriteStringProperty;
import ch.admin.bar.siardsuite.ui.component.rendering.model.RenderableForm;
import ch.admin.bar.siardsuite.ui.component.rendering.model.RenderableFormGroup;
import ch.admin.bar.siardsuite.ui.component.rendering.model.RenderableTable;
import ch.admin.bar.siardsuite.model.database.DatabaseSchema;
import ch.admin.bar.siardsuite.model.database.DatabaseType;
import ch.admin.bar.siardsuite.framework.i18n.keys.I18nKey;
import ch.admin.bar.siardsuite.ui.presenter.archive.browser.forms.utils.Converter;
import lombok.NonNull;

public class TypesOverviewForm {

    private static final I18nKey LABEL_SCHEMA = I18nKey.of("tableContainer.labelSchema");
    private static final I18nKey LABEL_DESC_SCHEMA = I18nKey.of("tableContainer.labelDescSchema");

    private static final I18nKey NAME = I18nKey.of("tableContainer.types.header.name");
    private static final I18nKey CATEGORY = I18nKey.of("tableContainer.types.header.category");
    private static final I18nKey IS_INSTANTIABLE = I18nKey.of("tableContainer.types.header.instantiable");
    private static final I18nKey IS_FINAL = I18nKey.of("tableContainer.types.header.final");
    private static final I18nKey BASE = I18nKey.of("tableContainer.types.header.base");
    private static final I18nKey DESCRIPTION = I18nKey.of("tableContainer.types.header.description");

    public static RenderableForm<DatabaseSchema> create(@NonNull final DatabaseSchema schema) {

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
                        .property(RenderableTable.<DatabaseSchema, DatabaseType>builder()
                                .dataExtractor(DatabaseSchema::getTypes)
                                .property(new ReadOnlyStringProperty<>(
                                        NAME,
                                        DatabaseType::getName
                                ))
                                .property(new ReadOnlyStringProperty<>(
                                        CATEGORY,
                                        DatabaseType::getCategory
                                ))
                                .property(new ReadOnlyStringProperty<>(
                                        IS_INSTANTIABLE,
                                        Converter.booleanToString(DatabaseType::isInstantiable)
                                ))
                                .property(new ReadOnlyStringProperty<>(
                                        IS_FINAL,
                                        Converter.booleanToString(DatabaseType::isFinal)
                                ))
                                .property(new ReadOnlyStringProperty<>(
                                        BASE,
                                        DatabaseType::getBase
                                ))
                                .property(new ReadOnlyStringProperty<>(
                                        DESCRIPTION,
                                        DatabaseType::getDescription
                                ))
                                .build())
                        .build())
                .build();
    }
}
