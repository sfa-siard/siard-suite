/*======================================================================
OracleMethodSpecification overrides MethodSpecification of SQL parser.
Version     : $Id: $
Application : SIARD2
Description : OracleMethodSpecification overrides MethodSpecification
			  of SQL parser, because Oracle has limited method 
			  characteristics 
			  see: https://docs.oracle.com/cd/B19306_01/server.102/b14200/statements_8001.htm#i2126584
Platform    : Java 7   
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rüti ZH, Switzerland
Created    : 30.06.2016, Simon Jutz
======================================================================*/
package ch.admin.bar.siard2.oracle.ddl;

import ch.enterag.sqlparser.SqlFactory;
import ch.enterag.sqlparser.ddl.MethodSpecification;

/*====================================================================*/
public class OracleMethodSpecification
	extends MethodSpecification {

    /*------------------------------------------------------------------*/
	/**
	 * format the method specification
	 * @return the SQL string corresponding to a method specification
	 */
	@Override
	public String format() {
		String sDefinition = "";
		return sDefinition;
	} /* format */

    /*------------------------------------------------------------------*/
    /** constructor with factory only to be called by factory.
     * @param sf factory.
     */
	public OracleMethodSpecification(SqlFactory sf) {
		super(sf);
	} /* constructor */
}
