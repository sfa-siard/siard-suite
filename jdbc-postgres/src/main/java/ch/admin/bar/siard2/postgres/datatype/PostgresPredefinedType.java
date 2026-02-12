/*======================================================================
PostgresPredefinedType implements the type translation from ISO SQL to Postgres.
Application : SIARD2
Description : PostgresPredefinedType implements the type translation from 
              ISO SQL:2008 to Postgres. 
Platform    : Java 17
------------------------------------------------------------------------
Copyright  : 2019, Swiss Federal Archives, Berne, Switzerland
License    : CDDL 1.0
Created    : 29.07.2017, Hartwig Thomas, Enter AG, Rüti ZH, Switzerland
======================================================================*/
package ch.admin.bar.siard2.postgres.datatype;

import ch.admin.bar.siard2.postgres.PostgresType;
import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.datatype.*;
import ch.enterag.sqlparser.datatype.enums.PreType;

/*====================================================================*/
/** PostgresPredefinedType implements the type translation from 
 * ISO SQL:2008 to Postgres. 
 * @author Hartwig Thomas
 */
public class PostgresPredefinedType
  extends PredefinedType
{
  /*------------------------------------------------------------------*/
  /** decimals of seconds in parentheses (maximum 6).
   * @param iDefaultDecimals default decimals.
   * @return seconds decimals in parentheses.
   */
  protected String formatSecondsDecimals(int iDefaultDecimals)
  {
    String sSecondsDecimals = "";
    if (iDefaultDecimals > PredefinedType.iTIMESTAMP_DECIMALS_DEFAULT)
      iDefaultDecimals = PredefinedType.iTIMESTAMP_DECIMALS_DEFAULT;
    if (getSecondsDecimals() != iUNDEFINED)
    {
      if (getSecondsDecimals() < iDefaultDecimals)
        sSecondsDecimals = sSecondsDecimals + sLEFT_PAREN + String.valueOf(getSecondsDecimals()) + sRIGHT_PAREN;
    }
    return sSecondsDecimals;
  } /* formatSecondsDecimals */
  
  /*------------------------------------------------------------------*/
  /** length field in parentheses.
   * @return length in parentheses.
   */
  protected String formatBitLength()
  {
    String sLength = "";
    if (getLength() != iUNDEFINED)
      sLength = sLEFT_PAREN + String.valueOf(8*getLength()) + sRIGHT_PAREN;
    return sLength;
  } /* formatLength */
  
  /*------------------------------------------------------------------*/
  /** format the predefined data type.
   * @return the SQL string corresponding to the fields of the data type.
   */
  @Override
  public String format() {
    String sType = null;
    if (getType() != null) {
      PostgresType pgt = PostgresType.getByPreType(getType());
      sType = pgt.getKeyword();
      if ((pgt != PostgresType.TEXT) && (pgt != PostgresType.BYTEA)) {
        if ((getType() == PreType.CHAR) || (getType() == PreType.VARCHAR) || (getType() == PreType.NCHAR) || (getType() == PreType.NVARCHAR)) {
          sType = sType + formatLength();
        } else if ((getType() == PreType.NUMERIC) || (getType() == PreType.DECIMAL)) {
          sType = sType + formatPrecisionScale();
        } else if (getType() == PreType.TIME) {
          sType = sType + formatSecondsDecimals(iTIME_DECIMALS_DEFAULT) + formatTimeZone();
        } else if (getType() == PreType.TIMESTAMP) {
          sType = sType + formatSecondsDecimals(iTIMESTAMP_DECIMALS_DEFAULT) + formatTimeZone();
        } else if (getType() == PreType.INTERVAL) {
          sType = sType + sSP + getIntervalQualifier().format();
        } else if (getType() == PreType.DATALINK) {
          sType = PreType.BLOB.getKeyword();
        }
      }
    }
    return sType;
  }

  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public PostgresPredefinedType(SqlFactory sf)
  {
    super(sf);
  } /* constructor */

} /* class PostgresPredefinedType */
