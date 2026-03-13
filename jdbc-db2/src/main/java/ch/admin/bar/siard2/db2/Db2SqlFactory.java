/*======================================================================
Db2SqlFactory overrides the BaseSqlFactory for the DB2-specific SQL 
parser classes.
Version     : $Id: $
Application : SIARD2
Description : Db2SqlFactory overrides the BaseSqlFactory for the DB2-specific 
              SQL parser classes.
Platform    : Java 7   
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rüti ZH, Switzerland
Created    : 04.11.2016, Hartwig Thomas
======================================================================*/
package ch.admin.bar.siard2.db2;

import ch.admin.bar.siard2.db2.datatype.Db2PredefinedType;
import ch.admin.bar.siard2.db2.ddl.*;
import ch.admin.bar.siard2.db2.expression.Db2NumericValueFunction;
import ch.admin.bar.siard2.db2.expression.Db2QuerySpecification;
import ch.admin.bar.siard2.db2.expression.Db2UnsignedLiteral;
import ch.admin.bar.siard2.db2.expression.Db2ValueExpressionPrimary;
import ch.admin.bar.siard2.jdbc.Db2Connection;
import ch.enterag.sqlparser.BaseSqlFactory;
import ch.enterag.sqlparser.SqlFactory;
import ch.enterag.sqlparser.datatype.PredefinedType;
import ch.enterag.sqlparser.ddl.*;
import ch.enterag.sqlparser.expression.NumericValueFunction;
import ch.enterag.sqlparser.expression.QuerySpecification;
import ch.enterag.sqlparser.expression.UnsignedLiteral;
import ch.enterag.sqlparser.expression.ValueExpressionPrimary;

/*====================================================================*/

/** MsSqlSqlFactory overrides the BaseSqlFactory for the MSSQL-specific
 * SQL parser classes. 
 * @author Hartwig Thomas
 */
public class Db2SqlFactory
        extends BaseSqlFactory
        implements SqlFactory {
    private Db2Connection _db2conn = null;

    public void setConnection(Db2Connection db2conn) {
        _db2conn = db2conn;
    }

    public Db2Connection getConnection() {
        return _db2conn;
    }

    @Override
    public CreateTypeStatement newCreateTypeStatement() {
        return new Db2CreateTypeStatement(this);
    } /* newCreateTypeStatement */

    @Override
    public DropSchemaStatement newDropSchemaStatement() {
        return new Db2DropSchemaStatement(this);
    } /* newDropSchemaStatement */

    @Override
    public DropTypeStatement newDropTypeStatement() {
        return new Db2DropTypeStatement(this);
    } /* newDropTypeStatement */

    @Override
    public DropTableStatement newDropTableStatement() {
        return new Db2DropTableStatement(this);
    } /* newDropTableStatement */

    @Override
    public DropViewStatement newDropViewStatement() {
        return new Db2DropViewStatement(this);
    } /* newDropViewStatement */

    @Override
    public QuerySpecification newQuerySpecification() {
        return new Db2QuerySpecification(this);
    } /* newQuerySpecification */

    @Override
    public PredefinedType newPredefinedType() {
        return new Db2PredefinedType(this);
    } /* newPredefinedType */

    @Override
    public UnsignedLiteral newUnsignedLiteral() {
        return new Db2UnsignedLiteral(this);
    } /* newUnsignedLiteral */

    @Override
    public ValueExpressionPrimary newValueExpressionPrimary() {
        return new Db2ValueExpressionPrimary(this);
    } /* newValueExpressionPrimary */

    @Override
    public NumericValueFunction newNumericValueFunction() {
        return new Db2NumericValueFunction(this);
    } /* newNumericValueFunction */

} /* Db2SqlFactory */
