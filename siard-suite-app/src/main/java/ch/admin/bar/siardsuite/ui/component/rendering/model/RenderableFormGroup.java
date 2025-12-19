package ch.admin.bar.siardsuite.ui.component.rendering.model;

import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class RenderableFormGroup<T> {
    @Singular
    @NonNull
    List<RenderableProperty<T>> properties;
}
