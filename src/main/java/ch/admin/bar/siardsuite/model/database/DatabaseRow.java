package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siard2.api.Record;
import javafx.beans.property.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseRow {

    private final LongProperty index;
    private final List<DatabaseCell> cells = new ArrayList<>();

    public DatabaseRow(Record record) {
        index = new SimpleLongProperty(record.getRecord());
        try {
            for (int i = 0; i < record.getCells(); i++) {
                cells.add(new DatabaseCell(record.getCell(i)));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
