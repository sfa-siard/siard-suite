package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siard2.api.MetaColumn;

public class DatabaseColumn {

    protected final DatabaseArchive archive;
    protected final DatabaseSchema schema;
    protected final DatabaseTable table;
    protected final String name;

    protected DatabaseColumn(DatabaseArchive archive, DatabaseSchema schema, DatabaseTable table, MetaColumn column) {
        this.archive = archive;
        this.schema = schema;
        this.table = table;
        name = column.getName();
    }

}
