package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siard2.api.MetaData;
import ch.admin.bar.siardsuite.model.facades.MetaDataFacade;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Calendar;

@Getter
@Setter
// understands additional metadata of the archive
public class SiardArchiveMetaData {

    private final String siardFormatVersion;
    private String databaseName;
    private final String databaseProduct;
    private final String databaseConnectionURL;
    private final String databaseUsername;
    private String databaseDescription;
    private String dataOwner;
    private String dataOriginTimespan;
    private final LocalDate archivingDate;
    private String archiverName;
    private String archiverContact;
    private URI lobFolder;

    public SiardArchiveMetaData(MetaData metaData) {
        siardFormatVersion = metaData.getVersion();
        databaseName = metaData.getDbName();
        databaseProduct = metaData.getDatabaseProduct();
        databaseConnectionURL = metaData.getConnection();
        databaseUsername = metaData.getDatabaseUser();
        databaseDescription = metaData.getDescription();
        dataOwner = metaData.getDataOwner();
        dataOriginTimespan = metaData.getDataOriginTimespan();
        final Calendar calendar = metaData.getArchivalDate();
        LocalDate date;
        try {
            date = LocalDate.of(calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.DATE));
        } catch (DateTimeException e) {
            date = LocalDate.now();
        }
        archivingDate = date;
        archiverName = metaData.getArchiver();
        archiverContact = metaData.getArchiverContact();
        lobFolder = metaData.getLobFolder();
    }

    public void write(Archive archive) {
        archive.getMetaData().setDbName(databaseName);
        archive.getMetaData().setArchiver(archiverName);
        archive.getMetaData().setArchiverContact(archiverContact);
        archive.getMetaData().setDescription(databaseDescription);
        archive.getMetaData().setDataOwner(dataOwner);
        archive.getMetaData().setDataOriginTimespan(dataOriginTimespan);
        try {
            if (lobFolder != null) {
                new MetaDataFacade(archive.getMetaData()).setLobFolder(this.lobFolder);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
