package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siardsuite.presenter.archive.ArchiveMetaDataVisitor;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.File;

// understands additional metadata of the archive
public class ArchiveMetaData {

    private final StringProperty description;
    private final StringProperty owner;
    private final StringProperty timeOfOrigin;
    private final StringProperty archiverName;
    private final StringProperty archiverContact;
    private File targetArchive; // not sure if this is the correct place here... maybe just use the model?

    public ArchiveMetaData(String description, String owner, String timeOfOrigin, String archiverName, String archiverContact,
                           File targetArchive) {
        this.description = new SimpleStringProperty(description);
        this.owner = new SimpleStringProperty(owner);
        this.timeOfOrigin = new SimpleStringProperty(timeOfOrigin);
        this.archiverName = new SimpleStringProperty(archiverName);
        this.archiverContact = new SimpleStringProperty(archiverContact);
        this.targetArchive = targetArchive;
    }

    public void accept(ArchiveMetaDataVisitor visitor) {
        visitor.visit(this.description.getValue(), this.owner.getValue(), this.timeOfOrigin.getValue(), this.archiverName.getValue(), this.archiverContact.getValue());
    }

    public File getTargetArchive() {
        return this.targetArchive;
    }
}
