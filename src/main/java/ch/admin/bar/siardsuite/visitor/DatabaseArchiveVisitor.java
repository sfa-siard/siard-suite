package ch.admin.bar.siardsuite.visitor;

import ch.admin.bar.siardsuite.model.database.*;

import java.util.List;

public interface DatabaseArchiveVisitor {

    void visit(String archiveName, boolean onlyMetaData, List<DatabaseSchema> schemas);

    void visit(String schemaName, List<DatabaseTable> tables);

    void visit(String tableName, List<DatabaseColumn> columns, List<DatabaseRow> rows);

    void visit(String columnName);

    void visit(SiardArchive archive);

}
