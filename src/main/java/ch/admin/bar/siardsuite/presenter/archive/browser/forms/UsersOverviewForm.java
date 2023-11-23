package ch.admin.bar.siardsuite.presenter.archive.browser.forms;

import ch.admin.bar.siardsuite.component.rendering.model.RenderableForm;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableFormGroup;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableTable;
import ch.admin.bar.siardsuite.component.rendering.model.TableColumnProperty;
import ch.admin.bar.siardsuite.model.database.SiardArchive;
import ch.admin.bar.siardsuite.model.database.User;
import ch.admin.bar.siardsuite.util.i18n.keys.I18nKey;
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
                                .property(new TableColumnProperty<>(
                                        USERNAME,
                                        User::getName
                                ))
                                .property(new TableColumnProperty<>(
                                        DESCRIPTION,
                                        User::getDescription
                                ))
                                .build())
                        .build())
                .build();
    }
}
