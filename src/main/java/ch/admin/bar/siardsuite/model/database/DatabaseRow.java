package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siard2.api.Record;
import ch.admin.bar.siardsuite.model.TreeContentView;
import ch.admin.bar.siardsuite.visitor.SiardArchiveVisitor;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseRow extends DatabaseObject {

    protected final SiardArchive archive;
    protected final DatabaseSchema schema;
    protected final DatabaseTable table;
    protected final Record record;
    protected final String index;
    protected final String name;
    protected final List<DatabaseCell> cells = new ArrayList<>();

    protected DatabaseRow(SiardArchive archive, DatabaseSchema schema, DatabaseTable table, Record record) {
        this.archive = archive;
        this.schema = schema;
        this.table = table;
        this.record = record;
        index = String.valueOf(record.getRecord());
        name = "Row " + index;
        try {
            for (int i = 0; i < record.getCells(); i++) {
                cells.add(new DatabaseCell(archive, schema, table, this, record.getCell(i)));
            }
        } catch (IOException ignored) {
        }
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
