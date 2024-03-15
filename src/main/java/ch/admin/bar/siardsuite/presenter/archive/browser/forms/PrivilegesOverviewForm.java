package ch.admin.bar.siardsuite.presenter.archive.browser.forms;

import ch.admin.bar.siardsuite.component.rendering.model.ReadOnlyStringProperty;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableForm;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableFormGroup;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableTable;
import ch.admin.bar.siardsuite.model.database.Privilige;
import ch.admin.bar.siardsuite.model.database.SiardArchive;
import ch.admin.bar.siardsuite.framework.i18n.keys.I18nKey;
import lombok.NonNull;

public class PrivilegesOverviewForm {

    private static final I18nKey TYPE = I18nKey.of("tableContainer.priviliges.header.type");
    private static final I18nKey OBJECT = I18nKey.of("tableContainer.priviliges.header.object");
    private static final I18nKey GRANTOR = I18nKey.of("tableContainer.priviliges.header.grantor");
    private static final I18nKey RECEIVER = I18nKey.of("tableContainer.priviliges.header.receiver");
    private static final I18nKey OPTION = I18nKey.of("tableContainer.priviliges.header.option");
    private static final I18nKey DESCRIPTION = I18nKey.of("tableContainer.priviliges.header.description");

    public static RenderableForm<SiardArchive> create(@NonNull final SiardArchive siardArchive) {
        return RenderableForm.<SiardArchive>builder()
                .dataSupplier(() -> siardArchive)
                .group(RenderableFormGroup.<SiardArchive>builder()
                        .property(RenderableTable.<SiardArchive, Privilige>builder()
                                .dataExtractor(SiardArchive::priviliges)
                                .property(new ReadOnlyStringProperty<>(
                                        TYPE,
                                        Privilige::getType
                                ))
                                .property(new ReadOnlyStringProperty<>(
                                        OBJECT,
                                        Privilige::getObject
                                ))
                                .property(new ReadOnlyStringProperty<>(
                                        GRANTOR,
                                        Privilige::getGrantor
                                ))
                                .property(new ReadOnlyStringProperty<>(
                                        RECEIVER,
                                        Privilige::getGrantee
                                ))
                                .property(new ReadOnlyStringProperty<>(
                                        OPTION,
                                        Privilige::getOption
                                ))
                                .property(new ReadOnlyStringProperty<>(
                                        DESCRIPTION,
                                        Privilige::getDescription
                                ))
                                .build())
                        .build())
                .build();
    }
}
