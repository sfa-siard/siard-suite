package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siardsuite.model.TreeContentView;
import ch.admin.bar.siardsuite.visitor.SiardArchiveMetaDataVisitor;
import ch.admin.bar.siardsuite.visitor.SiardArchiveVisitor;
import javafx.scene.control.TableView;
import java.io.File;
import java.util.*;

public class SiardArchive {

    private String archiveName;
    private boolean onlyMetaData = false;
    private final List<DatabaseSchema> schemas = new ArrayList<>();
    private SiardArchiveMetaData metaData;

    public SiardArchive() {}

    public SiardArchive(String archiveName, Archive archive) {
        this(archiveName, archive, false);
    }

    public SiardArchive(String archiveName, Archive archive, boolean onlyMetaData) {
        this.onlyMetaData = onlyMetaData;
        this.archiveName = archiveName;
        metaData = new SiardArchiveMetaData(archive.getMetaData());
        for (int i = 0; i < archive.getSchemas(); i++) {
            schemas.add(new DatabaseSchema(this, archive.getSchema(i), onlyMetaData));
        }
    }

    public void addArchiveMetaData(String databaseDescription, String databaseOwner, String dataOriginTimespan,
                                   String archiverName, String archiverContact, File targetArchive) {
        this.metaData = new SiardArchiveMetaData(databaseDescription, databaseOwner, dataOriginTimespan,
                archiverName, archiverContact, targetArchive);
    }

    public void shareProperties(SiardArchiveVisitor visitor) {
        visitor.visit(archiveName, onlyMetaData, schemas);
    }

    public void shareProperties (SiardArchiveVisitor visitor, DatabaseObject databaseObject) {
        if (databaseObject != null) {
            databaseObject.shareProperties(visitor);
        }
    }

    public void shareObject(SiardArchiveVisitor visitor) {
        visitor.visit(this);
    }

    public void shareProperties(SiardArchiveMetaDataVisitor visitor) {
        if (metaData != null) {
            metaData.shareProperties(visitor);
        }
    }

    public void shareObject(SiardArchiveMetaDataVisitor visitor) {
        if (metaData != null) {
            metaData.shareObject(visitor);
        }
    }

    public void populate(TableView<Map> tableView, DatabaseObject databaseObject, TreeContentView type) {
        if (databaseObject != null) {
            databaseObject.populate(tableView, type);
        }
    }

}
