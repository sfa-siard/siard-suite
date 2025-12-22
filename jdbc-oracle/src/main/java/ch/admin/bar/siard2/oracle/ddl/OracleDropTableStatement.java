/*======================================================================
DropTableStatement overrides DropTableStatement of SQL parser.
Version     : $Id: $
Application : SIARD2
Description : OracleDropTableStatement overrides DropTableStatement
			  of SQL parser, because Oracle requires an additional
			  keyword "CONSTRAINTS" to the CASCADE subclause
			  see:
			  https://docs.oracle.com/cd/B19306_01/server.102/b14200/statements_9003.htm
Platform    : Java 7   
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rüti ZH, Switzerland
Created    : 21.06.2016, Simon Jutz
======================================================================*/
package ch.admin.bar.siard2.oracle.ddl;

import ch.enterag.sqlparser.K;
import ch.enterag.sqlparser.SqlFactory;
import ch.enterag.sqlparser.ddl.DropTableStatement;
import ch.enterag.sqlparser.ddl.enums.DropBehavior;

/*====================================================================*/
/** OracleDropTableStatement implements the formatting of DROP
 * TABLE statements
 * @author Simon Jutz
 */
public class OracleDropTableStatement
	extends DropTableStatement {
    /*------------------------------------------------------------------*/
	/**
	 * format the drop schema statement
	 * @return the SQL string corresponding to a drop schema statement
	 */
	@Override
	public String format() {
	    String sStatement = null; 

	    sStatement = K.DROP.getKeyword() + sSP + K.TABLE.getKeyword() + sSP + getTableName().quote();
	    if (getDropBehavior() == DropBehavior.CASCADE)
	      sStatement += sSP + K.CASCADE.getKeyword() + sSP + K.CONSTRAINTS.getKeyword();
	    
	    return sStatement;
	} /* format */

    /*------------------------------------------------------------------*/
    /** constructor with factory only to be called by factory.
     * @param sf factory.
     */
	public OracleDropTableStatement(SqlFactory sf) {
		super(sf);
	} /* constructor */
}
