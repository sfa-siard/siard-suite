package ch.admin.bar.siardsuite.model;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siard2.api.primary.ArchiveImpl;
import ch.admin.bar.siardsuite.database.DatabaseConnectionFactory;
import ch.admin.bar.siardsuite.database.DatabaseConnectionProperties;
import ch.admin.bar.siardsuite.model.database.DatabaseObject;
import ch.admin.bar.siardsuite.model.database.SiardArchive;
import ch.admin.bar.siardsuite.presenter.PreviewPresenter;
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
import java.util.*;

public class Model {

  private View currentView = View.START;
  private TableSearchBase currentTableSearchBase = null;
  private TableSearchButton currentTableSearchButton = null;
  private PreviewPresenter currentPreviewPresenter = null;
  private String currentTableSearch = null;
  private String currentMetaSearch = null;
  private DatabaseConnectionProperties dbConnectionProps = new DatabaseConnectionProperties();
  private Map<String, String> schemaMap = new HashMap<>();
  private SiardArchive archive = new SiardArchive();

  public Model() {}

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

  public  TableSearchButton getCurrentTableSearchButton() {
    return currentTableSearchButton;
  }

  public void setCurrentTableSearchButton(MFXButton button, Boolean active) {
    currentTableSearchButton = new TableSearchButton(button, active);
  }

  public PreviewPresenter getCurrentPreviewPresenter() {
    return currentPreviewPresenter;
  }

  public void setCurrentPreviewPresenter(PreviewPresenter presenter) {
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
    return this.initArchive(new File("sample.siard"));
  }

  public Archive initArchive(File fileArchive) {
    if (fileArchive.exists()) {
      fileArchive.delete();
    }
    final Archive archive = ArchiveImpl.newInstance();
    try {
      archive.create(fileArchive);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return archive;
  }

  public void setArchive(SiardArchive archive) {
    this.archive = archive;
  }

  public void setArchive(String name, Archive archive) {
    setArchive(name, archive, false);
  }

  public void setArchive(String name, Archive archive, boolean onlyMetaData) {
    this.archive = new SiardArchive(name, archive, onlyMetaData);
  }

  public SiardArchive getArchive() {
    return archive;
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

  public DatabaseConnectionProperties.DatabaseProperties getDatabaseProps() {
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

  // TODO: check if this is correctly placed in the model. I think the model should just represent the state of the application
  // loading the database is not a state... it's an effect

  public void closeDbConnection() {
    DatabaseConnectionFactory.disconnect();
  }

  // TODO: maybe use some sort of visitor or provider or...
  public void updateArchiveMetaData(String dbName, String description, String owner, String dataOriginTimespan, String archiverName,
                                    String archiverContact, File targetArchive) {
    getArchive().addArchiveMetaData(dbName, description, owner, dataOriginTimespan, archiverName, archiverContact, targetArchive);
  }

  public void provideDatabaseArchiveProperties(SiardArchiveVisitor visitor) {
    if (getArchive() != null) {
      getArchive().shareProperties(visitor);
    }
  }

  public void provideDatabaseArchiveProperties(SiardArchiveVisitor visitor, DatabaseObject databaseObject) {
    if (getArchive() != null) {
      getArchive().shareProperties(visitor, databaseObject);
    }
  }

  public void provideDatabaseArchiveObject(SiardArchiveVisitor visitor) {
    if (getArchive() != null) {
      getArchive().shareObject(visitor);
    }
  }

  public void populate(TableView<Map> tableView, DatabaseObject databaseObject, TreeContentView type) {
    if (getArchive() != null) {
      getArchive().populate(tableView, databaseObject, type);
    }
  }

  public void populate(VBox vBox, DatabaseObject databaseObject, TreeContentView type) {
    if (getArchive() != null) {
      getArchive().populate(vBox, databaseObject, type);
    }
  }

  public void populate(TreeItem root) {
    getArchive().populate(root);
  }

  public void provideDatabaseArchiveMetaDataProperties(SiardArchiveMetaDataVisitor visitor) {
    if (getArchive() != null) {
      getArchive().shareProperties(visitor);
    }
  }

  public void provideDatabaseArchiveMetaDataObject(SiardArchiveMetaDataVisitor visitor) {
    if (getArchive() != null) {
      getArchive().shareObject(visitor);
    }
  }

  public void provideArchiveProperties(ArchiveVisitor visitor) {
    if (getArchive() != null) {
      getArchive().shareProperties(visitor);
    }
  }

  public void provideArchiveObject(ArchiveVisitor visitor) {
    if (getArchive() != null) {
      getArchive().shareObject(visitor);
    }
  }

  public TreeSet<MetaSearchHit> aggregatedMetaSearch(String s) {
    TreeSet<MetaSearchHit> hits = new TreeSet<>();
    if (getArchive() != null) {
      hits = getArchive().aggregatedMetaSearch(s);
    }
    return hits;
  }

  public void setSchemaMap(Map schemaMap) {
    this.schemaMap = schemaMap;
  }

  public Map<String, String> getSchemaMap() {
    return schemaMap;
  }


}
