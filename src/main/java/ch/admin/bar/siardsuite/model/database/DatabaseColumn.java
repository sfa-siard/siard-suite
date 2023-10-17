package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siard2.api.MetaColumn;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.net.URI;

@Getter
public class DatabaseColumn extends DatabaseObject {

    protected final SiardArchive archive;
    protected final DatabaseSchema schema;
    protected final MetaColumn column;
    protected final String index;
    protected final String name;
    protected final String type;

    @Setter
    protected String lobFolder;

    @Setter
    protected String mimeType;
    protected final String userDefinedTypeSchema;
    protected final String userDefinedTypeName;
    protected final String originalType;
    protected final String isNullable;
    protected final String defaultValue;
    protected String cardinality;

    @Setter
    protected String description;

    protected DatabaseColumn(
            SiardArchive archive,
            DatabaseSchema schema,
            MetaColumn column
    ) {
        this.archive = archive;
        this.schema = schema;
        this.column = column;
        this.index = String.valueOf(column.getPosition());
        name = column.getName();
        String type = null;
        try {
            type = column.getType();
        } catch (IOException ignored) {
        }
        this.type = type;
        lobFolder = column.getLobFolder() != null ? column.getLobFolder().toString() : "";
        mimeType = column.getMimeType();
        userDefinedTypeSchema = column.getTypeSchema();
        userDefinedTypeName = column.getTypeName();
        originalType = column.getTypeOriginal();
        isNullable = String.valueOf(column.isNullable());
        defaultValue = column.getDefaultValue();

        try {
            cardinality = formatCardinality(column.getCardinality());
        } catch (IOException e) {
            cardinality = "";
        }

        description = column.getDescription();
    }

    @Override
    public String name() {
        return this.name;
    }

    public String index() {
        return index;
    }

    public String type() {
        return type;
    }

    public String cardinality() {
        return cardinality;
    }

    public void write() throws IOException {
        column.setLobFolder(URI.create(lobFolder));
        column.setMimeType(mimeType);
        column.setDescription(description);
    }

    private String formatCardinality(final int cardinality) {
        if (cardinality == -1) return "";
        return String.valueOf(cardinality);
    }
}
