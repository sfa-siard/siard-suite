package ch.admin.bar.siardsuite.component.rendered.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Function;
import java.util.function.Supplier;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Converter {
    public static <T> Function<T, String> intToString(Function<T, Integer> intGetter) {
        return t -> String.valueOf(intGetter.apply(t));
    }

    public static <T> Function<T, String> longToString(Function<T, Long> longGetter) {
        return t -> String.valueOf(longGetter.apply(t));
    }

    public static <T, P> Function<T, P> catchExceptions(ThrowingFunction<T, P> throwingFunction, P defaultValue) {
        return t -> {
            try {
                return throwingFunction.apply(t);
            } catch (Exception e) {
                log.error("Exception thrown by a supplier (probably a bad designed getter) caught and suppressed", e);
                return defaultValue;
            }
        };
    }

    public static <T> Supplier<T> catchExceptions(ThrowingSupplier<T> throwingSupplier, T defaultValue) {
        return () -> {
            try {
                return throwingSupplier.get();
            } catch (Exception e) {
                log.error("Exception thrown by a supplier (probably a bad designed getter) caught and suppressed", e);
                return defaultValue;
            }
        };
    }

    public interface ThrowingSupplier<T> {
        T get() throws Exception;
    }

    public interface ThrowingFunction<P, R> {
        R apply(P parameter) throws Exception;
    }
}
