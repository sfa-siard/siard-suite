package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siardsuite.model.TreeContentView;
import ch.admin.bar.siardsuite.visitor.DatabaseArchiveMetaDataVisitor;
import ch.admin.bar.siardsuite.visitor.DatabaseArchiveVisitor;
import javafx.scene.control.TableView;
import java.io.File;
import java.util.*;

public class SiardArchive {

    private String archiveName;
    private boolean onlyMetaData = false;
    private static final Map<String, List<String>> dbTypes = Map.of(
            "MS Access", List.of("access", ""),
            "DB/2", List.of("db2", "50000"),
            "MySQL", List.of("mysql", "3306"),
            "Oracle", List.of("oracle", "1521"),
            "PostgreSQL", List.of("postgresql", "5432"),
            "Microsoft SQL Server", List.of("sqlserver", "1433"));
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

    public void shareProperties(DatabaseArchiveVisitor visitor) {
        visitor.visit(archiveName, onlyMetaData, schemas);
    }

    public void shareProperties (DatabaseArchiveVisitor visitor, DatabaseSchema schema) {
        schema.shareProperties(visitor);
    }

    public void shareProperties(DatabaseArchiveVisitor visitor, DatabaseTable table) {
        table.shareProperties(visitor);
    }

    public void shareProperties(DatabaseArchiveVisitor visitor, DatabaseColumn column) {
        column.shareProperties(visitor);
    }

    public void shareObject(DatabaseArchiveVisitor visitor) {
        visitor.visit(this);
    }

    public void shareProperties(DatabaseArchiveMetaDataVisitor visitor) {
        if (metaData != null) {
            metaData.shareProperties(visitor);
        }
    }

    public void shareObject(DatabaseArchiveMetaDataVisitor visitor) {
        if (metaData != null) {
            metaData.shareObject(visitor);
        }
    }

    public void populate(TableView<Map> tableView, DatabaseObject databaseObject, TreeContentView type) {
        databaseObject.populate(tableView, type);
    }

}
