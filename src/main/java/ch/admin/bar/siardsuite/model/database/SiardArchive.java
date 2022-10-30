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
        visitor.visit(archiveName, onlyMetaData);
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

    public List<DatabaseSchema> schemas() {
        return schemas;
    }

    public List<DatabaseTable> tables(DatabaseSchema schema) {
        List<DatabaseTable> l = new ArrayList<>();
        Optional<DatabaseSchema> o = schemas.stream().filter(s -> s.equals(schema)).findFirst();
        if (o.isPresent()) {
            l = o.get().tables;
        }
        return l;
    }

    public List<DatabaseColumn> columns(DatabaseTable table) {
        List<DatabaseColumn> l = new ArrayList<>();
        Optional<DatabaseTable> o = schemas.stream().flatMap(s -> s.tables.stream()).filter(t -> t.equals(table)).findFirst();
        if (o.isPresent()) {
            l = o.get().columns;
        }
        return l;
    }

    public List<DatabaseRow> rows(DatabaseTable table) {
        List<DatabaseRow> l = new ArrayList<>();
        Optional<DatabaseTable> o = schemas.stream().flatMap(s -> s.tables.stream()).filter(t -> t.equals(table)).findFirst();
        if (o.isPresent()) {
            l = o.get().rows;
        }
        return l;
    }

    public String name(DatabaseSchema schema) {
        String n = "";
        Optional<DatabaseSchema> o = schemas.stream().filter(s -> s.equals(schema)).findFirst();
        if (o.isPresent()) {
            n = o.get().name;
        }
        return n;
    }

    public String name(DatabaseTable table) {
        String n = "";
        Optional<DatabaseTable> o = schemas.stream().flatMap(s -> s.tables.stream()).filter(t -> t.equals(table)).findFirst();
        if (o.isPresent()) {
            n = o.get().name;
        }
        return n;
    }

    public String name(DatabaseColumn column) {
        String n = "";
        Optional<DatabaseColumn> o = schemas.stream().flatMap(s -> s.tables.stream().flatMap(t -> t.columns.stream())).filter(c -> c.equals(column)).findFirst();
        if (o.isPresent()) {
            n = o.get().name;
        }
        return n;
    }

    public void populate(TableView<Map> tableView, DatabaseObject databaseObject, TreeContentView type) {
        databaseObject.populate(tableView, type);
    }

}
