/*======================================================================
Implements the type translation between MySql and ISO SQL
Version     : $Id: $
Application : SIARD2
Description : Implements the type translation between MySql and ISO SQL
Platform    : Java 7   
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rüti ZH, Switzerland
Created    : 31.10.2016, Simon Jutz
======================================================================*/
package ch.admin.bar.siard2.jdbc;

import java.sql.*;
import java.util.*;
import ch.admin.bar.siard2.mysql.*;
import ch.enterag.sqlparser.datatype.enums.*;

/* =============================================================================== */
/**
 * Implements the type translation between MySql and ISO SQL
 * @author Simon Jutz
 */
public class MySqlMetaColumns extends MySqlResultSet 
{
	private static Map<MySqlType,PreType> mapNAME_MYSQL_TO_ISO = new HashMap<MySqlType,PreType>();

	static 
	{
		mapNAME_MYSQL_TO_ISO.put(MySqlType.BIGINT, PreType.BIGINT);
		mapNAME_MYSQL_TO_ISO.put(MySqlType.BIGINTU, PreType.BIGINT);
		mapNAME_MYSQL_TO_ISO.put(MySqlType.BINARY, PreType.BINARY);
		mapNAME_MYSQL_TO_ISO.put(MySqlType.BIT, PreType.BOOLEAN);
		mapNAME_MYSQL_TO_ISO.put(MySqlType.MULTIBIT, PreType.BINARY);
		mapNAME_MYSQL_TO_ISO.put(MySqlType.BLOB, PreType.BLOB);
		mapNAME_MYSQL_TO_ISO.put(MySqlType.BOOL, PreType.BOOLEAN);
		mapNAME_MYSQL_TO_ISO.put(MySqlType.BOOLEAN, PreType.BOOLEAN);
		mapNAME_MYSQL_TO_ISO.put(MySqlType.CHAR, PreType.CHAR);
		mapNAME_MYSQL_TO_ISO.put(MySqlType.DATE, PreType.DATE);
		mapNAME_MYSQL_TO_ISO.put(MySqlType.DATETIME, PreType.TIMESTAMP);
		mapNAME_MYSQL_TO_ISO.put(MySqlType.DECIMAL, PreType.DECIMAL);
		mapNAME_MYSQL_TO_ISO.put(MySqlType.DECIMALU, PreType.DECIMAL);
		mapNAME_MYSQL_TO_ISO.put(MySqlType.DOUBLE, PreType.DOUBLE);
		mapNAME_MYSQL_TO_ISO.put(MySqlType.DOUBLEU, PreType.DOUBLE);
		mapNAME_MYSQL_TO_ISO.put(MySqlType.ENUM, PreType.VARCHAR);
		mapNAME_MYSQL_TO_ISO.put(MySqlType.FLOAT, PreType.FLOAT);
		mapNAME_MYSQL_TO_ISO.put(MySqlType.FLOATU, PreType.FLOAT);
		mapNAME_MYSQL_TO_ISO.put(MySqlType.INT, PreType.INTEGER);
		mapNAME_MYSQL_TO_ISO.put(MySqlType.INTU, PreType.BIGINT);
		mapNAME_MYSQL_TO_ISO.put(MySqlType.INTEGER, PreType.INTEGER);
		mapNAME_MYSQL_TO_ISO.put(MySqlType.INTEGERU, PreType.BIGINT);
		mapNAME_MYSQL_TO_ISO.put(MySqlType.LONGBLOB, PreType.BLOB);
		mapNAME_MYSQL_TO_ISO.put(MySqlType.LONGTEXT, PreType.CLOB);
		mapNAME_MYSQL_TO_ISO.put(MySqlType.MEDIUMBLOB, PreType.BLOB);
		mapNAME_MYSQL_TO_ISO.put(MySqlType.MEDIUMINT, PreType.INTEGER);
		mapNAME_MYSQL_TO_ISO.put(MySqlType.MEDIUMINTU, PreType.BIGINT);
		mapNAME_MYSQL_TO_ISO.put(MySqlType.MEDIUMTEXT, PreType.CLOB);
		mapNAME_MYSQL_TO_ISO.put(MySqlType.SET, PreType.VARCHAR);
		mapNAME_MYSQL_TO_ISO.put(MySqlType.SMALLINT, PreType.SMALLINT);
		mapNAME_MYSQL_TO_ISO.put(MySqlType.SMALLINTU, PreType.INTEGER);
		mapNAME_MYSQL_TO_ISO.put(MySqlType.TEXT, PreType.CLOB);
		mapNAME_MYSQL_TO_ISO.put(MySqlType.TIME, PreType.TIME);
		mapNAME_MYSQL_TO_ISO.put(MySqlType.TIMESTAMP, PreType.TIMESTAMP);
		mapNAME_MYSQL_TO_ISO.put(MySqlType.TINYBLOB, PreType.VARBINARY);
		mapNAME_MYSQL_TO_ISO.put(MySqlType.TINYINT, PreType.SMALLINT);
		mapNAME_MYSQL_TO_ISO.put(MySqlType.TINYINTU, PreType.SMALLINT);
		mapNAME_MYSQL_TO_ISO.put(MySqlType.TINYTEXT, PreType.VARCHAR);
		mapNAME_MYSQL_TO_ISO.put(MySqlType.VARBINARY, PreType.VARBINARY);
		mapNAME_MYSQL_TO_ISO.put(MySqlType.VARCHAR, PreType.VARCHAR);
		mapNAME_MYSQL_TO_ISO.put(MySqlType.YEAR, PreType.SMALLINT);
		mapNAME_MYSQL_TO_ISO.put(MySqlType.GEOMETRY, PreType.CLOB);
		mapNAME_MYSQL_TO_ISO.put(MySqlType.POINT, PreType.CLOB);
		mapNAME_MYSQL_TO_ISO.put(MySqlType.LINESTRING, PreType.CLOB);
		mapNAME_MYSQL_TO_ISO.put(MySqlType.POLYGON, PreType.CLOB);
		mapNAME_MYSQL_TO_ISO.put(MySqlType.MULTIPOINT, PreType.CLOB);
		mapNAME_MYSQL_TO_ISO.put(MySqlType.MULTILINESTRING, PreType.CLOB);
		mapNAME_MYSQL_TO_ISO.put(MySqlType.MULTIPOLYGON, PreType.CLOB);
		mapNAME_MYSQL_TO_ISO.put(MySqlType.GEOMETRYCOLLECTION, PreType.CLOB);
		mapNAME_MYSQL_TO_ISO.put(MySqlType.JSON, PreType.CLOB);
	}

