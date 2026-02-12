/*======================================================================
OracleCreateFunctionStatement overrides CreateFunctionStatement of SQL parser.
Version     : $Id: $
Application : SIARD2
Description : OracleCreateFunctionStatement overrides CreateFunctionStatement
			  of SQL parser.
Platform    : Java 7   
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rüti ZH, Switzerland
Created    : 30.06.2016, Simon Jutz
======================================================================*/
package ch.admin.bar.siard2.oracle.ddl;

import ch.enterag.sqlparser.K;
import ch.enterag.sqlparser.SqlFactory;
import ch.enterag.sqlparser.ddl.CreateFunctionStatement;

/*====================================================================*/
/** OracleCreateFunctionStatement overrides the formatting
 * of the CreateFunctionStatement
 * @author jutzs
 *
 */
public class OracleCreateFunctionStatement
	extends CreateFunctionStatement
{
    /*------------------------------------------------------------------*/
	/**
	 * format the drop schema statement
	 * @return the SQL string corresponding to a drop schema statement
	 */
	@Override
	public String format() {
	    String sStatement = K.CREATE.getKeyword() + sSP + K.FUNCTION.getKeyword() + sSP +
	  	      getFunctionName().quote() + formatParameters() + sSP + getReturnsClause().format();
	  	    if (getRoutineBody() != null)
	  	      sStatement = sStatement + K.IS.getKeyword() + sNEW_LINE + getRoutineBody().format();
	  	    return sStatement;
	} /* format */

    /*------------------------------------------------------------------*/
    /** constructor with factory only to be called by factory.
     * @param sf factory.
     */
	public OracleCreateFunctionStatement(SqlFactory sf) {
		super(sf);
	} /* constructor */
}
