package ch.admin.bar.siardsuite.component.rendered;

import ch.admin.bar.siardsuite.component.rendering.model.ReadOnlyStringProperty;
import ch.admin.bar.siardsuite.component.rendering.model.ReadWriteStringProperty;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableForm;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableFormGroup;
import ch.admin.bar.siardsuite.model.database.SiardArchiveMetaData;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.util.I18nKey;
import lombok.val;

import java.net.URI;
import java.util.Optional;

public class MetadataDetailsForm {
    public static RenderableForm<SiardArchiveMetaData> create() {
        return RenderableForm.<SiardArchiveMetaData>builder()
                .dataExtractor(controller -> controller.getSiardArchive().getMetaData())
                .saveAction((controller, siardArchiveMetaData) -> {
                    val archive = controller.getSiardArchive().getArchive();
                    siardArchiveMetaData.write(archive);
                })
                .group(RenderableFormGroup.<SiardArchiveMetaData>builder()
                        .property(new ReadOnlyStringProperty<>(
                                I18nKey.of("archiveDetails.labelFormat"),
                                SiardArchiveMetaData::getSiardFormatVersion))
                        .property(new ReadWriteStringProperty<>(
                                I18nKey.of("archiveDetails.labelDb"),
                                SiardArchiveMetaData::getDatabaseName,
                                SiardArchiveMetaData::setDatabaseName,
                                ReadWriteStringProperty.IS_NOT_EMPTY_VALIDATOR
                        ))
                        .property(new ReadOnlyStringProperty<>(
                                I18nKey.of("archiveDetails.labelProduct"),
                                SiardArchiveMetaData::getDatabaseProduct))
                        .property(new ReadOnlyStringProperty<>(
                                I18nKey.of("archiveDetails.labelConnection"),
                                SiardArchiveMetaData::getDatabaseConnectionURL))
                        .property(new ReadOnlyStringProperty<>(
                                I18nKey.of("archiveDetails.labelUsername"),
                                SiardArchiveMetaData::getDatabaseUsername))
                        .build())
                .group(RenderableFormGroup.<SiardArchiveMetaData>builder()
                        .property(new ReadWriteStringProperty<>(
                                I18nKey.of("archiveDetails.labelDesc"),
                                SiardArchiveMetaData::getDatabaseDescription,
                                SiardArchiveMetaData::setDatabaseDescription
                        ))
                        .property(new ReadWriteStringProperty<>(
                                I18nKey.of("archiveDetails.labelOwner"),
                                SiardArchiveMetaData::getDataOwner,
                                SiardArchiveMetaData::setDataOwner,
                                ReadWriteStringProperty.IS_NOT_EMPTY_VALIDATOR
                        ))
                        .property(new ReadWriteStringProperty<>(
                                I18nKey.of("archiveDetails.labelCreationDate"),
                                SiardArchiveMetaData::getDataOriginTimespan,
                                SiardArchiveMetaData::setDataOriginTimespan,
                                ReadWriteStringProperty.IS_NOT_EMPTY_VALIDATOR
                        ))
                        .property(new ReadOnlyStringProperty<>(
                                I18nKey.of("archiveDetails.labelArchiveDate"),
                                siardArchiveMetaData -> I18n.getLocaleDate(siardArchiveMetaData.getArchivingDate())
                        ))
                        .property(new ReadWriteStringProperty<>(
                                I18nKey.of("archiveDetails.labelArchiveUser"),
                                SiardArchiveMetaData::getArchiverName,
                                SiardArchiveMetaData::setArchiverName
                        ))
                        .property(new ReadWriteStringProperty<>(
                                I18nKey.of("archiveDetails.labelContactArchiveUser"),
                                SiardArchiveMetaData::getArchiverContact,
                                SiardArchiveMetaData::setArchiverContact
                        ))
                        .property(new ReadOnlyStringProperty<>(
                                I18nKey.of("archiveDetails.labelLOBFolder"),
                                siardArchiveMetaData -> Optional.ofNullable(siardArchiveMetaData.getLobFolder())
                                        .map(URI::getPath)
                                        .orElse("")
                        ))
                        .build())
                .build();
    }
}
