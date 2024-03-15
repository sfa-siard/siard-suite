package ch.admin.bar.siardsuite.framework.navigation;

import ch.admin.bar.siardsuite.framework.ServicesFacade;
import ch.admin.bar.siardsuite.framework.ViewDisplay;
import ch.admin.bar.siardsuite.framework.hooks.HooksCaller;
import ch.admin.bar.siardsuite.framework.view.LoadedView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

/**
 * Navigator class for controlling navigation within the application.
 */
@Slf4j
@RequiredArgsConstructor
public class Navigator {

    private final ViewDisplay viewDisplay;
    private final ServicesFacade servicesFacade;

    private final HooksCaller hooksCaller = new HooksCaller();

    public void navigate(SimpleNavigationTarget target) {
        val nextView = target.getViewSupplier().apply(servicesFacade);

        log.info("Navigate {}->{} without data",
                hooksCaller.getPreviouslyLoadedView()
                        .map(loadedView -> loadedView.getController().getClass().getSimpleName())
                        .orElse("n/a"),
                nextView.getController().getClass().getSimpleName());

        navigate(nextView);
    }

    public <T> void navigate(NavigationTarget<T> target, T data) {
        val nextView = target.getViewSupplier().apply(data, servicesFacade);

        log.info("Navigate {}->{} with data {}",
                hooksCaller.getPreviouslyLoadedView()
                        .map(loadedView -> loadedView.getController().getClass().getSimpleName())
                        .orElse(""),
                nextView.getController().getClass().getSimpleName(),
                data);

        navigate(nextView);
    }

    private void navigate(final LoadedView loadedView) {
        viewDisplay.displayView(hooksCaller.nextView(loadedView).getNode());
    }
}
