package ch.admin.bar.siardsuite.component.rendering.model;

import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import lombok.Value;

import java.util.List;
import java.util.function.Supplier;

@Value
@Builder
public class RenderableForm<T> {
    @Singular
    @NonNull
    List<RenderableFormGroup<T>> groups;

    @NonNull
    Supplier<T> dataSupplier;

    @NonNull
    @Builder.Default
    AfterSaveChangesAction<T> afterSaveAction = edited -> {};

    public interface AfterSaveChangesAction<T> {
        void doAfterSaveChanges(T edited) throws Exception;
    }
}
