package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siardsuite.model.TreeContentView;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.visitor.SiardArchiveVisitor;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseTypes extends DatabaseObject {

    private List<DatabaseType> types;

    public DatabaseTypes(List<DatabaseType> types) {
        this.types = types;
    }

    @Override
    protected void shareProperties(SiardArchiveVisitor visitor) {
    }

    @Override
    protected void populate(TableView<Map> tableView, TreeContentView type) {
        if (tableView != null && type != null) {
            final TableColumn<Map, StringProperty> col0 = new TableColumn<>();
            final TableColumn<Map, StringProperty> col1 = new TableColumn<>();
            final TableColumn<Map, StringProperty> col2 = new TableColumn<>();
            final TableColumn<Map, StringProperty> col3 = new TableColumn<>();
            final TableColumn<Map, StringProperty> col4 = new TableColumn<>();
            final TableColumn<Map, StringProperty> col5 = new TableColumn<>();

            col0.textProperty().bind(I18n.createStringBinding("tableContainer.types.header.name"));
            col1.textProperty().bind(I18n.createStringBinding("tableContainer.types.header.category"));
            col2.textProperty().bind(I18n.createStringBinding("tableContainer.types.header.instantiable"));
            col3.textProperty().bind(I18n.createStringBinding("tableContainer.types.header.final"));
            col4.textProperty().bind(I18n.createStringBinding("tableContainer.types.header.base"));
            col5.textProperty().bind(I18n.createStringBinding("tableContainer.types.header.description"));

            col0.setCellValueFactory(new MapValueFactory<>("name"));
            col1.setCellValueFactory(new MapValueFactory<>("category"));
            col2.setCellValueFactory(new MapValueFactory<>("instantiable"));
            col3.setCellValueFactory(new MapValueFactory<>("final"));
            col4.setCellValueFactory(new MapValueFactory<>("base"));
            col5.setCellValueFactory(new MapValueFactory<>("description"));

            tableView.getColumns().add(col0);
            tableView.getColumns().add(col1);
            tableView.getColumns().add(col2);
            tableView.getColumns().add(col3);
            tableView.getColumns().add(col4);
            tableView.getColumns().add(col5);
            tableView.setItems(items());
        }
    }

    private ObservableList<Map> items() {
        final ObservableList<Map> items = FXCollections.observableArrayList();
        MapTypesVisitor visitor = new MapTypesVisitor();
        for (DatabaseType type : types) {
            items.add(type.accept(visitor));
        }
        return items;
    }

    @Override
    protected void populate(VBox vBox, TreeContentView type) {

    }

    private class MapTypesVisitor implements TypeVisitor<Map<String, String>> {

        @Override
        public Map<String, String> visit(String name, String category, boolean instantiable, boolean isFinal,
                                         String base,
                                         String description) {

            Map<String, String> item = new HashMap<>();

            item.put("name", name);
            item.put("category", category);
            item.put("instantiable", String.valueOf(instantiable));
            item.put("final", String.valueOf(isFinal));
            item.put("base", base);
            item.put("description", description);


            item.put("username", name);
            item.put("description", description);
            return item;
        }
    }
}
