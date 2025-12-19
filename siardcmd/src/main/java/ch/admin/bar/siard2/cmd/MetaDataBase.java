package ch.admin.bar.siard2.cmd;

import java.text.*;
import java.sql.*;
import java.util.*;
import ch.enterag.utils.jdbc.*;
import ch.enterag.sqlparser.identifier.*;
import ch.admin.bar.siard2.api.*;

public abstract class MetaDataBase
{
  protected DatabaseMetaData _dmd = null;
  protected MetaData _md = null;
  protected int _iQueryTimeoutSeconds = 30;
  public void setQueryTimeout(int iQueryTimeoutSeconds) { _iQueryTimeoutSeconds = iQueryTimeoutSeconds; }
  private boolean _bSupportsArrays = false;
  public boolean supportsArrays() { return _bSupportsArrays; }
  private boolean _bSupportsDistincts = false;
  public boolean supportsDistincts() { return _bSupportsDistincts; }
  private boolean _bSupportsUdts = false;
  public boolean supportsUdts() { return _bSupportsUdts; }
  private Set<QualifiedId> _setUsedTypes = null;

  /*------------------------------------------------------------------*/
  /** if a type is only used for procedure parameters but not for table
   * columns, then we do not treat the database as "supporting" the type.
   * @param qiType type to test.
   * @return true if the type is used for a column of a table. 
   */
  private boolean isUsedInColumn(QualifiedId qiType)
    throws SQLException
  {
    if (_setUsedTypes == null)
    {
      _setUsedTypes = new HashSet<QualifiedId>();
      ResultSet rs = _dmd.getColumns(null,"%","%","%");
      while (rs.next())
      {
        int iDataType = rs.getInt("DATA_TYPE");
        if ((iDataType == Types.DISTINCT) || (iDataType == Types.STRUCT))
        {
          String sTypeName = rs.getString("TYPE_NAME");
          QualifiedId qi = null;
          try { qi = new QualifiedId(sTypeName); }
          catch(ParseException pe) { throw new SQLException(sTypeName + " could not be parsed!",pe); }
          if (qi != null)
            _setUsedTypes.add(qi);
        }
      }
      rs.close();
    }
    return _setUsedTypes.contains(qiType);
  } /* isUsedInColumn */
  
  /*------------------------------------------------------------------*/
  /** constructor
   * @param dmd database meta data.
   * @param md SIARD meta data.
   */
  protected MetaDataBase(DatabaseMetaData dmd, MetaData md)
    throws SQLException
  {
    _dmd = dmd;
    _md = md;
    /* determine, whether UDTs are supported */
    ResultSet rs = _dmd.getUDTs(null, "%", "%", null);
    while(rs.next() && ((_bSupportsUdts == false) || (_bSupportsDistincts == false)))
    {
      String sTypeSchema = rs.getString("TYPE_SCHEM");
      String sTypeName = rs.getString("TYPE_NAME");
      QualifiedId qiType = new QualifiedId(null,sTypeSchema,sTypeName);
      if (isUsedInColumn(qiType))
      {
        int iDataType = rs.getInt("DATA_TYPE");
        if (iDataType == Types.STRUCT)
          _bSupportsUdts = true;
        else if (iDataType == Types.DISTINCT)
          _bSupportsDistincts = true;
      }
    }
    rs.close();
    try
    {
      Array array = _dmd.getConnection().createArrayOf("INTEGER", new Integer[]{1,2});
      array.free();
      _bSupportsArrays = true;
    }
    catch (SQLFeatureNotSupportedException sfnse) { _bSupportsArrays = false; }
  } /* constructor */
  
} /* MetaDataBase */
