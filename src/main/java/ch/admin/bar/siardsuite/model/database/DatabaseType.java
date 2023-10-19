package ch.admin.bar.siardsuite.model.database;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
public class DatabaseType {

    private final String name;
    private final String category;
    private final boolean instantiable;
    private final boolean isFinal;
    private final String base;

    @Setter
    private String description;

    private final List<DatabaseAttribute> databaseAttributes;

    public DatabaseType(String name, String category, boolean instantiable, boolean isFinal, String base,
                        String description, List<DatabaseAttribute> metaAttributes) {
        this.name = name;
        this.category = category;
        this.instantiable = instantiable;
        this.isFinal = isFinal;
        this.base = base;
        this.description = description;
        this.databaseAttributes = metaAttributes;
    }

    public void write() {
        throw new UnsupportedOperationException("Not implemented yet"); // TODO
    }
}
