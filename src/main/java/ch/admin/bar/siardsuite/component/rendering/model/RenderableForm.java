package ch.admin.bar.siardsuite.component.rendering.model;

import ch.admin.bar.siardsuite.Controller;
import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import lombok.Value;

import java.util.List;
import java.util.function.Function;

@Value
@Builder
public class RenderableForm<T> {
    @Singular
    @NonNull
    List<RenderableFormGroup<T>> groups;

    @NonNull
    Function<Controller, T> dataExtractor;
}