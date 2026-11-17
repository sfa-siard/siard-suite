package ch.enterag.utils.jdbc;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * ResultSet wrapper for {@link DatabaseMetaData#getIndexInfo} results that maps
 * {@link DatabaseMetaData#tableIndexOther} to {@link DatabaseMetaData#tableIndexHashed}
 * in the TYPE column.
 * <p>
 * Some drivers (e.g. DB/2, SQL Server, PostgreSQL) report regular indexes as
 * {@code tableIndexOther}. Mapping them to {@code tableIndexHashed} allows unique
 * indexes (e.g. backing unique constraints) to be recognized as candidate keys.
 */
public class IndexInfoResultSet
        extends BaseResultSet {

    private final int typeColumnIndex;

    public IndexInfoResultSet(ResultSet resultSet) throws SQLException {
        super(resultSet);
        typeColumnIndex = findColumn("TYPE");
    }

    @Override
    public int getInt(int columnIndex) throws SQLException {
        int type = super.getInt(columnIndex);
        if (columnIndex == typeColumnIndex && type == DatabaseMetaData.tableIndexOther) {
            type = DatabaseMetaData.tableIndexHashed;
        }
        return type;
    }
}
