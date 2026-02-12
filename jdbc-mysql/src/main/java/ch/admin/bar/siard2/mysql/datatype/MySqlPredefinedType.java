/*======================================================================
MySqlPredefinedType implements the type translation from ISO SQL to MySql
Version     : $Id: $
Application : SIARD2
Description : MySqlPredefinedType implements the type translation from ISO SQL to MySql
Platform    : Java 7   
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rüti ZH, Switzerland
Created    : 02.11.2016, Simon Jutz
======================================================================*/
package ch.admin.bar.siard2.mysql.datatype;

import java.util.HashMap;
import java.util.Map;

import ch.enterag.sqlparser.SqlFactory;
import ch.enterag.sqlparser.datatype.PredefinedType;
import ch.enterag.sqlparser.datatype.enums.PreType;

/*====================================================================*/
/**
 * MySqlPredefinedType implements the type translation from ISO SQL to MySql
 * @author Simon Jutz
 */
public class MySqlPredefinedType extends PredefinedType 
{
	private static Map<PreType, String> mapISO_TO_MYSQL = new HashMap<PreType, String>();
	
	static {
		mapISO_TO_MYSQL.put(PreType.CHAR, "CHAR");
		mapISO_TO_MYSQL.put(PreType.VARCHAR, "VARCHAR");
		mapISO_TO_MYSQL.put(PreType.CLOB, "LONGTEXT");
		mapISO_TO_MYSQL.put(PreType.NCHAR, "NCHAR");
		mapISO_TO_MYSQL.put(PreType.NVARCHAR, "NVARCHAR");
		mapISO_TO_MYSQL.put(PreType.NCLOB, "LONGTEXT");
		mapISO_TO_MYSQL.put(PreType.XML, "LONGTEXT");
		mapISO_TO_MYSQL.put(PreType.BINARY, "BINARY");
		mapISO_TO_MYSQL.put(PreType.VARBINARY, "VARBINARY");
		mapISO_TO_MYSQL.put(PreType.BLOB, "LONGBLOB");
		mapISO_TO_MYSQL.put(PreType.NUMERIC, "NUMERIC");
		mapISO_TO_MYSQL.put(PreType.DECIMAL, "DECIMAL");
		mapISO_TO_MYSQL.put(PreType.SMALLINT, "SMALLINT");
		mapISO_TO_MYSQL.put(PreType.INTEGER, "INTEGER");
		mapISO_TO_MYSQL.put(PreType.BIGINT, "BIGINT");
		mapISO_TO_MYSQL.put(PreType.FLOAT, "FLOAT");
		mapISO_TO_MYSQL.put(PreType.REAL, "REAL");
		mapISO_TO_MYSQL.put(PreType.DOUBLE, "DOUBLE");
		mapISO_TO_MYSQL.put(PreType.BOOLEAN, "BOOLEAN");
		mapISO_TO_MYSQL.put(PreType.DATE, "DATE");
		mapISO_TO_MYSQL.put(PreType.TIME, "TIME");
		mapISO_TO_MYSQL.put(PreType.TIMESTAMP, "DATETIME");
		mapISO_TO_MYSQL.put(PreType.INTERVAL, "VARBINARY");
		mapISO_TO_MYSQL.put(PreType.DATALINK, "LONGBLOB");
	}
	/**
	 * Constructor
	 * @param sf factory
	 */
	public MySqlPredefinedType(SqlFactory sf) {
		super(sf);
	} /* constructor */
	
  /*------------------------------------------------------------------*/
  /** decimals of seconds in parentheses.
   * @param iDefaultDecimals default decimals.
   * @return seconds decimals in parentheses.
   */
  protected String formatSecondsDecimals(int iDefaultDecimals)
  {
    String sSecondsDecimals = "";
    int iSecondsDecimals = getSecondsDecimals();
    if (iSecondsDecimals != iUNDEFINED)
    {
      if (iSecondsDecimals > 6)
        iSecondsDecimals = 6; // maximum precision for DATETIME
      if (iSecondsDecimals != iDefaultDecimals)
        sSecondsDecimals = sSecondsDecimals + sLEFT_PAREN + String.valueOf(iSecondsDecimals) + sRIGHT_PAREN;
    }
    return sSecondsDecimals;
  } /* formatSecondsDecimals */
  
