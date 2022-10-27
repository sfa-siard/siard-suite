package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siard2.api.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseTable {

    private final StringProperty name;
    private final List<DatabaseColumn> columns = new ArrayList<>();
    private final List<DatabaseRow> rows = new ArrayList<>();

    public DatabaseTable(Table table) {
        this(table, false);
    }

    public DatabaseTable(Table table, boolean onlyMetaData) {
        name = new SimpleStringProperty(table.getMetaTable().getName());
        for (int i = 0; i < table.getMetaTable().getMetaColumns(); i++) {
            columns.add(new DatabaseColumn(table.getMetaTable().getMetaColumn(i)));
        }
        if (!onlyMetaData) {
            try {
                int i = 0;
                final RecordDispenser recordDispenser = table.openRecords();
                while (i < table.getMetaTable().getRows()) {
                    rows.add(new DatabaseRow(recordDispenser.get()));
                    i++;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    public DatabaseTable(String name) {
        this.name = new SimpleStringProperty(name);
    }

    public StringProperty getName() {
        return name;
    }

    public List<DatabaseColumn> getColumns() {
        return columns;
    }

    public List<DatabaseRow> getRows() {
        return rows;
    }

}
