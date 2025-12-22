package ch.admin.bar.siard2.mssql.expression;

import ch.admin.bar.siard2.mssql.*;
import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.expression.*;

public class MsSqlSelectSublist
  extends SelectSublist
{

  /*------------------------------------------------------------------*/
  /** format the select sublist.
   * @return the SQL string corresponding to the fields of the select sublist.
   */
  @Override
  public String format()
  {
    String s = super.format();
    String sSuffix = ".ToString()";
    if (s.endsWith(sSuffix))
      s = s + " AS " + SqlLiterals.formatId(s.substring(0,s.length()-sSuffix.length()));
    return s;
  } /* format */

  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public MsSqlSelectSublist(MsSqlSqlFactory sf)
  {
    super(sf);
  } /* constructor */
    
}
