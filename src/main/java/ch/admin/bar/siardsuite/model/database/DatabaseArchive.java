package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siardsuite.visitor.DatabaseArchiveMetaDataVisitor;
import ch.admin.bar.siardsuite.visitor.DatabaseArchiveVisitor;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DatabaseArchive {

    private StringProperty archiveName = new SimpleStringProperty();
    private boolean onlyMetaData = false;
    private static final Map<String, List<String>> dbTypes = Map.of(
            "MS Access", List.of("access", ""),
            "DB/2", List.of("db2", "50000"),
            "MySQL", List.of("mysql", "3306"),
            "Oracle", List.of("oracle", "1521"),
            "PostgreSQL", List.of("postgresql", "5432"),
            "Microsoft SQL Server", List.of("sqlserver", "1433"));
    private final List<DatabaseSchema> schemas = new ArrayList<>();
    private DatabaseArchiveMetaData metaData;

    public DatabaseArchive() {
    }

    public DatabaseArchive(String archiveName, Archive archive) {
        this(archiveName, archive, false);
    }

    public DatabaseArchive(String archiveName, Archive archive, boolean onlyMetaData) {
        this.onlyMetaData = onlyMetaData;
        this.archiveName = new SimpleStringProperty(archiveName);
        metaData = new DatabaseArchiveMetaData(archive.getMetaData());
        for (int i = 0; i < archive.getSchemas(); i++) {
            schemas.add(new DatabaseSchema(archive.getSchema(i), onlyMetaData));
        }
    }

    public void addArchiveMetaData(String databaseDescription, String databaseOwner, String dataOriginTimespan,
                                   String archiverName, String archiverContact, File targetArchive) {
        this.metaData = new DatabaseArchiveMetaData(databaseDescription, databaseOwner, dataOriginTimespan,
                archiverName, archiverContact, targetArchive);
    }

    public void shareProperties(DatabaseArchiveVisitor visitor) {
        visitor.visit(archiveName.getValue(), onlyMetaData, schemas);
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

}
