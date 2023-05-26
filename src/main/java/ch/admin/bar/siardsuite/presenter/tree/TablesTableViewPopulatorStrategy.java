package ch.admin.bar.siardsuite.presenter.tree;

import ch.admin.bar.siardsuite.model.database.DatabaseTable;
import ch.admin.bar.siardsuite.util.I18n;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.MapValueFactory;

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
        final TableColumn<Map, StringProperty> col0 = new TableColumn<>();
        final TableColumn<Map, StringProperty> col1 = new TableColumn<>();
        final TableColumn<Map, StringProperty> col2 = new TableColumn<>();
        col0.textProperty().bind(I18n.createStringBinding("tableContainer.table.header.row"));
        col1.textProperty().bind(I18n.createStringBinding("tableContainer.table.header.tableName"));
        col2.textProperty().bind(I18n.createStringBinding("tableContainer.table.header.numberOfColumns"));
        col0.setCellValueFactory(new MapValueFactory<>("index"));
        col1.setCellValueFactory(new MapValueFactory<>("name"));
        col2.setCellValueFactory(new MapValueFactory<>("numberOfColumns"));
        tableView.getColumns().add(col0);
        tableView.getColumns().add(col1);
        tableView.getColumns().add(col2);
        if (!onlyMetaData) {
            final TableColumn<Map, StringProperty> col3 = new TableColumn<>();
            col3.textProperty().bind(I18n.createStringBinding("tableContainer.table.header.numberOfRows"));
            col3.setCellValueFactory(new MapValueFactory<>("numberOfRows"));
            tableView.getColumns().add(col3);
        }
        tableView.setItems(items(tables));
    }

    private ObservableList<Map> items(List<DatabaseTable> tables) {
        final ObservableList<Map> items = FXCollections.observableArrayList();
        for (DatabaseTable table : tables) {
            Map<String, String> item = new HashMap<>();
            item.put("index", String.valueOf(tables.indexOf(table) + 1));
            item.put("name", table.name);
            item.put("numberOfColumns", table.numberOfColumns);
            item.put("numberOfRows", table.numberOfRows);
            items.add(item);
        }
        return items;
    }

}
