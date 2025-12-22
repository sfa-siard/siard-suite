/* OracleArray wraps a native oracle.sql.ARRAY extending its result
 * to the maximum declared length of the array.
 */
package ch.admin.bar.siard2.jdbc;

import java.sql.*;
import java.text.*;
import java.util.*;
import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.identifier.*;
import ch.enterag.utils.logging.*;

@SuppressWarnings("deprecation")
public class OracleArray
  implements Array
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(OracleArray.class.getName());
  private oracle.sql.ARRAY _oarray = null;
  private long _lMaxLength = -1;
  private QualifiedId _qiVarray = null;
  private String _sBaseTypeName = null;
  private Object[] _aoElements = null;
  private oracle.jdbc.OracleConnection _conn = null;
  
  /*------------------------------------------------------------------*/
  /** match the token to the beginning of the text and return
   * the text shortened by the token's length, if it matches.
   * @param sText text.
   * @param sToken token to be matched.
   * @return shortened text if they match, null if they don't.
   */
  private static String matchToken(String sText, String sToken)
  {
    String sMatch = sText.substring(0,sToken.length());
    if (sMatch.equalsIgnoreCase(sToken))
      sText = sText.substring(sToken.length()).trim();
    else
      sText = null;
    return sText;
  } /* matchToken */
  
  /*------------------------------------------------------------------*/
  /** check, whether the source text of the type definition matches the 
   * base type and the length.
   * @param sText source text.
   * @param sBaseType full base type.
   * @param iLength cardinality of VARRAY
   * @return true, if the source text matches the base type and the cardinality.
   */
  private static boolean matchesText(String sText, String sBaseType, int iLength)
  {
    boolean bMatch = false;
    sText = sText.trim();
    try
    {
      sText = matchToken(sText,"TYPE");
      if (sText != null)
      {
        String sToken = SqlLiterals.parseIdPrefix(sText);
        sText = sText.substring(sToken.length()).trim();
        if (sText.startsWith("."))
        {
          sText = sText.substring(1);
          sToken = SqlLiterals.parseIdPrefix(sText);
          sText = sText.substring(sToken.length()).trim();
        }
        sText = matchToken(sText,"AS");
        if (sText != null)
        {
          sText = matchToken(sText,"VARRAY");
          if (sText != null)
          {
            if (sText.startsWith("("))
            {
              int i = sText.indexOf(')');
              if (i > 1)
              {
                String sNumber = sText.substring(1,i);
                if (Long.parseLong(sNumber) == (long)iLength)
                {
                  sText = sText.substring(i+1).trim();
                  sText = matchToken(sText,"OF");
                  if (sBaseType.equalsIgnoreCase(sText))
                    bMatch = true;
                }
              }
            }
          }
        }
      }
    }
    catch (ParseException pe) {}
    return bMatch;
  } /* matchesText */
  
  /*------------------------------------------------------------------*/
  /** find or create an VARRAY type for the given base type and length
   * to which the PUBLIC has EXECUTE privileges.
   * @param conn open native connection.
   * @param sBaseType full base type (e.g. "VARCHAR(255)")
   * @param iLength cardinality of the array.
   * @return type name of the VARRAY.
   */
  public static QualifiedId findOrCreateVarray(oracle.jdbc.OracleConnection conn, String sBaseType, int iLength)
    throws SQLException
  {
    _il.enter(sBaseType,String.valueOf(iLength));
    QualifiedId qiVarray = null;
    /* search for a data type with this base type and this length */
    String sSql = "SELECT\r\n" +
                  " T.OWNER AS OWNER,\r\n" +
                  " T.TYPE_NAME AS TYPE_NAME,\r\n" +
                  " S.TEXT AS TEXT\r\n" +
                  "FROM ALL_TYPES T, ALL_SOURCE S, ALL_TAB_PRIVS P\r\n" +
                  "WHERE T.TYPECODE = 'COLLECTION'\r\n" +
                  " AND T.OWNER = S.OWNER\r\n" +
                  " AND T.TYPE_NAME = S.NAME\r\n" +
                  " AND P.TABLE_SCHEMA = T.OWNER\r\n" +
                  " AND P.TABLE_NAME = T.TYPE_NAME\r\n" +
                  " AND P.PRIVILEGE = 'EXECUTE'\r\n" +
                  " AND P.GRANTEE = 'PUBLIC'\r\n" +
                  " AND S.TEXT LIKE 'TYPE%VARRAY(%)%OF%'";
    
    Statement stmt = conn.createStatement();
    _il.event("Query: "+sSql);
    ResultSet rs = stmt.executeQuery(sSql);
    while ((qiVarray == null) && rs.next())
    {
      String sSchema = rs.getString("OWNER");
      String sTypeName = rs.getString("TYPE_NAME");
      String sText = rs.getString("TEXT");
      if (matchesText(sText,sBaseType,iLength))
        qiVarray = new QualifiedId(null,sSchema,sTypeName);
    }
    rs.close();
    if (qiVarray == null)
    {
      String sVarrayType = null;
      sVarrayType = sBaseType.replace(" ", "").replace('(', '_').replace(')', '_').replace(',', '_');
      /***
      int iParen = sBaseType.indexOf('(');
      if (iParen >= 0)
        sVarrayType = sBaseType.substring(0,iParen).trim();
      ***/
      sVarrayType = sVarrayType +"_"+String.valueOf(iLength) + "_V";
      qiVarray = new QualifiedId(null,conn.getMetaData().getUserName(),sVarrayType);
      
      StringBuilder sbSql = new StringBuilder();
      sbSql.append("CREATE TYPE ");
      sbSql.append(qiVarray.quote());
      sbSql.append(" AS VARRAY(");
      sbSql.append(String.valueOf(iLength));
      sbSql.append(") OF ");
      sbSql.append(sBaseType);
      stmt = conn.createStatement();
      _il.event("Query: "+sbSql.toString());
      int iReturn = stmt.executeUpdate(sbSql.toString());
      if (iReturn != 0)
        throw new IllegalArgumentException("CREATE TYPE failed!");
      sbSql = new StringBuilder();
      sbSql.append("GRANT EXECUTE ON ");
      sbSql.append(qiVarray.quote());
      sbSql.append(" TO PUBLIC");
      stmt = conn.createStatement();
      iReturn = stmt.executeUpdate(sbSql.toString());
      if (iReturn != 0)
        throw new IllegalArgumentException("GRANT EXECUTE ON TYPE TO PUBLIC failed!");
    }
    _il.exit(qiVarray);
    return qiVarray;
  } /* findOrCreateVarray */
  
  private oracle.sql.ARRAY getOracleArray()
    throws SQLException
  {
    if (_oarray == null)
    {
      if (_qiVarray == null)
        _qiVarray = findOrCreateVarray(_conn,_sBaseTypeName,_aoElements.length);
      _oarray = _conn.createARRAY(_qiVarray.format(), _aoElements);
      _lMaxLength = _oarray.getDescriptor().getMaxLength();
    }
    return _oarray;
  } /* getOracleArray */
  
  public OracleArray(oracle.sql.ARRAY oarray)
    throws SQLException
  {
    _oarray = oarray;
    _lMaxLength = _oarray.getDescriptor().getMaxLength();
    QualifiedId qiVarray = new QualifiedId(null,
      _oarray.getDescriptor().getSchemaName(),
      _oarray.getDescriptor().getTypeName());
    _conn = _oarray.getConnection();
    String sFullArrayTypeName = OracleMetaColumns.getFullArrayTypeName(_oarray.getConnection(),qiVarray);
    int i = sFullArrayTypeName.lastIndexOf(" ARRAY[");
    _sBaseTypeName = sFullArrayTypeName.substring(0,i).trim();
  }
  
  public OracleArray(Connection conn, String sBaseTypeName, Object[] aoElements)
    throws SQLException
  {
    _conn = (oracle.jdbc.OracleConnection)conn.unwrap(Connection.class);
    _sBaseTypeName = sBaseTypeName;
    _aoElements = aoElements;
  }

  @Override
  public String getBaseTypeName() throws SQLException
  {
    return _sBaseTypeName;
  }
  
  public oracle.sql.ARRAY getOracleArray(String sVarrayTypeName)
    throws SQLException
  {
    if (_oarray == null)
    {
      String[] as = sVarrayTypeName.split("\\.");
      _qiVarray = new QualifiedId();
      _qiVarray.setName(as[as.length - 1]);
      if (as.length > 1)
        _qiVarray.setSchema(as[as.length - 2]);
      if (as.length > 2)
        _qiVarray.setCatalog(as[as.length - 3]);
    }
    return getOracleArray();
  }

  @Override
  public int getBaseType() throws SQLException
  {
    return getOracleArray().getBaseType();
  }
  
  private Object[] adjustArray(Object[] oa)
  {
    if (oa.length != _lMaxLength)
      oa = Arrays.copyOf(oa, (int)_lMaxLength);
    return oa;
  } /* adjustArray */
  
  @Override
  public Object getArray() throws SQLException
  {
    return adjustArray((Object[])getOracleArray().getArray());
  } /* getArray */

  @Override
  public Object getArray(Map<String, Class<?>> map) throws SQLException
  {
    return adjustArray((Object[])getOracleArray().getArray(map));
  }

  @Override
  public Object getArray(long index, int count) throws SQLException
  {
    return adjustArray((Object[])getOracleArray().getArray(index,count));
  }

  @Override
  public Object getArray(long index, int count,
    Map<String, Class<?>> map) throws SQLException
  {
    return adjustArray((Object[])getOracleArray().getArray(index,count,map));
  }

  @Override
  public ResultSet getResultSet() throws SQLException
  {
    // we would have to wrap the ResultSet in order to extend it ... 
    throw new SQLFeatureNotSupportedException("ResultSet interface of arrays is not supported.");
  }

  @Override
  public ResultSet getResultSet(Map<String, Class<?>> map)
    throws SQLException
  {
    // we would have to wrap the ResultSet in order to extend it ... 
    throw new SQLFeatureNotSupportedException("ResultSet interface of arrays is not supported.");
  }

  @Override
  public ResultSet getResultSet(long index, int count)
    throws SQLException
  {
    // we would have to wrap the ResultSet in order to extend it ... 
    throw new SQLFeatureNotSupportedException("ResultSet interface of arrays is not supported.");
  }

  @Override
  public ResultSet getResultSet(long index, int count,
    Map<String, Class<?>> map) throws SQLException
  {
    // we would have to wrap the ResultSet in order to extend it ... 
    throw new SQLFeatureNotSupportedException("ResultSet interface of arrays is not supported.");
  }

  @Override
  public void free() throws SQLException
  {
    if (_oarray != null)
      _oarray.free();
  }

} /* OracleArray */
