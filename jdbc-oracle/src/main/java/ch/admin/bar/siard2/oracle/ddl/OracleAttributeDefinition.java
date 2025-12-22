/*======================================================================
OracleAttributeDefinition overrides AttributeDefinition of SQL parser.
Version     : $Id: $
Application : SIARD2
Description : OracleAttributeDefinition overrides AttributeDefinition
			  of SQL parser, because Oracle has limited attribute 
			  characteristics (i.e. no reference scope check, no
			  attribute default, no collate clause)
			  see: https://docs.oracle.com/cd/B19306_01/server.102/b14200/statements_8001.htm#i2126584
Platform    : Java 7   
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rüti ZH, Switzerland
Created    : 30.06.2016, Simon Jutz
======================================================================*/
package ch.admin.bar.siard2.oracle.ddl;

import ch.enterag.sqlparser.SqlFactory;
import ch.enterag.sqlparser.ddl.AttributeDefinition;

/*====================================================================*/
public class OracleAttributeDefinition
	extends AttributeDefinition {

    /*------------------------------------------------------------------*/
	/**
	 * format the attribute definition
	 * @return the SQL string corresponding to a attribute definition
	 */
	@Override
	public String format() {
		String sDefinition = getAttributeName().quote() + sSP + getDataType().format();
		return sDefinition;
	}

	public OracleAttributeDefinition(SqlFactory sf) {
		super(sf);
	} /* constructor */
}
