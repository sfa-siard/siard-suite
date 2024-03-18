package ch.admin.bar.siardsuite.framework.errors;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.function.Predicate;

@Value
@Builder
public class TypeAndMessageMatcher implements Predicate<Throwable> {
    @NonNull Class exceptionType;
    @NonNull String partOfMessage;

    @Override
    public boolean test(Throwable throwable) {
        return exceptionType.equals(throwable.getClass()) &&
                throwable.getMessage() != null &&
                throwable.getMessage().contains(partOfMessage);
    }
}