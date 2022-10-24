package ch.admin.bar.siardsuite.visitor;

import ch.admin.bar.siardsuite.model.database.DatabaseArchiveMetaData;

public interface DatabaseArchiveMetaDataVisitor {

    void visit(String siardFormatVersion, String description, String owner, String timeOfOrigin, String archiverName, String archiverContact);

}
