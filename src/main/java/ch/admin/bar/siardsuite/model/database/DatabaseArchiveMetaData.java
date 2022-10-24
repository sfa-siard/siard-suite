package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siard2.api.MetaData;
import ch.admin.bar.siardsuite.visitor.DatabaseArchiveMetaDataVisitor;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.File;

// understands additional metadata of the archive
public class DatabaseArchiveMetaData {

    private final StringProperty siardFormatVersion;
    private final StringProperty description;
    private final StringProperty owner;
    private final StringProperty timeOfOrigin;
    private final StringProperty archiverName;
    private final StringProperty archiverContact;
    private File targetArchive; // not sure if this is the correct place here... maybe just use the model?

    public DatabaseArchiveMetaData(String siardFormatVersion, String description, String owner, String timeOfOrigin,
                                   String archiverName, String archiverContact, File targetArchive) {
        this.siardFormatVersion = new SimpleStringProperty(siardFormatVersion);
        this.description = new SimpleStringProperty(description);
        this.owner = new SimpleStringProperty(owner);
        this.timeOfOrigin = new SimpleStringProperty(timeOfOrigin);
        this.archiverName = new SimpleStringProperty(archiverName);
        this.archiverContact = new SimpleStringProperty(archiverContact);
        this.targetArchive = targetArchive;
    }

    public DatabaseArchiveMetaData(MetaData metaData) {
        siardFormatVersion = new SimpleStringProperty(metaData.getVersion());
        description = new SimpleStringProperty(metaData.getDescription());
        owner = new SimpleStringProperty(metaData.getDataOwner());
        timeOfOrigin = new SimpleStringProperty(metaData.getDataOriginTimespan());
        archiverName = new SimpleStringProperty(metaData.getArchiver());
        archiverContact = new SimpleStringProperty(metaData.getArchiverContact());
    }

    public void accept(DatabaseArchiveMetaDataVisitor visitor) {
        visitor.visit(siardFormatVersion.getValue(), description.getValue(), owner.getValue(), timeOfOrigin.getValue(), archiverName.getValue(), archiverContact.getValue());
    }
}
