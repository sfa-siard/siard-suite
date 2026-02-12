package ch.admin.bar.siardsuite.ui.component.rendering.model;

import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import lombok.Value;

import java.util.List;
import java.util.function.Function;

@Value
public class RenderableLazyLoadingTable<T, I> implements RenderableProperty<T> {

    @NonNull
    Function<T, LazyLoadingDataSource<I>> dataExtractor;

    @Singular
    @NonNull
    List<TableColumnProperty<I>> properties;

    @Builder
    public RenderableLazyLoadingTable(
            @NonNull Function<T, LazyLoadingDataSource<I>> dataExtractor,
            @NonNull @Singular List<TableColumnProperty<I>> properties) {
        this.dataExtractor = dataExtractor;
        this.properties = properties;
    }
}
