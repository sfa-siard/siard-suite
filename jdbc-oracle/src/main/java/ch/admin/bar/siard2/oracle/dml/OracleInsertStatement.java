package ch.admin.bar.siard2.oracle.dml;

import java.sql.*;
import java.util.*;
import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.dml.*;
import ch.admin.bar.siard2.jdbc.*;
import ch.admin.bar.siard2.oracle.*;
import ch.admin.bar.siard2.oracle.expression.*;

public class OracleInsertStatement
  extends InsertStatement
{
  private void getArrayTypes()
  {
    try
    {
      /* get array type names for this table from meta data */
      Map<Integer,String> mapArrayTypes = new HashMap<Integer,String>();
      OracleSqlFactory osf = (OracleSqlFactory)getSqlFactory();
      OracleDatabaseMetaData dmd = (OracleDatabaseMetaData)osf.getConnection().getMetaData();
      ResultSet rs = dmd.unwrap(DatabaseMetaData.class).getColumns(null, 
        dmd.toPattern(getTableName().getSchema()), 
        dmd.toPattern(getTableName().getName()), "%");
      while (rs.next())
      {
        String sTypeName = rs.getString("TYPE_NAME");
        int iOrdinalPosition = rs.getInt("ORDINAL_POSITION");
        mapArrayTypes.put(Integer.valueOf(iOrdinalPosition), sTypeName);
      }
      rs.close();
      String[] asArrayType = new String[mapArrayTypes.size()];
      for (int i = 0; i < asArrayType.length; i++)
        asArrayType[i] = mapArrayTypes.get(Integer.valueOf(i+1));
      OracleQueryExpressionBody.setArrayTypes(asArrayType);
    }
    catch(SQLException se) {}
  }
  
  @Override
  public String format()
  {
    getArrayTypes();
    return super.format();
  } /* format */
  
  public OracleInsertStatement(SqlFactory sf) {
    super(sf);
  } /* constructor */

}
