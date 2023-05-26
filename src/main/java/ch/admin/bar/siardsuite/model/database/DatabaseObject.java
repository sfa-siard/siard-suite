package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siardsuite.model.TreeContentView;
import ch.admin.bar.siardsuite.visitor.SiardArchiveVisitor;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

import java.util.Map;
import java.util.regex.Pattern;

public abstract class DatabaseObject {

    public static boolean contains(String s1, String s2) {
        return s1 != null
                && s2 != null
                && !s1.isEmpty()
                && !s2.isEmpty()
                && Pattern.compile(Pattern.quote(s2), Pattern.CASE_INSENSITIVE).matcher(s1).find();
    }

    public static boolean contains(Map map, String s) {
        for (Object key : map.keySet()) {
            if (map.get(key) instanceof String && contains((String) map.get(key), s)) {
                return true;
            }
        }
        return false;
    }

    protected abstract void shareProperties(SiardArchiveVisitor visitor);

    protected abstract void populate(TableView<Map> tableView, TreeContentView type);

    protected abstract void populate(VBox vBox, TreeContentView type);

    protected static Label createLabel(String value, String id) {
        Label label = new Label(value != null && !value.isEmpty() ? value : "-");
        label.setId(id);
        return label;
    }
}
