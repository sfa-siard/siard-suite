package ch.admin.bar.siardsuite.model.database;

public class User {
    private final String name;

    private final String description;

    public User(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public void accept(UserVisitor visitor) {
        visitor.visit(this.name, this.description);
    }


}
