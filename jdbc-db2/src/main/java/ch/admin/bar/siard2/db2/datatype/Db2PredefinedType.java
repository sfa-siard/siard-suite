/*======================================================================
Db2PredefinedType implements the type translation from ISO SQL to DB/2.
Application : SIARD2
Description : Db2PredefinedType implements the type translation from 
              ISO SQL:2008 to DB/2. 
Platform    : Java 7   
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rüti ZH, Switzerland
Created    : 29.11.2016, Hartwig Thomas
======================================================================*/
package ch.admin.bar.siard2.db2.datatype;

import java.sql.*;

import ch.enterag.utils.database.*;
import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.datatype.*;
import ch.enterag.sqlparser.datatype.enums.*;
import ch.enterag.sqlparser.ddl.enums.*;
import ch.admin.bar.siard2.db2.*;
import ch.admin.bar.siard2.jdbc.*;

/*====================================================================*/
/** Db2PredefinedType implements the type translation from ISO SQL to 
 * DB/2.
 * @author Hartwig Thomas
 */
public class Db2PredefinedType
  extends PredefinedType
{
  public static final String sYEAR_MONTH_DISTINCT_TYPE = "TYPYEARMONTHDISTINCT";
  public static final String sDAY_SECOND_DISTINCT_TYPE = "TYPDAYSECONDDISTINCT";

  private static final long lKILO = 1024l;
  private static final long lMEGA = lKILO*lKILO;
  private static final long lGIGA = lMEGA*lKILO;
  
  /*------------------------------------------------------------------*/
  /** LOB length (possibly qualified by K, M, or G) in parentheses. 
   * @return lob length in parentheses.
   */
  protected String formatLobLength(int iFactor)
  {
    String sLength = "";
    long lLength = getLength();
    if (lLength == iUNDEFINED)
      lLength = Integer.MAX_VALUE/iFactor;
    else
    {
      Multiplier mult = getMultiplier();
      if (mult != null)
      {
        switch(mult)
        {
          case K:
            lLength = lLength * lKILO;
            break;
          case M:
            lLength = lLength * lMEGA;
            break;
          case G:
            lLength = lLength * lGIGA;
            break;
        }
      }
      if (iFactor*lLength > Integer.MAX_VALUE)
        lLength = Integer.MAX_VALUE/iFactor;
    }
    sLength = sLEFT_PAREN + String.valueOf(lLength) + sRIGHT_PAREN;
    return sLength;
  } /* formatLobLength */
  
  /*------------------------------------------------------------------*/
  /** format the predefined data type for DB/2.
   * In DB/2 SMALLINT is a short, INTEGER is an int and BIGINT is a long.
   * We translate BOOLEANs to SMALLINT.
   * We handle intervals as BIGINT (YEAR/MONTH) or DECIMAL (DAY/MINUTE)
   * @return the SQL string corresponding to the fields of the data type.
   */
  @Override
  public String format()
  {
    String sType = null;
    if (getType() != null)
    {
      sType = getType().getKeyword();
      switch(getType())
      {
        case CHAR:
        case VARCHAR:
        case NCHAR:
        case NVARCHAR:
        case BINARY:
        case VARBINARY:
          sType = sType + formatLength();
          break;
        case CLOB:
        case BLOB:
          sType = sType + formatLobLength(1);
          break;
        case DATALINK:
          sType = "BLOB" + formatLobLength(1);
          break;
        case NCLOB:
          sType = "DBCLOB" + formatLobLength(2);
          break;
        case DECIMAL:
          if (getPrecision() > 31)
            sType = "DECFLOAT";
          else
            sType = sType + formatPrecisionScale();
          break;
        case NUMERIC:
        case FLOAT:
          sType = sType + formatPrecisionScale();
          break;
        case TIMESTAMP:
          sType = sType + formatSecondsDecimals(iTIMESTAMP_DECIMALS_DEFAULT);
          /* we ignore time zone nonsense for the moment */
          break;
        case BOOLEAN:
          sType = "SMALLINT";
          break;
        case INTERVAL:
          String sBaseType = null;
          int iDistinctDataType = Types.NULL;
          IntervalQualifier iq = getIntervalQualifier();
          if ((iq.getStartField() == IntervalField.YEAR) ||
              (iq.getStartField() == IntervalField.MONTH))
          {
            sType = sYEAR_MONTH_DISTINCT_TYPE;
            iDistinctDataType = Types.BIGINT;
            sBaseType = "BIGINT";
          }
          else
          {
            sType = sDAY_SECOND_DISTINCT_TYPE;
            iDistinctDataType = Types.DECIMAL;
            sBaseType = "DECIMAL(31,9)";
          }
          try
          {
            boolean bTypeExists = false;
            Connection conn = ((Db2SqlFactory)getSqlFactory()).getConnection();
            Db2DatabaseMetaData dmd = (Db2DatabaseMetaData)conn.getMetaData();
            ResultSet rs = dmd.getUDTs(null,
              dmd.toPattern(dmd.getUserName()),
              dmd.toPattern(sType),new int[] { Types.DISTINCT });
            if (rs.next())
            {
              int iDataType = rs.getInt("DATA_TYPE");
              if (iDataType != iDistinctDataType)
                throw new SQLException("Distinct type "+sType+" for data type "+SqlTypes.getTypeName(iDataType)+" already exists!");
              bTypeExists = true;
            }
            rs.close();
            if (!bTypeExists)
            {
              String sSql = "CREATE TYPE "+sType+" AS "+sBaseType; 
              Statement stmt = conn.createStatement();
              stmt.executeUpdate(sSql);
              stmt.close();
            }
          }
          catch (SQLException se) { _il.exception(se); }
          break;
        default: // all other types do not need embellishments
          break;
      }
    }
    return sType;
  } /* format */

  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public Db2PredefinedType(SqlFactory sf)
  {
    super(sf);
  } /* constructor */

} /* class MsSqlPredefinedType */
