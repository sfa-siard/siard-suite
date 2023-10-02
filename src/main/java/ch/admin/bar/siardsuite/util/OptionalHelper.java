package ch.admin.bar.siardsuite.util;

import java.util.Optional;
import java.util.function.Consumer;

public class OptionalHelper {

    /**
     * Helper method, because the ifPresentOrElse-method on an {@link Optional} is not available in Java 8
     */
    public static <T> void ifPresentOrElse(final Optional<T> optional, Consumer<? super T> action, Runnable emptyAction) {
        if (optional.isPresent()) {
            action.accept(optional.get());
        } else {
            emptyAction.run();
        }
    }
}
