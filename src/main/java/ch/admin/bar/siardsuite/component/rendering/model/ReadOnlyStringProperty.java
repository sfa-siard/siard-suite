package ch.admin.bar.siardsuite.component.rendering.model;

import ch.admin.bar.siardsuite.util.i18n.DisplayableText;
import ch.admin.bar.siardsuite.util.i18n.keys.I18nKey;
import lombok.NonNull;
import lombok.Value;

import java.util.function.Function;

@Value
public class ReadOnlyStringProperty<T> implements RenderableProperty<T> {
    @NonNull DisplayableText title;
    @NonNull Function<T, String> valueExtractor;

    public ReadOnlyStringProperty(
            @NonNull DisplayableText title,
            @NonNull Function<T, String> valueExtractor
    ) {
        this.title = title;
        this.valueExtractor = valueExtractor;
    }

    public ReadOnlyStringProperty(
            @NonNull I18nKey titleKey,
            @NonNull Function<T, String> valueExtractor
    ) {
        this.title = DisplayableText.of(titleKey);
        this.valueExtractor = valueExtractor;
    }
}
