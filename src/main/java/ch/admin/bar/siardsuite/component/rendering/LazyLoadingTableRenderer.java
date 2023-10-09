package ch.admin.bar.siardsuite.component.rendering;

import ch.admin.bar.siardsuite.component.rendering.model.LazyLoadingDataSource;
import ch.admin.bar.siardsuite.component.rendering.model.ReadOnlyStringProperty;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableLazyLoadingTable;
import ch.admin.bar.siardsuite.component.rendering.utils.LoadingBatchManager;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.view.TableSize;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lombok.Builder;
import lombok.val;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LazyLoadingTableRenderer<T, I> {

    private static final String TABLE_STYLE_CLASS = "tree-table-view";

    private final RenderableLazyLoadingTable<T, I> renderableTable;
    private final T dataHolder;
    private final LazyLoadingDataSource<I> lazyLoadingDataSource;

    private final List<TableRow<I>> tableRows = new ArrayList<>();
    private final List<I> displayedItems = new ArrayList<>();

    private final ObservableList<I> observableRows = FXCollections.observableArrayList();

    // state
    private int indexFirstLoaded = -1;
    private int numberOfLoaded = -1;
    private final int loadingBatchSize = 100;

    @Builder
    public LazyLoadingTableRenderer(RenderableLazyLoadingTable<T, I> renderableTable, T dataHolder) {
        this.renderableTable = renderableTable;
        this.dataHolder = dataHolder;
        this.lazyLoadingDataSource = renderableTable.getDataExtractor().apply(dataHolder);
    }

    public TableView<I> render() {

        val loadingBatchManager = new LoadingBatchManager<>(lazyLoadingDataSource);
        loadingBatchManager.loadDataIfNecessary(0);
        val tableView = new TableView<I>(loadingBatchManager.getObservableList());

        tableView.getColumns().addAll(
                renderableTable.getProperties().stream()
                        .map(this::column)
                        .collect(Collectors.toList()));


        tableView.setRowFactory(param -> {
            val row = new TableRow<I>();
            tableRows.add(row);

            row.itemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    val index = lazyLoadingDataSource.findIndexOf(newValue);
                    loadingBatchManager.loadDataIfNecessary(index);
                }
            });

            return row;
        });

        tableView.getStyleClass().add(TABLE_STYLE_CLASS);
        VBox.setVgrow(tableView, Priority.ALWAYS);

        tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        tableView.autosize();
        //new TableSize(tableView).resize();


        return tableView;
    }

    private TableColumn<I, String> column(final ReadOnlyStringProperty<I> columnProperty) {
        val column = new TableColumn<I, String>(I18n.get(columnProperty.getTitle()));




        column.setSortable(false); // Not sortable because of lazy loading
        column.setMinWidth(100);
        column.setCellValueFactory(cellData -> {
            val value = columnProperty.getValueExtractor().apply(cellData.getValue());
            return new SimpleStringProperty(value);
        });

        return column;
    }
}
