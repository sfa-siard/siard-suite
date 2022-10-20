package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siardsuite.presenter.archive.ArchiveMetaDataVisitor;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

// understands additional metadata of the archive
public class ArchiveMetaData {

    private StringProperty description;
    private StringProperty owner;
    private StringProperty timeOfOrigin;
    private StringProperty archiverName;
    private StringProperty archiverContact;

    public ArchiveMetaData(String description, String owner, String timeOfOrigin, String archiverName, String archiverContact) {
        this.description = new SimpleStringProperty(description);
        this.owner = new SimpleStringProperty(owner);
        this.timeOfOrigin = new SimpleStringProperty(timeOfOrigin);
        this.archiverName = new SimpleStringProperty(archiverName);
        this.archiverContact = new SimpleStringProperty(archiverContact);
    }

    public void accept(ArchiveMetaDataVisitor visitor) {
        visitor.visit(this.description.getValue(), this.owner.getValue(), this.timeOfOrigin.getValue(), this.archiverName.getValue(), this.archiverContact.getValue());
    }
}
