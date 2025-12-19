package ch.admin.bar.siardsuite.model.database;

import lombok.Getter;

@Getter
public class User {
    private final String name;

    private final String description;

    public User(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
