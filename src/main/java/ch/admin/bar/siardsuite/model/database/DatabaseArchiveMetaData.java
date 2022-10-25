package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siard2.api.MetaData;
import ch.admin.bar.siardsuite.visitor.DatabaseArchiveMetaDataVisitor;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.File;

// understands additional metadata of the archive
public class DatabaseArchiveMetaData {

    private final StringProperty siardFormatVersion;
    private final StringProperty databaseName;
    private final StringProperty databaseProduct;
    private final StringProperty databaseConnectionURL;
    private final StringProperty databaseUsername;
    private final StringProperty databaseDescription;
    private final StringProperty databaseOwner;
    private final StringProperty databaseCreationDate;
    private final StringProperty archivingDate;
    private final StringProperty archiverName;
    private final StringProperty archiverContact;
    private File targetArchive; // not sure if this is the correct place here... maybe just use the model?

    public DatabaseArchiveMetaData(String siardFormatVersion, String databaseName, String databaseProduct,
                                   String databaseConnectionURL, String databaseUsername, String databaseDescription,
                                   String databaseOwner, String databaseCreationDate, String archivingDate,
                                   String archiverName, String archiverContact, File targetArchive) {
        this.siardFormatVersion = new SimpleStringProperty(siardFormatVersion);
        this.databaseName = new SimpleStringProperty(databaseName);
        this.databaseProduct = new SimpleStringProperty(databaseProduct);
        this.databaseConnectionURL = new SimpleStringProperty(databaseConnectionURL);
        this.databaseUsername = new SimpleStringProperty(databaseUsername);
        this.databaseDescription = new SimpleStringProperty(databaseDescription);
        this.databaseOwner = new SimpleStringProperty(databaseOwner);
        this.databaseCreationDate = new SimpleStringProperty(databaseCreationDate);
        this.archivingDate = new SimpleStringProperty(archivingDate);
        this.archiverName = new SimpleStringProperty(archiverName);
        this.archiverContact = new SimpleStringProperty(archiverContact);
        this.targetArchive = targetArchive;
    }

    public DatabaseArchiveMetaData(MetaData metaData) {
        siardFormatVersion = new SimpleStringProperty(metaData.getVersion());
        databaseName = new SimpleStringProperty(metaData.getDbName());
        databaseProduct = new SimpleStringProperty(metaData.getDatabaseProduct());
        databaseConnectionURL = new SimpleStringProperty(metaData.getConnection());
        databaseUsername = new SimpleStringProperty(metaData.getDatabaseUser());
        databaseDescription = new SimpleStringProperty(metaData.getDescription());
        databaseOwner = new SimpleStringProperty(metaData.getDataOwner());
        databaseCreationDate = new SimpleStringProperty(metaData.getDataOriginTimespan());
        archivingDate = new SimpleStringProperty(metaData.getDataOriginTimespan());
        archiverName = new SimpleStringProperty(metaData.getArchiver());
        archiverContact = new SimpleStringProperty(metaData.getArchiverContact());
    }

    public void shareProperties(DatabaseArchiveMetaDataVisitor visitor) {
        visitor.visit(siardFormatVersion.getValue(), databaseName.getValue(), databaseProduct.getValue(),
                databaseConnectionURL.getValue(), databaseUsername.getValue(), databaseDescription.getValue(),
                databaseOwner.getValue(), databaseCreationDate.getValue(), archivingDate.getValue(),
                archiverName.getValue(), archiverContact.getValue(), targetArchive);
    }

    public void shareObject(DatabaseArchiveMetaDataVisitor visitor) {
        visitor.visit(this);
    }

    public void write(Archive archive) {
        archive.getMetaData().setArchiver(this.archiverName.getValue());
        archive.getMetaData().setArchiverContact(this.archiverContact.getValue());
        archive.getMetaData().setDescription(this.databaseDescription.getValue());
        archive.getMetaData().setDataOwner(this.databaseOwner.getValue());
        archive.getMetaData().setDataOriginTimespan(this.databaseCreationDate.getValue());
    }

}
