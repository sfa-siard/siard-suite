package ch.admin.bar.siardsuite.framework.hooks;

import ch.admin.bar.siardsuite.framework.view.LoadedView;
import ch.admin.bar.siardsuite.util.CastHelper;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class HooksCaller {

    private final AtomicReference<LoadedView> previouslyLoadedView = new AtomicReference<>();

    public LoadedView nextView(@Nullable final LoadedView loadedView) {
        return previouslyLoadedView.updateAndGet(previouslyLoadedView -> {
            if (previouslyLoadedView != null) {
                log.debug("Call destruction method on {}", previouslyLoadedView.getController().getClass().getSimpleName());
                CastHelper.tryCast(previouslyLoadedView.getController(), Destructible.class)
                        .ifPresent(Destructible::destruct);
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
