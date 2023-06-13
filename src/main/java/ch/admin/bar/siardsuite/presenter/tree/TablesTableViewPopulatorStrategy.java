package ch.admin.bar.siardsuite.presenter.tree;

import ch.admin.bar.siardsuite.component.SiardTableView;
import ch.admin.bar.siardsuite.model.database.DatabaseTable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TablesTableViewPopulatorStrategy implements TableViewPopulatorStrategy<DatabaseTable> {

    private final List<DatabaseTable> tables;

    public TablesTableViewPopulatorStrategy(List<DatabaseTable> tables) {
        this.tables = tables;
    }

    @Override
    public void populate(TableView<Map> tableView, boolean onlyMetaData) {
        new SiardTableView(tableView, onlyMetaData)
                .withColumn(TABLE_CONTAINER_TABLE_HEADER_ROW, INDEX)
                .withColumn(TABLE_CONTAINER_TABLE_HEADER_TABLE_NAME, NAME)
                .withColumn(TABLE_CONTAINER_TABLE_HEADER_NUMBER_OF_COLUMNS, NUMBER_OF_COLUMNS)
                .withDataColumn(TABLE_CONTAINER_TABLE_HEADER_NUMBER_OF_ROWS, NUMBER_OF_ROWS)
                .withItems(items(tables));
    }

    private ObservableList<Map> items(List<DatabaseTable> tables) {
        final ObservableList<Map> items = FXCollections.observableArrayList();
        for (DatabaseTable table : tables) {
            Map<String, String> item = new HashMap<>();
            item.put(INDEX, String.valueOf(tables.indexOf(table) + 1));
            item.put(NAME, table.name);
            item.put(NUMBER_OF_COLUMNS, table.numberOfColumns);
            item.put(NUMBER_OF_ROWS, table.numberOfRows);
            items.add(item);
        }
        return items;
    }

    private static final String TABLE_CONTAINER_TABLE_HEADER_ROW = "tableContainer.table.header.row";
    private static final String INDEX = "index";
    private static final String TABLE_CONTAINER_TABLE_HEADER_TABLE_NAME = "tableContainer.table.header.tableName";
    private static final String NAME = "name";
    private static final String TABLE_CONTAINER_TABLE_HEADER_NUMBER_OF_COLUMNS = "tableContainer.table.header.numberOfColumns";
    private static final String NUMBER_OF_COLUMNS = "numberOfColumns";
    private static final String TABLE_CONTAINER_TABLE_HEADER_NUMBER_OF_ROWS = "tableContainer.table.header.numberOfRows";
    private static final String NUMBER_OF_ROWS = "numberOfRows";
}
