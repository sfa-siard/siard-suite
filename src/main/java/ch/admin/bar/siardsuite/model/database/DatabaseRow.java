package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siard2.api.Record;
import lombok.Getter;

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

    @Getter
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

}
