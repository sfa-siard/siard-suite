package ch.admin.bar.siardsuite.util;

import ch.admin.bar.siardsuite.util.i18n.DisplayableText;
import ch.admin.bar.siardsuite.util.i18n.keys.I18nKey;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Optional;
import java.util.function.Predicate;

@Value
public class Validator<T> {
    private static final I18nKey CAN_NOT_BE_EMPTY = I18nKey.of("valueValidation.canNotBeEmpty");
    private static final I18nKey NEED_TO_EXIST = I18nKey.of("valueValidation.needsToBeExistingFile");

    public static final Validator<String> IS_NOT_EMPTY_STRING_VALIDATOR = Validator.<String>builder()
            .message(DisplayableText.of(CAN_NOT_BE_EMPTY))
            .isValidCheck(nullableValue -> Optional.ofNullable(nullableValue)
                    .filter(value -> !value.isEmpty() && !value.trim().isEmpty())
                    .isPresent())
            .titleSuffix("*")
            .build();

    public static final Validator<File> IS_EXISTING_FILE_VALIDATOR = Validator.<File>builder()
            .message(DisplayableText.of(NEED_TO_EXIST))
            .isValidCheck(nullableValue -> Optional.ofNullable(nullableValue)
                    .filter(value -> value.isFile() && value.exists())
                    .isPresent())
            .titleSuffix("*")
            .build();


    @NonNull DisplayableText message;
    @NonNull Predicate<T> isValidCheck;
    @NonNull Optional<String> titleSuffix;

    @Builder
    public Validator(
            @NonNull final DisplayableText message,
            @NonNull final Predicate<T> isValidCheck,
            @Nullable final String titleSuffix) {
        this.message = message;
        this.isValidCheck = isValidCheck;
        this.titleSuffix = Optional.ofNullable(titleSuffix);
    }
}
