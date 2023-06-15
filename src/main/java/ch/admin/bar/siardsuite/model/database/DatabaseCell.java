package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siard2.api.Cell;
import ch.admin.bar.siardsuite.model.TreeContentView;
import ch.admin.bar.siardsuite.visitor.SiardArchiveVisitor;
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

    protected DatabaseCell(SiardArchive archive, DatabaseSchema schema, DatabaseTable table, DatabaseRow row,
                           Cell cell) {
        this.archive = archive;
        this.schema = schema;
        this.table = table;
        column = new DatabaseColumn(archive, schema, table, cell.getMetaColumn());
        this.row = row;
        this.cell = cell;
        index = String.valueOf(cell.getMetaColumn().getPosition());
        name = "Cell " + index;
        String type = null;
        try {
            type = cell.getMetaColumn().getType();
        } catch (IOException ignored) {
        }
        this.type = type;
        String value = null;
        try {
            value = cell.getString();
        } catch (IOException | IllegalArgumentException ignored) {
        }
        this.value = value;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    protected void shareProperties(SiardArchiveVisitor visitor) {
    }

    @Override
    public void populate(TableView tableView, TreeContentView type) {
    }

    @Override
    public void populate(VBox vbox, TreeContentView type) {
    }

}
