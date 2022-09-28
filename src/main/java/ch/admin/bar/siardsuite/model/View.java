package ch.admin.bar.siardsuite.model;

public enum View {

  ROOT("fxml/root-view.fxml"),
  START("fxml/start-view.fxml"),
  ARCHIVE("fxml/archive/archive-db-view.fxml"),
  ARCHIVE_STEPPER("fxml/archive/archive-stepper-view.fxml"),
  DIALOG("fxml/dialog-view.fxml"),
  ARCHIVE_DB_DIALOG("fxml/archive/archive-db-dialog-view.fxml"),
  ARCHIVE_ABORT_DIALOG("fxml/archive/archive-abort-dialog-view.fxml");

  private final String viewName;

  View(String v) {
    this.viewName = v;
  }

  public String getName() {
    return viewName;
  }

}
