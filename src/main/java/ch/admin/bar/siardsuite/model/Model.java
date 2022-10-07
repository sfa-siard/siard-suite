package ch.admin.bar.siardsuite.model;


import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siard2.api.primary.ArchiveImpl;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

public class Model {

  private String currentView = View.START.getName();

  private DatabaseConnectionProperties dbConnectionProps = new DatabaseConnectionProperties();

  public Archive getArchiveImpl() {
    return archiveImpl;
  }

  public void setArchiveImpl(Archive archiveImpl) {
    this.archiveImpl = archiveImpl;
  }

  Archive archiveImpl = ArchiveImpl.newInstance();

  private StringProperty siardVersion = new SimpleStringProperty("2.1");

  public Model() {

  }
  public String getCurrentView() {
    return currentView;
  }

  public void setCurrentView(String currentView) {
    this.currentView = currentView;
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



  public void setDatabaseData(ReadOnlyObjectProperty<ObservableList<DataTable>> valueProperty) {

  }

  public StringProperty getSiardFormat() {
    return siardVersion;
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
}
