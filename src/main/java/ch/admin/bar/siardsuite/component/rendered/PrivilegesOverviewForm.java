package ch.admin.bar.siardsuite.component.rendered;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.component.rendering.model.ReadOnlyStringProperty;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableForm;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableFormGroup;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableTable;
import ch.admin.bar.siardsuite.model.database.Privilige;
import ch.admin.bar.siardsuite.model.database.SiardArchive;
import ch.admin.bar.siardsuite.util.I18nKey;

public class PrivilegesOverviewForm {
    public static RenderableForm<SiardArchive> create() {
        return RenderableForm.<SiardArchive>builder()
                .dataExtractor(Controller::getSiardArchive)
                .group(RenderableFormGroup.<SiardArchive>builder()
                        .property(RenderableTable.<SiardArchive, Privilige>builder()
                                .dataExtractor(SiardArchive::priviliges)
                                .property(new ReadOnlyStringProperty<>(
                                        I18nKey.of("tableContainer.priviliges.header.type"),
                                        Privilige::getType
                                ))
                                .property(new ReadOnlyStringProperty<>(
                                        I18nKey.of("tableContainer.priviliges.header.object"),
                                        Privilige::getObject
                                ))
                                .property(new ReadOnlyStringProperty<>(
                                        I18nKey.of("tableContainer.priviliges.header.grantor"),
                                        Privilige::getGrantor
                                ))
                                .property(new ReadOnlyStringProperty<>(
                                        I18nKey.of("tableContainer.priviliges.header.receiver"),
                                        Privilige::getGrantee
                                ))
                                .property(new ReadOnlyStringProperty<>(
                                        I18nKey.of("tableContainer.priviliges.header.option"),
                                        Privilige::getOption
                                ))
                                .property(new ReadOnlyStringProperty<>(
                                        I18nKey.of("tableContainer.priviliges.header.description"),
                                        Privilige::getDescription
                                ))
                                .build())
                        .build())
                .build();
    }
}
