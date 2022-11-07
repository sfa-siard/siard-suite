package ch.admin.bar.siardsuite.model;

public enum View {

  ROOT("fxml/root-view.fxml"),
  START("fxml/start-view.fxml"),
  ARCHIVE_DB("fxml/archive/archive-choose-dbms-view.fxml"),
  ARCHIVE_CONNECTION("fxml/archive/archive-connection-view.fxml"),
  ARCHIVE_STEPPER("fxml/archive/archive-stepper-view.fxml"),
  DIALOG("fxml/dialog-view.fxml"),
  ARCHIVE_DB_DIALOG("fxml/archive/archive-db-dialog-view.fxml"),
  ARCHIVE_ABORT_DIALOG("fxml/archive/archive-abort-dialog-view.fxml"),
  ARCHIVE_PREVIEW("fxml/archive/archive-preview-view.fxml"),
  ARCHIVE_LOADING_PREVIEW("fxml/archive/archive-loading-preview-view.fxml"),
  ARCHIVE_METADATA_EDITOR("fxml/archive/archive-metadata-editor-view.fxml"),
  ARCHIVE_DOWNLOAD("fxml/archive/archive-download-view.fxml"),
  OPEN_SIARD_ARCHIVE_DIALOG("fxml/open/open-siard-archive-dialog-view.fxml"),
  OPEN_SIARD_ARCHIVE_PREVIEW("fxml/open/open-preview-view.fxml"),
  EXPORT_SELECT_TABLES("fxml/export/export-select-tables-dialog.fxml"),
  EXPORT_SUCCESS("fxml/export/export-success-dialog.fxml"),
  UPLOAD_DB_CONNECTION_DIALOG("fxml/upload/upload-db-dialog.fxml"),
  UPLOAD_STEPPER("fxml/upload/upload-stepper.fxml"),
  UPLOAD_CHOOSE_DBMS("fxml/upload/upload-choose-dbms.fxml"),
  UPLOAD_ABORT_DIALOG("fxml/upload/upload-abort-dialog.fxml"),
  UPLOAD_DB_CONNECTION("fxml/upload/upload-db-connection.fxml"),
  UPLOADING("fxml/upload/upload-uploading.fxml"),
  UPLOAD_DB_CONNECTION("fxml/upload/upload-db-connection.fxml"),
  SEARCH_METADATA_DIALOG("fxml/search/search-metadata-dialog-view.fxml");

  private final String viewName;

  View(String v) {
    this.viewName = v;
  }

  public String getName() {
    return viewName;
  }

}
