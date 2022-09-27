package ch.admin.bar.siardsuite;

import ch.admin.bar.siardsuite.model.Model;

public class Controller {

  private final Model model;

  public Controller(Model model) {
    this.model = model;
  }

  public void setDatabaseType(String databaseType) {
    model.setDatabaseType(databaseType);
  }

}
