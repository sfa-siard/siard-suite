/*======================================================================
MsSqlLiteral implements the value translation from ISO SQL to MSSQL.
Version     : $Id: $
Application : SIARD2
Description : MsSqlLiteral implements the value translation from 
              ISO SQL:2008 to MS SQL Server.
Platform    : Java 7   
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rüti ZH, Switzerland
Created    : 23.05.2016, Hartwig Thomas
======================================================================*/
package ch.admin.bar.siard2.mssql.expression;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.expression.*;
import ch.admin.bar.siard2.mssql.*;

/*====================================================================*/
/** MsSqlLiteral implements the value translation from ISO 
 * SQL:2008 to MS SQL Server.
 * @author Hartwig Thomas
 */
public class MsSqlLiteral
  extends Literal
{
  /*------------------------------------------------------------------*/
  /** format the literal
   * @return the MSSQL string corresponding to the fields of the literal.
   */
  @Override
  public String format()
  {
    String sFormatted = "";
    if (getBytes() != null)
      sFormatted = MsSqlLiterals.formatBytesLiteral(getBytes());
    else if (getDate() != null)
      sFormatted = MsSqlLiterals.formatDateLiteral(getDate());
    else if (getTime() != null)
      sFormatted = MsSqlLiterals.formatTimeLiteral(getTime());
    else if (getTimestamp() != null)
      sFormatted = MsSqlLiterals.formatTimestampLiteral(getTimestamp());
    else if (getBoolean() != null)
      sFormatted = MsSqlLiterals.formatBooleanLiteral(getBoolean());
    return sFormatted;
  } /* format */

  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public MsSqlLiteral(SqlFactory sf)
  {
    super(sf);
  } /* constructor */

} /* class MsSqlLiteral */