	private static final int iMAX_VARCHAR_LENGTH = 21845; 
	private int _iDataType = -1;
	private int _iTypeName = -1;
	private int _iPrecision = -1;
	private int _iLength = -1;
	private ResultSet _rsUnwrapped = null;

	/* ------------------------------------------------------------------------ */
	/**
	 * Constructor
	 * @param rsWrapped ResultSet to be wrapped
	 */
	public MySqlMetaColumns(ResultSet rsWrapped, int iDataType, int iTypeName,
			int iPrecision, int iLength, MySqlConnection conn) throws SQLException
  {
		super(rsWrapped, conn);
		_iDataType = iDataType;
		_iTypeName = iTypeName;
		_iPrecision = iPrecision;
		_iLength = iLength;
		_rsUnwrapped = unwrap(ResultSet.class);
	} /* constructor */


	/** Implements the type translation between MySql and ISO SQL
	 * @param sTypeName original type name.
	 * @return data type from java.sql.Types.
	 */
	static int getDataType(String sTypeName) {
		if (sTypeName == null) {
			return Types.OTHER;
		}

		String sTypeNameLower = sTypeName.toLowerCase();
		String sBaseTypeName = sTypeNameLower;
		int iParenIndex = sBaseTypeName.indexOf('(');
		if (iParenIndex > 0) {
			sBaseTypeName = sBaseTypeName.substring(0, iParenIndex);
		}

		// For BIT types, pass the full type name to getByTypeName to handle length
		if (sBaseTypeName.equals("bit")) {
			MySqlType mst = MySqlType.getByTypeName(sTypeNameLower);
			PreType pt = mapNAME_MYSQL_TO_ISO.get(mst);
			return pt.getSqlType();
		}

		// For other types, use the base type name
		MySqlType mst = MySqlType.getByTypeName(sBaseTypeName);

		PreType pt = mapNAME_MYSQL_TO_ISO.get(mst);
		return pt.getSqlType();
	}

