package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siard2.api.Table;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;
import java.util.List;

public class DatabaseTable {

    private StringProperty name;
    private List<DatabaseColumn> columns = new ArrayList<>();

    public DatabaseTable(Table table) {
        name = new SimpleStringProperty(table.getMetaTable().getName());
        for (int i = 0; i < table.getMetaTable().getMetaColumns(); i++) {
            columns.add(new DatabaseColumn(table.getMetaTable().getMetaColumn(i)));
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

}
