package ch.admin.bar.siardsuite.presenter.tree;

import ch.admin.bar.siardsuite.model.database.Routine;
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

public class RoutinesTableViewPopulatorStrategy implements TableViewPopulatorStrategy<Routine> {
    @Override
    public void populate(TableView<Map> tableView, List<Routine> routines, boolean onlyMetaData) {
        tableView.getColumns().add(createColumn("tableContainer.table.header.row", "index"));
        tableView.getColumns().add(createColumn("tableContainer.routine.header.name", "name"));
        tableView.getColumns().add(createColumn("tableContainer.routine.header.specificName", "specificName"));
        tableView.getColumns().add(createColumn("tableContainer.routine.header.characteristics", "characteristics"));
        tableView.getColumns().add(createColumn("tableContainer.routine.header.returnType", "returnType"));
        tableView.getColumns()
                 .add(createColumn("tableContainer.routine.header.numberOfParameters", "numberOfParameters"));
        tableView.setItems(items(routines));
    }

    private ObservableList<Map> items(List<Routine> routines) {
        final ObservableList<Map> items = FXCollections.observableArrayList();
        for (Routine routine : routines) {
            Map<String, String> item = new HashMap<>();
            item.put("index", String.valueOf(routines.indexOf(routine) + 1));
            item.put("name", routine.name());
            item.put("specificName", routine.specificName());
            item.put("characteristics", routine.characteristics());
            item.put("returnType", routine.returnType());
            item.put("numberOfParameters", routine.numberOfParameters());
            items.add(item);
        }
        return items;
    }

    private TableColumn<Map, ?> createColumn(String label, String valueName) {
        final TableColumn<Map, StringProperty> column = new TableColumn<>();
        column.textProperty().bind(I18n.createStringBinding(label));
        column.setCellValueFactory(new MapValueFactory<>(valueName));
        return column;
    }
}
