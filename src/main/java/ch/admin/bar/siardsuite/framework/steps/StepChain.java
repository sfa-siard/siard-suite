package ch.admin.bar.siardsuite.framework.steps;

import lombok.NonNull;
import lombok.Value;

import java.util.List;

@Value
public class StepChain {
    @NonNull List<Step> steps;

    public <TOut> StepperNavigator<TOut> getNavigatorOfStep(final StepDefinition stepDefinition) {
        return steps.stream()
                .filter(step -> step.getDefinition() == stepDefinition)
                .findAny()
                .map(step -> (StepperNavigator<TOut>)step.getNavigator())
                .orElseThrow(() -> new IllegalArgumentException("Searched step not found"));
    }
}
