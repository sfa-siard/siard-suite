package ch.admin.bar.siardsuite.model;


public class Model {

  private String currentView = View.START.getName();

  public Model() {

  }
  public String getCurrentView() {
    return currentView;
  }

  public void setCurrentView(String currentView) {
    this.currentView = currentView;
  }



}
