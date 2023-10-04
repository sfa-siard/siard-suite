package ch.admin.bar.siardsuite.component.rendering.model;

import ch.admin.bar.siardsuite.util.I18nKey;
import lombok.NonNull;
import lombok.Value;

import java.util.function.Function;

@Value
public class ReadOnlyStringProperty<T> implements RenderableProperty<T> {
    @NonNull I18nKey title;
    @NonNull Function<T, String> valueExtractor;
}
