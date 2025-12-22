/*======================================================================
OracleReturnsClause overrides ReturnsClause of SQL parser.
Version     : $Id: $
Application : SIARD2
Description : OracleReturnsClause overrides ReturnsClause
			  of SQL parser.
Platform    : Java 7   
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rüti ZH, Switzerland
Created    : 30.06.2016, Simon Jutz
======================================================================*/
package ch.admin.bar.siard2.oracle.ddl;

import ch.enterag.sqlparser.K;
import ch.enterag.sqlparser.SqlFactory;
import ch.enterag.sqlparser.ddl.ReturnsClause;

/*====================================================================*/
/** OracleReturnsClause overrides the formatting
 * of the ReturnsClause
 * @author Simon Jutz
 */
public class OracleReturnsClause
	extends ReturnsClause
{
    /*------------------------------------------------------------------*/
	/**
	 * format the returns clause
	 * @return the SQL string corresponding to a returns clause
	 */
	@Override
	public String format() {
		String sReturnsClause = K.RETURN.getKeyword() + sSP + getDataType().format();
		return sReturnsClause;
	} /* format */

    /*------------------------------------------------------------------*/
    /** constructor with factory only to be called by factory.
     * @param sf factory.
     */
	public OracleReturnsClause(SqlFactory sf) {
		super(sf);
	} /* constructor */
}
