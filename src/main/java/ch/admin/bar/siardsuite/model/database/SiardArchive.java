package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siard2.api.MetaData;
import ch.admin.bar.siard2.api.primary.ArchiveImpl;
import ch.admin.bar.siardsuite.model.facades.ArchiveFacade;
import ch.admin.bar.siardsuite.model.facades.MetaDataFacade;
import ch.admin.bar.siardsuite.presenter.tree.SiardArchiveMetaDataDetailsVisitor;
import ch.admin.bar.siardsuite.visitor.ArchiveVisitor;
import ch.admin.bar.siardsuite.visitor.SiardArchiveMetaDataVisitor;
import ch.enterag.utils.io.DiskFile;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeItem;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// understands the content of a SIARD Archive
public class SiardArchive {

    @Getter
    private Archive archive;
    private String name;
    // another hack to reuse tmp file for metadata export
    private DiskFile tmpPath;
    private boolean onlyMetaData = false;
    private List<DatabaseSchema> schemas = new ArrayList<>();
    private List<User> users = new ArrayList<>();
    private List<Privilige> priviliges = new ArrayList<>();
    @Getter
    protected SiardArchiveMetaData metaData;

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

    public void export(File directory) {
        List<String> allTables = this.schemas.stream()
                .flatMap(schema -> schema.getTables().stream())
                .map(databaseTable -> databaseTable.name())
                .collect(
                        Collectors.toList());
        this.export(allTables, directory);
    }

    public void export(List<String> tablesToExport, File directory) {
        this.schemas.forEach(schema -> schema.export(tablesToExport, directory));
    }

    public void populate(TreeItem root) {
        List<CheckBoxTreeItem<String>> checkBoxTreeItems = this.schemas.stream().map(schema -> {
            CheckBoxTreeItem<String> schemaItem = new CheckBoxTreeItem<>(schema.name());
            schemaItem.setExpanded(true);
            schema.populate(schemaItem);

            return schemaItem;
        }).collect(Collectors.toList());
        root.getChildren().setAll(checkBoxTreeItems);
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

