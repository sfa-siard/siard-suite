package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siard2.api.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class DatabaseTable extends DatabaseObject {

    protected final SiardArchive siardArchive;
    protected final DatabaseSchema schema;

    @Getter
    protected final Table table;
    protected final boolean onlyMetaData;
    @Getter
    public final String name;
    @Getter
    @Setter
    public String description;
    @Getter
    protected final List<DatabaseColumn> columns = new ArrayList<>();
    public final String numberOfColumns;
    @Getter
    protected final List<DatabaseRow> rows = new ArrayList<>();
    @Getter
    public final String numberOfRows;

    public DatabaseTable(SiardArchive archive, DatabaseSchema schema, Table table) {
        this(archive, schema, table, false);
    }

    public DatabaseTable(SiardArchive archive, DatabaseSchema schema, Table table, boolean onlyMetaData) {
        this.siardArchive = archive;
        this.schema = schema;
        this.table = table;
        this.onlyMetaData = onlyMetaData;
        name = table.getMetaTable().getName();
        description = table.getMetaTable().getDescription();
        for (int i = 0; i < table.getMetaTable().getMetaColumns(); i++) {
            columns.add(new DatabaseColumn(archive, schema, table.getMetaTable().getMetaColumn(i)));
        }
        numberOfColumns = String.valueOf(columns.size());
        numberOfRows = String.valueOf(table.getMetaTable().getRows());
    }

    protected void export(File directory) throws IOException {
        File destination = new File(directory.getAbsolutePath(), this.name + ".html");
        File lobFolder = new File(directory, "lobs/"); //TODO: was taken from the user properties in the original GUI
        OutputStream outPutStream = new FileOutputStream(destination);
        this.table.getParentSchema()
                .getParentArchive()
                .getSchema(this.schema.name)
                .getTable(this.name)
                .exportAsHtml(outPutStream, lobFolder);
        outPutStream.close();
    }

    @Override
    public String name() {
        return name;
    }

    public List<DatabaseColumn> columns() {
        return this.columns;
    }

    public String numberOfRows() {
        return this.numberOfRows;
    }

    public void write() {
        val schema = siardArchive.getArchive()
                .getSchema(name);
        schema.getMetaSchema().setDescription(description);
    }
}
