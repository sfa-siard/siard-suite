package ch.admin.bar.siardsuite.component;

import ch.admin.bar.siardsuite.util.I18n;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.MapValueFactory;

import java.util.Map;

public class SiardTableView {
    private final TableView<Map> tableView;

    public SiardTableView(TableView<Map> tableView) {
        this.tableView = tableView;
    }

    public SiardTableView withColumn(String label, String key) {
        tableView.getColumns().add(column(label, key));
        return this;
    }

    public SiardTableView withItems(ObservableList<Map> items) {
        if (items.isEmpty()) tableView.setVisible(false);
        tableView.setItems(items);
        return this;
    }

    private TableColumn<Map, ?> column(String label, String key) {
        final TableColumn<Map, StringProperty> column = new TableColumn<>();
        I18n.bind(column.textProperty(), label);
        column.setCellValueFactory(new MapValueFactory<>(key));
        return column;
    }
}
