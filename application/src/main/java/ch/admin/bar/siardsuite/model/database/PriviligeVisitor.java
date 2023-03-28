package ch.admin.bar.siardsuite.model.database;

public interface PriviligeVisitor<T> {

    T visit(String type, String object, String grantor, String grantee, String option, String description);
}
