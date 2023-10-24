package ch.admin.bar.siardsuite.component.rendering;

import ch.admin.bar.siardsuite.component.rendering.model.ReadOnlyStringProperty;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableTable;
import ch.admin.bar.siardsuite.view.TableSize;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.function.Predicate;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Builder
public class TableRenderer<T, I> implements SearchableFormEntry{

    private static final String TABLE_STYLE_CLASS = "tree-table-view";

    private final RenderableTable<T, I> renderableTable;
    private final T data;

    private final ObservableList<I> tableItems = FXCollections.observableArrayList();
    private final Predicate<I> doNotFilter = i -> true;
    private final FilteredList<I> filteredTableItems = tableItems
            .filtered(doNotFilter);

    public TableView<I> render() {
        val entries = renderableTable.getDataExtractor().apply(data);
        tableItems.setAll(entries);

        val tableView = new TableView<>(filteredTableItems);

        tableView.getColumns().addAll(
                renderableTable.getProperties().stream()
                        .map(this::column)
                        .collect(Collectors.toList()));

        tableView.getStyleClass().add(TABLE_STYLE_CLASS);
        VBox.setVgrow(tableView, Priority.ALWAYS);

        tableView.autosize();
        new TableSize(tableView).resize();

        return tableView;
    }

    @Override
    public void applySearchTerm(final String searchTerm) {
        filteredTableItems.setPredicate(i -> renderableTable.getProperties().stream()
                    .map(iReadOnlyStringProperty -> iReadOnlyStringProperty.getValueExtractor().apply(i))
                    .anyMatch(s -> s.contains(searchTerm)));
    }

    @Override
    public void clearSearchTerm() {
        filteredTableItems.setPredicate(doNotFilter);
    }

    private TableColumn<I, String> column(final ReadOnlyStringProperty<I> columnProperty) {
        val column = new TableColumn<I, String>(columnProperty.getTitle().getText());
        column.setCellValueFactory(cellData -> {
            val value = columnProperty.getValueExtractor().apply(cellData.getValue());
            return new SimpleStringProperty(value);
        });

        return column;
    }
}
