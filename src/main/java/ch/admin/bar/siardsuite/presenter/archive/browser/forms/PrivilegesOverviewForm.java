package ch.admin.bar.siardsuite.presenter.archive.browser.forms;

import ch.admin.bar.siardsuite.component.rendering.model.*;
import ch.admin.bar.siardsuite.model.database.Privilige;
import ch.admin.bar.siardsuite.model.database.SiardArchive;
import ch.admin.bar.siardsuite.util.i18n.keys.I18nKey;
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
                                .property(new TableColumnProperty<>(
                                        TYPE,
                                        Privilige::getType
                                ))
                                .property(new TableColumnProperty<>(
                                        OBJECT,
                                        Privilige::getObject
                                ))
                                .property(new TableColumnProperty<>(
                                        GRANTOR,
                                        Privilige::getGrantor
                                ))
                                .property(new TableColumnProperty<>(
                                        RECEIVER,
                                        Privilige::getGrantee
                                ))
                                .property(new TableColumnProperty<>(
                                        OPTION,
                                        Privilige::getOption
                                ))
                                .property(new TableColumnProperty<>(
                                        DESCRIPTION,
                                        Privilige::getDescription
                                ))
                                .build())
                        .build())
                .build();
    }
}
