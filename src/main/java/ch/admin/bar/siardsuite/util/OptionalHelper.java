package ch.admin.bar.siardsuite.util;

import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

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

    /**
     * Helper method, because the stream-method on an {@link Optional} is not available in Java 8
     */
    public static <T> Stream<T> stream(final Optional<T> optional) {
        return optional
                .map(Stream::of)
                .orElse(Stream.empty());
    }

    public static <T> Optional<T> firstPresent(final ThrowingSupplier<Optional<T>>... suppliers) {
        for (val supplier : suppliers) {
            try {
                val candidate = supplier.get();
                if (candidate.isPresent()) {
                    return candidate;
                }
            } catch (Exception ex) {
            }
        }

        return Optional.empty();
    }
}
