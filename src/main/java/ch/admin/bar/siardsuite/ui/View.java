package ch.admin.bar.siardsuite.ui;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siardsuite.model.Failure;
import ch.admin.bar.siardsuite.model.TreeAttributeWrapper;
import ch.admin.bar.siardsuite.model.Tuple;
import ch.admin.bar.siardsuite.ui.common.Workflow;
import ch.admin.bar.siardsuite.ui.component.rendering.TreeItemsExplorer;
import ch.admin.bar.siardsuite.framework.dialogs.ShowDialogTarget;
import ch.admin.bar.siardsuite.framework.dialogs.SimpleShowDialogTarget;
import ch.admin.bar.siardsuite.framework.navigation.NavigationTarget;
import ch.admin.bar.siardsuite.framework.navigation.SimpleNavigationTarget;
import ch.admin.bar.siardsuite.framework.errors.WarningDefinition;
import ch.admin.bar.siardsuite.ui.presenter.ErrorDialogPresenter;
import ch.admin.bar.siardsuite.ui.presenter.StartPresenter;
import ch.admin.bar.siardsuite.ui.presenter.archive.ArchiveStepperPresenter;
import ch.admin.bar.siardsuite.ui.presenter.archive.browser.OpenArchiveBrowser;
import ch.admin.bar.siardsuite.ui.presenter.archive.browser.dialogues.SearchMetadataDialogPresenter;
import ch.admin.bar.siardsuite.ui.presenter.archive.browser.dialogues.SearchTableDialogPresenter;
import ch.admin.bar.siardsuite.ui.presenter.archive.browser.dialogues.UnsavedChangesDialogPresenter;
import ch.admin.bar.siardsuite.ui.presenter.common.RecentConnectionsDialogPresenter;
import ch.admin.bar.siardsuite.ui.presenter.export.ExportSelectTablesDialogPresenter;
import ch.admin.bar.siardsuite.ui.presenter.export.ExportSuccessDialogPresenter;
import ch.admin.bar.siardsuite.ui.presenter.info.InfoDialogPresenter;
import ch.admin.bar.siardsuite.ui.presenter.open.OpenSiardArchiveDialogPresenter;
import ch.admin.bar.siardsuite.ui.presenter.option.OptionDialogPresenter;
import ch.admin.bar.siardsuite.ui.presenter.common.AbortDialogPresenter;
import ch.admin.bar.siardsuite.ui.presenter.upload.UploadStepperPresenter;
import ch.admin.bar.siardsuite.service.preferences.RecentDbConnection;
import javafx.scene.control.TreeItem;

import java.io.File;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class View {
    public static final SimpleNavigationTarget START =
            new SimpleNavigationTarget(StartPresenter::load);
    public static final NavigationTarget<Workflow> START_WITH_WORKFLOW =
            new NavigationTarget<>(StartPresenter::load);
    public static final NavigationTarget<Archive> OPEN_SIARD_ARCHIVE_PREVIEW =
            new NavigationTarget<>(OpenArchiveBrowser::load);
    public static final NavigationTarget<Optional<RecentDbConnection>> ARCHIVE_STEPPER =
            new NavigationTarget<>(ArchiveStepperPresenter::load);
    public static final NavigationTarget<Archive> UPLOAD_STEPPER =
            new NavigationTarget<>(UploadStepperPresenter::load);
    public static final NavigationTarget<Tuple<Archive, RecentDbConnection>> UPLOAD_STEPPER_WITH_RECENT_CONNECTION =
            new NavigationTarget<>(UploadStepperPresenter::loadWithRecentConnection);

    public static final ShowDialogTarget<BiConsumer<File, Archive>> OPEN_SIARD_ARCHIVE_DIALOG =
            new ShowDialogTarget<>(OpenSiardArchiveDialogPresenter::load);
    public static final ShowDialogTarget<Archive> EXPORT_SELECT_TABLES =
            new ShowDialogTarget<>(ExportSelectTablesDialogPresenter::load);
    public static final SimpleShowDialogTarget EXPORT_SUCCESS =
            new SimpleShowDialogTarget(ExportSuccessDialogPresenter::load);
    public static final SimpleShowDialogTarget INFO_DIALOG =
            new SimpleShowDialogTarget(InfoDialogPresenter::load);
    public static final SimpleShowDialogTarget OPTION_DIALOG =
            new SimpleShowDialogTarget(OptionDialogPresenter::load);
    public static final SimpleShowDialogTarget UPLOAD_ABORT_DIALOG =
            new SimpleShowDialogTarget(AbortDialogPresenter::loadForUpload);
    public static final SimpleShowDialogTarget ARCHIVE_ABORT_DIALOG =
            new SimpleShowDialogTarget(AbortDialogPresenter::loadForArchive);
    public static final ShowDialogTarget<Consumer<Optional<RecentDbConnection>>> RECENT_CONNECTIONS_FOR_ARCHIVING =
            new ShowDialogTarget<>(RecentConnectionsDialogPresenter::loadForArchiving);
    public static final ShowDialogTarget<Consumer<Optional<RecentDbConnection>>> RECENT_CONNECTIONS_FOR_UPLOAD =
            new ShowDialogTarget<>(RecentConnectionsDialogPresenter::loadForUpload);
    public static final ShowDialogTarget<Consumer<UnsavedChangesDialogPresenter.Result>> UNSAVED_CHANGES =
            new ShowDialogTarget<>(UnsavedChangesDialogPresenter::load);
    public static final ShowDialogTarget<Consumer<Optional<String>>> SEARCH_TABLE =
            new ShowDialogTarget<>(SearchTableDialogPresenter::load);
    public static final ShowDialogTarget<Tuple<TreeItemsExplorer, Consumer<TreeItem<TreeAttributeWrapper>>>> SEARCH_METADATA =
            new ShowDialogTarget<>(SearchMetadataDialogPresenter::load);

    public static final ShowDialogTarget<Failure> ERROR =
            new ShowDialogTarget<>(ErrorDialogPresenter::load);
    public static final ShowDialogTarget<WarningDefinition> WARNING =
            new ShowDialogTarget<>(ErrorDialogPresenter::load);
}
