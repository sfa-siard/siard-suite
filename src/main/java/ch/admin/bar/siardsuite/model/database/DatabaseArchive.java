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
    private StringProperty databaseName = new SimpleStringProperty();
    private StringProperty databaseProduct = new SimpleStringProperty();
    private StringProperty databaseConnectionUrl = new SimpleStringProperty();
    private static final Map<String, List<String>> dbTypes = Map.of(
            "MS Access", List.of("access", ""),
            "DB/2", List.of("db2", "50000"),
            "MySQL", List.of("mysql", "3306"),
            "Oracle", List.of("oracle", "1521"),
            "PostgreSQL", List.of("postgresql", "5432"),
            "Microsoft SQL Server", List.of("sqlserver", "1433"));
    private StringProperty databaseUsername = new SimpleStringProperty();
    private List<DatabaseSchema> schemas = new ArrayList<>();
    private DatabaseArchiveMetaData metaData;

    public DatabaseArchive() {
    }

    public DatabaseArchive(String archiveName, Archive archive) {
        this.archiveName = new SimpleStringProperty(archiveName);
        databaseName = new SimpleStringProperty(archive.getMetaData().getDbName());
        databaseProduct = new SimpleStringProperty(archive.getMetaData().getDatabaseProduct());
        databaseConnectionUrl = new SimpleStringProperty(archive.getMetaData().getConnection());
        for (int i = 0; i < archive.getSchemas(); i++) {
            schemas.add(new DatabaseSchema(archive.getSchema(i)));
        }
        metaData = new DatabaseArchiveMetaData(archive.getMetaData());
    }

    public void addArchiveMetaData(String siardFormatVersion, String databaseName, String databaseProduct,
                                   String databaseConnectionURL, String databaseUsername, String databaseDescription,
                                   String databaseOwner, String databaseCreationDate, String archivingDate,
                                   String archiverName, String archiverContact, File targetArchive) {
        this.metaData = new DatabaseArchiveMetaData(siardFormatVersion, databaseName, databaseProduct,
                databaseConnectionURL, databaseUsername, databaseDescription, databaseOwner, databaseCreationDate,
                archivingDate, archiverName, archiverContact, targetArchive);
    }

    public void accept(DatabaseArchiveVisitor visitor) {
        visitor.visit(archiveName.getValue(), schemas);
    }

    public void accept(DatabaseArchiveMetaDataVisitor visitor) {
        metaData.accept(visitor);
    }

}
