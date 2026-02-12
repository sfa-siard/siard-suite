/*======================================================================
OracleCreateProcedureStatement overrides CreateProcedureStatement of SQL parser.
Version     : $Id: $
Application : SIARD2
Description : OracleCreateProcedureStatement overrides CreateProcedureStatement
			  of SQL parser.
Platform    : Java 7   
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rüti ZH, Switzerland
Created    : 30.06.2016, Simon Jutz
======================================================================*/
package ch.admin.bar.siard2.oracle.ddl;

import ch.enterag.sqlparser.K;
import ch.enterag.sqlparser.SqlFactory;
import ch.enterag.sqlparser.ddl.CreateProcedureStatement;

/*====================================================================*/
public class OracleCreateProcedureStatement
	extends CreateProcedureStatement 
{
    /*------------------------------------------------------------------*/
	/**
	 * format the create procedure statement
	 * @return the SQL string corresponding to a create procedure statement
	 */
	@Override
	public String format() {
	    String sStatement = K.CREATE.getKeyword() + sSP + K.PROCEDURE.getKeyword() + sSP +
	      getProcedureName().quote() + formatParameters();
	    if (getRoutineBody() != null)
	      sStatement = sStatement + K.AS.getKeyword() + sNEW_LINE + getRoutineBody().format();
	    return sStatement;
	} /* format */

    /*------------------------------------------------------------------*/
    /** constructor with factory only to be called by factory.
     * @param sf factory.
     */
	public OracleCreateProcedureStatement(SqlFactory sf) {
		super(sf);
	} /* constructor */

}
