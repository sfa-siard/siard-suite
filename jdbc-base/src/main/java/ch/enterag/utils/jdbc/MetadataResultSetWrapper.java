package ch.enterag.utils.jdbc;

import ch.enterag.sqlparser.identifier.QualifiedId;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Wrapper for JDBC metadata ResultSets that provides convenient access to
 * catalog/schema/name triplets from different metadata query types.
 * <p>
 * JDBC metadata methods return ResultSets with different column names for
 * similar information (e.g., TABLE_CAT vs TYPE_CAT). This wrapper abstracts
 * those differences and provides a uniform interface.
 */
public class MetadataResultSetWrapper {
    private final ResultSet rs;
    private final String catalogColumn;
    private final String schemaColumn;
    private final String nameColumn;

    private MetadataResultSetWrapper(ResultSet rs, String catalogColumn, String schemaColumn, String nameColumn) {
        this.rs = rs;
        this.catalogColumn = catalogColumn;
        this.schemaColumn = schemaColumn;
        this.nameColumn = nameColumn;
    }

    /**
     * Creates a wrapper for table-related metadata ResultSets.
     * Uses TABLE_CAT, TABLE_SCHEM, TABLE_NAME columns.
     *
     * @param rs ResultSet from getTables(), getColumns(), getPrimaryKeys(), etc.
     * @return wrapper instance
     */
    public static MetadataResultSetWrapper forTable(ResultSet rs) {
        return new MetadataResultSetWrapper(rs, "TABLE_CAT", "TABLE_SCHEM", "TABLE_NAME");
    }

    /**
     * Creates a wrapper for type-related metadata ResultSets.
     * Uses TYPE_CAT, TYPE_SCHEM, TYPE_NAME columns.
     *
     * @param rs ResultSet from getUDTs(), getAttributes(), etc.
     * @return wrapper instance
     */
    public static MetadataResultSetWrapper forType(ResultSet rs) {
        return new MetadataResultSetWrapper(rs, "TYPE_CAT", "TYPE_SCHEM", "TYPE_NAME");
    }

    /**
     * Gets the catalog name from the current row.
     *
     * @return catalog name
     * @throws SQLException if a database access error occurs
     */
    public String getCatalog() throws SQLException {
        return rs.getString(catalogColumn);
    }

    /**
     * Gets the schema name from the current row.
     *
     * @return schema name
     * @throws SQLException if a database access error occurs
     */
    public String getSchema() throws SQLException {
        return rs.getString(schemaColumn);
    }

    /**
     * Gets the name (table name or type name) from the current row.
     *
     * @return name
     * @throws SQLException if a database access error occurs
     */
    public String getName() throws SQLException {
        return rs.getString(nameColumn);
    }

    /**
     * Creates a QualifiedId from the current row's catalog, schema, and name.
     *
     * @return QualifiedId instance
     * @throws SQLException if a database access error occurs
     */
    public QualifiedId toQualifiedId() throws SQLException {
        return new QualifiedId(getCatalog(), getSchema(), getName());
    }

    /**
     * Provides access to the underlying ResultSet for additional column access.
     *
     * @return the wrapped ResultSet
     */
    public ResultSet getResultSet() {
        return rs;
    }
}
