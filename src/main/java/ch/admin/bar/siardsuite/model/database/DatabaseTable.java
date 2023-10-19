package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siard2.api.MetaTable;
import ch.admin.bar.siard2.api.Table;
import ch.admin.bar.siardsuite.component.rendered.utils.ListAssembler;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DatabaseTable {




    @Getter
    private final MetaTable metaTable = null; // TODO;


    @Getter
    private final Table table;
    @Getter
    private final String name;

    @Getter
    private final List<DatabaseColumn> columns;
    @Getter
    private final long numberOfRows;

    @Getter
    @Setter
    private String description;

    public DatabaseTable(Table table) {
        this.table = table;

        val metatable = table.getMetaTable();

        name = metatable.getName();
        description = metatable.getDescription();

        this.columns = new ListAssembler<>(metatable::getMetaColumns, metatable::getMetaColumn).assemble()
                .stream()
                .map(DatabaseColumn::new)
                .collect(Collectors.toList());

        numberOfRows = table.getMetaTable().getRows();
    }



    public String name() {
        return name;
    }

    public List<DatabaseColumn> columns() {
        return this.columns;
    }

    public void write() {
        table.getMetaTable().setDescription(description);
    }
}
