package ch.admin.bar.siardsuite.visitor;

import ch.admin.bar.siardsuite.model.database.DatabaseArchiveMetaData;
import ch.admin.bar.siardsuite.model.database.DatabaseSchema;
import javafx.beans.property.StringProperty;

import java.util.List;

public interface DatabaseArchiveVisitor {

    void visit(StringProperty archiveName, List<DatabaseSchema> schemas);

}
