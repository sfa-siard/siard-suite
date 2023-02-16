package ch.admin.bar.siardsuite.visitor;

import ch.admin.bar.siardsuite.model.database.*;

import java.util.List;

public interface SiardArchiveVisitor {

    void visit(String archiveName, boolean onlyMetaData, List<DatabaseSchema> schemas, List<User> users);

    void visit(String schemaName, String schemaDescription, List<DatabaseTable> tables);

    void visit(String tableName, String numberOfRows, List<DatabaseColumn> columns, List<DatabaseRow> rows);

    void visit(String columnName);

    void visit(SiardArchive archive);

}
