package ch.admin.bar.siard2.jdbc;

import java.math.*;
import java.sql.*;
import java.util.*;
import javax.xml.datatype.*;
import ch.enterag.utils.jdbc.*;

public class OracleResultSetMetaData extends BaseResultSetMetaData implements ResultSetMetaData {
	private static Map<String, Class<?>> mapCLASS_ORACLE_TO_ISO = new HashMap<String, Class<?>>();

	static {
		// @see https://docs.oracle.com/cd/B19306_01/java.102/b14188/datamap.htm
    mapCLASS_ORACLE_TO_ISO.put(String.class.getName(), String.class);
		mapCLASS_ORACLE_TO_ISO.put("byte[]", byte[].class);
		mapCLASS_ORACLE_TO_ISO.put(Integer.class.getName(), Integer.class);
		mapCLASS_ORACLE_TO_ISO.put(BigDecimal.class.getName(), BigDecimal.class);
		mapCLASS_ORACLE_TO_ISO.put(Double.class.getName(), Double.class);
		mapCLASS_ORACLE_TO_ISO.put(Float.class.getName(), Float.class);
		mapCLASS_ORACLE_TO_ISO.put(Timestamp.class.getName(), Timestamp.class);
    mapCLASS_ORACLE_TO_ISO.put(Boolean.class.getName(), Boolean.class);
		mapCLASS_ORACLE_TO_ISO.put(oracle.jdbc.OracleBlob.class.getName(), Blob.class);
    mapCLASS_ORACLE_TO_ISO.put(oracle.jdbc.OracleClob.class.getName(), Clob.class);
    mapCLASS_ORACLE_TO_ISO.put(oracle.jdbc.OracleNClob.class.getName(), NClob.class);
    mapCLASS_ORACLE_TO_ISO.put(oracle.jdbc.OracleBfile.class.getName(), Blob.class);
    mapCLASS_ORACLE_TO_ISO.put(oracle.jdbc.OracleStruct.class.getName(), Struct.class);
    mapCLASS_ORACLE_TO_ISO.put(oracle.jdbc.OracleArray.class.getName(), Array.class);
    mapCLASS_ORACLE_TO_ISO.put(java.sql.SQLXML.class.getName(), SQLXML.class);
    /*** the versions of the Oracle JDBC drivers get on one's nerves!
    mapCLASS_ORACLE_TO_ISO.put(oracle.sql.BLOB.class.getName(), Blob.class);
    mapCLASS_ORACLE_TO_ISO.put(oracle.sql.CLOB.class.getName(), Clob.class);
    mapCLASS_ORACLE_TO_ISO.put(oracle.sql.NCLOB.class.getName(), NClob.class);
    mapCLASS_ORACLE_TO_ISO.put(oracle.sql.BFILE.class.getName(), Blob.class);
    mapCLASS_ORACLE_TO_ISO.put(oracle.sql.OPAQUE.class.getName(), SQLXML.class);
    mapCLASS_ORACLE_TO_ISO.put(oracle.jdbc.OracleOpaque.class.getName(), SQLXML.class);
    ***/
    mapCLASS_ORACLE_TO_ISO.put(oracle.sql.INTERVALYM.class.getName(), Duration.class);
    mapCLASS_ORACLE_TO_ISO.put(oracle.sql.INTERVALDS.class.getName(), Duration.class);
    mapCLASS_ORACLE_TO_ISO.put(oracle.sql.BINARY_FLOAT.class.getName(), Float.class);
    mapCLASS_ORACLE_TO_ISO.put(oracle.sql.BINARY_DOUBLE.class.getName(), Double.class);
    mapCLASS_ORACLE_TO_ISO.put(oracle.sql.TIMESTAMP.class.getName(), Timestamp.class);
    mapCLASS_ORACLE_TO_ISO.put(oracle.sql.TIMESTAMPLTZ.class.getName(), Timestamp.class);
    mapCLASS_ORACLE_TO_ISO.put(oracle.sql.TIMESTAMPTZ.class.getName(), Timestamp.class);
	}
  private Connection _conn = null;

	public OracleResultSetMetaData(ResultSetMetaData rsmdWrapped, Connection conn) 
	{
		super(rsmdWrapped);
		_conn = conn;
	} /* constructor */

	/*------------------------------------------------------------------*/
	/**
	 * {@inheritDoc} fix invalid original class name.
	 */
	@Override
	public String getColumnClassName(int column) throws SQLException
	{
    Class<?> cls = null;
		String sClassName = super.getColumnClassName(column);
		if (sClassName != null)
	    cls = mapCLASS_ORACLE_TO_ISO.get(sClassName);
		else
		{
	    int iColumnType = super.getColumnType(column);
	    switch(iColumnType)
	    {
        case 100: cls = Float.class; break;
        case 101: cls = Double.class; break;
	    }
		}
		return cls.getName();
	} /* getColumnClassName */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc} 
	 * map java.sql.Types type.
	 */
	@Override
	public int getColumnType(int column) throws SQLException 
	{
    return OracleMetaColumns.getDataType(
      super.getColumnType(column),
      super.getColumnTypeName(column),
      super.getPrecision(column),
      super.getScale(column),
      _conn,
      super.getCatalogName(column), 
      super.getSchemaName(column));
	} /* getColumnType */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} 
   * map java.sql.Types type.
   */
	@Override
	public int getPrecision(int column) throws SQLException
	{
	  return (int)OracleMetaColumns.getColumnSize(
      super.getColumnType(column),
      super.getColumnTypeName(column),
      super.getPrecision(column),
      super.getScale(column),
      _conn,
      super.getCatalogName(column), 
      super.getSchemaName(column));
	} /* getColumnSize */
	
	/*------------------------------------------------------------------*/
	/** {@inheritDoc}
	 * map type name.
	 */
	@Override
	public String getColumnTypeName(int column) throws SQLException 
	{
	  int iDataType = super.getColumnType(column);
	  String sTypeName = super.getColumnTypeName(column);
	  if ((iDataType == Types.ARRAY) ||
	      (iDataType == Types.DISTINCT) ||
	      (iDataType == Types.STRUCT))
      sTypeName = OracleMetaColumns.getTypeName(
        super.getColumnTypeName(column),
        super.getColumnType(column),
        super.getPrecision(column),
        super.getScale(column),
        _conn,
        super.getCatalogName(column), 
        super.getSchemaName(column));
	  return sTypeName;
	} /* getColumnTypeName */

} /* class OracleResultSetMetaData */
