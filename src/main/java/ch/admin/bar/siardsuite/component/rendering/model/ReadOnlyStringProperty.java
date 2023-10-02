package ch.admin.bar.siardsuite.component.rendering.model;

import lombok.Value;

@Value
public class ReadOnlyStringProperty implements RenderableProperty {
    String title;
    String value;
    //Function<T, String> valueExtractor;
}
