/*======================================================================
PostgresUnsignedLiteral overrides UnsignedLiteral of SQL parser.
Application : SIARD2
Description : PostgresUnsignedLiteral overrides UnsignedLiteral of SQL 
              parser and uses PostgresLiteral for formatting. 
Platform    : Java 17   
------------------------------------------------------------------------
Copyright  : 2019, Swiss Federal Archives, Berne, Switzerland
License    : CDDL 1.0
Created    : 21.08.2017, Hartwig Thomas, Enter AG, Rüti ZH, Switzerland
======================================================================*/
package ch.admin.bar.siard2.postgres.expression;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.expression.*;
import ch.admin.bar.siard2.postgres.*;

/*====================================================================*/
/** PostgresUnsignedLiteral overrides UnsignedLiteral of SQL parser and 
 * uses PostgresLiteral for formatting.
 * @author Hartwig Thomas
 */
public class PostgresUnsignedLiteral
  extends UnsignedLiteral
{
  /*------------------------------------------------------------------*/
  /** format the unsigned literal
   * @return the SQL string corresponding to the fields of the unsigned literal.
   */
  @Override
  public String format()
  {
    String sFormatted = null;
    if (getApproximate() != null)
      sFormatted = PostgresLiterals.formatApproximateLiteral(getApproximate());
    else if (getExact() != null)
      sFormatted = PostgresLiterals.formatExactLiteral(getExact());
    else if (getCharacterString() != null)
      sFormatted = PostgresLiterals.formatStringLiteral(getCharacterString());
    else if (getNationalCharacterString() != null)
      sFormatted = PostgresLiterals.formatNationalStringLiteral(getNationalCharacterString());
    else if (getBitString() != null)
      sFormatted = PostgresLiterals.formatBitStringLiteral(getBitString());
    else if (getBytes() != null)
      sFormatted = PostgresLiterals.formatBytesLiteral(getBytes());
    else if (getDate() != null)
      sFormatted = PostgresLiterals.formatDateLiteral(getDate());
    else if (getTime() != null)
      sFormatted = PostgresLiterals.formatTimeLiteral(getTime());
    else if (getTimestamp() != null)
      sFormatted = PostgresLiterals.formatTimestampLiteral(getTimestamp());
    else if (getInterval() != null)
      sFormatted = PostgresLiterals.formatIntervalLiteral(getInterval());
    else if (getBoolean() != null)
      sFormatted = PostgresLiterals.formatBooleanLiteral(getBoolean());
    return sFormatted;
  } /* format */

  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public PostgresUnsignedLiteral(SqlFactory sf)
  {
    super(sf);
  } /* constructor */

} /* class PostgresUnsignedLiteral */
