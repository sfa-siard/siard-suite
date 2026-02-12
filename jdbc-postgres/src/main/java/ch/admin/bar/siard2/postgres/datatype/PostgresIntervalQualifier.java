/*======================================================================
PostgresIntervalQualifier implements the interval translation from ISO SQL to Postgres.
Application : SIARD2
Description : PostgresIntervalQualifier implements the interval translation 
              from ISO:2008 SQL to Postgres. 
Platform    : Java 17
------------------------------------------------------------------------
Copyright  : 2019, Swiss Federal Archives, Berne, Switzerland
License    : CDDL 1.0
Created    : 29.07.2017, Hartwig Thomas, Enter AG, Rüti ZH, Switzerland
======================================================================*/
package ch.admin.bar.siard2.postgres.datatype;

import ch.enterag.sqlparser.K;
import ch.enterag.sqlparser.SqlFactory;
import ch.enterag.sqlparser.datatype.*;
import ch.enterag.sqlparser.datatype.enums.IntervalField;

/*====================================================================*/
/** PostgresIntervalQualifier implements the interval translation from 
 * ISO SQL:2008 to Postgres. 
 * @author Hartwig Thomas
 */
public class PostgresIntervalQualifier
  extends IntervalQualifier
{
  /*------------------------------------------------------------------*/
  /** format the interval qualifier.
   * @return the SQL string corresponding to the fields of the interval 
   *   qualifier.
   */
  @Override
  public String format()
  {
    String sQualifier = getStartField().getKeywords();
    /***
    if (getPrecision() != iUNDEFINED)
    {
      sQualifier = sQualifier + sLEFT_PAREN + String.valueOf(getPrecision());
      if ((getStartField() == IntervalField.SECOND) && 
          (getEndField() == null) && 
          (getSecondsDecimals() != iUNDEFINED))
        sQualifier = sQualifier + sCOMMA + sSP + String.valueOf(getSecondsDecimals());
      sQualifier = sQualifier + sRIGHT_PAREN;
    }
    ***/
    if (getEndField() != null)
    {
      sQualifier = sQualifier + sSP + K.TO + sSP + getEndField().getKeywords();
      if ((getEndField() == IntervalField.SECOND) && 
          (getSecondsDecimals() != iUNDEFINED))
        sQualifier = sQualifier + sLEFT_PAREN + String.valueOf(getSecondsDecimals()) + sRIGHT_PAREN;
    }
    return sQualifier;
  } /* format */

  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public PostgresIntervalQualifier(SqlFactory sf)
  {
    super(sf);
  } /* constructor */

} /* class PostgresIntervalQualifier */
