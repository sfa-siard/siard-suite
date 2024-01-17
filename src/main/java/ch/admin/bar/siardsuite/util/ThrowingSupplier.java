package ch.admin.bar.siardsuite.util;

public interface ThrowingSupplier<T> {
    T get() throws Exception;
}
