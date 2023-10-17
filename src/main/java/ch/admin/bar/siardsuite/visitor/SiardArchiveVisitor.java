package ch.admin.bar.siardsuite.visitor;

import ch.admin.bar.siardsuite.model.database.*;

import java.util.List;

public interface SiardArchiveVisitor {

    void visit(String archiveName, boolean onlyMetaData, List<DatabaseSchema> schemas, List<User> users,
               List<Privilige> priviliges);
}
