package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siard2.api.Cell;
import ch.admin.bar.siardsuite.model.facades.PreTypeFacade;
import ch.enterag.utils.BU;
import lombok.Getter;

import java.io.IOException;

public class DatabaseCell {

    private final SiardArchive archive;
    private final DatabaseSchema schema;
    private final DatabaseTable table;
    private final DatabaseColumn column;
    private final DatabaseRow row;
    private final Cell cell;
    private final String index;
    private final String name;
    private final String type;
    @Getter
    private final String value;

    protected DatabaseCell(SiardArchive archive, DatabaseSchema schema, DatabaseTable table, DatabaseRow row,
                           Cell cell) {
        this.archive = archive;
        this.schema = schema;
        this.table = table;
        column = new DatabaseColumn(archive, schema, cell.getMetaColumn());
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
}
