package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siard2.api.Cell;
import ch.admin.bar.siardsuite.model.TreeContentView;
import ch.admin.bar.siardsuite.visitor.DatabaseArchiveVisitor;
import javafx.scene.control.TableView;
import java.io.IOException;

public class DatabaseCell extends DatabaseObject {

    protected final String index;
    protected final String name;
    protected final String type;

    protected DatabaseCell(Cell cell) {
        index = String.valueOf(cell.getMetaColumn().getPosition());
        name = cell.getMetaColumn().getName();
        try {
            type = cell.getMetaColumn().getType();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void shareProperties(DatabaseArchiveVisitor visitor) {}

    @Override
    protected void populate(TableView tableView, TreeContentView type) {}

}