	/*------------------------------------------------------------------*/
	/** implements length translation.
	 * @param lColumnSize unwrapped length
	 * @param sTypeName original type name.
	 * @return translated length.
	 */
	static long getColumnSize(long lColumnSize, String sTypeName)
	{
		if (lColumnSize <= 0) // VARCHAR must always have a length > 0!
		{
			int iDataType = getDataType(sTypeName);
			if ((iDataType == Types.VARCHAR) ||
					(iDataType == Types.NVARCHAR) ||
					(iDataType == Types.VARBINARY))
				lColumnSize = iMAX_VARCHAR_LENGTH;
		}
		return lColumnSize;
	} /* getColumnSize */
	
	/* ------------------------------------------------------------------------ */
	@Override
	public int getInt(int columnIndex) throws SQLException 
	{
		int iResult = -1;
		if(columnIndex == _iDataType) 
			iResult = getDataType(_rsUnwrapped.getString(_iTypeName));
		else if ((columnIndex == _iPrecision) ||
				(columnIndex == _iLength))
		{
			int iLength = _rsUnwrapped.getInt(_iPrecision);
			if (iLength <= 0)
				iLength = _rsUnwrapped.getInt(_iLength);
			iResult = (int)getColumnSize(
					iLength,
					_rsUnwrapped.getString(_iTypeName));
		}
		else
			iResult = super.getInt(columnIndex);
		return iResult;
	} /* getInt */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc}
	 * Mapped java.sql.Types type is returned in DATA_TYPE.
	 * Original java.sql.Types type can be retrieved by using unwrap. 
	 * Column size is adjusted to CHARS rather than BYTES.  
	 */
	@Override
	public long getLong(int columnIndex) throws SQLException
	{
		long lResult = -1;
		if ((columnIndex == _iPrecision) ||
				(columnIndex == _iLength))
		{
			long lLength = _rsUnwrapped.getLong(_iPrecision);
			if (lLength <= 0)
				lLength = _rsUnwrapped.getLong(_iLength);
			lResult = getColumnSize(
					lLength,
					_rsUnwrapped.getString(_iTypeName));
		}
		else if (columnIndex == _iDataType)
			lResult = getDataType(_rsUnwrapped.getString(_iTypeName));
		else
			lResult = _rsUnwrapped.getLong(columnIndex);
		return lResult;
	} /* getLong */

	/* ------------------------------------------------------------------------ */
	@Override
	public Object getObject(int columnIndex) throws SQLException 
	{
		Object oResult = _rsUnwrapped.getObject(columnIndex);

		
		if (oResult instanceof Integer)
			oResult = Integer.valueOf(getInt(columnIndex));
		else if (oResult instanceof Long)
			oResult = Long.valueOf(getLong(columnIndex));
		else if (oResult instanceof String)
			oResult = getString(columnIndex);

		if(columnIndex == _iLength)
			oResult = Long.valueOf(getLong(columnIndex));
			
		return oResult;
	} /* getObject */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc} */
	@Override
	public boolean next() throws SQLException
	{
		return _rsUnwrapped.next();
	} /* next */

} /* class MySqlMetaColumns */
