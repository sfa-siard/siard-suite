/*======================================================================
MySqlDataType implements the type translation from ISO SQL to MySQL for 
              complex types.
Version     : $Id: $
Application : SIARD2
Description : MySqlDataType implements the type translation from ISO 
              SQL:2008 to MySQL for complex types.
Platform    : Java 7   
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rüti ZH, Switzerland
Created    : 08.11.2016, Simon Jutz
======================================================================*/
package ch.admin.bar.siard2.mysql.datatype;

import ch.enterag.sqlparser.SqlFactory;
import ch.enterag.sqlparser.datatype.DataType;
import ch.enterag.sqlparser.datatype.PredefinedType;

/*====================================================================*/
/** MySqlDataType implements the type translation from ISO SQL to MySQL
 * for complex types.
 * @author Simon Jutz
 */
public class MySqlDataType extends DataType {

	/*------------------------------------------------------------------*/
	/** constructor with factory only to be called by factory.
	 * @param sf factory.
	 */
	public MySqlDataType(SqlFactory sf) {
		super(sf);
	} /* constructor */

	/*------------------------------------------------------------------*/
	/**
	 * format the STRUCT type for MySql (serialized as a BLOB)
	 */
	@Override
	public String formatStructType() {
	    PredefinedType pt = getSqlFactory().newPredefinedType();
	    pt.initBlobType(PredefinedType.iUNDEFINED, null);
	    return pt.format();
	} /* formatStructType */

	/*------------------------------------------------------------------*/
	/**
	 * format the ROW type for MySql (serialized as a BLOB)
	 */
	@Override
	public String formatRowType() {
	    PredefinedType pt = getSqlFactory().newPredefinedType();
	    pt.initBlobType(PredefinedType.iUNDEFINED, null);
	    return pt.format();
	} /* formatStructType */

	/*------------------------------------------------------------------*/
	/**
	 * format the REF type for MySql (serialized as a BLOB)
	 */
	@Override
	public String formatRefType() {
	    PredefinedType pt = getSqlFactory().newPredefinedType();
	    pt.initBlobType(PredefinedType.iUNDEFINED, null);
	    return pt.format();
	} /* formatStructType */

	/*------------------------------------------------------------------*/
	/**
	 * format the ARRAY type for MySql (serialized as a BLOB)
	 */
	@Override
	public String formatArrayType() {
	    PredefinedType pt = getSqlFactory().newPredefinedType();
	    pt.initBlobType(PredefinedType.iUNDEFINED, null);
	    return pt.format();
	} /* formatStructType */

	/*------------------------------------------------------------------*/
	/**
	 * format the MULTISET type for MySql (serialized as a BLOB)
	 */
	@Override
	public String formatMultisetType() {
	    PredefinedType pt = getSqlFactory().newPredefinedType();
	    pt.initBlobType(PredefinedType.iUNDEFINED, null);
	    return pt.format();
	} /* formatStructType */

	/*------------------------------------------------------------------*/
	/** format the data type for MySql.
	 * @return the SQL string corresponding to the fields of the data type.
	 */
	@Override
	public String format()
	{
		String sDataType = null;
		switch (getType())
		{
		case PRE: sDataType = getPredefinedType().format(); break;
		case STRUCT: sDataType = formatStructType(); break;
		case ROW: sDataType = formatRowType(); break;
		case REF: sDataType = formatRefType(); break;
		case ARRAY: sDataType = formatArrayType(); break;
		case MULTISET: sDataType = formatMultisetType(); break;
		}
		return sDataType;
	} /* format */

} /* class MySqlDataType */