package ch.admin.bar.siardsuite.framework.dialogs;

import ch.admin.bar.siardsuite.framework.DialogCloser;
import ch.admin.bar.siardsuite.framework.DialogDisplay;
import ch.admin.bar.siardsuite.framework.ServicesFacade;
import ch.admin.bar.siardsuite.framework.hooks.HooksCaller;
import ch.admin.bar.siardsuite.framework.view.LoadedView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

/**
 * Defines a set of methods for displaying various dialogs.
 */
@Slf4j
@RequiredArgsConstructor
public class Dialogs implements DialogCloser {

    private final DialogDisplay dialogDisplay;
    private final ServicesFacade servicesFacade;

    private final HooksCaller hooksCaller = new HooksCaller();

    /**
     * Opens the specified view as a dialog.
     */
    public void open(SimpleShowDialogTarget target) {
        val loaded = target.getViewSupplier().apply(servicesFacade);

        log.info("Show dialog {} without data",
                loaded.getController().getClass().getSimpleName());

        open(loaded);
    }

    /**
     * Opens the specified view as a dialog.
     */
    public <T> void open(final ShowDialogTarget<T> target, final T data) {
        val loaded = target.getViewSupplier().apply(data, servicesFacade);

        log.info("Show dialog {} with data {}",
                loaded.getController().getClass().getSimpleName(),
                data);

        open(loaded);
    }

    private void open(LoadedView loadedView) {
        dialogDisplay.displayDialog(hooksCaller.nextView(loadedView).getNode());
    }

    @Override
    public void closeDialog() {
        dialogDisplay.closeDialog();
        hooksCaller.clear();
    }
}
