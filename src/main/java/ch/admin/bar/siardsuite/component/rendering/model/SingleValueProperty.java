package ch.admin.bar.siardsuite.component.rendering.model;

import ch.admin.bar.siardsuite.util.I18nKey;

import java.util.function.Function;

public interface SingleValueProperty<T> {
    I18nKey getTitle();

    Function<T, String> getValueExtractor();
}
