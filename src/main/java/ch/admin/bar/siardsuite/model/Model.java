package ch.admin.bar.siardsuite.model;

import ch.admin.bar.siardsuite.util.View;

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
