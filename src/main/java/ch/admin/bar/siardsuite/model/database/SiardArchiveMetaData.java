package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siard2.api.MetaData;
import ch.admin.bar.siardsuite.model.facades.MetaDataFacade;
import ch.admin.bar.siardsuite.presenter.tree.SiardArchiveMetaDataDetailsVisitor;
import ch.admin.bar.siardsuite.visitor.SiardArchiveMetaDataVisitor;
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
    private File targetArchive; // not sure if this is the correct place here... maybe just use the model?
    private URI lobFolder;
    private boolean viewsAsTables;

    public SiardArchiveMetaData(
            String databaseName,
            String databaseDescription,
            String dataOwner,
            String dataOriginTimespan,
            String archiverName,
            String archiverContact,
            URI lobFolder,
            File targetArchive,
            boolean viewsAsTables) {

        this.siardFormatVersion = "";
        this.databaseName = databaseName;
        this.databaseProduct = "";
        this.databaseConnectionURL = "";
        this.databaseUsername = "";
        this.databaseDescription = databaseDescription;
        this.dataOwner = dataOwner;
        this.dataOriginTimespan = dataOriginTimespan;
        this.archivingDate = LocalDate.now();
        this.archiverName = archiverName;
        this.archiverContact = archiverContact;
        this.targetArchive = targetArchive;
        this.lobFolder = lobFolder;
        this.viewsAsTables = viewsAsTables;
    }

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

    public void accept(SiardArchiveMetaDataDetailsVisitor visitor) {
        visitor.visit(
                siardFormatVersion,
                databaseName,
                databaseProduct,
                databaseConnectionURL,
                databaseUsername,
                databaseDescription,
                dataOwner,
                dataOriginTimespan,
                archivingDate,
                archiverName,
                archiverContact,
                targetArchive,
                lobFolder,
                viewsAsTables
        );
    }


    public void shareObject(SiardArchiveMetaDataVisitor visitor) {
        visitor.visit(this);
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
            //throw new RuntimeException(e);
        }
    }

}
