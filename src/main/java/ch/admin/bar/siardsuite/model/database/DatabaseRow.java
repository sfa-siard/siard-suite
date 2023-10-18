package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siard2.api.Record;
import ch.admin.bar.siardsuite.component.rendered.utils.ListAssembler;
import lombok.Getter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseRow {

    private final SiardArchive archive;
    private final DatabaseSchema schema;
    private final DatabaseTable table;
    private final Record record;
    private final String index;
    private final String name;

    protected DatabaseRow(SiardArchive archive, DatabaseSchema schema, DatabaseTable table, Record record) {
        this.archive = archive;
        this.schema = schema;
        this.table = table;
        this.record = record;
        index = String.valueOf(record.getRecord());
        name = "Row " + index;
    }
}
