package ch.admin.bar.siardsuite.model;


public class Model {

  private String currentView = View.START.getName();

  private SiardArchive archive;
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
}
