/*======================================================================
DropTypeStatement overrides DropTypeStatement of SQL parser.
Version     : $Id: $
Application : SIARD2
Description : OracleDropTypeStatement overrides DropTypeStatement
			  of SQL parser, because Oracle has no drop behavior
			  on types
			  https://docs.oracle.com/cd/B19306_01/server.102/b14200/statements_9006.htm
Platform    : Java 7   
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rüti ZH, Switzerland
Created    : 30.06.2016, Simon Jutz
======================================================================*/
package ch.admin.bar.siard2.oracle.ddl;

import ch.enterag.sqlparser.K;
import ch.enterag.sqlparser.SqlFactory;
import ch.enterag.sqlparser.ddl.DropTypeStatement;

/*====================================================================*/
/** OracleDropTypeStatement implements the formatting of DROP
 * Type statements
 * @author Simon Jutz
 */
public class OracleDropTypeStatement
	extends DropTypeStatement {
    /*------------------------------------------------------------------*/
	/**
	 * format the drop type statement
	 * @return the SQL string corresponding to a drop type statement
	 */
	@Override
	public String format() {
		String sStatement = K.DROP.getKeyword() + sSP + K.TYPE.getKeyword() + sSP + 
			      getUdtName().quote();
	    
	    return sStatement;
	} /* format */

    /*------------------------------------------------------------------*/
    /** constructor with factory only to be called by factory.
     * @param sf factory.
     */
	public OracleDropTypeStatement(SqlFactory sf) {
		super(sf);
	} /* constructor */
}
