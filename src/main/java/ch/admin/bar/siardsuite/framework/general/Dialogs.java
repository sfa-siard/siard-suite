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

public interface Dialogs {
    @Deprecated
    void openDialog(View view);

    void openUnsavedChangesDialog(final Consumer<UnsavedChangesDialogPresenter.Result> resultCallback);

    void openSearchTableDialog(final Consumer<Optional<String>> searchTermConsumer);

    void openSearchMetaDataDialog(
            final TreeItemsExplorer treeItemsExplorer,
            final Consumer<TreeItem<TreeAttributeWrapper>> onSelected);

    void openRecentConnectionsDialogForArchiving(
            final Runnable onNewConnection,
            final Consumer<RecentDbConnection> onRecentConnectionSelected
    );

    void openRecentConnectionsDialogForUploading(
            final Runnable onNewConnection,
            final Consumer<RecentDbConnection> onRecentConnectionSelected
    );

    void openSelectSiardFileDialog(final BiConsumer<File, Archive> onArchiveSelected);
}
