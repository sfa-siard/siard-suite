/*======================================================================
Db2ResultSetMetaData implements wrapped DB/2 ResultSetMetaData.
Application : SIARD2
Description : Db2ResultSetMetaData implements wrapped DB/2 ResultSetMetaData.
Platform    : Java 7   
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rüti ZH, Switzerland
Created    : 08.11.2016, Hartwig Thomas
======================================================================*/
package ch.admin.bar.siard2.jdbc;

import java.sql.*;
import java.text.*;
import java.util.*;
import ch.enterag.utils.jdbc.*;
import ch.enterag.sqlparser.identifier.*;

/*====================================================================*/
/** Db2ResultSetMetaData implements wrapped DB/2 ResultSetMetaData.
 * @author Hartwig Thomas
 */
public class Db2ResultSetMetaData
extends BaseResultSetMetaData
implements ResultSetMetaData
{
  private class MetaData
  {
    private String _sCatalogName = null;
    public void setCatalogName(String sCatalogName) { _sCatalogName = sCatalogName; }
    public String getCatalogName() { return _sCatalogName; }
    private String _sSchemaName = null;
    public void setSchemaName(String sSchemaName) { _sSchemaName = sSchemaName; }
    public String getSchemaName() { return _sSchemaName; }
    private String _sTableName = null;
    public void setTableName(String sTableName) { _sTableName = sTableName; }
    public String getTableName() { return _sTableName; }
    private String _sColumnName = null;
    public void setColumnName(String sColumnName) { _sColumnName = sColumnName; }
    public String getColumnName() { return _sColumnName; }
    private String _sColumnLabel = null;
    public void setColumnLabel(String sColumnLabel) { _sColumnLabel = sColumnLabel; }
    public String getColumnLabel() { return _sColumnLabel; }
    private int _iColumnType = Types.NULL;
    public void setColumnType(int iColumnType) { _iColumnType = iColumnType; }
    public int getColumnType() { return _iColumnType; }
    private String _sColumnTypeName = null;
    public void setColumnTypeName(String sColumnTypeName) { _sColumnTypeName = sColumnTypeName; }
    public String getColumnTypeName() { return _sColumnTypeName; }
    private String _sColumnClassName = null;
    public void setColumnClassName(String sColumnClassName) { _sColumnClassName = sColumnClassName; }
    public String getColumnClassName() { return _sColumnClassName; }
    private int _iColumnDisplaySize = -1;
    public void setColumnDisplaySize(int iColumnDisplaySize) { _iColumnDisplaySize = iColumnDisplaySize; }
    public int getColumnDisplaySize() { return _iColumnDisplaySize; }
    private int _iPrecision = -1;
    public void setPrecision(int iPrecision) { _iPrecision = iPrecision; }
    public int getPrecision() { return _iPrecision; }
    private int _iScale = -1;
    public void setScale(int iScale) { _iScale = iScale; }
    public int getScale() { return _iScale; }
    private boolean _bAutoIncrement = false;
    public void setAutoIncrement(boolean bAutoIncrement) { _bAutoIncrement = bAutoIncrement; }
    public boolean isAutoIncrement() { return _bAutoIncrement; }
    private boolean _bCaseSensitive = false;
    public void setCaseSensitive(boolean bCaseSensitive) { _bCaseSensitive = bCaseSensitive; }
    public boolean isCaseSensitive() { return _bCaseSensitive; }
    private boolean _bCurrency = false;
    public void setCurrency(boolean bCurrency) { _bCurrency = bCurrency; }
    public boolean isCurrency() { return _bCurrency; }
    private boolean _bDefinitelyWritable = false;
    public void setDefinitelyWritable(boolean bDefinitelyWritable) { _bDefinitelyWritable = bDefinitelyWritable; }
    public boolean isDefinitelyWritable() { return _bDefinitelyWritable; }
    private int  _iNullable = ResultSetMetaData.columnNullableUnknown;
    public void setNullable(int iNullable) { _iNullable = iNullable; }
    public int isNullable() { return _iNullable; }
    private boolean _bReadOnly = false;
    public void setReadOnly(boolean bReadOnly) { _bReadOnly = bReadOnly; }
    public boolean isReadOnly() { return _bReadOnly; }
    private boolean _bSearchable = false;
    public void setSearchable(boolean bSearchable) { _bSearchable = bSearchable; }
    public boolean isSearchable() { return _bSearchable; }
    private boolean _bSigned = false;
    public void setSigned(boolean bSigned) { _bSigned = bSigned; }
    public boolean isSigned() { return _bSigned; }
    private boolean _bWritable = false;
    public void setWritable(boolean bWritable) { _bWritable = bWritable; }
    public boolean isWritable() { return _bWritable; }
  }
  private List<MetaData> _listMetaData = new ArrayList<MetaData>();

  private MetaData getMetaData(int iColumn)
    throws SQLException
  {
    MetaData md = new MetaData();
    md.setCatalogName(super.getCatalogName(iColumn));
    /* DB/2 anomaly: short schema name can have trailing blanks!
     * (Probably declared as CHAR(8)) */
    md.setSchemaName(super.getSchemaName(iColumn).trim());
    md.setTableName(super.getTableName(iColumn));
    md.setColumnName(super.getColumnName(iColumn));
    md.setColumnLabel(super.getColumnLabel(iColumn));
    md.setColumnTypeName(super.getColumnTypeName(iColumn));
    int iDataType = Db2MetaColumns.getDataType(
      super.getColumnType(iColumn),
      md.getColumnTypeName());
    md.setColumnType(iDataType);
    String sClassName = super.getColumnClassName(iColumn);
    if (iDataType == Types.SMALLINT)
      sClassName = Short.class.getName();
    md.setColumnClassName(sClassName);
    md.setColumnDisplaySize(super.getColumnDisplaySize(iColumn));
    md.setPrecision(super.getPrecision(iColumn));
    md.setScale(super.getScale(iColumn));
    md.setAutoIncrement(super.isAutoIncrement(iColumn));
    md.setCaseSensitive(super.isCaseSensitive(iColumn));
    md.setCurrency(super.isCurrency(iColumn));
    md.setDefinitelyWritable(super.isDefinitelyWritable(iColumn));
    md.setNullable(super.isNullable(iColumn));
    md.setReadOnly(super.isReadOnly(iColumn));
    md.setSearchable(super.isSearchable(iColumn));
    md.setSigned(super.isSigned(iColumn));
    md.setWritable(super.isWritable(iColumn));
    return md;
  } /* getMetaData */

  /*------------------------------------------------------------------*/
  /** retrieve all types that have the given attribute in the given position.
   * @param setTypesMatching the set of all types whose attributes with 
   * a smaller position already match. Ignored, if iPosition is 1.
   * @param sAttrName name of the attribute.
   * @param iPosition position of the attribute.
   * @param iType type of the attribute.
   * @param setAttrTypes set of possible attribute types, if iType is Types.STRUCT.
   * @throws SQLException in a database exception occurs.
   */
  private Set<QualifiedId> getAttrTypes(Connection conn, Set<QualifiedId> setTypesMatching, 
    String sAttrName, int iPosition, int iDataType, Set<QualifiedId> setAttrTypes)
    throws SQLException
  {
    Set<QualifiedId>  setTypes = new HashSet<QualifiedId>();
    String sAttrTypeName = null;
    Db2DatabaseMetaData dmd = (Db2DatabaseMetaData)conn.getMetaData();
    ResultSet rs = dmd.getAttributes(null, null, null, dmd.toPattern(sAttrName));
    try
    {
      while (rs.next())
      {
        String sCatalog = rs.getString("TYPE_CAT");
        String sSchema = rs.getString("TYPE_SCHEM");
        String sType = rs.getString("TYPE_NAME");
        QualifiedId qiType = new QualifiedId(sCatalog,sSchema,sType);
        String sAttribute = rs.getString("ATTR_NAME");
        int iType = rs.getInt("DATA_TYPE");
        int iOrdinalPosition = rs.getInt("ORDINAL_POSITION");
        if (sAttribute.equals(sAttrName) && 
            (iOrdinalPosition == iPosition) &&
            (iDataType == iType) &&
            ((iPosition == 1) || (setTypesMatching.contains(qiType))))
        {
          if (iDataType != Types.STRUCT)
            setTypes.add(qiType);
          else
          {
            sAttrTypeName = rs.getString("ATTR_TYPE_NAME");
            QualifiedId qiAttrType = new QualifiedId(sAttrTypeName);
            if (setAttrTypes.contains(qiAttrType))
              setTypes.add(qiType);
          }
        }
      }
    }
    catch(ParseException pe) { throw new SQLException("Type name \""+sAttrTypeName+"\" could not be parsed!",pe); }
    finally { rs.close(); }
    return setTypes;
  } /* getAttrTypes */
  
  /*------------------------------------------------------------------*/
  /** retrieve all types whose attributes match the metadata in the list.
   * @param listStruct attribute meta data
   * @param sPrefix prefix of column name to be ignored.
   * @return the set of all types matching the meta data in the list.
   * @throws SQLException in a database exception occurs.
   */
  private Set<QualifiedId> getStructTypes(Connection conn, List<MetaData> listStruct, String sPrefix)
    throws SQLException
  {
    Set<QualifiedId> setTypes = new HashSet<QualifiedId>();
    int iAttribute = 0;
    for (int iAttr = 0; iAttr < listStruct.size(); iAttr++)
    {
      iAttribute++;
      MetaData md = listStruct.get(iAttr);
      String sAttrName = md.getColumnLabel().substring(sPrefix.length());
      int iDataType = md.getColumnType();
      int iPeriod = sAttrName.indexOf('.');
      if (iPeriod < 0)
        setTypes = getAttrTypes(conn,setTypes,sAttrName,iAttribute,iDataType,null);
      else
      {
        List<MetaData> listAttrStruct = new ArrayList<MetaData>();
        sAttrName = sAttrName.substring(0,iPeriod);
        String sAttrPrefix = sPrefix + sAttrName + ".";
        for (; (iAttr < listStruct.size() && listStruct.get(iAttr).getColumnLabel().startsWith(sAttrPrefix)); iAttr++)
          listAttrStruct.add(listStruct.get(iAttr));
        iAttr--;
        Set<QualifiedId> setAttrTypes = getStructTypes(conn,listAttrStruct,sAttrPrefix);
        setTypes = getAttrTypes(conn,setTypes,sAttrName,iAttribute,Types.STRUCT,setAttrTypes);
      }
    }
    return setTypes;
  } /* getStructTypes */
  
  /*------------------------------------------------------------------*/
  /** constructor
   * @param rsWrapped result set to be wrapped.
   */
  public Db2ResultSetMetaData(ResultSetMetaData rsmdWrapped, Connection conn)
    throws SQLException
  {
    super(rsmdWrapped);
    for (int iColumn = 1; iColumn <= super.getColumnCount(); iColumn++)
    {
      MetaData md = getMetaData(iColumn);
      QualifiedId qiTable = new QualifiedId(null,md.getSchemaName(),md.getTableName());
      String sColumnLabel = md.getColumnLabel();
      int iPeriod = sColumnLabel.indexOf('.');
      Db2DatabaseMetaData dmd = (Db2DatabaseMetaData)conn.getMetaData();  
      if (iPeriod >= 0)
      {
        ResultSet rsColumns = dmd.getColumns(
          qiTable.getCatalog(), 
          dmd.toPattern(qiTable.getSchema()),
          dmd.toPattern(qiTable.getName()),
          dmd.toPattern(sColumnLabel));
        if (rsColumns.next())
          iPeriod = -1;
        rsColumns.close();
      }
      if (iPeriod < 0)
        _listMetaData.add(md);
      else
      {
        QualifiedId qiType = null;
        sColumnLabel = sColumnLabel.substring(0,iPeriod);
        String sPrefix = sColumnLabel +".";
        try
        {
          ResultSet rsColumns = dmd.getColumns(
            qiTable.getCatalog(), 
            dmd.toPattern(qiTable.getSchema()),
            dmd.toPattern(qiTable.getName()),
            dmd.toPattern(sColumnLabel));
          if (rsColumns.next())
          {
            String sTypeName = rsColumns.getString("TYPE_NAME");
            if (!rsColumns.next())
              qiType = new QualifiedId(sTypeName);
          }
          rsColumns.close();
        }
        catch(ParseException pe) { qiType = null; }
        int iColumnDisplaySize = 0;
        if (qiType != null)
        {
          // match it with the meta data
          while (md.getColumnLabel().startsWith(sPrefix) && (iColumn <= super.getColumnCount()))
          {
            iColumnDisplaySize = iColumnDisplaySize + md.getColumnDisplaySize();
            iColumn++;
            if (iColumn <= super.getColumnCount())
              md = getMetaData(iColumn);
          }
          iColumn--;
        }
        else // try more round-about type matching
        {
          List<MetaData> listStruct = new ArrayList<MetaData>();
          while(md.getColumnLabel().startsWith(sPrefix) && (iColumn <= super.getColumnCount()))
          {
            iColumnDisplaySize = iColumnDisplaySize + md.getColumnDisplaySize();
            listStruct.add(md);
            iColumn++;
            if (iColumn <= super.getColumnCount())
              md = getMetaData(iColumn);
          }
          iColumn--;
          Set<QualifiedId> setStructTypes = getStructTypes(conn,listStruct,sPrefix);
          // get all types that are matched with this column name
          Set<QualifiedId> setTypes = new HashSet<QualifiedId>();
          String sTypeName = null;
          try
          {
            ResultSet rs = dmd.getColumns(null,null,null,dmd.toPattern(sColumnLabel));
            while(rs.next())
            {
              String sCatalog = rs.getString("TABLE_CAT");
              String sSchema = rs.getString("TABLE_SCHEM");
              String sTable = rs.getString("TABLE_NAME");
              String sColumn = rs.getString("COLUMN_NAME");
              int iDataType = rs.getInt("DATA_TYPE");
              sTypeName = rs.getString("TYPE_NAME");
              if (sColumn.equals(sColumnLabel) && 
                  (iDataType == Types.STRUCT))
              {
                qiType = new QualifiedId(sTypeName);
                if (qiType.getCatalog() == null)
                  qiType.setCatalog(sCatalog);
                if (qiType.getSchema() == null)
                  qiType.setSchema(sSchema);
                if (setStructTypes.contains(qiType))
                {
                  setTypes.add(qiType);
                  qiTable = new QualifiedId(sCatalog,sSchema,sTable);
                }
              }
            }
            rs.close();
            if (setTypes.size() == 1)
            {
              qiType = null;
              for (Iterator<QualifiedId> iterType = setTypes.iterator(); iterType.hasNext(); )
                qiType = iterType.next();
            }
          }
          catch(ParseException pe) { qiType = null; }
        }
        if (qiType != null)
        {
          md.setCatalogName(qiTable.getCatalog());
          md.setSchemaName(qiTable.getSchema());
          md.setTableName(qiTable.getName());
          md.setColumnName(sColumnLabel);
          md.setColumnLabel(sColumnLabel);
          md.setColumnType(Types.STRUCT);
          md.setColumnClassName(Struct.class.getName());
          md.setColumnTypeName(qiType.format());
          md.setColumnDisplaySize(iColumnDisplaySize);
          md.setAutoIncrement(false);
          md.setCurrency(false);
          md.setDefinitelyWritable(false);
          md.setNullable(ResultSetMetaData.columnNullableUnknown);
          md.setPrecision(0);
          md.setReadOnly(true);
          md.setScale(0);
          md.setSearchable(false);
          md.setSigned(false);
          md.setWritable(false);
          _listMetaData.add(md);
        }
        else
          throw new SQLException("Type matching failed for "+sColumnLabel+"!");
      }
    }
  } /* constructor */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc}
   */
  @Override
  public int getColumnCount() throws SQLException
  {
    return _listMetaData.size();
  } /* getColumnType */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  public String getCatalogName(int iColumn) { return _listMetaData.get(iColumn-1).getCatalogName(); }
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  public String getSchemaName(int iColumn) { return _listMetaData.get(iColumn-1).getSchemaName(); }
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  public String getTableName(int iColumn) { return _listMetaData.get(iColumn-1).getTableName(); }
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  public String getColumnName(int iColumn) { return _listMetaData.get(iColumn-1).getColumnName(); }
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  public String getColumnLabel(int iColumn) { return _listMetaData.get(iColumn-1).getColumnLabel(); }
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  public int getColumnType(int iColumn) { return _listMetaData.get(iColumn-1).getColumnType(); }
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  public String getColumnTypeName(int iColumn) { return _listMetaData.get(iColumn-1).getColumnTypeName(); }
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  public String getColumnClassName(int iColumn) { return _listMetaData.get(iColumn-1).getColumnClassName(); }
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  public int getColumnDisplaySize(int iColumn) { return _listMetaData.get(iColumn-1).getColumnDisplaySize(); }
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  public int getPrecision(int iColumn) { return _listMetaData.get(iColumn-1).getPrecision(); }
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  public int getScale(int iColumn) { return _listMetaData.get(iColumn-1).getScale(); }
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  public boolean isAutoIncrement(int iColumn) { return _listMetaData.get(iColumn-1).isAutoIncrement(); }
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  public boolean isCaseSensitive(int iColumn) { return _listMetaData.get(iColumn-1).isCaseSensitive(); }
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  public boolean isCurrency(int iColumn) { return _listMetaData.get(iColumn-1).isCurrency(); }
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  public boolean isDefinitelyWritable(int iColumn) { return _listMetaData.get(iColumn-1).isDefinitelyWritable(); }
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  public int isNullable(int iColumn) { return _listMetaData.get(iColumn-1).isNullable(); }
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  public boolean isReadOnly(int iColumn) { return _listMetaData.get(iColumn-1).isReadOnly(); }
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  public boolean isSearchable(int iColumn) { return _listMetaData.get(iColumn-1).isSearchable(); }
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  public boolean isSigned(int iColumn) { return _listMetaData.get(iColumn-1).isSigned(); }
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  public boolean isWritable(int iColumn) { return _listMetaData.get(iColumn-1).isWritable(); }
} /* class Db2ResultSetMetaData */
