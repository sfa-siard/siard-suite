package ch.admin.bar.siardsuite.ui.component.rendering.model;

import ch.admin.bar.siardsuite.framework.i18n.DisplayableText;
import lombok.NonNull;
import lombok.Value;

import java.util.Optional;
import java.util.function.Function;

@Value
public class TableColumnProperty<T> implements RenderableProperty<T> {
    @NonNull DisplayableText title;
    @NonNull Function<T, String> valueExtractor;
    @NonNull Optional<CellClickedListener<T>> onCellClickedListener;

    public interface CellClickedListener<T> {
        void onClick(TableColumnProperty<T> property, T value) throws Exception;
    }
}
