package ch.admin.bar.siardsuite.framework.navigation;

import ch.admin.bar.siardsuite.framework.Destructible;
import ch.admin.bar.siardsuite.framework.ServicesFacade;
import ch.admin.bar.siardsuite.framework.ViewDisplay;
import ch.admin.bar.siardsuite.util.CastHelper;
import ch.admin.bar.siardsuite.framework.view.LoadedView;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Navigator class for controlling navigation within the application.
 */
@RequiredArgsConstructor
public class Navigator {

    private final ViewDisplay viewDisplay;
    private final ServicesFacade servicesFacade;

    private final AtomicReference<LoadedView> previouslyLoadedView = new AtomicReference<>();

    public void navigate(SimpleNavigationTarget target) {
        navigate(target.getViewSupplier().apply(servicesFacade));
    }

    public <T> void navigate(NavigationTarget<T> target, T data) {
        navigate(target.getViewSupplier().apply(data, servicesFacade));
    }

    private void navigate(final LoadedView loadedView) {
        val loaded = previouslyLoadedView.updateAndGet(previouslyLoadedFxml -> {
            if (previouslyLoadedFxml != null) {
                CastHelper.tryCast(previouslyLoadedFxml.getController(), Destructible.class)
                        .ifPresent(Destructible::destruct);
            }

            return loadedView;
        });

        viewDisplay.displayView(loaded.getNode());
    }
}
