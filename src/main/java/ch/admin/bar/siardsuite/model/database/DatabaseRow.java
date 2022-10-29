package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siard2.api.Record;
import javafx.beans.property.*;
import javafx.scene.control.TableView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseRow {

    protected final DatabaseArchive archive;
    protected final DatabaseSchema schema;
    protected final DatabaseTable table;
    protected final StringProperty index;
    protected final List<DatabaseCell> cells = new ArrayList<>();
    protected TableView<DatabaseCell> tableView;

    protected DatabaseRow(DatabaseArchive archive, DatabaseSchema schema, DatabaseTable table, Record record) {
        this.archive = archive;
        this.schema = schema;
        this.table = table;
        index = new SimpleStringProperty(String.valueOf(record.getRecord()));
        try {
            for (int i = 0; i < record.getCells(); i++) {
                cells.add(new DatabaseCell(record.getCell(i)));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