	/**
	 * format the predefined data type.
	 * @return the SQL string corresponding to the fields of the data type.
	 */
	public String format() 
	{
		String sType = null;
		sType = mapISO_TO_MYSQL.get(getType());
		if(getType() == PreType.NUMERIC || 
		   getType() == PreType.DECIMAL) 
			sType = sType + formatPrecisionScale(); // [(M,D)]
		else if (getType() == PreType.FLOAT || 
				   getType() == PreType.REAL || 
				   getType() == PreType.DOUBLE || 
				   getType() == PreType.FLOAT) 
			sType = sType + formatPrecisionScale(); // [(M[,D])]
		else if (getType() == PreType.SMALLINT || 
				   getType() == PreType.INTEGER || 
				   getType() == PreType.BIGINT) 
			sType = sType + formatPrecisionScale(); // [(M)]
    else if (getType() == PreType.CHAR || 
      getType() == PreType.VARCHAR || 
      getType() == PreType.NCHAR ||
      getType() == PreType.NVARCHAR)
    {
      sType = sType + formatLength(); // (M)
      long l = getLength();
      if ((l == (long)iUNDEFINED) && 
          ((getType() == PreType.CHAR) || getType() == PreType.NCHAR))
        l = 1;
      if (l != (long)iUNDEFINED)
      {
        if (l >= 16777216)
          sType = "LONGTEXT";
        else if (l >= 65536)
          sType = "MEDIUMTEXT";
        else if (l >= 256)
          sType = "TEXT";
      }
      else
        sType = "TEXT";
    }
    else if (getType() == PreType.BINARY || 
      getType() == PreType.VARBINARY)
    {
      sType = sType + formatLength(); // (M)
      long l = getLength();
      if ((l == (long)iUNDEFINED) && 
          (getType() == PreType.BINARY))
        l = 1;
      if (getLength() != (long)iUNDEFINED)
      {
        if (l >= 16777216)
          sType = "LONGBLOB";
        else if (l >= 65536)
          sType = "MEDIUMBLOB";
        else if (l >= 256)
          sType = "BLOB";
      }
      else
        sType = "BLOB";
    }
		else if (getType() == PreType.TIME ||
				   getType() == PreType.TIMESTAMP ||
				   getType() == PreType.DATE) 
			sType = sType + formatSecondsDecimals(iTIME_DECIMALS_DEFAULT); // [(M)]
		else if (getType() == PreType.INTERVAL)
		  sType = sType + "(255)";
		else if ((getType() == PreType.CLOB) ||
		         (getType() == PreType.NCLOB))
		{
      long l = getLength();
		  if (getLength() != (long)iUNDEFINED)
		  {
		    if (getMultiplier() != null)
		      l = l * getMultiplier().getValue();
		    if (l < 65536)
		      sType = "TEXT"; // 2 bytes
	      else if (l < 16777216)
		      sType = "MEDIUMTEXT"; // 3 bytes
		  }
		}
    else if ((getType() == PreType.BLOB) || getType() == PreType.DATALINK)
    {
      if (getLength() != iUNDEFINED)
      {
        long l = getLength();
        if (getMultiplier() != null)
          l = l * getMultiplier().getValue();
        if (l < 65536) // 2 bytes
          sType = "BLOB";
        else if (l < 16777216)
          sType = "MEDIUMBLOB"; // 3 bytes
      }
    }
		if (sType.startsWith("CHAR") || 
		    sType.startsWith("VARCHAR") || 
		    sType.startsWith("TEXT") ||
		    sType.startsWith("MEDIUMTEXT") ||
		    sType.startsWith("LONGTEXT"))
		  sType = sType + " BINARY";
		return sType;
	} /* format */

} /* class MySqlPredefinedType */
