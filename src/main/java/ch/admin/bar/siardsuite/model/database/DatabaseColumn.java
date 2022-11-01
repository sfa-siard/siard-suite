package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siard2.api.MetaColumn;
import ch.admin.bar.siardsuite.model.TreeContentView;
import ch.admin.bar.siardsuite.visitor.SiardArchiveVisitor;
import javafx.scene.control.TableView;
import java.io.IOException;

public class DatabaseColumn extends DatabaseObject {

    protected final SiardArchive archive;
    protected final DatabaseSchema schema;
    protected final DatabaseTable table;
    protected final String index;
    protected final String name;
    protected final String type;

    protected DatabaseColumn(SiardArchive archive, DatabaseSchema schema, DatabaseTable table, MetaColumn column) {
        this.archive = archive;
        this.schema = schema;
        this.table = table;
        this.index = String.valueOf(column.getPosition());
        name = column.getName();
        try {
            type = column.getType();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void shareProperties(SiardArchiveVisitor visitor) {
        visitor.visit(name);
    }

    @Override
    protected void populate(TableView tableView, TreeContentView type) {}

}
