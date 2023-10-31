package ch.admin.bar.siardsuite.presenter.archive.browser.forms;

import ch.admin.bar.siardsuite.component.rendering.model.ReadOnlyStringProperty;
import ch.admin.bar.siardsuite.component.rendering.model.ReadWriteStringProperty;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableForm;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableFormGroup;
import ch.admin.bar.siardsuite.model.database.SiardArchive;
import ch.admin.bar.siardsuite.model.database.SiardArchiveMetaData;
import ch.admin.bar.siardsuite.util.i18n.keys.I18nKey;
import lombok.NonNull;
import lombok.val;

import java.net.URI;
import java.util.Optional;

import static ch.admin.bar.siardsuite.presenter.archive.browser.forms.utils.Converter.localDateToString;

public class MetadataDetailsForm {

    private static final I18nKey LABEL_FORMAT = I18nKey.of("archiveDetails.labelFormat");
    private static final I18nKey LABEL_DB = I18nKey.of("archiveDetails.labelDb");
    private static final I18nKey LABEL_PRODUCT = I18nKey.of("archiveDetails.labelProduct");
    private static final I18nKey LABEL_CONNECTION = I18nKey.of("archiveDetails.labelConnection");
    private static final I18nKey LABEL_USERNAME = I18nKey.of("archiveDetails.labelUsername");
    private static final I18nKey LABEL_DESC = I18nKey.of("archiveDetails.labelDesc");
    private static final I18nKey LABEL_OWNER = I18nKey.of("archiveDetails.labelOwner");
    private static final I18nKey LABEL_CREATION_DATE = I18nKey.of("archiveDetails.labelCreationDate");
    private static final I18nKey LABEL_ARCHIVE_DATE = I18nKey.of("archiveDetails.labelArchiveDate");
    private static final I18nKey LABEL_ARCHIVE_USER = I18nKey.of("archiveDetails.labelArchiveUser");
    private static final I18nKey LABEL_CONTACT_ARCHIVE_USER = I18nKey.of("archiveDetails.labelContactArchiveUser");
    private static final I18nKey LABEL_LOB_FOLDER = I18nKey.of("archiveDetails.labelLOBFolder");

    public static RenderableForm create(@NonNull final SiardArchive siardArchive) {
        return RenderableForm.<SiardArchiveMetaData>builder()
                .dataSupplier(siardArchive::getMetaData)
                .afterSaveAction(siardArchiveMetaData -> {
                    val metadata = siardArchive.getMetaData();
                    metadata.write(siardArchive.getArchive());
                })
                .group(RenderableFormGroup.<SiardArchiveMetaData>builder()
                        .property(new ReadOnlyStringProperty<>(
                                LABEL_FORMAT,
                                SiardArchiveMetaData::getSiardFormatVersion
                        ))
                        .property(new ReadWriteStringProperty<>(
                                LABEL_DB,
                                SiardArchiveMetaData::getDatabaseName,
                                SiardArchiveMetaData::setDatabaseName,
                                ReadWriteStringProperty.IS_NOT_EMPTY_VALIDATOR
                        ))
                        .property(new ReadOnlyStringProperty<>(
                                LABEL_PRODUCT,
                                SiardArchiveMetaData::getDatabaseProduct
                        ))
                        .property(new ReadOnlyStringProperty<>(
                                LABEL_CONNECTION,
                                SiardArchiveMetaData::getDatabaseConnectionURL
                        ))
                        .property(new ReadOnlyStringProperty<>(
                                LABEL_USERNAME,
                                SiardArchiveMetaData::getDatabaseUsername
                        ))
                        .build())
                .group(RenderableFormGroup.<SiardArchiveMetaData>builder()
                        .property(new ReadWriteStringProperty<>(
                                LABEL_DESC,
                                SiardArchiveMetaData::getDatabaseDescription,
                                SiardArchiveMetaData::setDatabaseDescription
                        ))
                        .property(new ReadWriteStringProperty<>(
                                LABEL_OWNER,
                                SiardArchiveMetaData::getDataOwner,
                                SiardArchiveMetaData::setDataOwner,
                                ReadWriteStringProperty.IS_NOT_EMPTY_VALIDATOR
                        ))
                        .property(new ReadWriteStringProperty<>(
                                LABEL_CREATION_DATE,
                                SiardArchiveMetaData::getDataOriginTimespan,
                                SiardArchiveMetaData::setDataOriginTimespan,
                                ReadWriteStringProperty.IS_NOT_EMPTY_VALIDATOR
                        ))
                        .property(new ReadOnlyStringProperty<>(
                                LABEL_ARCHIVE_DATE,
                                localDateToString(SiardArchiveMetaData::getArchivingDate)
                        ))
                        .property(new ReadWriteStringProperty<>(
                                LABEL_ARCHIVE_USER,
                                SiardArchiveMetaData::getArchiverName,
                                SiardArchiveMetaData::setArchiverName
                        ))
                        .property(new ReadWriteStringProperty<>(
                                LABEL_CONTACT_ARCHIVE_USER,
                                SiardArchiveMetaData::getArchiverContact,
                                SiardArchiveMetaData::setArchiverContact
                        ))
                        .property(new ReadOnlyStringProperty<>(
                                LABEL_LOB_FOLDER,
                                siardArchiveMetaData -> Optional.ofNullable(siardArchiveMetaData.getLobFolder())
                                        .map(URI::getPath)
                                        .orElse("")
                        ))
                        .build())
                .build();
    }
}
