package ch.admin.bar.siard2.mssql.expression;

import java.sql.*;
import ch.enterag.sqlparser.expression.*;
import ch.admin.bar.siard2.mssql.*;

public class MsSqlGeneralValueSpecification
  extends GeneralValueSpecification
{
  /*------------------------------------------------------------------*/
  /** format the general value specification.
   * @return the SQL string corresponding to the fields of the general value specification.
   */
  @Override
  public String format()
  {
    String sSpecification = super.format();
    if (getColumnOrParameter().isSet())
    {
      int iDataType = ((MsSqlSqlFactory)getSqlFactory()).getDataType(getColumnOrParameter());
      if (iDataType == Types.JAVA_OBJECT) // render JAVA_OBJECTs as Strings!
        sSpecification = sSpecification + ".ToString()";
    }
    return sSpecification;
  } /* format */

  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public MsSqlGeneralValueSpecification(MsSqlSqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class GeneralValueSpecification */
