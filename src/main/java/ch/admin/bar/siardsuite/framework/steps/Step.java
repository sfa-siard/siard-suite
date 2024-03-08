package ch.admin.bar.siardsuite.framework.steps;

import ch.admin.bar.siardsuite.util.fxml.LoadedFxml;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.function.Supplier;

@Value
@Builder
public class Step {
    int stepIndex;
    @NonNull StepDefinition<?, ?> definition;
    @NonNull StepperNavigator navigator;
    @NonNull Supplier<LoadedFxml> viewLoader;
}
