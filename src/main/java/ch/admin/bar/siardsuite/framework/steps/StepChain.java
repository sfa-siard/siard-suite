package ch.admin.bar.siardsuite.framework.steps;

import lombok.NonNull;
import lombok.Value;

import java.util.List;

/**
 * Represents a chain of steps in a workflow, providing access to the list of steps and a method to retrieve the
 * navigator for a specific step.
 */
@Value
public class StepChain {
    @NonNull List<Step> steps;

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
}
