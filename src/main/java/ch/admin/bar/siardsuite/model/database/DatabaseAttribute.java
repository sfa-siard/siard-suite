package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siard2.api.MetaAttribute;
import ch.admin.bar.siardsuite.model.TreeContentView;
import ch.admin.bar.siardsuite.visitor.SiardArchiveVisitor;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

import java.util.Map;

public class DatabaseAttribute extends DatabaseObject {

    // TODO: not sure if i should store props in fields or just keep the metaAttribute and then go from there...
    private final String name;

    public DatabaseAttribute(MetaAttribute metaAttribute) {
        this.name = metaAttribute.getName();
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

    public String name() {
        return this.name;
    }
}
