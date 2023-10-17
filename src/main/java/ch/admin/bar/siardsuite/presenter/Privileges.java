package ch.admin.bar.siardsuite.presenter;

import ch.admin.bar.siardsuite.model.database.DatabaseObject;
import ch.admin.bar.siardsuite.model.database.Privilige;

import java.util.List;

public class Privileges extends DatabaseObject {

    private final List<Privilige> privileges;

    public Privileges(List<Privilige> privileges) {
        this.privileges = privileges;
    }

    @Override
    public String name() {
        return null;
    }

}
