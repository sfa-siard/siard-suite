package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siard2.api.Cell;
import javafx.beans.property.*;

import java.io.IOException;

public class DatabaseCell {

    private StringProperty index;
    private StringProperty name;
    private StringProperty type;

    public DatabaseCell(Cell cell) {
        index = new SimpleStringProperty(String.valueOf(cell.getMetaColumn().getPosition()));
        name = new SimpleStringProperty(cell.getMetaColumn().getName());
        try {
            type = new SimpleStringProperty(cell.getMetaColumn().getType());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
