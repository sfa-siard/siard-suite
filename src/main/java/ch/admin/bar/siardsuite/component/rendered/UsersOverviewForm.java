package ch.admin.bar.siardsuite.component.rendered;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.component.rendering.model.ReadOnlyStringProperty;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableForm;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableFormGroup;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableTable;
import ch.admin.bar.siardsuite.model.database.SiardArchive;
import ch.admin.bar.siardsuite.model.database.User;
import ch.admin.bar.siardsuite.util.I18nKey;
import lombok.NonNull;

public class UsersOverviewForm {
    public static RenderableForm<SiardArchive> create(@NonNull final SiardArchive siardArchive) {
        return RenderableForm.<SiardArchive>builder()
                .dataSupplier(() -> siardArchive)
                .group(RenderableFormGroup.<SiardArchive>builder()
                        .property(RenderableTable.<SiardArchive, User>builder()
                                .dataExtractor(SiardArchive::users)
                                .property(new ReadOnlyStringProperty<>(
                                        I18nKey.of("tableContainer.users.header.username"),
                                        User::getName
                                ))
                                .property(new ReadOnlyStringProperty<>(
                                        I18nKey.of("tableContainer.users.header.description"),
                                        User::getDescription
                                ))
                                .build())
                        .build())
                .build();
    }
}
