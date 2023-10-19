package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siard2.api.MetaColumn;
import ch.admin.bar.siard2.api.MetaValue;
import ch.admin.bar.siardsuite.component.rendered.utils.Converter;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.net.URI;

@Getter
@Setter
public class DatabaseColumn {

    private final MetaColumn column;
    private final String index;
    private final String name;
    private final String type;
    private final String userDefinedTypeSchema;
    private final String userDefinedTypeName;
    private final String originalType;
    private final String isNullable;
    private final String defaultValue;
    private final String cardinality;

    private String lobFolder;
    private String mimeType;
    private String description;

    protected DatabaseColumn(MetaColumn column) {
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
        cardinality = Converter.<MetaColumn>cardinalityToString(MetaValue::getCardinality).apply(column);
        description = column.getDescription();
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
}
