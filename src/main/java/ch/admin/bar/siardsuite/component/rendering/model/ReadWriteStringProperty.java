package ch.admin.bar.siardsuite.component.rendering.model;

import ch.admin.bar.siardsuite.util.I18nKey;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Value
public class ReadWriteStringProperty<T> implements RenderableProperty<T> {

    private static final I18nKey CAN_NOT_BE_EMPTY = I18nKey.of("valueValidation.canNotBeEmpty");

    public static final Validator IS_NOT_EMPTY_VALIDATOR = Validator.builder()
            .message(CAN_NOT_BE_EMPTY)
            .isValidCheck(nullableValue -> Optional.ofNullable(nullableValue)
                    .filter(value -> !value.isEmpty() && !value.trim().isEmpty())
                    .isPresent())
            .titleSuffix("*")
            .build();

    @NonNull I18nKey title;
    @NonNull Function<T, String> valueExtractor;
    @NonNull Persistor<T> valuePersistor;

    @NonNull Set<Validator> valueValidators;

    public ReadWriteStringProperty(
            @NonNull I18nKey title,
            @NonNull Function<T, String> valueExtractor,
            @NonNull Persistor<T> valuePersistor,
            @NonNull Validator... valueValidators) {
        this.title = title;
        this.valueExtractor = valueExtractor;
        this.valuePersistor = valuePersistor;
        this.valueValidators = Arrays.stream(valueValidators)
                .collect(Collectors.toSet());
    }

    public interface Persistor<T> {
        void persist(T valueHolder, String value) throws Exception;
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
                final String titleSuffix) {
            this.message = message;
            this.isValidCheck = isValidCheck;
            this.titleSuffix = Optional.ofNullable(titleSuffix);
        }
    }
}
