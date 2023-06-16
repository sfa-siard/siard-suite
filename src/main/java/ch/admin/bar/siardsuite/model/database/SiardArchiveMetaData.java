package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siard2.api.MetaData;
import ch.admin.bar.siardsuite.model.facades.MetaDataFacade;
import ch.admin.bar.siardsuite.presenter.tree.SiardArchiveMetaDataDetailsVisitor;
import ch.admin.bar.siardsuite.visitor.SiardArchiveMetaDataVisitor;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Calendar;

// understands additional metadata of the archive
public class SiardArchiveMetaData {

    protected final StringProperty siardFormatVersion;
    protected final StringProperty databaseName;
    protected final StringProperty databaseProduct;
    protected final StringProperty databaseConnectionURL;
    protected final StringProperty databaseUsername;
    protected final StringProperty databaseDescription;
    protected final StringProperty dataOwner;
    protected final StringProperty dataOriginTimespan;
    protected final LocalDate archivingDate;
    protected final StringProperty archiverName;
    protected final StringProperty archiverContact;
    protected File targetArchive; // not sure if this is the correct place here... maybe just use the model?
    protected URI lobFolder;
    protected boolean viewsAsTables;

    public SiardArchiveMetaData(String dbName, String databaseDescription, String dataOwner, String dataOriginTimespan,
                                String archiverName, String archiverContact, URI lobFolder, File targetArchive,
                                boolean viewsAsTables) {

        siardFormatVersion = new SimpleStringProperty();
        databaseName = new SimpleStringProperty(dbName);
        databaseProduct = new SimpleStringProperty();
        databaseConnectionURL = new SimpleStringProperty();
        databaseUsername = new SimpleStringProperty();
        this.databaseDescription = new SimpleStringProperty(databaseDescription);
        this.dataOwner = new SimpleStringProperty(dataOwner);
        this.dataOriginTimespan = new SimpleStringProperty(dataOriginTimespan);
        archivingDate = LocalDate.now();
        this.archiverName = new SimpleStringProperty(archiverName);
        this.archiverContact = new SimpleStringProperty(archiverContact);
        this.targetArchive = targetArchive;
        this.lobFolder = lobFolder;
        this.viewsAsTables = viewsAsTables;
    }

    public SiardArchiveMetaData(MetaData metaData) {
        siardFormatVersion = new SimpleStringProperty(metaData.getVersion());
        databaseName = new SimpleStringProperty(metaData.getDbName());
        databaseProduct = new SimpleStringProperty(metaData.getDatabaseProduct());
        databaseConnectionURL = new SimpleStringProperty(metaData.getConnection());
        databaseUsername = new SimpleStringProperty(metaData.getDatabaseUser());
        databaseDescription = new SimpleStringProperty(metaData.getDescription());
        dataOwner = new SimpleStringProperty(metaData.getDataOwner());
        dataOriginTimespan = new SimpleStringProperty(metaData.getDataOriginTimespan());
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
        archiverName = new SimpleStringProperty(metaData.getArchiver());
        archiverContact = new SimpleStringProperty(metaData.getArchiverContact());
        lobFolder = metaData.getLobFolder();
    }

    public void accept(SiardArchiveMetaDataDetailsVisitor visitor) {
        visitor.visit(siardFormatVersion.getValue(), databaseName.getValue(), databaseProduct.getValue(),
                      databaseConnectionURL.getValue(), databaseUsername.getValue(), databaseDescription.getValue(),
                      dataOwner.getValue(), dataOriginTimespan.getValue(), archivingDate,
                      archiverName.getValue(), archiverContact.getValue(), targetArchive, lobFolder, viewsAsTables);
    }


    public void shareObject(SiardArchiveMetaDataVisitor visitor) {
        visitor.visit(this);
    }

    public void write(Archive archive) {
        archive.getMetaData().setDbName(databaseName.getValue());
        archive.getMetaData().setArchiver(archiverName.getValue());
        archive.getMetaData().setArchiverContact(archiverContact.getValue());
        archive.getMetaData().setDescription(databaseDescription.getValue());
        archive.getMetaData().setDataOwner(dataOwner.getValue());
        archive.getMetaData().setDataOriginTimespan(dataOriginTimespan.getValue());
        try {
            new MetaDataFacade(archive.getMetaData()).setLobFolder(this.lobFolder);
        } catch (IOException e) {
            //throw new RuntimeException(e);
        }
    }

}
