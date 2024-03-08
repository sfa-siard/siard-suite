package ch.admin.bar.siardsuite.framework.steps;

import lombok.NonNull;
import lombok.Value;

import java.util.List;

@Value
public class StepChain {
    @NonNull List<Step> steps;

    public <TOut> StepperNavigator<TOut> getNavigatorOfStep(final StepId stepId) {
        return steps.stream()
                .filter(step -> step.getId().equals(stepId))
                .findAny()
                .map(step -> (StepperNavigator<TOut>)step.getNavigator())
                .orElseThrow(() -> new IllegalArgumentException("Searched step not found"));
    }
}
