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

import ch.admin.bar.siard2.mysql.datatype.MySqlPredefinedType;
import ch.admin.bar.siard2.mysql.ddl.MySqlDropSchemaStatement;
import ch.admin.bar.siard2.mysql.ddl.MySqlDropTableStatement;
import ch.admin.bar.siard2.mysql.expression.MySqlLiteral;
import ch.admin.bar.siard2.mysql.expression.MySqlUnsignedLiteral;
import ch.admin.bar.siard2.mysql.expression.MySqlValueExpressionPrimary;
import ch.enterag.sqlparser.BaseSqlFactory;
import ch.enterag.sqlparser.SqlFactory;
import ch.enterag.sqlparser.datatype.PredefinedType;
import ch.enterag.sqlparser.ddl.DropSchemaStatement;
import ch.enterag.sqlparser.ddl.DropTableStatement;
import ch.enterag.sqlparser.expression.Literal;
import ch.enterag.sqlparser.expression.UnsignedLiteral;
import ch.enterag.sqlparser.expression.ValueExpressionPrimary;


/**
 * MySqlSqlFactory implements a wrapped MySql SqlFactory
 * @author Simon Jutz
 */
public class MySqlSqlFactory extends BaseSqlFactory implements SqlFactory {


    /**
     * Returns a new wrapped predefined type
     */
    @Override
    public PredefinedType newPredefinedType() {
        return new MySqlPredefinedType(this);
    }


    /**
     * Returns a new wrapped literal
     */
    @Override
    public Literal newLiteral() {
        return new MySqlLiteral(this);
    }


    /**
     * Creates a new wrapped unsigned literal
     */
    @Override
    public UnsignedLiteral newUnsignedLiteral() {
        return new MySqlUnsignedLiteral(this);
    }


    /**
     * Creates a new wrapped value expression primary
     */
    @Override
    public ValueExpressionPrimary newValueExpressionPrimary() {
        return new MySqlValueExpressionPrimary(this);
    }


    /**
     * Creates a new DROP SCHEMA statement
     */
    @Override
    public DropSchemaStatement newDropSchemaStatement() {
        return new MySqlDropSchemaStatement(this);
    }


    /** Creates a new DROP TABLE statement */
    @Override
    public DropTableStatement newDropTableStatement() {
        return new MySqlDropTableStatement(this);
    }

}
