/*======================================================================
MySqlLiteral implements the value translation from ISO SQL to MySql
Version     : $Id: $
Application : SIARD2
Description : MySqlLiteral implements the value translation 
			  from ISO SQL to MySql
Platform    : Java 7   
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rüti ZH, Switzerland
Created    : 30.11.2016, Simon Jutz
======================================================================*/
package ch.admin.bar.siard2.mysql.expression;

import ch.admin.bar.siard2.mysql.MySqlLiterals;
import ch.enterag.sqlparser.SqlFactory;
import ch.enterag.sqlparser.expression.Literal;

/* =============================================================================== */
/**
 * MySqlLiteral implements the value translation from ISO SQL:2008 to My Sql Server
 * @author Simon Jutz
 */
public class MySqlLiteral extends Literal {
	
	/* ------------------------------------------------------------------------ */
	/**
	 * Constructor with factory only to be called by factory
	 * @param sf factory
	 */
	public MySqlLiteral(SqlFactory sf) {
		super(sf);
	} /* constructor */
	
	/* ------------------------------------------------------------------------ */
	/**
	 * format the literal
	 * @return the MySql string corresponding to the fields of the literal
	 */
	@Override
	public String format() 
	{
		String sFormatted = "";
		
		if (getBytes() != null) {
			sFormatted = MySqlLiterals.formatBytesLiteral(getBytes());
		}
		
		return sFormatted;
	}  /* format */

} /* class MySqlLiteral */
