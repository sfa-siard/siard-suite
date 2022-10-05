package ch.admin.bar.siardsuite.model;


import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

import java.util.List;

public class Model {

  private String currentView = View.START.getName();

  private SiardArchive archive = new SiardArchive();

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
    if (this.archive == null) {
      this.archive = new SiardArchive();
    }
    this.archive.setDatabaseProduct(databaseType);
  }

  public void setConnectionUrl(String connectionUrl) {
    this.archive.setConnectionUrl(connectionUrl);
  }

  public List getDatabaseDriver() {
    return this.archive.getDatabaseProductInfo();
  }



  public void setDatabaseData(ReadOnlyObjectProperty<ObservableList<DataTable>> valueProperty) {

  }

  public StringProperty getSiardFormat() {
    return siardVersion;
  }

  public StringProperty getDatabaseName() {
    return this.archive.getDatabaseName();
  }

  public StringProperty getDatabaseProduct() {
    return this.archive.getDatabaseProduct();
  }

  public StringProperty getConnectionUrl() {
    return this.archive.getConnectionUrl();
  }

  public StringProperty getDatabaseUsername() {
    return this.archive.getDatabaseUsername();
  }

  public void setDatabaseName(String databaseName) {
    this.archive.setDatabaseName(databaseName);
  }

  public void setUsername(String username) {
    this.archive.setDatabaseUsername(username);
  }

  public void setPassword(String password) {
    this.archive.setPassword(password);
  }
}
