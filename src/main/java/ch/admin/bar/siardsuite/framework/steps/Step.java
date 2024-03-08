package ch.admin.bar.siardsuite.framework.steps;

import ch.admin.bar.siardsuite.util.fxml.LoadedFxml;
import ch.admin.bar.siardsuite.util.i18n.keys.I18nKey;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.Optional;
import java.util.function.Supplier;

@Value
@Builder
public class Step {
    int stepIndex;
    @NonNull StepId id;
    @NonNull StepperNavigator navigator;
    @NonNull Supplier<LoadedFxml> viewLoader;

    @NonNull
    @Builder.Default
    Optional<I18nKey> title = Optional.empty();

    public boolean isVisible() {
        return title.isPresent();
    }
}
