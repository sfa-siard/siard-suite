package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siardsuite.model.TreeContentView;
import ch.admin.bar.siardsuite.util.MetaSearchTerm;
import ch.admin.bar.siardsuite.visitor.SiardArchiveVisitor;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

import java.util.Map;
import java.util.regex.Pattern;

public abstract class DatabaseObject {

    public abstract String name();
}
