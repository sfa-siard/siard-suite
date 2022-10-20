package ch.admin.bar.siardsuite.model;

public enum View {

  ROOT("fxml/root-view.fxml"),
  START("fxml/start-view.fxml"),
  ARCHIVE_DB("fxml/archive/archive-db-view.fxml"),
  ARCHIVE_CONNECTION("fxml/archive/archive-connection-view.fxml"),
  ARCHIVE_STEPPER("fxml/archive/archive-stepper-view.fxml"),
  DIALOG("fxml/dialog-view.fxml"),
  ARCHIVE_DB_DIALOG("fxml/archive/archive-db-dialog-view.fxml"),
  ARCHIVE_ABORT_DIALOG("fxml/archive/archive-abort-dialog-view.fxml"),
  ARCHIVE_PREVIEW("fxml/archive/archive-preview-view.fxml"),
  ARCHIVE_LOADING_PREVIEW("fxml/archive/archive-loading-preview-view.fxml"),
  ARCHIVE_METADATA_EDITOR("fxml/archive/archive-metadata-editor-view.fxml"),
  OPEN_SIARD_ARCHIVE_DIALOG("fxml/open/open-siard-archive-dialog-view.fxml"),
  OPEN_SIARD_ARCHIVE_PREVIEW("fxml/open/open-preview-view.fxml");

  private final String viewName;

  View(String v) {
    this.viewName = v;
  }

  public String getName() {
    return viewName;
  }

}
