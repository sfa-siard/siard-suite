/*======================================================================
MySqlDropTableStatement overrides DropTableStatement of SQL parser.
Application : SIARD2
Description : MySqlDropTableStatement overrides DropTableStatement of SQL 
              parser because MSSQL does not support drop behavior 
              (CASCADE, RESTRICT) for tables. (RESTRICT is the implicit
              default.) 
Platform    : Java 7   
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rüti ZH, Switzerland
Created    : 01.06.2016, Hartwig Thomas
======================================================================*/
package ch.admin.bar.siard2.mysql.ddl;

import ch.enterag.sqlparser.K;
import ch.enterag.sqlparser.SqlFactory;
import ch.enterag.sqlparser.ddl.DropTableStatement;

/*====================================================================*/
/** MySqlDropTableStatement overrides DropTableStatement of SQL parser 
 * because MSSQL does not support drop behavior (CASCADE, RESTRICT) for 
 * tables. (RESTRICT is the implicit default.)
 * @author Hartwig Thomas
 */
public class MySqlDropTableStatement
  extends DropTableStatement
{
  /*------------------------------------------------------------------*/
  /** format the drop table statement for MySQL without the drop behavior.
   * @return the SQL string corresponding to the fields of the drop table statement.
   */
  @Override
  public String format()
  {
    String sStatement = K.DROP.getKeyword() + sSP + K.TABLE.getKeyword() + sSP + 
      getTableName().format(); 
    return sStatement;
  } /* format */

  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public MySqlDropTableStatement(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
    
} /* class MySqlDropTableStatement */
