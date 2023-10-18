package ch.admin.bar.siardsuite.component.rendering;

import ch.admin.bar.siardsuite.component.rendering.model.LazyLoadingDataSource;
import ch.admin.bar.siardsuite.component.rendering.model.ReadOnlyStringProperty;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableLazyLoadingTable;
import ch.admin.bar.siardsuite.component.rendering.utils.LoadingBatchManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lombok.Builder;
import lombok.val;

import java.util.stream.Collectors;

public class LazyLoadingTableRenderer<T, I> {

    private static final String TABLE_STYLE_CLASS = "tree-table-view";

    private final RenderableLazyLoadingTable<T, I> renderableTable;
    private final LazyLoadingDataSource<I> lazyLoadingDataSource;


    @Builder
    public LazyLoadingTableRenderer(RenderableLazyLoadingTable<T, I> renderableTable, T dataHolder) {
        this.renderableTable = renderableTable;
        this.lazyLoadingDataSource = renderableTable.getDataExtractor().apply(dataHolder);
    }

    public TableView<I> render() {

        val loadingBatchManager = new LoadingBatchManager<>(lazyLoadingDataSource);
        val tableView = new TableView<>(loadingBatchManager.getObservableList());

        tableView.getColumns().addAll(
                renderableTable.getProperties().stream()
                        .map(this::column)
                        .collect(Collectors.toList()));

        tableView.setRowFactory(param -> {
            val row = new TableRow<I>();

            row.itemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    val index = lazyLoadingDataSource.findIndexOf(newValue);
                    loadingBatchManager.loadDataIfNecessary(index);
                }
            });

            return row;
        });

        VBox.setVgrow(tableView, Priority.ALWAYS);
        tableView.getStyleClass().add(TABLE_STYLE_CLASS);
        tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        tableView.autosize();

        return tableView;
    }

    private TableColumn<I, String> column(final ReadOnlyStringProperty<I> columnProperty) {
        val column = new TableColumn<I, String>(columnProperty.getTitle().getText());

        column.setSortable(false); // Not sortable because of lazy loading
        column.setMinWidth(100);
        column.setCellValueFactory(cellData -> {
            val value = columnProperty.getValueExtractor().apply(cellData.getValue());
            return new SimpleStringProperty(value);
        });

        return column;
    }
}
