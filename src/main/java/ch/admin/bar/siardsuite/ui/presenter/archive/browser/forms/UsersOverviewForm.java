package ch.admin.bar.siardsuite.ui.presenter.archive.browser.forms;

import ch.admin.bar.siardsuite.ui.component.rendering.model.ReadOnlyStringProperty;
import ch.admin.bar.siardsuite.ui.component.rendering.model.RenderableForm;
import ch.admin.bar.siardsuite.ui.component.rendering.model.RenderableFormGroup;
import ch.admin.bar.siardsuite.ui.component.rendering.model.RenderableTable;
import ch.admin.bar.siardsuite.model.database.SiardArchive;
import ch.admin.bar.siardsuite.model.database.User;
import ch.admin.bar.siardsuite.framework.i18n.keys.I18nKey;
import lombok.NonNull;

public class UsersOverviewForm {

    private static final I18nKey USERNAME = I18nKey.of("tableContainer.users.header.username");
    private static final I18nKey DESCRIPTION = I18nKey.of("tableContainer.users.header.description");

    public static RenderableForm<SiardArchive> create(@NonNull final SiardArchive siardArchive) {
        return RenderableForm.<SiardArchive>builder()
                .dataSupplier(() -> siardArchive)
                .group(RenderableFormGroup.<SiardArchive>builder()
                        .property(RenderableTable.<SiardArchive, User>builder()
                                .dataExtractor(SiardArchive::users)
                                .property(new ReadOnlyStringProperty<>(
                                        USERNAME,
                                        User::getName
                                ))
                                .property(new ReadOnlyStringProperty<>(
                                        DESCRIPTION,
                                        User::getDescription
                                ))
                                .build())
                        .build())
                .build();
    }
}
