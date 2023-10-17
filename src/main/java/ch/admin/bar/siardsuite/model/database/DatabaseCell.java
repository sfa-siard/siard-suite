package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siard2.api.Cell;
import ch.admin.bar.siardsuite.model.TreeContentView;
import ch.admin.bar.siardsuite.model.facades.PreTypeFacade;
import ch.admin.bar.siardsuite.visitor.SiardArchiveVisitor;
import ch.enterag.utils.BU;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import lombok.Getter;

import java.io.IOException;

public class DatabaseCell extends DatabaseObject {

    protected final SiardArchive archive;
    protected final DatabaseSchema schema;
    protected final DatabaseTable table;
    protected final DatabaseColumn column;
    protected final DatabaseRow row;
    protected final Cell cell;
    protected final String index;
    protected final String name;
    protected final String type;
    @Getter
    protected final String value;

    protected DatabaseCell(SiardArchive archive, DatabaseSchema schema, DatabaseTable table, DatabaseRow row,
                           Cell cell) {
        this.archive = archive;
        this.schema = schema;
        this.table = table;
        column = new DatabaseColumn(archive, schema, table, cell.getMetaColumn());
        this.row = row;
        this.cell = cell;
        index = String.valueOf(cell.getMetaColumn().getPosition());
        name = "Cell " + index;
        String type = null;
        try {
            type = cell.getMetaColumn().getType();
        } catch (IOException ignored) {
        }
        this.type = type;
        String value = null;
        try {
            value = stringifyValueFromCell();
        } catch (IOException | IllegalArgumentException ignored) {
        }
        this.value = value;
    }

    private String stringifyValueFromCell() throws IOException {
        if (new PreTypeFacade(this.cell.getMetaColumn().getPreType()).isBlob()) {
            return "0x" + BU.toHex(this.cell.getBytes()).substring(0, 16) + "...";
        }
        return this.cell.getString();
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    protected void shareProperties(SiardArchiveVisitor visitor) {
    }
}
