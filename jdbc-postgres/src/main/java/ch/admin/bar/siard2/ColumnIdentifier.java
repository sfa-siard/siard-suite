package ch.admin.bar.siard2;

import ch.admin.bar.siard2.jdbc.PostgresMetaColumns;

import java.sql.SQLException;

/**
 * Represents a database column, identified by its schema name,
 * table name, and column name.
 */
public class ColumnIdentifier {
    private String schemaName;
    private String tableName;
    private String columnName;

    public ColumnIdentifier(String schemaName, String tableName, String columnName) {
        this.schemaName = schemaName;
        this.tableName = tableName;
        this.columnName = columnName;
    }

    public ColumnIdentifier(PostgresMetaColumns pgMetaColumns) throws SQLException {
        this(pgMetaColumns.getString(2), pgMetaColumns.getString(3), pgMetaColumns.getString(4));
    }

    public String getSchemaName() {
        return schemaName;
    }

    public String getTableName() {
        return tableName;
    }

    public String getColumnName() {
        return columnName;
    }
}
