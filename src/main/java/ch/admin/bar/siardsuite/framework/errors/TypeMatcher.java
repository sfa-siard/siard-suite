package ch.admin.bar.siardsuite.framework.errors;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.function.Predicate;

@Value
@Builder
public class TypeMatcher implements Predicate<Throwable> {
    @NonNull Class expectedExceptionType;

    @Override
    public boolean test(Throwable throwable) {
        return expectedExceptionType.equals(throwable.getClass());
    }
}
