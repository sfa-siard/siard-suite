package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siard2.api.Cell;
import ch.admin.bar.siardsuite.visitor.SiardArchiveVisitor;
import ch.admin.bar.siardsuite.model.TreeContentView;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class DatabaseCell extends DatabaseObject {

    protected final SiardArchive archive;
    protected final DatabaseSchema schema;
    protected final DatabaseTable table;
    protected final DatabaseColumn column;
    protected final DatabaseRow row;
    protected final Cell cell;
    protected final String index;
    protected final String name;
    protected final String type;
    protected final String value;

    protected DatabaseCell(SiardArchive archive, DatabaseSchema schema, DatabaseTable table, DatabaseRow row, Cell cell) {
        this.archive = archive;
        this.schema = schema;
        this.table = table;
        column = new DatabaseColumn(archive, schema, table, cell.getMetaColumn());
        this.row = row;
        this.cell = cell;
        index = String.valueOf(cell.getMetaColumn().getPosition());
        name = "Cell " + index;
        try {
            type = cell.getMetaColumn().getType();
            value = cell.getString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void shareProperties(SiardArchiveVisitor visitor) {}

    @Override
    protected void populate(TableView tableView, TreeContentView type) {}

    @Override
    protected void populate(VBox vbox, TreeContentView type) {}

}
