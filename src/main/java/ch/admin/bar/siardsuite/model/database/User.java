package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siardsuite.model.TreeContentView;
import ch.admin.bar.siardsuite.visitor.SiardArchiveVisitor;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

import java.util.Map;

public class User {
    private final String name;

    private final String description;

    public User(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public <T> T accept(UserVisitor<T> visitor) {
        return visitor.visit(this.name, this.description);
    }

}
