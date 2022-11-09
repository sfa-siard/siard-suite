package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siardsuite.model.MetaSearchHit;
import ch.admin.bar.siardsuite.model.TreeContentView;
import ch.admin.bar.siardsuite.visitor.SiardArchiveMetaDataVisitor;
import ch.admin.bar.siardsuite.visitor.SiardArchiveSchemasVisitor;
import ch.admin.bar.siardsuite.visitor.SiardArchiveVisitor;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.VBox;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class SiardArchive extends DatabaseObject {

    protected Archive archive;
    protected String archiveName;
    protected boolean onlyMetaData = false;
    protected final List<DatabaseSchema> schemas = new ArrayList<>();
    protected SiardArchiveMetaData metaData;
    protected final TreeContentView treeContentView = TreeContentView.ROOT;

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

    public void shareSchemas(SiardArchiveSchemasVisitor visitor) {
        if (!schemas.isEmpty()) {
            visitor.visit(schemas);
        }
    }

    @Override
    protected void populate(TableView tableView, TreeContentView type) {}

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

    @Override
    protected void populate(VBox vbox, TreeContentView type) {}

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

    private TreeSet<MetaSearchHit> metaSearch(String s) {
        TreeSet<MetaSearchHit> hits = new TreeSet<>();
        final List<String> nodeIds = new ArrayList<>();
        if (contains(metaData.siardFormatVersion.get(), s)) {
            nodeIds.add("siardFormatVersion");
        }
        if (contains(metaData.databaseName.get(), s)) {
            nodeIds.add("databaseName");
        }
        if (contains(metaData.databaseProduct.get(), s)) {
            nodeIds.add("databaseName");
        }
        if (contains(metaData.databaseProduct.get(), s)) {
            nodeIds.add("databaseName");
        }
        if (contains(metaData.databaseConnectionURL.get(), s)) {
            nodeIds.add("databaseConnectionURL");
        }
        if (contains(metaData.databaseUsername.get(), s)) {
            nodeIds.add("databaseUsername");
        }
        if (contains(metaData.databaseDescription.get(), s)) {
            nodeIds.add("databaseDescription");
        }
        if (contains(metaData.dataOwner.get(), s)) {
            nodeIds.add("dataOwner");
        }
        if (contains(metaData.dataOriginTimespan.get(), s)) {
            nodeIds.add("dataOriginTimespan");
        }
        if (contains(metaData.archivingDate.toString(), s)) {
            nodeIds.add("archivingDate");
        }
        if (contains(metaData.archiverName.get(), s)) {
            nodeIds.add("archiverName");
        }
        if (contains(metaData.archiverContact.get(), s)) {
            nodeIds.add("archiverContact");
        }
        if (nodeIds.size() > 0) {
            hits = new TreeSet<>(List.of(new MetaSearchHit("Database Information", this, treeContentView, nodeIds)));
        }
        return hits;
    }

    public TreeSet<MetaSearchHit> aggregatedMetaSearch(String s) {
        final TreeSet<MetaSearchHit> hits = metaSearch(s);
        hits.addAll(schemas.stream().flatMap(schema -> schema.aggregatedMetaSearch(s).stream()).toList());
        return hits;
    }


}
