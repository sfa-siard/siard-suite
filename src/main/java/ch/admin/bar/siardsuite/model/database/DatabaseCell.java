package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siard2.api.Cell;
import ch.admin.bar.siardsuite.util.I18n;
import javafx.beans.property.*;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;

public class DatabaseCell {

    protected StringProperty index;
    protected StringProperty name;
    protected StringProperty type;

    protected DatabaseCell(Cell cell) {
        index = new SimpleStringProperty(String.valueOf(cell.getMetaColumn().getPosition()));
        name = new SimpleStringProperty(cell.getMetaColumn().getName());
        try {
            type = new SimpleStringProperty(cell.getMetaColumn().getType());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
