package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siard2.api.MetaData;
import ch.admin.bar.siardsuite.visitor.SiardArchiveMetaDataVisitor;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.File;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Calendar;

// understands additional metadata of the archive
public class SiardArchiveMetaData {

    private final StringProperty siardFormatVersion;
    private final StringProperty databaseName;
    private final StringProperty databaseProduct;
    private final StringProperty databaseConnectionURL;
    private final StringProperty databaseUsername;
    private final StringProperty databaseDescription;
    private final StringProperty dataOwner;
    private final StringProperty dataOriginTimespan;
    private final LocalDate archivingDate;
    private final StringProperty archiverName;
    private final StringProperty archiverContact;
    private File targetArchive; // not sure if this is the correct place here... maybe just use the model?

    public SiardArchiveMetaData(String databaseDescription, String dataOwner, String dataOriginTimespan,
                                String archiverName, String archiverContact, File targetArchive) {
        siardFormatVersion = new SimpleStringProperty();
        databaseName = new SimpleStringProperty();
        databaseProduct = new SimpleStringProperty();
        databaseConnectionURL = new SimpleStringProperty();
        databaseUsername = new SimpleStringProperty();
        this.databaseDescription = new SimpleStringProperty(databaseDescription);
        this.dataOwner = new SimpleStringProperty(dataOwner);
        this.dataOriginTimespan = new SimpleStringProperty(dataOriginTimespan);
        // TODO: What shall we do if some nonsense for archivingDate is written in the meta data?
        archivingDate = LocalDate.now();
        this.archiverName = new SimpleStringProperty(archiverName);
        this.archiverContact = new SimpleStringProperty(archiverContact);
        this.targetArchive = targetArchive;
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
        // TODO: What shall we do if some nonsense for archivingDate is written in the meta data?
        LocalDate date;
        try {
            date = LocalDate.of(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
        } catch (DateTimeException e) {
            date = LocalDate.now();
        }
        archivingDate = date;
        archiverName = new SimpleStringProperty(metaData.getArchiver());
        archiverContact = new SimpleStringProperty(metaData.getArchiverContact());
    }

    public void shareProperties(SiardArchiveMetaDataVisitor visitor) {
        visitor.visit(siardFormatVersion.getValue(), databaseName.getValue(), databaseProduct.getValue(),
                databaseConnectionURL.getValue(), databaseUsername.getValue(), databaseDescription.getValue(),
                dataOwner.getValue(), dataOriginTimespan.getValue(), archivingDate,
                archiverName.getValue(), archiverContact.getValue(), targetArchive);
    }

    public void shareObject(SiardArchiveMetaDataVisitor visitor) {
        visitor.visit(this);
    }

    public void write(Archive archive) {
        archive.getMetaData().setArchiver(archiverName.getValue());
        archive.getMetaData().setArchiverContact(archiverContact.getValue());
        archive.getMetaData().setDescription(databaseDescription.getValue());
        archive.getMetaData().setDataOwner(dataOwner.getValue());
        archive.getMetaData().setDataOriginTimespan(dataOriginTimespan.getValue());
    }

}
