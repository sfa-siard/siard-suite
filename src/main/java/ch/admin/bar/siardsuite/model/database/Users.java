package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siardsuite.component.SiardTableView;
import ch.admin.bar.siardsuite.model.TreeContentView;
import ch.admin.bar.siardsuite.visitor.SiardArchiveVisitor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Users extends DatabaseObject {

    private List<User> users;

    public Users(List<User> users) {
        this.users = users;
    }

    @Override
    protected void populate(TableView<Map> tableView, TreeContentView type) {
        if (tableView == null || type == null) return;
        new SiardTableView(tableView)
                .withColumn(TABLE_CONTAINER_USERS_HEADER_USERNAME, USERNAME)
                .withColumn(TABLE_CONTAINER_USERS_HEADER_DESCRIPTION, DESCRIPTION)
                .withItems(items());
    }

    private ObservableList<Map> items() {
        MapUserVisitor visitor = new MapUserVisitor();
        return FXCollections.observableArrayList(users.stream()
                                                      .map(user -> user.accept(visitor))
                                                      .collect(Collectors.toList()));
    }

    @Override
    protected void populate(VBox vBox, TreeContentView type) {

    }

    @Override
    public String name() {
        return null;
    }

    @Override
    protected void shareProperties(SiardArchiveVisitor visitor) {
    }

    private class MapUserVisitor implements UserVisitor<Map<String, String>> {
        @Override
        public Map<String, String> visit(String name, String description) {
            Map<String, String> item = new HashMap<>();
            item.put(USERNAME, name);
            item.put(DESCRIPTION, description);
            return item;
        }
    }

    private static final String TABLE_CONTAINER_USERS_HEADER_USERNAME = "tableContainer.users.header.username";
    private static final String TABLE_CONTAINER_USERS_HEADER_DESCRIPTION = "tableContainer.users.header.description";
    private static final String USERNAME = "username";
    private static final String DESCRIPTION = "description";
}
