/*======================================================================
Db2DropViewStatement overrides DropViewStatement of SQL parser.
Application : SIARD2
Description : Db2DropViewStatement overrides DropViewStatement of SQL 
              parser because DB/2 does not support drop behavior 
              (CASCADE, RESTRICT) for views. 
              (RESTRICT is the implicit default.) 
Platform    : Java 7   
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rüti ZH, Switzerland
Created    : 30.11.2016, Hartwig Thomas
======================================================================*/
package ch.admin.bar.siard2.db2.ddl;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.ddl.*;
import ch.enterag.sqlparser.ddl.enums.*;

/*====================================================================*/
/** Db2DropViewStatement overrides DropViewStatement of SQL parser 
 * because DB/2 does not support drop behavior (CASCADE, RESTRICT) for 
 * views. (RESTRICT is the implicit default.)
 * @author Hartwig Thomas
 */
public class Db2DropViewStatement
  extends DropViewStatement
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
      throw new IllegalArgumentException("View drop behavior RESTRICT not supported by DB/2!");
    String sStatement = K.DROP.getKeyword() + sSP + K.VIEW.getKeyword() + sSP + 
      getViewName().format(); 
    return sStatement;
  } /* format */

  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public Db2DropViewStatement(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* DropViewStatement */
