/*======================================================================
MySqlDropSchemaStatement overrides DropSchemaStatement because MySql
does not support drop behaviors such as CASCADE or RESTRICT
Application : SIARD2
Description : MySqlDropSchemaStatement overrides DropSchemaStatement because 
MySql does not support drop behaviors such as CASCADE or RESTRICT
Platform    : Java 7   
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rüti ZH, Switzerland
Created    : 02.11.2016, Simon Jutz
======================================================================*/
package ch.admin.bar.siard2.mysql.ddl;

import ch.enterag.sqlparser.K;
import ch.enterag.sqlparser.SqlFactory;
import ch.enterag.sqlparser.ddl.DropSchemaStatement;
import ch.enterag.sqlparser.ddl.enums.DropBehavior;

/**
 * MySqlDropSchemaStatement overrides DropSchemaStatement because MySql
 * does not support drop behaviors such as CASCADE or RESTRICT
 * @author Simon Jutz
 */
public class MySqlDropSchemaStatement extends DropSchemaStatement 
{

	/* ------------------------------------------------------------------------ */
	public MySqlDropSchemaStatement(SqlFactory sf) 
	{
		super(sf);
	} /* constructor */

	/* ------------------------------------------------------------------------ */
	@Override
	public String format() 
	{
		if (getDropBehavior() == DropBehavior.CASCADE)
			throw new IllegalArgumentException("Schema drop behavior CASCADE not supported by MSSQL!");
		String sStatement = K.DROP.getKeyword() + sSP + K.SCHEMA.getKeyword() + sSP + getSchemaName().format(); 
		return sStatement;
	} /* format */
	
} /* class MySqlDropSchemaStatement */
