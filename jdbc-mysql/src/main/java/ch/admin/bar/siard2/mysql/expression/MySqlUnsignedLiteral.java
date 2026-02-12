/*======================================================================
MySqlUnsignedLiteral implements the value translation from ISO SQL to MySql
Version     : $Id: $
Application : SIARD2
Description : MySqlUnsignedLiteral implements the value translation 
			  from ISO SQL to MySql
Platform    : Java 7   
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rüti ZH, Switzerland
Created    : 30.11.2016, Simon Jutz
======================================================================*/
package ch.admin.bar.siard2.mysql.expression;

import ch.admin.bar.siard2.mysql.MySqlLiterals;
import ch.enterag.sqlparser.SqlFactory;
import ch.enterag.sqlparser.expression.UnsignedLiteral;

/* =============================================================================== */
/**
 * MySqlLiteral implements the value translation from ISO SQL:2008 to MySql Server
 * @author Simon Jutz
 */
public class MySqlUnsignedLiteral extends UnsignedLiteral {

	/* ------------------------------------------------------------------------ */
	/**
	 * Constructor with factory only to be called by factory
	 * @param sf factory
	 */
	public MySqlUnsignedLiteral(SqlFactory sf) {
		super(sf);
	}

	/* ------------------------------------------------------------------------ */
	
	/**
	 * formats the unsigned literal value
	 */
	@Override
	public String format() {
		String sExpression = null;
		
		if(getInterval() != null) {
			sExpression = MySqlLiterals.formatIntervalLiteral(getInterval()); // no interval data type in MySql
		} else {
			sExpression = super.format(); 
		}
		
		return sExpression;
	} /* format */
	
} /* class MySqlUnsignedLiteral */
