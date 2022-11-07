package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siardsuite.model.TreeContentView;
import ch.admin.bar.siardsuite.visitor.SiardArchiveVisitor;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

import java.util.Map;

public abstract class DatabaseObject {

    protected abstract void shareProperties(SiardArchiveVisitor visitor);

    protected abstract void populate(TableView<Map> tableView, TreeContentView type);

    protected abstract void populate(VBox vBox, TreeContentView type);

}
