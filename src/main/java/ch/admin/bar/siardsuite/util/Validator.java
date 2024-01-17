package ch.admin.bar.siardsuite.util;

import ch.admin.bar.siardsuite.util.i18n.DisplayableText;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Predicate;

@Value
public class Validator<T> {
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
