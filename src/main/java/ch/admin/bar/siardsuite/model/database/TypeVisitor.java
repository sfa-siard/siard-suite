package ch.admin.bar.siardsuite.model.database;

public interface TypeVisitor<T> {
    T visit(String name, String category, boolean instantiable, boolean isFinal, String base, String description);
}
