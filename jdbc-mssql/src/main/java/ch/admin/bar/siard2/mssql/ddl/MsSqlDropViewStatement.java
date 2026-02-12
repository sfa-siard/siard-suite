/*======================================================================
MsSqlDropViewStatement overrides DropViewStatement of SQL parser.
Application : SIARD2
Description : MsSqlDropViewStatement overrides DropViewStatement of SQL 
              parser because MSSQL does not support drop behavior 
              (CASCADE, RESTRICT) for views. (RESTRICT is the implicit
              default.) 
Platform    : Java 7   
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rüti ZH, Switzerland
Created    : 01.06.2016, Hartwig Thomas
======================================================================*/
package ch.admin.bar.siard2.mssql.ddl;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.ddl.*;

/*====================================================================*/
/** MsSqlDropViewStatement overrides DropViewStatement of SQL parser 
 * because MSSQL does not support drop behavior (CASCADE, RESTRICT) for 
 * views. (RESTRICT is the implicit default.)
 * @author Hartwig Thomas
 */
public class MsSqlDropViewStatement
  extends DropViewStatement
{
  /*------------------------------------------------------------------*/
  /** format the drop view statement for MSSQL without the drop behavior.
   * @return the SQL string corresponding to the fields of the drop view statement.
   */
  @Override
  public String format()
  {
    String sStatement = K.DROP.getKeyword() + sSP + K.VIEW.getKeyword() + sSP + 
      getViewName().format(); 
    return sStatement;
  } /* format */

  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public MsSqlDropViewStatement(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class MsSqlDropViewStatement */
