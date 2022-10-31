package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siard2.api.MetaColumn;
import ch.admin.bar.siardsuite.model.TreeContentView;
import ch.admin.bar.siardsuite.visitor.DatabaseArchiveVisitor;
import javafx.scene.control.TableView;

public class DatabaseColumn extends DatabaseObject {

    protected final SiardArchive archive;
    protected final DatabaseSchema schema;
    protected final DatabaseTable table;
    protected final String name;

    protected DatabaseColumn(SiardArchive archive, DatabaseSchema schema, DatabaseTable table, MetaColumn column) {
        this.archive = archive;
        this.schema = schema;
        this.table = table;
        name = column.getName();
    }

    protected void shareProperties(DatabaseArchiveVisitor visitor) {
        visitor.visit(name);
    }

    @Override
    protected void populate(TableView tableView, TreeContentView type) {}

}
