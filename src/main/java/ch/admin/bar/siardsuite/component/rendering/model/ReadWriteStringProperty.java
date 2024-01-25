package ch.admin.bar.siardsuite.component.rendering.model;

import ch.admin.bar.siardsuite.util.Validator;
import ch.admin.bar.siardsuite.util.i18n.DisplayableText;
import ch.admin.bar.siardsuite.util.i18n.keys.I18nKey;
import lombok.NonNull;
import lombok.Value;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Value
public class ReadWriteStringProperty<T> implements RenderableProperty<T> {

    private static final I18nKey CAN_NOT_BE_EMPTY = I18nKey.of("valueValidation.canNotBeEmpty");

    public static final Validator<String> IS_NOT_EMPTY_VALIDATOR = Validator.<String>builder()
            .message(DisplayableText.of(CAN_NOT_BE_EMPTY))
            .isValidCheck(nullableValue -> Optional.ofNullable(nullableValue)
                    .filter(value -> !value.isEmpty() && !value.trim().isEmpty())
                    .isPresent())
            .titleSuffix("*")
            .build();

    @NonNull DisplayableText title;
    @NonNull Function<T, String> valueExtractor;
    @NonNull Persistor<T> valuePersistor;

    @NonNull Set<Validator<String>> valueValidators;

    public ReadWriteStringProperty(
            @NonNull DisplayableText title,
            @NonNull Function<T, String> valueExtractor,
            @NonNull Persistor<T> valuePersistor,
            @NonNull Validator<String>... valueValidators) {
        this.title = title;
        this.valueExtractor = valueExtractor;
        this.valuePersistor = valuePersistor;
        this.valueValidators = Arrays.stream(valueValidators)
                .collect(Collectors.toSet());
    }

    public ReadWriteStringProperty(
            @NonNull I18nKey titleKey,
            @NonNull Function<T, String> valueExtractor,
            @NonNull Persistor<T> valuePersistor,
            @NonNull Validator<String>... valueValidators) {
        this.title = DisplayableText.of(titleKey);
        this.valueExtractor = valueExtractor;
        this.valuePersistor = valuePersistor;
        this.valueValidators = Arrays.stream(valueValidators)
                .collect(Collectors.toSet());
    }


    public interface Persistor<T> {
        void persist(T valueHolder, String value) throws Exception;
    }
}
