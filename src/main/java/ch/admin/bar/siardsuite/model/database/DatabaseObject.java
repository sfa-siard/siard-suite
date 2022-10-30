package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siardsuite.model.TreeContentView;
import javafx.scene.control.TableView;
import java.util.Map;

public abstract class DatabaseObject {

    protected abstract void populate(TableView<Map> tableView, TreeContentView type);
}
