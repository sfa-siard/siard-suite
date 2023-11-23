package ch.admin.bar.siardsuite.component.rendering.model;

public interface ThrowingExtractor<T, R> {
    R extract(T t) throws Exception;
}
