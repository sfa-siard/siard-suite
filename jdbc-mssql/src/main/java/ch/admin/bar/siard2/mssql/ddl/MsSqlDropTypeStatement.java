/*======================================================================
MsSqlDropTypeStatement overrides DropTypeStatement of SQL parser.
Version     : $Id: $
Application : SIARD2
Description : MsSqlDropTypeStatement overrides DropTypeStatement of SQL 
              parser because MSSQL does not support drop behavior 
              (CASCADE, RESTRICT) for types.
Platform    : Java 7   
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rüti ZH, Switzerland
Created    : 07.06.2016, Hartwig Thomas
======================================================================*/
package ch.admin.bar.siard2.mssql.ddl;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.ddl.*;

/*====================================================================*/
/** MsSqlDropTypeStatement overrides DropTableStatement of SQL parser 
 * because MSSQL does not support drop behavior (CASCADE, RESTRICT) for 
 * types.
 * @author Hartwig Thomas
 */
public class MsSqlDropTypeStatement
  extends DropTypeStatement
{

  /*------------------------------------------------------------------*/
  /** format the drop type statement for MSSQL without the drop behavior.
   * @return the SQL string corresponding to the fields of the drop type statement.
   */
  @Override
  public String format()
  {
    String sStatement = K.DROP.getKeyword() + sSP + K.TYPE.getKeyword() + sSP +
      getUdtName().format(); 
    return sStatement;
  } /* format */

  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public MsSqlDropTypeStatement(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
    
} /* class MsSqlDropTypeStatement */
