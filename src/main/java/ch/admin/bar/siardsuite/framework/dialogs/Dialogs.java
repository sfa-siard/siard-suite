package ch.admin.bar.siardsuite.framework.dialogs;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.component.rendering.TreeItemsExplorer;
import ch.admin.bar.siardsuite.framework.general.ServicesFacade;
import ch.admin.bar.siardsuite.model.TreeAttributeWrapper;
import ch.admin.bar.siardsuite.presenter.archive.browser.dialogues.SearchMetadataDialogPresenter;
import ch.admin.bar.siardsuite.presenter.archive.browser.dialogues.SearchTableDialogPresenter;
import ch.admin.bar.siardsuite.presenter.archive.browser.dialogues.UnsavedChangesDialogPresenter;
import ch.admin.bar.siardsuite.presenter.archive.dialogs.ArchiveRecentConnectionsDialogPresenter;
import ch.admin.bar.siardsuite.util.preferences.RecentDbConnection;
import ch.admin.bar.siardsuite.view.DialogCloser;
import ch.admin.bar.siardsuite.view.RootStage;
import javafx.scene.control.TreeItem;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * Defines a set of methods for displaying various dialogs.
 */
@RequiredArgsConstructor
public class Dialogs implements DialogCloser {

    private final Controller controller;
    private final RootStage rootStage;
    private final ServicesFacade servicesFacade = ServicesFacade.INSTANCE; // TODO

    /**
     * Opens the specified view as a dialog.
     */
    @Deprecated
    public void openDialog(LegacyShowDialogTarget target) {
        val loaded = target.getViewSupplier().apply(controller, rootStage);
        rootStage.displayDialog(loaded);
    }

    public <T> void open(final ShowDialogTarget<T> target, final T data) {
        val loaded = target.getViewSupplier().apply(data, servicesFacade);
        rootStage.displayDialog(loaded.getNode());
    }

    @Override
    @Deprecated
    public void closeDialog() {
        rootStage.closeDialog();
    }

    /**
     * Opens a dialog for handling unsaved changes, invoking the specified callback upon completion.
     */
    public void openUnsavedChangesDialog(final Consumer<UnsavedChangesDialogPresenter.Result> resultCallback) {
        val loaded = UnsavedChangesDialogPresenter.load(result -> {
            rootStage.closeDialog();
            resultCallback.accept(result);
        });

        rootStage.displayDialog(loaded.getNode());
    }

    /**
     * Opens a dialog for searching tables, invoking the specified callback with the search term upon completion.
     */
    public void openSearchTableDialog(final Consumer<Optional<String>> searchTermConsumer) {
        val loaded = SearchTableDialogPresenter.load(rootStage::closeDialog, searchTermConsumer);

        rootStage.displayDialog(loaded.getNode());
    }

    /**
     * Opens a dialog for searching metadata using the provided tree items explorer. The specified callback
     * will be invoked, after a result will be selected.
     */
    public void openSearchMetaDataDialog(
            final TreeItemsExplorer treeItemsExplorer,
            final Consumer<TreeItem<TreeAttributeWrapper>> onSelected) {
        val loaded = SearchMetadataDialogPresenter.load(
                rootStage::closeDialog,
                treeItemsExplorer,
                onSelected
        );

        rootStage.displayDialog(loaded.getNode());
    }

    /**
     * Opens a dialog for selecting recent database connections for archiving.
     *
     * @param onNewConnection            The callback to be invoked when creating a new connection.
     * @param onRecentConnectionSelected The callback to be invoked with the selected recent database connection.
     */
    public void openRecentConnectionsDialogForArchiving(
            final Runnable onNewConnection,
            final Consumer<RecentDbConnection> onRecentConnectionSelected
    ) {
        val loaded = ArchiveRecentConnectionsDialogPresenter.loadForArchiving(
                rootStage::closeDialog,
                onNewConnection,
                onRecentConnectionSelected
        );

        rootStage.displayDialog(loaded.getNode());
    }

    /**
     * Opens a dialog for selecting recent database connections for uploading.
     *
     * @param onNewConnection            The callback to be invoked when creating a new connection.
     * @param onRecentConnectionSelected The callback to be invoked with the selected recent database connection.
     */
    public void openRecentConnectionsDialogForUploading(
            final Runnable onNewConnection,
            final Consumer<RecentDbConnection> onRecentConnectionSelected
    ) {
        val loaded = ArchiveRecentConnectionsDialogPresenter.loadForUpload(
                rootStage::closeDialog,
                onNewConnection,
                onRecentConnectionSelected
        );

        rootStage.displayDialog(loaded.getNode());
    }
}
