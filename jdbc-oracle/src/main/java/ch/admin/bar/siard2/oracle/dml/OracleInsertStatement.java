package ch.admin.bar.siard2.oracle.dml;

import ch.admin.bar.siard2.jdbc.OracleDatabaseMetaData;
import ch.admin.bar.siard2.oracle.OracleSqlFactory;
import ch.admin.bar.siard2.oracle.expression.OracleQueryExpressionBody;
import ch.enterag.sqlparser.SqlFactory;
import ch.enterag.sqlparser.dml.InsertStatement;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class OracleInsertStatement
        extends InsertStatement {
    private void getArrayTypes() {
        try {
            /* get array type names for this table from meta data */
            Map<Integer, String> mapArrayTypes = new HashMap<Integer, String>();
            OracleSqlFactory osf = (OracleSqlFactory) getSqlFactory();
            OracleDatabaseMetaData dmd = (OracleDatabaseMetaData) osf.getConnection()
                                                                     .getMetaData();
            ResultSet rs = dmd.unwrap(DatabaseMetaData.class)
                              .getColumns(null,
                                          dmd.toPattern(getTableName().getSchema()),
                                          dmd.toPattern(getTableName().getName()), "%");
            while (rs.next()) {
                String sTypeName = rs.getString("TYPE_NAME");
                int iOrdinalPosition = rs.getInt("ORDINAL_POSITION");
                mapArrayTypes.put(Integer.valueOf(iOrdinalPosition), sTypeName);
            }
            rs.close();
            String[] asArrayType = new String[mapArrayTypes.size()];
            for (int i = 0; i < asArrayType.length; i++)
                asArrayType[i] = mapArrayTypes.get(Integer.valueOf(i + 1));
            OracleQueryExpressionBody.setArrayTypes(asArrayType);
        } catch (SQLException se) {
        }
    }

    @Override
    public String format() {
        getArrayTypes();
        return super.format();
    }

    public OracleInsertStatement(SqlFactory sf) {
        super(sf);
    }

}
