/*======================================================================
MsSqlPredefinedType implements the type translation from ISO SQL to MSSQL.
Version     : $Id: $
Application : SIARD2
Description : MsSqlPredefinedType implements the type translation from 
              ISO SQL:2008 to MSSQL. 
Platform    : Java 7   
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rüti ZH, Switzerland
Created    : 19.05.2016, Hartwig Thomas
======================================================================*/
package ch.admin.bar.siard2.mssql.datatype;

import java.util.*;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.datatype.*;
import ch.enterag.sqlparser.datatype.enums.*;

/*====================================================================*/
/** MsSqlPredefinedType implements the type translation from ISO SQL to 
 * MSSQL.
 * @author Hartwig Thomas
 */
public class MsSqlPredefinedType
  extends PredefinedType
{
  /* translation of ISO SQL data types to MSSQL data types in DDL */
  private static Map<PreType, String> mapISO_TO_MSSQL = new HashMap<PreType, String>();
  static
  {
    mapISO_TO_MSSQL.put(PreType.CHAR,"char");
    mapISO_TO_MSSQL.put(PreType.VARCHAR,"varchar");
    mapISO_TO_MSSQL.put(PreType.CLOB,"text");
    mapISO_TO_MSSQL.put(PreType.NCHAR,"nchar");
    mapISO_TO_MSSQL.put(PreType.NVARCHAR,"nvarchar");
    mapISO_TO_MSSQL.put(PreType.NCLOB,"ntext");
    mapISO_TO_MSSQL.put(PreType.XML,"xml");
    mapISO_TO_MSSQL.put(PreType.BINARY, "binary");
    mapISO_TO_MSSQL.put(PreType.VARBINARY, "varbinary");
    mapISO_TO_MSSQL.put(PreType.BLOB,"image");
    mapISO_TO_MSSQL.put(PreType.NUMERIC,"numeric");
    mapISO_TO_MSSQL.put(PreType.DECIMAL,"decimal");
    mapISO_TO_MSSQL.put(PreType.SMALLINT,"smallint");
    mapISO_TO_MSSQL.put(PreType.INTEGER,"integer");
    mapISO_TO_MSSQL.put(PreType.BIGINT,"bigint");
    mapISO_TO_MSSQL.put(PreType.FLOAT,"float");
    mapISO_TO_MSSQL.put(PreType.REAL,"real");
    mapISO_TO_MSSQL.put(PreType.DOUBLE,"float");
    mapISO_TO_MSSQL.put(PreType.BOOLEAN,"bit");
    mapISO_TO_MSSQL.put(PreType.DATE,"date");
    mapISO_TO_MSSQL.put(PreType.TIME,"time");
    mapISO_TO_MSSQL.put(PreType.TIMESTAMP,"datetime2");
    mapISO_TO_MSSQL.put(PreType.INTERVAL,"nvarchar");
    mapISO_TO_MSSQL.put(PreType.DATALINK, "image");
  }
  private static int iMAX_BYTE_LENGTH = 8000;

  /*------------------------------------------------------------------*/
  /** format the predefined data type for MSSQL.
   * In MSSQL SMALLINT is a short, INTEGER is an int and BIGINT is a long.
   * There are no limits to character or binary strings.
   * @return the SQL string corresponding to the fields of the data type.
   */
  @Override
  public String format()
  {
    String sType = null;
    if (getType() != null)
    {
      sType = mapISO_TO_MSSQL.get(getType());
      if ((getType() == PreType.CHAR) ||
        (getType() == PreType.VARCHAR))
      {
        if (getLength() <= iMAX_BYTE_LENGTH)
          sType = sType + formatLength();
        else
          sType = sType + "(max)";
      }
      else if ((getType() == PreType.NCHAR) ||
          (getType() == PreType.NVARCHAR))
      {
        if (getLength() <= iMAX_BYTE_LENGTH/2)
          sType = sType + formatLength();
        else
          sType = sType + "(max)";
      }
      else if ((getType() == PreType.BINARY) ||
        (getType() == PreType.VARBINARY))
      {
        if (getLength() <= iMAX_BYTE_LENGTH)
          sType = sType + formatLength();
        else
          sType = sType + "(max)";
      }
      else if ((getType() == PreType.NUMERIC) ||
               (getType() == PreType.DECIMAL) ||
               (getType() == PreType.FLOAT))
        sType = sType + formatPrecisionScale();
      else if (getType() == PreType.TIME)
        sType = sType + formatSecondsDecimals(iTIME_DECIMALS_DEFAULT);
      else if (getType() == PreType.TIMESTAMP)
      {
        if (getSecondsDecimals() > 7)
          setSecondsDecimals(7);
        sType = sType + formatSecondsDecimals(iTIMESTAMP_DECIMALS_DEFAULT);
        /* we ignore time zone nonsense for the moment */
      }
      if (getType() == PreType.INTERVAL)
      {
        setLength(128);
        sType = sType + formatLength();
        /* we ignore time zone nonsense for the moment */
      }
    }
    return sType;
  } /* format */

  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public MsSqlPredefinedType(SqlFactory sf)
  {
    super(sf);
  } /* constructor */

} /* class MsSqlPredefinedType */
