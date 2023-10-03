package ch.admin.bar.siardsuite.component.rendering;

import ch.admin.bar.siardsuite.component.rendering.model.ReadOnlyStringProperty;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableTable;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.view.TableSize;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@Builder
public class TableRenderer<T, I> {

    private static final String TABLE_STYLE_CLASS = "tree-table-view";

    private final RenderableTable<T, I> renderableTable;
    private final T data;

    public TableView<I> render() {
        val entries = renderableTable.getDataExtractor().apply(data);
        val tableView = new TableView<I>(FXCollections.observableArrayList(entries));

        tableView.getColumns().addAll(
                renderableTable.getProperties().stream()
                        .map(this::column)
                        .collect(Collectors.toList()));

        tableView.getStyleClass().add(TABLE_STYLE_CLASS);

        tableView.autosize();
        new TableSize(tableView).resize();

        VBox.setVgrow(tableView, Priority.ALWAYS);


        return tableView;
    }

    private TableColumn<I, String> column(final ReadOnlyStringProperty<I> columnProperty) {
        val column = new TableColumn<I, String>(I18n.get(columnProperty.getTitle()));
        column.setCellValueFactory(cellData -> {
            val value = columnProperty.getValueExtractor().apply(cellData.getValue());
            return new SimpleStringProperty(value);
        });

        return column;
    }
}
