package ch.admin.bar.siardsuite.model;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.Workflow;
import ch.admin.bar.siardsuite.presenter.Presenter;
import ch.admin.bar.siardsuite.presenter.archive.browser.OpenArchiveBrowser;
import ch.admin.bar.siardsuite.presenter.archive.browser.PreviewArchiveBrowser;
import ch.admin.bar.siardsuite.util.fxml.FXMLLoadHelper;
import ch.admin.bar.siardsuite.util.fxml.LoadedFxml;
import ch.admin.bar.siardsuite.view.RootStage;
import lombok.Getter;
import lombok.val;

import java.util.function.BiFunction;

public enum View {

    ROOT("fxml/root.fxml"),
    START("fxml/start.fxml"),
    ARCHIVE_DB("fxml/archive/archive-choose-dbms.fxml"),
    ARCHIVE_CONNECTION("fxml/archive/archive-connection.fxml"),
    ARCHIVE_STEPPER("fxml/archive/archive-stepper.fxml"),
    DIALOG("fxml/dialog.fxml"),
    ARCHIVE_DB_DIALOG("fxml/archive/archive-db-dialog.fxml"),
    ARCHIVE_ABORT_DIALOG("fxml/archive/archive-abort-dialog.fxml"),
    ARCHIVE_PREVIEW(PreviewArchiveBrowser.VIEW_CREATOR),
    ARCHIVE_LOADING_PREVIEW("fxml/archive/archive-loading-preview.fxml"),
    ARCHIVE_METADATA_EDITOR("fxml/archive/archive-metadata-editor.fxml"),
    ARCHIVE_DOWNLOAD("fxml/archive/archive-download.fxml"),
    OPEN_SIARD_ARCHIVE_DIALOG("fxml/open/open-siard-archive-dialog.fxml"),
    OPEN_SIARD_ARCHIVE_PREVIEW(OpenArchiveBrowser.VIEW_CREATOR),
    EXPORT_SELECT_TABLES("fxml/export/export-select-tables-dialog.fxml"),
    EXPORT_SUCCESS("fxml/export/export-success-dialog.fxml"),
    INFO_DIALOG("fxml/info/info-dialog.fxml"),
    OPTION_DIALOG("fxml/option/option-dialog.fxml"),
    UPLOAD_STEPPER("fxml/upload/upload-stepper.fxml"),
    UPLOAD_CHOOSE_DBMS("fxml/upload/upload-choose-dbms.fxml"),
    UPLOAD_ABORT_DIALOG("fxml/upload/upload-abort-dialog.fxml"),
    UPLOAD_DB_CONNECTION("fxml/upload/upload-db-connection.fxml"),
    UPLOADING("fxml/upload/upload-uploading.fxml"),
    UPLOAD_RESULT("fxml/upload/upload-result.fxml"),
    SEARCH_TABLE_DIALOG("fxml/search/search-table-dialog.fxml"),
    SEARCH_METADATA_DIALOG("fxml/search/search-metadata-dialog.fxml"),
    ERROR_DIALOG("fxml/error-dialog.fxml");

    @Getter
    private final BiFunction<Controller, RootStage, LoadedFxml<Presenter>> viewCreator;

    View(String v) {
        this.viewCreator = (controller, rootStage) -> {
            val loaded = FXMLLoadHelper.<Presenter>load(v);
            loaded.getController().init(controller, rootStage);
            return loaded;
        };
    }

    View(BiFunction<Controller, RootStage, LoadedFxml<Presenter>> viewCreator) {
        this.viewCreator = viewCreator;
    }

    public static View forWorkflow(Workflow workflow) {
        if (Workflow.ARCHIVE.equals(workflow)) return ARCHIVE_DB_DIALOG;
        return OPEN_SIARD_ARCHIVE_DIALOG;
    }
}
