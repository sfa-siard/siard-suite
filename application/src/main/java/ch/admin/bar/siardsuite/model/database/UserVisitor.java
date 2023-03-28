package ch.admin.bar.siardsuite.model.database;

public interface UserVisitor<T> {

    T visit(String name, String description);
}
