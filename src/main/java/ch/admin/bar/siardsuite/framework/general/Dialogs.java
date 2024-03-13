package ch.admin.bar.siardsuite.framework.general;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siardsuite.component.rendering.TreeItemsExplorer;
import ch.admin.bar.siardsuite.model.TreeAttributeWrapper;
import ch.admin.bar.siardsuite.model.View;
import ch.admin.bar.siardsuite.presenter.archive.browser.dialogues.UnsavedChangesDialogPresenter;
import ch.admin.bar.siardsuite.util.preferences.RecentDbConnection;
import javafx.scene.control.TreeItem;

import java.io.File;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Defines a set of methods for displaying various dialogs.
 */
public interface Dialogs {
    /**
     * Opens the specified view as a dialog.
     *
     * @param view The view to be opened as a dialog.
     * @deprecated This method is deprecated and should not be used. It exists only for compatibility
     * reasons (legacy way to display a dialog) and will be removed in further releases.
     */
    @Deprecated
    void openDialog(View view);

    /**
     * Opens a dialog for handling unsaved changes, invoking the specified callback upon completion.
     */
    void openUnsavedChangesDialog(final Consumer<UnsavedChangesDialogPresenter.Result> resultCallback);

    /**
     * Opens a dialog for searching tables, invoking the specified callback with the search term upon completion.
     */
    void openSearchTableDialog(final Consumer<Optional<String>> searchTermConsumer);

    /**
     * Opens a dialog for searching metadata using the provided tree items explorer. The specified callback
     * will be invoked, after a result will be selected.
     */
    void openSearchMetaDataDialog(
            final TreeItemsExplorer treeItemsExplorer,
            final Consumer<TreeItem<TreeAttributeWrapper>> onSelected);

    /**
     * Opens a dialog for selecting recent database connections for archiving.
     *
     * @param onNewConnection The callback to be invoked when creating a new connection.
     * @param onRecentConnectionSelected The callback to be invoked with the selected recent database connection.
     */
    void openRecentConnectionsDialogForArchiving(
            final Runnable onNewConnection,
            final Consumer<RecentDbConnection> onRecentConnectionSelected
    );

    /**
     * Opens a dialog for selecting recent database connections for uploading.
     *
     * @param onNewConnection The callback to be invoked when creating a new connection.
     * @param onRecentConnectionSelected The callback to be invoked with the selected recent database connection.
     */
    void openRecentConnectionsDialogForUploading(
            final Runnable onNewConnection,
            final Consumer<RecentDbConnection> onRecentConnectionSelected
    );

    /**
     * Opens a dialog for selecting a SIARD file, invoking the specified callback upon selection.
     */
    void openSelectSiardFileDialog(final BiConsumer<File, Archive> onArchiveSelected);
}
