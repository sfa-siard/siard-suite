package ch.admin.bar.siardsuite.util;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Static class, which contains small helper methods for class casting
 */
public final class CastHelper {

    private CastHelper() {
    }

    public static <T> Optional<T> tryCast(final Object o, final Class<T> type) {
        if (type.isInstance(o)) {
            return Optional.of((T) o);
        }
        return Optional.empty();
    }

    public static <I, O> Function<I, Optional<O>> tryCast(final Class<O> type) {
        return t -> tryCast(t, type);
    }

    public static <I, O> Function<I, Stream<O>> tryCastInStream(final Class<O> type) {
        return t -> tryCastWithStream(t, type);
    }

    /**
     * Helper method, because the stream-method on an {@link Optional} is not available in Java 8
     */
    public static <T> Stream<T> tryCastWithStream(final Object o, final Class<T> type) {
        if (type.isInstance(o)) {
            return Stream.of((T) o);
        }
        return Stream.empty();
    }
}
