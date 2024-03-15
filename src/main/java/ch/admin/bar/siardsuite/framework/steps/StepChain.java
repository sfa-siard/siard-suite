package ch.admin.bar.siardsuite.framework.steps;

import ch.admin.bar.siardsuite.framework.general.Destructible;
import ch.admin.bar.siardsuite.util.CastHelper;
import ch.admin.bar.siardsuite.util.fxml.LoadedFxml;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Represents a chain of steps in a workflow, providing access to the list of steps and a method to retrieve the
 * navigator for a specific step.
 */
@RequiredArgsConstructor
public class StepChain implements Destructible {
    @NonNull
    @Getter
    private final List<Step> steps;

    @NonNull
    private final AtomicReference<LoadedFxml> lastLoaded;

    /**
     * Gets the navigator for a specific step in the workflow.
     *
     * @param stepDefinition The definition of the step for which to retrieve the navigator.
     * @param <TOut>         The type of output data for the step.
     * @return The navigator for the specified step.
     * @throws IllegalArgumentException If the specified step is not found in the workflow.
     */
    public <TOut> StepperNavigator<TOut> getNavigatorOfStep(final StepDefinition stepDefinition) {
        return steps.stream()
                .filter(step -> step.getDefinition() == stepDefinition)
                .findAny()
                .map(step -> (StepperNavigator<TOut>)step.getNavigator())
                .orElseThrow(() -> new IllegalArgumentException("Searched step not found"));
    }

    @Override
    public void destruct() {
        lastLoaded.updateAndGet(lastLoadedFxml -> {
            if (lastLoadedFxml != null) {
                CastHelper.tryCast(lastLoadedFxml.getController(), Destructible.class)
                        .ifPresent(Destructible::destruct);
            }

            return null;
        });
    }
}
