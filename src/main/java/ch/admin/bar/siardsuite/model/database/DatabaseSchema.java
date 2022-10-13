package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siard2.api.Schema;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;
import java.util.List;

public class DatabaseSchema {

    private StringProperty name;
    private List<DatabaseTable> tables = new ArrayList<>();

    public DatabaseSchema(Schema schema) {
        name = new SimpleStringProperty(schema.getMetaSchema().getName());
        for (int i = 0; i < schema.getTables(); i++) {
            tables.add(new DatabaseTable(schema.getTable(i)));
        }
    }

    public StringProperty getName() {
        return name;
    }

    public List<DatabaseTable> getTables() {
        return tables;
    }

    public int getNumberOfDescendants() {
        return tables.stream()
                .map(DatabaseTable::getColumns)
                .mapToInt(List::size)
                .sum();
    }

}
