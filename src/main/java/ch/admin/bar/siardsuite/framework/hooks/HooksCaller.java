package ch.admin.bar.siardsuite.framework.hooks;

import ch.admin.bar.siardsuite.framework.view.LoadedView;
import ch.admin.bar.siardsuite.util.CastHelper;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Utility class which calling hooks on previously loaded views if a new view is loaded
 * or this class is cleared.
 */
@Slf4j
public class HooksCaller {

    private final AtomicReference<LoadedView> previouslyLoadedView = new AtomicReference<>();

    /**
     * Currently calls the hooks on the previously loaded view, if it is present and the controller
     * implements the {@link Destructible} interface.
     *
     * @param loadedView The newly loaded view.
     * @return The newly loaded view.
     */
    public LoadedView nextView(@Nullable final LoadedView loadedView) {
        return previouslyLoadedView.updateAndGet(previouslyLoadedView -> {
            if (previouslyLoadedView != null) {
                CastHelper.tryCast(previouslyLoadedView.getController(), Destructible.class)
                        .ifPresent(destructible -> {
                            log.debug("Call destruction method on {}", previouslyLoadedView.getController().getClass().getSimpleName());
                            destructible.destruct();
                        });
            }

            return loadedView;
        });
    }

    public Optional<LoadedView> getPreviouslyLoadedView() {
        return Optional.ofNullable(previouslyLoadedView.get());
    }

    public void clear() {
        nextView(null);
    }
}
