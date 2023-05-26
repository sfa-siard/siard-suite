package ch.admin.bar.siardsuite.model;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siard2.api.primary.ArchiveImpl;
import ch.admin.bar.siardsuite.database.DatabaseConnectionProperties;
import ch.admin.bar.siardsuite.database.DatabaseProperties;
import ch.admin.bar.siardsuite.model.database.DatabaseObject;
import ch.admin.bar.siardsuite.model.database.SiardArchive;
import ch.admin.bar.siardsuite.presenter.ArchiveBrowserPresenter;
import ch.admin.bar.siardsuite.visitor.ArchiveVisitor;
import ch.admin.bar.siardsuite.visitor.SiardArchiveMetaDataVisitor;
import ch.admin.bar.siardsuite.visitor.SiardArchiveVisitor;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.*;

public class Model {

    private View currentView = View.START;
    private TableSearchBase currentTableSearchBase = null;
    private TableSearchButton currentTableSearchButton = null;
    private ArchiveBrowserPresenter currentPreviewPresenter = null;
    private String currentTableSearch = null;
    private String currentMetaSearch = null;
    private DatabaseConnectionProperties dbConnectionProps = new DatabaseConnectionProperties();
    private Map<String, String> schemaMap = new HashMap<>();
    private SiardArchive siardArchive = new SiardArchive();
    public static final String TMP_SIARD = "tmp.siard";
    private Failure failure = null;

    public Model() {
    }

    public View getCurrentView() {
        return currentView;
    }

    public void setCurrentView(View view) {
        this.currentView = view;
    }

    public TableSearchBase getCurrentTableSearchBase() {
        return currentTableSearchBase;
    }

    public void setCurrentTableSearchBase(TableView<Map> tableView, LinkedHashSet<Map> rows) {
        currentTableSearchBase = new TableSearchBase(tableView, rows);
    }

    public TableSearchButton getCurrentTableSearchButton() {
        return currentTableSearchButton;
    }

    public void setCurrentTableSearchButton(MFXButton button, Boolean active) {
        currentTableSearchButton = new TableSearchButton(button, active);
    }

    public ArchiveBrowserPresenter getCurrentPreviewPresenter() {
        return currentPreviewPresenter;
    }

    public void setCurrentPreviewPresenter(ArchiveBrowserPresenter presenter) {
        currentPreviewPresenter = presenter;
    }

    public String getCurrentTableSearch() {
        return currentTableSearch;
    }

    public void setCurrentTableSearch(String s) {
        currentTableSearch = s;
    }

    public String getCurrentMetaSearch() {
        return currentMetaSearch;
    }

    public void setCurrentMetaSearch(String s) {
        currentMetaSearch = s;
    }

    public Archive initArchive() {
        try {
            return this.initArchive(File.createTempFile("tmp", ".siard"), true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Archive initArchive(File fileArchive, Boolean metaLoad) {
        if (fileArchive.exists()) {
            fileArchive.delete();
        }
        final Archive archive = ArchiveImpl.newInstance();
        try {
            archive.create(fileArchive);
            if (metaLoad) {
                fileArchive.deleteOnExit();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return archive;
    }

    public void setSiardArchive(String name, Archive archive) {
        setSiardArchive(name, archive, false);
    }

    public void setSiardArchive(String name, Archive archive, boolean onlyMetaData) {
        this.siardArchive = new SiardArchive(name, archive, onlyMetaData);
    }

    public SiardArchive getSiardArchive() {
        if (this.siardArchive == null) this.siardArchive = new SiardArchive();
        return siardArchive;
    }

    public void setDatabaseType(String databaseType) {
        if (this.dbConnectionProps == null) {
            this.dbConnectionProps = new DatabaseConnectionProperties();
        }
        this.dbConnectionProps.setDatabaseProduct(databaseType);
    }

    public void setConnectionUrl(String connectionUrl) {
        this.dbConnectionProps.setConnectionUrl(connectionUrl);
    }

    public DatabaseProperties getDatabaseProps() {
        return this.dbConnectionProps.getDatabaseProps();
    }

    public List<String> getDatabaseTypes() {
        return this.dbConnectionProps.getDatabaseTypes();
    }

    public StringProperty getDatabaseName() {
        return this.dbConnectionProps.getDatabaseName();
    }

    public StringProperty getDatabaseProduct() {
        return this.dbConnectionProps.getDatabaseProduct();
    }

    public StringProperty getConnectionUrl() {
        return this.dbConnectionProps.getConnectionUrl();
    }

    public StringProperty getDatabaseUsername() {
        return this.dbConnectionProps.getDatabaseUsername();
    }

    public void setDatabaseName(String databaseName) {
        this.dbConnectionProps.setDatabaseName(databaseName);
    }

    public void setUsername(String username) {
        this.dbConnectionProps.setDatabaseUsername(username);
    }

    public void setPassword(String password) {
        this.dbConnectionProps.setPassword(password);
    }

    public String getDatabasePassword() {
        return this.dbConnectionProps.getPassword();
    }

    // TODO: maybe use some sort of visitor or provider or...
    public void updateArchiveMetaData(String dbName, String description, String owner, String dataOriginTimespan,
                                      String archiverName, String archiverContact, URI lobFolder, File targetArchive) {
        getSiardArchive().addArchiveMetaData(dbName,
                                             description,
                                             owner,
                                             dataOriginTimespan,
                                             archiverName,
                                             archiverContact,
                                             lobFolder,
                                             targetArchive);
    }

    public void provideDatabaseArchiveProperties(SiardArchiveVisitor visitor) {
        getSiardArchive().shareProperties(visitor);
    }

    public void provideDatabaseArchiveProperties(SiardArchiveVisitor visitor, DatabaseObject databaseObject) {
        getSiardArchive().shareProperties(visitor, databaseObject);
    }

    public void populate(TableView<Map> tableView, DatabaseObject databaseObject, TreeContentView type) {
        getSiardArchive().populate(tableView, databaseObject, type);
    }

    public void populate(VBox vBox, DatabaseObject databaseObject, TreeContentView type) {
        getSiardArchive().populate(vBox, databaseObject, type);
    }

    public void populate(TreeItem root) {
        getSiardArchive().populate(root);
    }

    public void provideDatabaseArchiveMetaDataProperties(SiardArchiveMetaDataVisitor visitor) {
        getSiardArchive().shareProperties(visitor);
    }

    public void provideDatabaseArchiveMetaDataObject(SiardArchiveMetaDataVisitor visitor) {
        getSiardArchive().shareObject(visitor);
    }

    public void provideArchiveProperties(ArchiveVisitor visitor) {
        getSiardArchive().shareProperties(visitor);
    }

    public void provideArchiveObject(ArchiveVisitor visitor) {
        getSiardArchive().shareObject(visitor);
    }

    public TreeSet<MetaSearchHit> aggregatedMetaSearch(String s) {
        return getSiardArchive().aggregatedMetaSearch(s);
    }

    public void setSchemaMap(Map schemaMap) {
        this.schemaMap = schemaMap;
    }

    public Map<String, String> getSchemaMap() {
        return schemaMap;
    }

    public void setFailure(Failure failure) {
        this.failure = failure;
    }

    public Failure getFailure() {
        return this.failure;
    }

    public void clearSiardArchive() {
        this.siardArchive = new SiardArchive();
    }
}
