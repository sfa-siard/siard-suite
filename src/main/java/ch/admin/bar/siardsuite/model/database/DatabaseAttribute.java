package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siard2.api.MetaAttribute;
import ch.admin.bar.siardsuite.model.TreeContentView;
import ch.admin.bar.siardsuite.visitor.SiardArchiveVisitor;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

import java.util.Map;

public class DatabaseAttribute extends DatabaseObject {

    public DatabaseAttribute(MetaAttribute metaAttribute) {

    }

    @Override
    protected void shareProperties(SiardArchiveVisitor visitor) {

    }

    @Override
    protected void populate(TableView<Map> tableView, TreeContentView type) {

    }

    @Override
    protected void populate(VBox vBox, TreeContentView type) {

    }
}
