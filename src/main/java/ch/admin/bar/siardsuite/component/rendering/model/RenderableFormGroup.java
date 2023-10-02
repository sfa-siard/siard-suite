package ch.admin.bar.siardsuite.component.rendering.model;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class RenderableFormGroup {
    @Singular
    List<RenderableProperty> properties;
}
