package ch.admin.bar.siardsuite.model;

public enum View {

  ROOT("fxml/root-view.fxml"),
  START("fxml/start-view.fxml"),
  ARCHIVE("fxml/archive-db-view.fxml"),
  ARCHIVE_STEPPER("fxml/archive-stepper-view.fxml"),
  DIALOG("fxml/dialog-view.fxml"),
  ARCHIVE_DB_DIALOG("fxml/archive-db-dialog-view.fxml");

  private String viewName;

  View(String v) {
    this.viewName = v;
  }

  public String getName() {
    return viewName;
  }

}
