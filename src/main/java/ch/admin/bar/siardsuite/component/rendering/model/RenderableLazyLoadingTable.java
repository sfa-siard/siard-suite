package ch.admin.bar.siardsuite.component.rendering.model;

import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import lombok.Value;

import java.util.List;
import java.util.function.Function;

@Value
@Builder
public class RenderableLazyLoadingTable<T, I> implements RenderableProperty<T> {

    @NonNull
    Function<T, LazyLoadingDataSource<I>> dataExtractor;

    @Singular
    @NonNull
    List<ReadOnlyStringProperty<I>> properties;
}
