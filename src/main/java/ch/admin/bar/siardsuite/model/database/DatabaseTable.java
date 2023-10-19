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

public class DatabaseTable {

    private final SiardArchive siardArchive;
    private final DatabaseSchema schema;

    @Getter
    private final Table table;
    @Getter
    private final String name;
    @Getter
    @Setter
    private String description;
    @Getter
    private final List<DatabaseColumn> columns = new ArrayList<>();
    @Getter
    private final long numberOfRows;

    public DatabaseTable(SiardArchive archive, DatabaseSchema schema, Table table) {
        this.siardArchive = archive;
        this.schema = schema;
        this.table = table;
        name = table.getMetaTable().getName();
        description = table.getMetaTable().getDescription();
        for (int i = 0; i < table.getMetaTable().getMetaColumns(); i++) {
            columns.add(new DatabaseColumn(archive, schema, table.getMetaTable().getMetaColumn(i)));
        }
        numberOfRows = table.getMetaTable().getRows();
    }

    protected void export(File directory) throws IOException {
        File destination = new File(directory.getAbsolutePath(), this.name + ".html");
        File lobFolder = new File(directory, "lobs/"); //TODO: was taken from the user properties in the original GUI
        OutputStream outPutStream = new FileOutputStream(destination);
        this.table.getParentSchema()
                .getParentArchive()
                .getSchema(this.schema.name())
                .getTable(this.name)
                .exportAsHtml(outPutStream, lobFolder);
        outPutStream.close();
    }

    public String name() {
        return name;
    }

    public List<DatabaseColumn> columns() {
        return this.columns;
    }

    public void write() {
        val schema = siardArchive.getArchive()
                .getSchema(name);
        schema.getMetaSchema().setDescription(description);
    }
}
