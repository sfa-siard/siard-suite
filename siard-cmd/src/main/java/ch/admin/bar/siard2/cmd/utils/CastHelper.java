package ch.admin.bar.siard2.cmd.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Static class, which contains small helper methods for class casting
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CastHelper {

    public static <T> Optional<T> tryCast(final Object o, final Class<T> type) {
        if (type.isInstance(o)) {
            return Optional.of((T) o);
        }
        return Optional.empty();
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

    public static <T, R> Function<T, Stream<R>> tryCast(final Class<R> type) {
        return t -> tryCastWithStream(t, type);
    }
}
