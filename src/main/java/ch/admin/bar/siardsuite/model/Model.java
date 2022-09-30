package ch.admin.bar.siardsuite.model;


import java.util.List;

public class Model {

  private String currentView = View.START.getName();

  private SiardArchive archive = new SiardArchive();
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
    return this.archive.getDatabaseProduct();
  }


}
