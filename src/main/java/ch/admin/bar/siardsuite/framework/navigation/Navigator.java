package ch.admin.bar.siardsuite.framework.navigation;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.Workflow;
import ch.admin.bar.siardsuite.framework.general.Destructible;
import ch.admin.bar.siardsuite.framework.general.ServicesFacade;
import ch.admin.bar.siardsuite.model.View;
import ch.admin.bar.siardsuite.presenter.StartPresenter;
import ch.admin.bar.siardsuite.util.CastHelper;
import ch.admin.bar.siardsuite.util.fxml.LoadedFxml;
import ch.admin.bar.siardsuite.view.RootStage;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Navigator class for controlling navigation within the application.
 */
@RequiredArgsConstructor
public class Navigator {

    private final Controller controller;
    private final RootStage stage;
    private final ServicesFacade servicesFacade = ServicesFacade.INSTANCE; // TODO

    private final AtomicReference<LoadedFxml> previouslyLoadedView = new AtomicReference<>();

    public void navigate(SimpleNavigationTarget target) {
        navigate(target.getViewSupplier().apply(servicesFacade));
    }

    public <T> void navigate(NavigationTarget<T> target, T data) {
        navigate(target.getViewSupplier().apply(data, servicesFacade));
    }

    private void navigate(final LoadedFxml loadedFxml) {
        val loaded = previouslyLoadedView.updateAndGet(previouslyLoadedFxml -> {
            if (previouslyLoadedFxml != null) {
                CastHelper.tryCast(previouslyLoadedFxml.getController(), Destructible.class)
                        .ifPresent(Destructible::destruct);
            }

            return loadedFxml;
        });

        stage.displayView(loaded.getNode());
    }
}
