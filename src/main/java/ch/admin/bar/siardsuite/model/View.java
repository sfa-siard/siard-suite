package ch.admin.bar.siardsuite.model;

import ch.admin.bar.siardsuite.Workflow;

public enum View {

    ROOT("fxml/root.fxml"),
    START("fxml/start.fxml"),
    ARCHIVE_DB("fxml/archive/archive-choose-dbms.fxml"),
    ARCHIVE_CONNECTION("fxml/archive/archive-connection.fxml"),
    ARCHIVE_STEPPER("fxml/archive/archive-stepper.fxml"),
    DIALOG("fxml/dialog.fxml"),
    ARCHIVE_DB_DIALOG("fxml/archive/archive-db-dialog.fxml"),
    ARCHIVE_ABORT_DIALOG("fxml/archive/archive-abort-dialog.fxml"),
    ARCHIVE_PREVIEW("fxml/archive/archive-preview.fxml"),
    ARCHIVE_LOADING_PREVIEW("fxml/archive/archive-loading-preview.fxml"),
    ARCHIVE_METADATA_EDITOR("fxml/archive/archive-metadata-editor.fxml"),
    ARCHIVE_DOWNLOAD("fxml/archive/archive-download.fxml"),
    OPEN_SIARD_ARCHIVE_DIALOG("fxml/open/open-siard-archive-dialog.fxml"),
    OPEN_SIARD_ARCHIVE_PREVIEW("fxml/open/open-preview.fxml"),
    EXPORT_SELECT_TABLES("fxml/export/export-select-tables-dialog.fxml"),
    EXPORT_SUCCESS("fxml/export/export-success-dialog.fxml"),
    INFO_DIALOG("fxml/info/info-dialog.fxml"),
    OPTION_DIALOG("fxml/option/option-dialog.fxml"),
    UPLOAD_DB_CONNECTION_DIALOG("fxml/upload/upload-db-dialog.fxml"),
    UPLOAD_STEPPER("fxml/upload/upload-stepper.fxml"),
    UPLOAD_CHOOSE_DBMS("fxml/upload/upload-choose-dbms.fxml"),
    UPLOAD_ABORT_DIALOG("fxml/upload/upload-abort-dialog.fxml"),
    UPLOAD_DB_CONNECTION("fxml/upload/upload-db-connection.fxml"),
    UPLOADING("fxml/upload/upload-uploading.fxml"),
    UPLOAD_RESULT("fxml/upload/upload-result.fxml"),
    SEARCH_TABLE_DIALOG("fxml/search/search-table-dialog.fxml"),
    SEARCH_METADATA_DIALOG("fxml/search/search-metadata-dialog.fxml"),
    ERROR_DIALOG("fxml/error-dialog.fxml");

    private final String viewName;

    View(String v) {
        this.viewName = v;
    }

    public static View forWorkflow(Workflow workflow) {
        if (Workflow.ARCHIVE.equals(workflow)) return ARCHIVE_DB_DIALOG;
        return OPEN_SIARD_ARCHIVE_DIALOG;
    }

    public String getName() {
        return viewName;
    }

}
