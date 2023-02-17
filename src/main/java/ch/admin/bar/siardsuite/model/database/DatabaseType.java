package ch.admin.bar.siardsuite.model.database;

public class DatabaseType {
    private final String name;
    private final String category;
    private final boolean instantiable;
    private final boolean isFinal;
    private final String base;
    private final String description;


    public DatabaseType(String name, String category, boolean instantiable, boolean isFinal, String base,
                        String description) {
        this.name = name;
        this.category = category;
        this.instantiable = instantiable;
        this.isFinal = isFinal;
        this.base = base;
        this.description = description;
    }

    public <T> T accept(TypeVisitor<T> visitor) {
        return visitor.visit(name, category, instantiable, isFinal, base, description);
    }
}
