package ch.admin.bar.siardsuite.framework.errors;

import ch.admin.bar.siardsuite.framework.i18n.DisplayableText;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.Optional;

@Value
@Builder
public class NewFailure { // TODO Rename
    @NonNull DisplayableText title;
    @NonNull DisplayableText message;

    @NonNull
    @Builder.Default
    Optional<Throwable> throwable = Optional.empty();

    @NonNull
    @Builder.Default
    Type type = Type.ERROR;

    public enum Type {
        ERROR,
        WARNING
    }
}
