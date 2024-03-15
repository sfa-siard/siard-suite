package ch.admin.bar.siardsuite.model;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siardsuite.Workflow;
import ch.admin.bar.siardsuite.framework.dialogs.ShowDialogTarget;
import ch.admin.bar.siardsuite.framework.dialogs.SimpleShowDialogTarget;
import ch.admin.bar.siardsuite.framework.navigation.NavigationTarget;
import ch.admin.bar.siardsuite.framework.navigation.SimpleNavigationTarget;
import ch.admin.bar.siardsuite.presenter.StartPresenter;
import ch.admin.bar.siardsuite.presenter.archive.ArchiveStepperPresenter;
import ch.admin.bar.siardsuite.presenter.archive.browser.OpenArchiveBrowser;
import ch.admin.bar.siardsuite.presenter.common.RecentConnectionsDialogPresenter;
import ch.admin.bar.siardsuite.presenter.export.ExportSelectTablesDialogPresenter;
import ch.admin.bar.siardsuite.presenter.export.ExportSuccessDialogPresenter;
import ch.admin.bar.siardsuite.presenter.info.InfoDialogPresenter;
import ch.admin.bar.siardsuite.presenter.open.OpenSiardArchiveDialogPresenter;
import ch.admin.bar.siardsuite.presenter.option.OptionDialogPresenter;
import ch.admin.bar.siardsuite.presenter.common.AbortDialogPresenter;
import ch.admin.bar.siardsuite.presenter.upload.UploadStepperPresenter;
import ch.admin.bar.siardsuite.service.preferences.RecentDbConnection;

import java.io.File;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class View {
    public static final SimpleNavigationTarget START = new SimpleNavigationTarget(StartPresenter::load);
    public static final NavigationTarget<Workflow> START_WITH_WORKFLOW = new NavigationTarget<>(StartPresenter::load);
    public static final NavigationTarget<Archive> OPEN_SIARD_ARCHIVE_PREVIEW = new NavigationTarget<>(OpenArchiveBrowser::load);

    public static final NavigationTarget<Optional<RecentDbConnection>> ARCHIVE_STEPPER = new NavigationTarget<>(ArchiveStepperPresenter::load);
    public static final NavigationTarget<Archive> UPLOAD_STEPPER = new NavigationTarget<>(UploadStepperPresenter::load);
    public static final NavigationTarget<Tuple<Archive, RecentDbConnection>> UPLOAD_STEPPER_WITH_RECENT_CONNECTION = new NavigationTarget<>(UploadStepperPresenter::loadWithRecentConnection);

    public static final ShowDialogTarget<BiConsumer<File, Archive>> OPEN_SIARD_ARCHIVE_DIALOG = new ShowDialogTarget<>(OpenSiardArchiveDialogPresenter::load);
    public static final ShowDialogTarget<Archive> EXPORT_SELECT_TABLES = new ShowDialogTarget<>(ExportSelectTablesDialogPresenter::load);

    public static final SimpleShowDialogTarget EXPORT_SUCCESS = new SimpleShowDialogTarget(ExportSuccessDialogPresenter::load);
    public static final SimpleShowDialogTarget INFO_DIALOG = new SimpleShowDialogTarget(InfoDialogPresenter::load);
    public static final SimpleShowDialogTarget OPTION_DIALOG = new SimpleShowDialogTarget(OptionDialogPresenter::load);
    public static final SimpleShowDialogTarget UPLOAD_ABORT_DIALOG = new SimpleShowDialogTarget(AbortDialogPresenter::loadForUpload);
    public static final SimpleShowDialogTarget ARCHIVE_ABORT_DIALOG = new SimpleShowDialogTarget(AbortDialogPresenter::loadForArchive);

    public static final ShowDialogTarget<Consumer<Optional<RecentDbConnection>>> RECENT_CONNECTIONS_FOR_ARCHIVING = new ShowDialogTarget<>(RecentConnectionsDialogPresenter::loadForArchiving); //
    public static final ShowDialogTarget<Consumer<Optional<RecentDbConnection>>> RECENT_CONNECTIONS_FOR_UPLOAD = new ShowDialogTarget<>(RecentConnectionsDialogPresenter::loadForUpload); //
}
