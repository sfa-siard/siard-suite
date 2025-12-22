/*======================================================================
OraclePredefinedType implements the type translation from ISO SQL to Oracle SQL.
Version     : $Id: $
Application : SIARD2
Description : OraclePredefinedType implements the type translation from 
              ISO SQL:2008 to Oracle SQL. 
Platform    : Java 7   
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rueti ZH, Switzerland
Created    : 27.06.2016, Simon Jutz
======================================================================*/
package ch.admin.bar.siard2.oracle.datatype;

import java.util.HashMap;
import java.util.Map;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.datatype.*;
import ch.enterag.sqlparser.datatype.enums.*;

/*====================================================================*/
/** OraclePredefinedType implements the type translation from ISO SQL to 
 * Oracle SQL.
 * @author Simon Jutz
 */
public class OraclePredefinedType
	extends PredefinedType
{
	public static final int iMAX_VARYING_CHARACTER_STRING_LENGTH = 4000;
	public static final int iMAX_CHARACTER_STRING_LENGTH = 2000;
	public static final int iMAX_RAW_LENGTH = 2000;
	  
	/* translation of ISO SQL data types to Oracle data types in DDL */
	private static Map<PreType, String> mapISO_TO_ORACLE = new HashMap<PreType, String>();
	
	static {
		mapISO_TO_ORACLE.put(PreType.CHAR,"CHAR"); // or CLOB
		mapISO_TO_ORACLE.put(PreType.VARCHAR,"VARCHAR2"); // or CLOB
		mapISO_TO_ORACLE.put(PreType.CLOB,"CLOB");
		mapISO_TO_ORACLE.put(PreType.NCHAR,"NCHAR"); // or NCLOB
		mapISO_TO_ORACLE.put(PreType.NVARCHAR,"NVARCHAR2"); // or NCLOB
		mapISO_TO_ORACLE.put(PreType.NCLOB,"NCLOB");
		mapISO_TO_ORACLE.put(PreType.XML,"XMLTYPE");
		mapISO_TO_ORACLE.put(PreType.BINARY, "RAW"); // or BLOB
		mapISO_TO_ORACLE.put(PreType.VARBINARY, "RAW"); // or BLOB
		mapISO_TO_ORACLE.put(PreType.BLOB,"BLOB");
        mapISO_TO_ORACLE.put(PreType.NUMERIC,"NUMERIC");
        mapISO_TO_ORACLE.put(PreType.DECIMAL,"DECIMAL");
        mapISO_TO_ORACLE.put(PreType.SMALLINT,"SMALLINT");
        mapISO_TO_ORACLE.put(PreType.INTEGER,"INTEGER");
        mapISO_TO_ORACLE.put(PreType.BIGINT,"NUMBER(20)");
        mapISO_TO_ORACLE.put(PreType.FLOAT,"BINARY_DOUBLE");
        mapISO_TO_ORACLE.put(PreType.REAL,"BINARY_FLOAT");
        mapISO_TO_ORACLE.put(PreType.DOUBLE,"BINARY_DOUBLE");
        mapISO_TO_ORACLE.put(PreType.BOOLEAN,"NUMBER(1)");
        mapISO_TO_ORACLE.put(PreType.DATE,"DATE");
        mapISO_TO_ORACLE.put(PreType.TIME,"TIMESTAMP");
        mapISO_TO_ORACLE.put(PreType.TIMESTAMP,"TIMESTAMP"); // or DATE
        mapISO_TO_ORACLE.put(PreType.INTERVAL,"INTERVAL");
        mapISO_TO_ORACLE.put(PreType.DATALINK,"BLOB");
	}
	
	/*------------------------------------------------------------------*/
	/** format the predefined data type for Oracle
	 * In Oracle both character and national character strings have limits.
	 * A DECIMAL with neither precision nor scale, can be represented as a
	 * INTEGER. A time object without fractional seconds precision can be
	 * stored as a DATE. Finally, binary strings have a limit too.
	 */
	@Override
	public String format()
	{
		String sType = null;
		if(getType() != null) 
		{
			sType = mapISO_TO_ORACLE.get(getType());
			switch (getType())
			{
			  case CHAR:
        case NCHAR:
          if (getLength() > iMAX_CHARACTER_STRING_LENGTH)
            sType = K.CLOB.getKeyword();
          else
            sType = sType + formatLength();
			    break;
        case VARCHAR:
        case NVARCHAR:
          if (getLength() > iMAX_VARYING_CHARACTER_STRING_LENGTH)
            sType = K.CLOB.getKeyword();
          else
            sType = sType + formatLength();
          break;
        case CLOB:
        case NCLOB:
          sType = K.CLOB.getKeyword();
          break;
        case BINARY:
        case VARBINARY:
          if (getLength() == iUNDEFINED)
            setLength(1);
          if (getLength() > iMAX_RAW_LENGTH)
            sType = K.BLOB.getKeyword();
          else
            sType = sType + formatLength();
          break;
        case DATALINK:
            sType = K.BLOB.getKeyword();
        case NUMERIC:
        case DECIMAL:
          sType = sType + formatPrecisionScale();
          break;
        case TIMESTAMP:
          sType = sType + formatSecondsDecimals(iTIMESTAMP_DECIMALS_DEFAULT) + formatTimeZone();
          break;
        case INTERVAL:
          IntervalQualifier iq = getIntervalQualifier(); 
          if ((iq.getStartField() == IntervalField.YEAR) ||
              (iq.getStartField() == IntervalField.MONTH))
          {
            if (iq.getStartField() != IntervalField.YEAR)
              iq.setPrecision(iUNDEFINED);
            iq.setStartField(IntervalField.YEAR);
            iq.setEndField(IntervalField.MONTH);
          }
          else
          {
            if (iq.getStartField() != IntervalField.DAY)
              iq.setPrecision(iUNDEFINED);
            iq.setStartField(IntervalField.DAY);
            iq.setEndField(IntervalField.SECOND);
          }
          sType = sType + sSP + getIntervalQualifier().format();
          break;
        default: // all the other types do not need additional treatment
          break;
			}
		}
		return sType;
	}

	
	/*------------------------------------------------------------------*/
	/**
	 * constructor with factory only to be called by factory
	 * @param sf factory.
	 */
	public OraclePredefinedType(SqlFactory sf) {
		super(sf);
	} /* constructor */

} /* class OraclePredefinedType */
