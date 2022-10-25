package ch.admin.bar.siardsuite.model;


import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siard2.api.primary.ArchiveImpl;
import ch.admin.bar.siardsuite.database.DbConnectionFactory;
import ch.admin.bar.siardsuite.model.database.DatabaseArchive;
import ch.admin.bar.siardsuite.visitor.DatabaseArchiveMetaDataVisitor;
import ch.admin.bar.siardsuite.visitor.DatabaseArchiveVisitor;
import javafx.beans.property.StringProperty;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Model {

  private View currentView = View.START;
  private DatabaseConnectionProperties dbConnectionProps = new DatabaseConnectionProperties();
  private DatabaseArchive archive = new DatabaseArchive();

  public Model() {}

  public View getCurrentView() {
    return currentView;
  }

  public void setCurrentView(View view) {
    this.currentView = view;
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

  public void setArchive(String name, Archive archive) {
    this.archive = new DatabaseArchive(name, archive);
  }

  public DatabaseArchive getArchive() {
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
    DbConnectionFactory.disconnect();
  }

  // TODO: maybe use some sort of visitor or provider or...
  public void updateArchiveMetaData(String siardFormatVersion, String dbName, String dbProduct, String connection,
                                    String dbUser, String description, String owner, String databaseCreationDate,
                                    String archivingDate, String archiverName, String archiverContact, File targetArchive) {
    getArchive().addArchiveMetaData(siardFormatVersion, dbName, dbProduct, connection, dbUser,
            description, owner, databaseCreationDate, archivingDate, archiverName, archiverContact, targetArchive);
  }

  public void provideDatabaseArchiveProperties(DatabaseArchiveVisitor visitor) {
    if (getArchive() != null) {
      getArchive().shareProperties(visitor);
    }
  }

  public void provideDatabaseArchiveMetaDataProperties(DatabaseArchiveMetaDataVisitor visitor) {
    if (getArchive() != null) {
      getArchive().shareProperties(visitor);
    }
  }

  public void provideDatabaseArchiveMetaDataObject(DatabaseArchiveMetaDataVisitor visitor) {
    if (getArchive() != null) {
      getArchive().shareObject(visitor);
    }
  }

}
