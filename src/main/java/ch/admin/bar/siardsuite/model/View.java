package ch.admin.bar.siardsuite.model;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.Workflow;
import ch.admin.bar.siardsuite.presenter.Presenter;
import ch.admin.bar.siardsuite.presenter.archive.ArchiveStepperPresenter;
import ch.admin.bar.siardsuite.presenter.archive.browser.OpenArchiveBrowser;
import ch.admin.bar.siardsuite.util.fxml.FXMLLoadHelper;
import ch.admin.bar.siardsuite.util.fxml.LoadedFxml;
import ch.admin.bar.siardsuite.view.RootStage;
import lombok.Getter;
import lombok.val;

import java.util.function.BiFunction;

public enum View {

    ROOT("fxml/root.fxml"),
    START("fxml/start.fxml"),
    ARCHIVE_STEPPER("fxml/archive/archive-stepper.fxml"),
    DIALOG("fxml/dialog.fxml"),
    ARCHIVE_DB_DIALOG("fxml/archive/archive-db-dialog.fxml"),
    ARCHIVE_ABORT_DIALOG("fxml/archive/archive-abort-dialog.fxml"),
    OPEN_SIARD_ARCHIVE_DIALOG("fxml/open/open-siard-archive-dialog.fxml"),
    OPEN_SIARD_ARCHIVE_PREVIEW(OpenArchiveBrowser.VIEW_CREATOR),
    EXPORT_SELECT_TABLES("fxml/export/export-select-tables-dialog.fxml"),
    EXPORT_SUCCESS("fxml/export/export-success-dialog.fxml"),
    INFO_DIALOG("fxml/info/info-dialog.fxml"),
    OPTION_DIALOG("fxml/option/option-dialog.fxml"),
    UPLOAD_STEPPER("fxml/upload/upload-stepper.fxml"),
    UPLOAD_ABORT_DIALOG("fxml/upload/upload-abort-dialog.fxml"),
    UPLOAD_DB_CONNECTION("fxml/upload/upload-db-connection.fxml");

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
