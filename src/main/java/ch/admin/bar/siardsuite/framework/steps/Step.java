package ch.admin.bar.siardsuite.framework.steps;

import ch.admin.bar.siardsuite.util.fxml.LoadedFxml;
import ch.admin.bar.siardsuite.util.i18n.keys.I18nKey;
import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import lombok.Value;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Value
@Builder(toBuilder = true)
public class Step {
    int stepIndex;
    @NonNull StepDefinition definition;
    @NonNull StepperNavigator navigator;
    @NonNull Supplier<LoadedFxml> viewSupplier;

    @NonNull
    @Singular
    List<Transformer> followingTransformers;

    @NonNull
    @Builder.Default
    Optional<I18nKey> title = Optional.empty();

    public boolean isVisible() {
        return title.isPresent();
    }
}
