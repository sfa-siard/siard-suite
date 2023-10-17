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
    public String name() {
        return null;
    }

    @Override
    protected void shareProperties(SiardArchiveVisitor visitor) {
    }
}
