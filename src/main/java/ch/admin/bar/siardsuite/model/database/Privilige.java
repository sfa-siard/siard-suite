package ch.admin.bar.siardsuite.model.database;

import lombok.Getter;

@Getter
public class Privilige {

    private final String type;
    private final String object;
    private final String grantor;
    private final String grantee;
    private final String option;
    private final String description;

    public Privilige(String type, String object, String grantor, String grantee, String option, String description) {
        this.type = type;
        this.object = object;
        this.grantor = grantor;
        this.grantee = grantee;
        this.option = option;
        this.description = description;
    }
}
