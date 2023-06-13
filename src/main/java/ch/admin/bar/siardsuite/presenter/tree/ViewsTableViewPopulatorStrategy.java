package ch.admin.bar.siardsuite.presenter.tree;

import ch.admin.bar.siardsuite.component.SiardTableView;
import ch.admin.bar.siardsuite.model.database.DatabaseView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewsTableViewPopulatorStrategy implements TableViewPopulatorStrategy<DatabaseView> {

    private final List<DatabaseView> views;

    public ViewsTableViewPopulatorStrategy(List<DatabaseView> views) {
        this.views = views;
    }

    public void populate(TableView<Map> tableView, boolean onlyMetaData) {
        new SiardTableView(tableView).withColumn(TABLE_CONTAINER_TABLE_HEADER_ROW, INDEX)
                                     .withColumn(TABLE_CONTAINER_TABLE_HEADER_VIEW_NAME, NAME)
                                     .withColumn(TABLE_CONTAINER_TABLE_HEADER_NUMBER_OF_COLUMNS, NUMBER_OF_COLUMNS)
                                     .withDataColumn(TABLE_CONTAINER_TABLE_HEADER_NUMBER_OF_ROWS, NUMBER_OF_ROWS)
                                     .withItems(items(views));
    }

    private ObservableList<Map> items(List<DatabaseView> views) {
        final ObservableList<Map> items = FXCollections.observableArrayList();
        for (DatabaseView view : views) {
            Map<String, String> item = new HashMap<>();
            item.put(INDEX, String.valueOf(views.indexOf(view) + 1));
            item.put(NAME, view.name());
            item.put(NUMBER_OF_COLUMNS, view.getNumberOfColumns());
            item.put(NUMBER_OF_ROWS, view.getNumberOfRows());
            items.add(item);
        }
        return items;
    }

    private static final String TABLE_CONTAINER_TABLE_HEADER_ROW = "tableContainer.table.header.row";
    private static final String INDEX = "index";
    private static final String TABLE_CONTAINER_TABLE_HEADER_VIEW_NAME = "tableContainer.table.header.viewName";
    private static final String NAME = "name";
    private static final String TABLE_CONTAINER_TABLE_HEADER_NUMBER_OF_COLUMNS = "tableContainer.table.header.numberOfColumns";
    private static final String NUMBER_OF_COLUMNS = "numberOfColumns";
    private static final String TABLE_CONTAINER_TABLE_HEADER_NUMBER_OF_ROWS = "tableContainer.table.header.numberOfRows";
    private static final String NUMBER_OF_ROWS = "numberOfRows";
}
