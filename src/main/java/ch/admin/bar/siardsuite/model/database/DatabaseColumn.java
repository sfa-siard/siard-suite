package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siard2.api.MetaColumn;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.net.URI;

@Getter
@Setter
public class DatabaseColumn {

    private final MetaColumn column;

    private String lobFolder;
    private String mimeType;
    private String description;

    protected DatabaseColumn(MetaColumn column) {
        this.column = column;

        lobFolder = column.getLobFolder() != null ? column.getLobFolder().toString() : "";
        mimeType = column.getMimeType();
        description = column.getDescription();
    }

    public int getIndex() {
        return column.getPosition();
    }

    public String getName() {
        return column.getName();
    }

    public String getType() throws IOException {
        return column.getType();
    }

    public String getUserDefinedTypeSchema() {
        return column.getTypeSchema();
    }

    public String getUserDefinedTypeName() {
        return column.getTypeName();
    }

    public String getOriginalType() {
        return column.getTypeOriginal();
    }

    public boolean isNullable() {
        return column.isNullable();
    }

    public String getDefaultValue() {
        return column.getDefaultValue();
    }

    public int getCardinality() throws IOException {
        return column.getCardinality();
    }

    public void write() throws IOException {
        column.setLobFolder(URI.create(lobFolder));
        column.setMimeType(mimeType);
        column.setDescription(description);
    }
}
