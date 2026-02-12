/*======================================================================
MsSqlDropSchemaStatement overrides DropSchemaStatement of SQL parser.
Version     : $Id: $
Application : SIARD2
Description : MsSqlDropSchemaStatement overrides DropSchemaStatement of SQL 
              parser because MSSQL does not support drop behavior 
              (CASCADE, RESTRICT) for schemas. (RESTRICT is the implicit
              default.) 
Platform    : Java 7   
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rüti ZH, Switzerland
Created    : 01.06.2016, Hartwig Thomas
======================================================================*/
package ch.admin.bar.siard2.mssql.ddl;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.ddl.*;
import ch.enterag.sqlparser.ddl.enums.*;

/*====================================================================*/
/** MsSqlDropSchemaStatement overrides DropSchemaStatement of SQL parser 
 * because MSSQL does not support drop behavior (CASCADE, RESTRICT) for 
 * schemas. (RESTRICT is the implicit default.)
 * @author Hartwig Thomas
 */
public class MsSqlDropSchemaStatement
  extends DropSchemaStatement
{
  /*------------------------------------------------------------------*/
  /** format the drop schema statement for MSSQL without the drop behavior.
   * @return the SQL string corresponding to the fields of the drop schema statement.
   */
  @Override
  public String format()
  {
    if (getDropBehavior() == DropBehavior.CASCADE)
      throw new IllegalArgumentException("Schema drop behavior CASCADE not supported by MSSQL!");
    String sStatement = K.DROP.getKeyword() + sSP + K.SCHEMA.getKeyword() + sSP + 
      getSchemaName().format(); 
    return sStatement;
  } /* format */

  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public MsSqlDropSchemaStatement(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class MsSqlDropSchemaStatement */
