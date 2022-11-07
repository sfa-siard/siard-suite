package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siardsuite.model.TreeContentView;
import ch.admin.bar.siardsuite.model.MetaSearchHit;
import ch.admin.bar.siardsuite.visitor.SiardArchiveMetaDataVisitor;
import ch.admin.bar.siardsuite.visitor.SiardArchiveVisitor;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.VBox;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class SiardArchive {

    private Archive archive;
    private String archiveName;
    private boolean onlyMetaData = false;
    private final List<DatabaseSchema> schemas = new ArrayList<>();
    private SiardArchiveMetaData metaData;
    private final TreeContentView treeContentView = TreeContentView.ROOT;

    public SiardArchive() {}

    public SiardArchive(String archiveName, Archive archive) {
        this(archiveName, archive, false);
    }

    public SiardArchive(String archiveName, Archive archive, boolean onlyMetaData) {
        this.archive = archive;
        this.onlyMetaData = onlyMetaData;
        this.archiveName = archiveName;
        metaData = new SiardArchiveMetaData(archive.getMetaData());
        for (int i = 0; i < archive.getSchemas(); i++) {
            schemas.add(new DatabaseSchema(this, archive.getSchema(i), onlyMetaData));
        }
    }

    public void addArchiveMetaData(String dbName, String databaseDescription, String databaseOwner, String dataOriginTimespan,
                                   String archiverName, String archiverContact, File targetArchive) {
        this.metaData = new SiardArchiveMetaData(dbName, databaseDescription, databaseOwner, dataOriginTimespan,
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

    public void populate(VBox vBox, DatabaseObject databaseObject, TreeContentView type) {
        if (databaseObject != null) {
            databaseObject.populate(vBox, type);
        }
    }

    public void export(File directory) {
        List<String> allTables = this.schemas.stream()
                                           .flatMap(schema -> schema.tables.stream())
                                           .map(databaseTable -> databaseTable.name)
                                           .collect(
                                                   Collectors.toList());
        this.export(allTables, directory);
    }
    public void export(List<String> tablesToExport, File directory) {
        this.schemas.forEach(schema -> schema.export(tablesToExport, directory));
    }

    public void populate(TreeItem root) {
        List<CheckBoxTreeItem<String>> checkBoxTreeItems = this.schemas.stream().map(schema -> {
            CheckBoxTreeItem<String> schemaItem = new CheckBoxTreeItem<>(schema.name);
            schemaItem.setExpanded(true);
            schema.populate(schemaItem);

            return schemaItem;
        }).toList();
        root.getChildren().setAll(checkBoxTreeItems);
    }

    public List<MetaSearchHit> aggregatedMetaSearch(String string) {
        final List<MetaSearchHit> hits = new ArrayList<>();
        hits.addAll(schemas.stream().flatMap(schema -> schema.aggregatedMetaSearch(string).stream()).toList());
        return hits;
    }

    public void databaseName(SiardArchiveDatabaseNameVisitor visitor) {
        this.metaData.accept(visitor);
    }
}
