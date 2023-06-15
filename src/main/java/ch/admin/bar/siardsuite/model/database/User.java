package ch.admin.bar.siardsuite.model.database;

public class User {
    private final String name;

    private final String description;

    public User(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public <T> T accept(UserVisitor<T> visitor) {
        return visitor.visit(this.name, this.description);
    }

}
