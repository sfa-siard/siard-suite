package ch.admin.bar.siardsuite.framework.errors;

import ch.admin.bar.siardsuite.framework.i18n.DisplayableText;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.function.Predicate;

@Value
@Builder
public class HandlingInstruction {
    @NonNull Predicate<Throwable> matcher;

    @NonNull DisplayableText title;
    @NonNull DisplayableText message;
}
