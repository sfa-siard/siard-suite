package ch.admin.bar.siard2.cmd;

import ch.admin.bar.siard2.api.MetaColumn;
import ch.admin.bar.siard2.api.MetaType;
import ch.admin.bar.siard2.api.generated.CategoryType;
import ch.enterag.utils.database.SqlTypes;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;


// understands how to read the value for a given cell from a result set
class ObjectValueReader {
    private final ResultSet resultSet;
    private final MetaColumn metaColumn;
    private final int position;

    public ObjectValueReader(ResultSet resultSet, MetaColumn metaColumn, int position) {
        this.resultSet = resultSet;
        this.metaColumn = metaColumn;
        this.position = position;
    }

    public Object read() throws SQLException, IOException {

        if (metaColumn.getTypeOriginal()
                      .equals("ROWID")) return null;

        // TODO: when migrating to Java 17+, use switch expression
        int dataType = getDataType(metaColumn);
        Object oValue = null;
        switch (dataType) {
            case Types.CHAR:
            case Types.VARCHAR:
                oValue = resultSet.getString(position);
                break;
            case Types.CLOB:
                oValue = resultSet.getClob(position);
                break;
            case Types.SQLXML:
                oValue = resultSet.getSQLXML(position);
                break;
            case Types.NCHAR:
            case Types.NVARCHAR:
                oValue = resultSet.getNString(position);
                break;
            case Types.NCLOB:
                oValue = resultSet.getNClob(position);
                break;
            case Types.BINARY:
            case Types.VARBINARY:
                oValue = resultSet.getBytes(position);
                break;
            case Types.BLOB:
                oValue = resultSet.getBlob(position);
                break;
            case Types.DATALINK:
                oValue = resultSet.getURL(position);
                break;
            case Types.BOOLEAN:
                oValue = resultSet.getBoolean(position);
                break;
            case Types.SMALLINT:
                oValue = resultSet.getInt(position);
                break;
            case Types.INTEGER:
                oValue = resultSet.getLong(position);
                break;
            case Types.BIGINT:
                BigDecimal bdInt = resultSet.getBigDecimal(position);
                if (bdInt != null) oValue = bdInt.toBigIntegerExact();
                break;
            case Types.DECIMAL:
            case Types.NUMERIC:
                oValue = resultSet.getBigDecimal(position);
                break;
            case Types.REAL:
                oValue = resultSet.getFloat(position);
                break;
            case Types.FLOAT:
            case Types.DOUBLE:
                oValue = resultSet.getDouble(position);
                break;
            case Types.DATE:
                oValue = resultSet.getDate(position);
                break;
            case Types.TIME:
                oValue = resultSet.getTime(position);
                break;
            case Types.TIMESTAMP:
                oValue = resultSet.getTimestamp(position);
                break;
            case Types.OTHER:
            case Types.STRUCT:
                oValue = resultSet.getObject(position);
                break;
            case Types.ARRAY:
                oValue = resultSet.getArray(position);
                break;
            default:
                throw new SQLException("Invalid data type " + dataType + " (" + SqlTypes.getTypeName(dataType) + ") encountered!");
        }
        if (resultSet.wasNull()) return null;

        return oValue;
    }


    // TODO: this method should be moved to MetaColumn in SiardAPI
    int getDataType(MetaColumn mc) throws IOException {
        int iDataType = mc.getPreType();
        if (mc.getCardinality() >= 0) iDataType = Types.ARRAY;
        MetaType mt = mc.getMetaType();
        if (mt != null) {
            CategoryType cat = mt.getCategoryType();
            if (cat == CategoryType.DISTINCT) iDataType = mt.getBasePreType();
            else iDataType = Types.STRUCT;
        }
        return iDataType;
    }
}
