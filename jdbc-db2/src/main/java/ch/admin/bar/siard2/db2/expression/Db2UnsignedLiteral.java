package ch.admin.bar.siard2.db2.expression;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.expression.*;
import ch.admin.bar.siard2.db2.*;

public class Db2UnsignedLiteral
  extends UnsignedLiteral
{

  /*------------------------------------------------------------------*/
  /** format the unsigned literal
   * @return the MSSQL string corresponding to the fields of the unsigned literal.
   */
  @Override
  public String format()
  {
    String sFormatted = null;
    if (getBytes() != null)
      sFormatted = Db2Literals.formatBytesLiteral(getBytes());
    else if (getDate() != null)
      sFormatted = Db2Literals.formatDateLiteral(getDate());
    else if (getTime() != null)
      sFormatted = Db2Literals.formatTimeLiteral(getTime());
    else if (getTimestamp() != null)
      sFormatted = Db2Literals.formatTimestampLiteral(getTimestamp());
    else if (getInterval() != null)
      sFormatted = Db2Literals.formatIntervalLiteral(getInterval());
    else if (getBoolean() != null)
      sFormatted = Db2Literals.formatBooleanLiteral(getBoolean());
    else
      sFormatted = super.format();
    return sFormatted;
  } /* format */

  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public Db2UnsignedLiteral(SqlFactory sf)
  {
    super(sf);
  } /* constructor */

} /* Db2UnsignedLiteral */
