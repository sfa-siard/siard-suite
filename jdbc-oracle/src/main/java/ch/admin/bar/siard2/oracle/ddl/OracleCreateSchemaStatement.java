/*======================================================================
OracleCreateSchemaStatement overrides CreateSchemaStatement of SQL parser.
Application : SIARD2
Description : OracleCreateSchemaStatement overrides CreateSchemaStatement
			        of SQL parser, because in Oracle, schemes are created by 
			        creating users.
			        see: https://docs.oracle.com/cd/B19306_01/server.102/b14200/statements_6014.htm
              Also we need to quote all identifiers rather than formatting them.
Platform    : Java 7   
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rüti ZH, Switzerland
Created    : 20.06.2016, Simon Jutz
======================================================================*/
package ch.admin.bar.siard2.oracle.ddl;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.ddl.*;

/*====================================================================*/
/** OracleCreateSchemaStatement implements the formatting of CREATE
 * SCHEMA statements
 * @author Simon Jutz
 */
public class OracleCreateSchemaStatement
	extends CreateSchemaStatement {

  private static final String _sSCHEMA_PASSWORD = "SCHEMAPWD";
  public static String getSchemaPassword() { return _sSCHEMA_PASSWORD; };
  private static final String _sDEFAULT_PERMANENT_TABLESPACE = "USERS";
  
  /*------------------------------------------------------------------*/
	/** * format the create schema statement as a create user statement
	 * not granting anything to the user and using the standard password
	 * SCHEMAPWD. 
	 * Default tablespace USERS appears to be fairly safe:
	 * select * from database_properties where property_name like 'DEFAULT%TABLESPACE';
	 * @return the SQL string corresponding to a create schema statement
	 */
  @Override
  public String format() {
    String sStatement = null; 
    sStatement = K.CREATE.getKeyword() + sSP + K.USER.getKeyword() + 
    		sSP + SqlLiterals.quoteId(getSchemaName().getSchema()) + 
    		sSP + "IDENTIFIED" + sSP + K.BY.getKeyword() + sSP + _sSCHEMA_PASSWORD +
    		sSP + "QUOTA" + sSP + "UNLIMITED" + 
    		sSP + K.ON.getKeyword() + sSP + _sDEFAULT_PERMANENT_TABLESPACE;
    return sStatement;
  } /* format */

  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
	public OracleCreateSchemaStatement(SqlFactory sf) {
		super(sf);
	} /* constructor */
	
} /* class OracleCreateStatement */
