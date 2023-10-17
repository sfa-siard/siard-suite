package ch.admin.bar.siardsuite.model.database;

import java.util.List;

public class Users extends DatabaseObject {

    private List<User> users;

    public Users(List<User> users) {
        this.users = users;
    }

    @Override
    public String name() {
        return null;
    }

}
