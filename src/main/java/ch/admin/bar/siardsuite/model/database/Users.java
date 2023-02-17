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

public class Users extends DatabaseObject {

    private List<User> users;

    public Users(List<User> users) {
        this.users = users;
    }

    @Override
    protected void shareProperties(SiardArchiveVisitor visitor) {}
    @Override
    protected void populate(TableView<Map> tableView, TreeContentView type) {
        if (tableView != null && type != null) {
            final TableColumn<Map, StringProperty> col0 = new TableColumn<>();
            final TableColumn<Map, StringProperty> col1 = new TableColumn<>();
            col0.textProperty().bind(I18n.createStringBinding("tableContainer.users.header.username"));
            col1.textProperty().bind(I18n.createStringBinding("tableContainer.users.header.description"));
            col0.setCellValueFactory(new MapValueFactory<>("username"));
            col1.setCellValueFactory(new MapValueFactory<>("description"));
            tableView.getColumns().add(col0);
            tableView.getColumns().add(col1);
            tableView.setItems(items());
        }
    }

    private ObservableList<Map> items() {
        final ObservableList<Map> items = FXCollections.observableArrayList();
        MapUserVisitor visitor = new MapUserVisitor();
        for (User user : users) {
            items.add(user.accept(visitor));
        }
        return items;
    }

    @Override
    protected void populate(VBox vBox, TreeContentView type) {

    }

    private class MapUserVisitor implements UserVisitor<Map<String, String>> {
        @Override
        public Map<String, String> visit(String name, String description) {
            Map<String, String> item = new HashMap<>();
            item.put("username", name);
            item.put("description", description);
            return item;
        }
    }
}
