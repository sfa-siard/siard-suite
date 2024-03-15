package ch.admin.bar.siardsuite.framework.dialogs;

import ch.admin.bar.siardsuite.component.rendering.TreeItemsExplorer;
import ch.admin.bar.siardsuite.framework.DialogCloser;
import ch.admin.bar.siardsuite.framework.DialogDisplay;
import ch.admin.bar.siardsuite.framework.ErrorHandler;
import ch.admin.bar.siardsuite.framework.ServicesFacade;
import ch.admin.bar.siardsuite.model.Failure;
import ch.admin.bar.siardsuite.model.TreeAttributeWrapper;
import ch.admin.bar.siardsuite.presenter.ErrorDialogPresenter;
import ch.admin.bar.siardsuite.presenter.archive.browser.dialogues.SearchMetadataDialogPresenter;
import ch.admin.bar.siardsuite.presenter.archive.browser.dialogues.SearchTableDialogPresenter;
import ch.admin.bar.siardsuite.presenter.archive.browser.dialogues.UnsavedChangesDialogPresenter;
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

    private final DialogDisplay dialogDisplay;
    private final ServicesFacade servicesFacade;

    /**
     * Opens the specified view as a dialog.
     */
    public void open(SimpleShowDialogTarget target) {
        val loaded = target.getViewSupplier().apply(servicesFacade);
        dialogDisplay.displayDialog(loaded.getNode());
    }

    /**
     * Opens the specified view as a dialog.
     */
    public <T> void open(final ShowDialogTarget<T> target, final T data) {
        val loaded = target.getViewSupplier().apply(data, servicesFacade);
        dialogDisplay.displayDialog(loaded.getNode());
    }

    public ErrorHandler errorHandler() {
        return ex -> {
            val loaded = ErrorDialogPresenter.load(new Failure(ex), servicesFacade);
            dialogDisplay.displayDialog(loaded.getNode());
        };
    }

    @Override
    @Deprecated
    public void closeDialog() {
        dialogDisplay.closeDialog();
    }

    /**
     * Opens a dialog for handling unsaved changes, invoking the specified callback upon completion.
     */
    public void openUnsavedChangesDialog(final Consumer<UnsavedChangesDialogPresenter.Result> resultCallback) {
        val loaded = UnsavedChangesDialogPresenter.load(result -> {
            closeDialog();
            resultCallback.accept(result);
        });

        dialogDisplay.displayDialog(loaded.getNode());
    }

    /**
     * Opens a dialog for searching tables, invoking the specified callback with the search term upon completion.
     */
    public void openSearchTableDialog(final Consumer<Optional<String>> searchTermConsumer) {
        val loaded = SearchTableDialogPresenter.load(this::closeDialog, searchTermConsumer);

        dialogDisplay.displayDialog(loaded.getNode());
    }

    /**
     * Opens a dialog for searching metadata using the provided tree items explorer. The specified callback
     * will be invoked, after a result will be selected.
     */
    public void openSearchMetaDataDialog(
            final TreeItemsExplorer treeItemsExplorer,
            final Consumer<TreeItem<TreeAttributeWrapper>> onSelected) {
        val loaded = SearchMetadataDialogPresenter.load(
                this::closeDialog,
                treeItemsExplorer,
                onSelected
        );

        dialogDisplay.displayDialog(loaded.getNode());
    }
}
