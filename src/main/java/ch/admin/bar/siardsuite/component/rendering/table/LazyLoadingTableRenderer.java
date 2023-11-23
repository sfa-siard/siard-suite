package ch.admin.bar.siardsuite.component.rendering.table;

import ch.admin.bar.siardsuite.component.rendering.model.LazyLoadingDataSource;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableLazyLoadingTable;
import ch.admin.bar.siardsuite.component.rendering.utils.LoadingBatchManager;
import ch.admin.bar.siardsuite.view.ErrorDialogOpener;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lombok.Builder;
import lombok.NonNull;
import lombok.val;

import java.util.stream.Collectors;

public class LazyLoadingTableRenderer<T, I> {

    private static final String TABLE_STYLE_CLASS = "tree-table-view";

    private final RenderableLazyLoadingTable<T, I> renderableTable;
    private final LazyLoadingDataSource<I> lazyLoadingDataSource;

    private final ErrorDialogOpener errorDialogOpener;


    @Builder
    public LazyLoadingTableRenderer(
            @NonNull final RenderableLazyLoadingTable<T, I> renderableTable,
            @NonNull final T dataHolder,
            @NonNull final ErrorDialogOpener errorDialogOpener) {
        this.renderableTable = renderableTable;
        this.errorDialogOpener = errorDialogOpener;
        this.lazyLoadingDataSource = renderableTable.getDataExtractor().apply(dataHolder);
    }

    public TableView<I> render() {

        val loadingBatchManager = new LoadingBatchManager<>(lazyLoadingDataSource);
        val tableView = new TableView<>(loadingBatchManager.getObservableList());

        val issueConcealer = new JumpingScrollingPositionIssueConcealer(loadingBatchManager, tableView);

        tableView.getColumns().addAll(
                renderableTable.getProperties().stream()
                        .map(TableColumnFactory::column)
                        .collect(Collectors.toList()));

        tableView.setRowFactory(param -> {
            val row = new TableRow<I>();

            row.itemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    val index = lazyLoadingDataSource.findIndexOf(newValue);
                    loadingBatchManager.loadDataIfNecessary(index);

                    issueConcealer.concealIssue();
                }
            });

            row.setOnMouseClicked(event -> {
                val selectionModel = tableView.getSelectionModel();
                if (selectionModel.getSelectedCells().isEmpty()) {
                    return;
                }

                val tablePosition = selectionModel.getSelectedCells().get(0);
                if (tablePosition.getColumn() < 0 ||
                        tablePosition.getColumn() >= renderableTable.getProperties().size()) {
                    return;
                }

                val column = renderableTable.getProperties().get(tablePosition.getColumn());

                column.getOnCellClickedListener()
                        .ifPresent(listener -> {
                            try {
                                listener.onClick(column, row.getItem());
                            } catch (Exception e) {
                                errorDialogOpener.openErrorDialog(e);
                            }
                        });
            });

            return row;
        });

        VBox.setVgrow(tableView, Priority.ALWAYS);
        tableView.getStyleClass().add(TABLE_STYLE_CLASS);
        tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        tableView.autosize();

        return tableView;
    }
}
