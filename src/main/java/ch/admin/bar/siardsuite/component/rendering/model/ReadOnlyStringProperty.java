package ch.admin.bar.siardsuite.component.rendering.model;

import ch.admin.bar.siardsuite.util.I18nKey;
import javafx.beans.property.StringProperty;
import lombok.Value;

import java.util.function.Function;

@Value
public class ReadOnlyStringProperty<T> implements RenderableProperty<T> {
    I18nKey title;
    Function<T, String> valueExtractor;
}
