package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siard2.api.MetaColumn;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class DatabaseColumn {

    private StringProperty name;

    public DatabaseColumn(MetaColumn column) {
        name = new SimpleStringProperty(column.getName());
    }

    public StringProperty getName() {
        return name;
    }

}
