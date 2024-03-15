package ch.admin.bar.siardsuite.model;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siardsuite.Workflow;
import ch.admin.bar.siardsuite.framework.dialogs.LegacyShowDialogTarget;
import ch.admin.bar.siardsuite.framework.dialogs.ShowDialogTarget;
import ch.admin.bar.siardsuite.framework.navigation.LegacyNavigationTarget;
import ch.admin.bar.siardsuite.framework.navigation.NavigationTarget;
import ch.admin.bar.siardsuite.framework.navigation.SimpleNavigationTarget;
import ch.admin.bar.siardsuite.presenter.StartPresenter;
import ch.admin.bar.siardsuite.presenter.archive.browser.OpenArchiveBrowser;
import ch.admin.bar.siardsuite.presenter.open.OpenSiardArchiveDialogPresenter;

import java.io.File;
import java.util.function.BiConsumer;

public class View {
    public static final SimpleNavigationTarget START = new SimpleNavigationTarget(StartPresenter::load);
    public static final NavigationTarget<Workflow> START_WITH_WORKFLOW = new NavigationTarget<>(StartPresenter::load);
    public static final LegacyNavigationTarget ARCHIVE_STEPPER = new LegacyNavigationTarget("fxml/archive/archive-stepper.fxml");
    public static final LegacyNavigationTarget OPEN_SIARD_ARCHIVE_PREVIEW = new LegacyNavigationTarget(OpenArchiveBrowser::load);
    public static final LegacyNavigationTarget UPLOAD_STEPPER = new LegacyNavigationTarget("fxml/upload/upload-stepper.fxml");

    public static final ShowDialogTarget<BiConsumer<File, Archive>> OPEN_SIARD_ARCHIVE_DIALOG = new ShowDialogTarget<>(OpenSiardArchiveDialogPresenter::load);
    public static final LegacyShowDialogTarget EXPORT_SELECT_TABLES = new LegacyShowDialogTarget("fxml/export/export-select-tables-dialog.fxml");
    public static final LegacyShowDialogTarget EXPORT_SUCCESS = new LegacyShowDialogTarget("fxml/export/export-success-dialog.fxml");
    public static final LegacyShowDialogTarget INFO_DIALOG = new LegacyShowDialogTarget("fxml/info/info-dialog.fxml");
    public static final LegacyShowDialogTarget OPTION_DIALOG = new LegacyShowDialogTarget("fxml/option/option-dialog.fxml");
    public static final LegacyShowDialogTarget UPLOAD_ABORT_DIALOG = new LegacyShowDialogTarget("fxml/upload/upload-abort-dialog.fxml");
    public static final LegacyShowDialogTarget ARCHIVE_ABORT_DIALOG = new LegacyShowDialogTarget("fxml/archive/archive-abort-dialog.fxml");
    public static final LegacyShowDialogTarget SEARCH_TABLE_DIALOG = new LegacyShowDialogTarget("fxml/search/search-table-dialog.fxml");
    public static final LegacyShowDialogTarget SEARCH_METADATA_DIALOG = new LegacyShowDialogTarget("fxml/search/search-metadata-dialog.fxml");
    public static final LegacyShowDialogTarget ERROR_DIALOG = new LegacyShowDialogTarget("fxml/error-dialog.fxml");
    public static final LegacyShowDialogTarget ARCHIVE_DB_DIALOG = new LegacyShowDialogTarget("fxml/archive/archive-db-dialog.fxml");
}
