package ch.admin.bar.siardsuite.framework.dialogs;

import ch.admin.bar.siardsuite.framework.DialogCloser;
import ch.admin.bar.siardsuite.framework.DialogDisplay;
import ch.admin.bar.siardsuite.framework.ServicesFacade;
import lombok.RequiredArgsConstructor;
import lombok.val;

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

    @Override
    public void closeDialog() {
        dialogDisplay.closeDialog();
    }
}
