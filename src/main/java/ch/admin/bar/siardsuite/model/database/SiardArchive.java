package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siard2.api.MetaData;
import ch.admin.bar.siard2.api.primary.ArchiveImpl;
import ch.admin.bar.siardsuite.model.MetaSearchHit;
import ch.admin.bar.siardsuite.model.TreeContentView;
import ch.admin.bar.siardsuite.model.facades.ArchiveFacade;
import ch.admin.bar.siardsuite.model.facades.MetaDataFacade;
import ch.admin.bar.siardsuite.presenter.tree.SiardArchiveMetaDataDetailsVisitor;
import ch.admin.bar.siardsuite.visitor.ArchiveVisitor;
import ch.admin.bar.siardsuite.visitor.SiardArchiveMetaDataVisitor;
import ch.admin.bar.siardsuite.visitor.SiardArchiveVisitor;
import ch.enterag.utils.io.DiskFile;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.VBox;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;

// understands the content of a SIARD Archive
public class SiardArchive extends DatabaseObject {

    private Archive archive;
    private String name;
    // another hack to reuse tmp file for metadata export
    private DiskFile tmpPath;
    private boolean onlyMetaData = false;
    private List<DatabaseSchema> schemas = new ArrayList<>();
    private List<User> users = new ArrayList<>();
    private List<Privilige> priviliges = new ArrayList<>();
    @Getter protected SiardArchiveMetaData metaData;
    protected final TreeContentView treeContentView = TreeContentView.ROOT;

    public SiardArchive() {
    }

    public SiardArchive(String name, Archive archive) {
        this(name, archive, false);
    }

    public SiardArchive(String name, Archive archive, boolean onlyMetaData) {
        this.archive = archive;
        this.tmpPath = ((ArchiveImpl) archive).getZipFile().getDiskFile();
        this.onlyMetaData = onlyMetaData;
        this.name = name;
        metaData = new SiardArchiveMetaData(archive.getMetaData());
        this.schemas = new ArchiveFacade(archive).schemas()
                                                 .stream()
                                                 .map(schema -> new DatabaseSchema(this, schema, onlyMetaData)).collect(
                        Collectors.toList());
        MetaDataFacade metaDataFacade = new MetaDataFacade(archive.getMetaData());
        this.users = metaDataFacade.users();
        this.priviliges = metaDataFacade.priviliges();
    }

    public void addArchiveMetaData(String dbName, String databaseDescription, String databaseOwner,
                                   String dataOriginTimespan,
                                   String archiverName, String archiverContact, URI lobFolder, File targetArchive,
                                   boolean viewsAsTables) {
        this.metaData = new SiardArchiveMetaData(dbName,
                                                 databaseDescription,
                                                 databaseOwner,
                                                 dataOriginTimespan,
                                                 archiverName,
                                                 archiverContact,
                                                 lobFolder,
                                                 targetArchive,
                                                 viewsAsTables);
        // set metadata in existing archive -> needed in metadata only export, otherwise metadata and archive is generated in DownloadTask
        if (this.archive != null && this.archive.getMetaData() != null) {
            MetaData meta = this.archive.getMetaData();
            meta.setDbName(dbName);
            meta.setDescription(databaseDescription);
            meta.setDataOwner(databaseOwner);
            meta.setDataOriginTimespan(dataOriginTimespan);
            meta.setArchiver(archiverName);
            meta.setArchiverContact(archiverContact);
            try {
                new MetaDataFacade(meta).setLobFolder(lobFolder);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void shareProperties(SiardArchiveVisitor visitor) {
        visitor.visit(name, onlyMetaData, schemas, users, priviliges);
    }

    public void shareProperties(SiardArchiveVisitor visitor, DatabaseObject databaseObject) {
        if (databaseObject != null) {
            databaseObject.shareProperties(visitor);
        }
    }

    public void shareObject(ArchiveVisitor visitor) {
        if (archive != null) {
            visitor.visit(archive);
        }
    }

    public void shareProperties(ArchiveVisitor visitor) {
        if (archive != null) {
            visitor.visit(archive.getMetaData());
        }
    }

    public void shareProperties(SiardArchiveMetaDataDetailsVisitor visitor) {
        if (metaData != null) {
            metaData.accept(visitor);
        }
    }

    public void shareObject(SiardArchiveMetaDataVisitor visitor) {
        if (metaData != null) {
            metaData.shareObject(visitor);
        }
    }

    @Override
    public void populate(TableView tableView, TreeContentView type) {
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

    @Override
    public void populate(VBox vbox, TreeContentView type) {
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
        }).collect(Collectors.toList());
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
            List<MetaSearchHit> metaSearchHits = new ArrayList<>();
            metaSearchHits.add(new MetaSearchHit("Database Information", this, treeContentView, nodeIds));
            hits = new TreeSet<>(
                    metaSearchHits);
        }
        return hits;
    }

    public TreeSet<MetaSearchHit> aggregatedMetaSearch(String s) {
        final TreeSet<MetaSearchHit> hits = metaSearch(s);
        hits.addAll(schemas.stream()
                           .flatMap(schema -> schema.aggregatedMetaSearch(s).stream())
                           .collect(Collectors.toList()));
        return hits;
    }

    public String name() {
        return name;
    }

    public List<User> users() {
        return this.users;
    }

    public List<Privilige> priviliges() {
        return this.priviliges;
    }

    public List<DatabaseSchema> schemas() {
        return this.schemas;
    }

    public boolean onlyMetaData() {
        return this.onlyMetaData;
    }

    public void save() throws IOException {
        this.archive.saveMetaData();
        this.archive.close();
    }

    public DiskFile getTmpPath() {
        return tmpPath;
    }
}

