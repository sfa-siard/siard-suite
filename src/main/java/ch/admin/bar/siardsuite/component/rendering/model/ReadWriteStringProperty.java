package ch.admin.bar.siardsuite.component.rendering.model;

import ch.admin.bar.siardsuite.util.I18nKey;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Value
public class ReadWriteStringProperty<T> implements RenderableProperty<T> {
    public static final Validator IS_NOT_EMPTY_VALIDATOR = Validator.builder()
            .message(I18nKey.of("validation.can.not.be.empty"))
            .isValidCheck(nullableValue -> Optional.ofNullable(nullableValue)
                    .filter(value -> !value.isEmpty() && !value.trim().isEmpty())
                    .isPresent())
            .titleSuffix("*")
            .build();

    @NonNull I18nKey title;
    @NonNull Function<T, String> valueExtractor;
    @NonNull BiConsumer<T, String> valuePersistor;

    @NonNull Set<Validator> valueValidators;

    public ReadWriteStringProperty(
            @NonNull I18nKey title,
            @NonNull Function<T, String> valueExtractor,
            @NonNull BiConsumer<T, String> valuePersistor,
            @NonNull Validator... valueValidators) {
        this.title = title;
        this.valueExtractor = valueExtractor;
        this.valuePersistor = valuePersistor;
        this.valueValidators = Arrays.stream(valueValidators)
                .collect(Collectors.toSet());
    }

    @Value
    public static class Validator {
        @NonNull I18nKey message;
        @NonNull Predicate<String> isValidCheck;
        @NonNull Optional<String> titleSuffix;

        @Builder
        public Validator(
                @NonNull final I18nKey message,
                @NonNull final Predicate<String> isValidCheck,
                @NonNull final String titleSuffix) {
            this.message = message;
            this.isValidCheck = isValidCheck;
            this.titleSuffix = Optional.ofNullable(titleSuffix);
        }
    }
}
