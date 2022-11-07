package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siardsuite.model.TreeContentView;
import ch.admin.bar.siardsuite.visitor.SiardArchiveVisitor;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import java.util.Map;
import java.util.regex.Pattern;

public abstract class DatabaseObject {

    protected static boolean contains(String s1, String s2) {
        return  s1 != null
                && s2 != null
                && !s1.isEmpty()
                && !s2.isEmpty()
                && !s1.isBlank()
                && !s2.isBlank()
                && Pattern.compile(Pattern.quote(s2), Pattern.CASE_INSENSITIVE).matcher(s1).find();
    }

    protected abstract void shareProperties(SiardArchiveVisitor visitor);

    protected abstract void populate(TableView<Map> tableView, TreeContentView type);

    protected abstract void populate(VBox vBox, TreeContentView type);

}
