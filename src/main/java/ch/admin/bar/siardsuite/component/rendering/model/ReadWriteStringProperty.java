package ch.admin.bar.siardsuite.component.rendering.model;

import lombok.Value;

@Value
public class ReadWriteStringProperty implements RenderableProperty {
    String title;
    String value;
}
