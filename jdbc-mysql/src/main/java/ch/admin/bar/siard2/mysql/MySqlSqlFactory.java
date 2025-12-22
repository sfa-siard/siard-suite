/*======================================================================
MySqlSqlFactory implements a wrapped MySql SqlFactory.
Version     : $Id: $
Application : SIARD2
Description : MySqlSqlFactory implements a wrapped MySql SqlFactory.
Platform    : Java 7   
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rüti ZH, Switzerland
Created    : 26.10.2016, Simon Jutz
======================================================================*/
package ch.admin.bar.siard2.mysql;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.datatype.*;
import ch.enterag.sqlparser.ddl.*;
import ch.enterag.sqlparser.expression.*;
import ch.admin.bar.siard2.mysql.datatype.*;
import ch.admin.bar.siard2.mysql.ddl.*;
import ch.admin.bar.siard2.mysql.expression.*;

/* =============================================================================== */
/**
 * MySqlSqlFactory implements a wrapped MySql SqlFactory
 * @author Simon Jutz
 */
public class MySqlSqlFactory extends BaseSqlFactory implements SqlFactory 
{
  
	/* ------------------------------------------------------------------------ */
	/**
	 * Returns a new wrapped predefined type
	 */
	@Override
	public PredefinedType newPredefinedType() {
		return new MySqlPredefinedType(this);
	} /* newPredefinedType */

	/* ------------------------------------------------------------------------ */
	/**
	 * Returns a new wrapped literal
	 */
	@Override
	public Literal newLiteral() {
		return new MySqlLiteral(this);
	} /* newLiteral */

	/* ------------------------------------------------------------------------ */
	/**
	 * Creates a new wrapped unsigned literal
	 */
	@Override
	public UnsignedLiteral newUnsignedLiteral() {
		return new MySqlUnsignedLiteral(this);
	} /* newUnsignedLiteral */

	/* ------------------------------------------------------------------------ */
	/**
	 * Creates a new wrapped value expression primary
	 */
	@Override
	public ValueExpressionPrimary newValueExpressionPrimary() {
		return new MySqlValueExpressionPrimary(this);
	} /* newValueExpressionPrimary */

	/* ------------------------------------------------------------------------ */
	/**
	 * Creates a new DROP SCHEMA statement 
	 */
	@Override
	public DropSchemaStatement newDropSchemaStatement() {
		return new MySqlDropSchemaStatement(this);
	} /* newDropSchemaStatement */

  /* ------------------------------------------------------------------------ */
  /** Creates a new DROP TABLE statement */
  @Override
  public DropTableStatement newDropTableStatement() 
  {
    return new MySqlDropTableStatement(this);
  } /* newDropTableStatement */
	
} /* class MySqlSqlFactory */