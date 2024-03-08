package ch.admin.bar.siardsuite.framework.steps;

import ch.admin.bar.siardsuite.util.i18n.keys.I18nKey;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.Optional;

@Value
@Builder
public class StepId {
    @NonNull
    @Builder.Default
    Optional<I18nKey> title = Optional.empty();

    @NonNull Class<?> inputType;
    @NonNull Class<?> outputType;
}
