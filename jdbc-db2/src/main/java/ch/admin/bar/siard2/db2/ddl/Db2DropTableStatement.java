/*======================================================================
Db2DropTableStatement overrides DropTableStatement of SQL parser.
Application : SIARD2
Description : Db2DropTableStatement overrides DropTableStatement of SQL 
              parser because DB/2 does not support drop behavior 
              (CASCADE, RESTRICT) for tables. 
              (RESTRICT is the implicit default.) 
Platform    : Java 7   
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rüti ZH, Switzerland
Created    : 15.11.2016, Hartwig Thomas
======================================================================*/
package ch.admin.bar.siard2.db2.ddl;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.ddl.*;
import ch.enterag.sqlparser.ddl.enums.*;

/*====================================================================*/
/** Db2DropTableStatement overrides DropTableStatement of SQL parser 
 * because DB/2 does not support drop behavior (CASCADE, RESTRICT) for 
 * tables. (CASCADE is the implicit default.)
 * @author Hartwig Thomas
 */
public class Db2DropTableStatement
  extends DropTableStatement
{

  /*------------------------------------------------------------------*/
  /** format the drop table statement for DB/2 without the drop behavior.
   * @return the SQL string corresponding to the fields of the drop 
   *         table statement.
   */
  @Override
  public String format()
  {
    if (getDropBehavior() == DropBehavior.RESTRICT)
      throw new IllegalArgumentException("Table drop behavior RESTRICT not supported by DB/2!");
    String sStatement = K.DROP.getKeyword() + sSP + K.TABLE.getKeyword() + sSP + 
      getTableName().format(); 
    return sStatement;
  } /* format */

  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public Db2DropTableStatement(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class Db2DropTableStatement */
