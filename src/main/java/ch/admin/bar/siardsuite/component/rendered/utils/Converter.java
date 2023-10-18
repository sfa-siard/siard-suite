package ch.admin.bar.siardsuite.component.rendered.utils;

import ch.admin.bar.siardsuite.util.I18n;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.net.URI;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Optional;
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

    public static <T> Function<T, String> localDateToString(Function<T, LocalDate> getter) {
        return t -> I18n.getLocaleDate(getter.apply(t));
    }

    public static <T> Function<T, String> uriToString(Function<T, URI> getter) {
        return t -> Optional.ofNullable(getter.apply(t))
                .map(URI::getPath)
                .orElse("");
    }

    public static <T> Function<T, LocalDate> calendarToLocalDate(Function<T, Calendar> getter) {
        return t -> {
            val calendar = getter.apply(t);
            try {
                return LocalDate.of(
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH) + 1,
                        calendar.get(Calendar.DATE));
            } catch (DateTimeException e) {
                log.error("Failed to convert calendar into local date", e);
                return LocalDate.now();
            }
        };
    }

    public static <T, P> Function<T, P> catchExceptions(ThrowingFunction<T, P> throwingFunction) {
        return t -> {
            try {
                return throwingFunction.apply(t);
            } catch (Exception e) {
                log.error("Exception thrown by a supplier (probably a bad designed getter)", e);
                throw new RuntimeException(e);
            }
        };
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

    public static <T> Supplier<T> catchExceptions(ThrowingSupplier<T> throwingSupplier) {
        return () -> {
            try {
                return throwingSupplier.get();
            } catch (Exception e) {
                log.error("Exception thrown by a supplier (probably a bad designed getter)", e);
                throw new RuntimeException(e);
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
