package ch.admin.bar.siardsuite.util;

public interface ThrowingFunction<P, R> {
    R apply(P parameter) throws Exception;
}
