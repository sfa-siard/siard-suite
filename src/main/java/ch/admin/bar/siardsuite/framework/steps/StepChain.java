package ch.admin.bar.siardsuite.framework.steps;

import ch.admin.bar.siardsuite.framework.hooks.Destructible;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;

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
    private final Runnable cleanUp;

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
                .map(step -> (StepperNavigator<TOut>) step.getNavigator())
                .orElseThrow(() -> new IllegalArgumentException("Searched step not found"));
    }

    @Override
    public void destruct() {
        cleanUp.run();
    }
}
