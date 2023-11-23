package ch.admin.bar.siardsuite.component.rendering.model;

import ch.admin.bar.siardsuite.util.i18n.DisplayableText;
import ch.admin.bar.siardsuite.util.i18n.keys.I18nKey;
import lombok.NonNull;
import lombok.Value;

import java.util.Optional;

@Value
public class TableColumnProperty<T> implements RenderableProperty<T> {
    @NonNull DisplayableText title;
    @NonNull ThrowingExtractor<T, String> valueExtractor;
    @NonNull Optional<CellClickedListener<T>> onCellClickedListener;

    public TableColumnProperty(@NonNull DisplayableText title, @NonNull ThrowingExtractor<T, String> valueExtractor, @NonNull Optional<CellClickedListener<T>> onCellClickedListener) {
        this.title = title;
        this.valueExtractor = valueExtractor;
        this.onCellClickedListener = onCellClickedListener;
    }

    public TableColumnProperty(@NonNull I18nKey titleKey, @NonNull ThrowingExtractor<T, String> valueExtractor) {
        this.title = DisplayableText.of(titleKey);
        this.valueExtractor = valueExtractor;
        this.onCellClickedListener = Optional.empty();
    }

    public interface CellClickedListener<T> {
        void onClick(TableColumnProperty<T> property, T value) throws Exception;
    }
}
