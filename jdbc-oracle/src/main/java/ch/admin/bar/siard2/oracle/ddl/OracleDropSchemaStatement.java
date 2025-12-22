/*======================================================================
OracleDropSchemaStatement overrides DropSchemaStatement of SQL parser.
Version     : $Id: $
Application : SIARD2
Description : OracleDropSchemaStatement overrides DropSchemaStatement of 
              SQL because in Oracle schemas are users.
              Also we need to quote all identifiers rather than formatting
              them. 
Platform    : Java 7   
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rüti ZH, Switzerland
Created    : 09.06.2016, Hartwig Thomas
======================================================================*/
package ch.admin.bar.siard2.oracle.ddl;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.ddl.*;
import ch.enterag.sqlparser.ddl.enums.*;

/*====================================================================*/
/** OracleDropSchemaStatement overrides DropSchemaStatement of SQL 
 * because in Oracle schemas are users.
 * Also we need to quote all identifiers rather than formatting them.
 * @author Hartwig Thomas
 */
public class OracleDropSchemaStatement
  extends DropSchemaStatement
{
  /*------------------------------------------------------------------*/
  /** format the drop schema statement for Oracle as a drop user statement.
   * @return the SQL string corresponding to the fields of the drop schema statement.
   */
  @Override
  public String format()
  {
    String sStatement = null;
    String sSchema = getSchemaName().getSchema();
    if ((!sSchema.equals("SYS")) && (!sSchema.equals("SYSTEM")))
    {
      sStatement = K.DROP.getKeyword() + sSP + K.USER.getKeyword() + sSP + 
        getSchemaName().quote();
      if (getDropBehavior() == DropBehavior.CASCADE)
        sStatement = sStatement + sSP + K.CASCADE.getKeyword();
    }
    else
      throw new IllegalArgumentException("User SYS or SYSTEM may never be dropped!");
    return sStatement;
  } /* format */

  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public OracleDropSchemaStatement(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class OracleDropSchemaStatement */
