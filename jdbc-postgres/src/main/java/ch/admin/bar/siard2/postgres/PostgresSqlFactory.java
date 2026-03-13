/*======================================================================
PostgresSqlFactory implements a wrapped SqlFactory for Postgres.
Application : SIARD2
Description : PostgresSqlFactory implements a wrapped SqlFactory for Postgres.
Platform    : Java 17   
------------------------------------------------------------------------
Copyright  : 2019, Swiss Federal Archives, Berne, Switzerland
License    : CDDL 1.0
Created    : 25.07.2017, Hartwig Thomas, Enter AG, Rüti ZH, Switzerland
======================================================================*/
package ch.admin.bar.siard2.postgres;

import ch.admin.bar.siard2.jdbc.PostgresConnection;
import ch.admin.bar.siard2.postgres.datatype.PostgresIntervalQualifier;
import ch.admin.bar.siard2.postgres.datatype.PostgresPredefinedType;
import ch.admin.bar.siard2.postgres.ddl.PostgresCreateTypeStatement;
import ch.admin.bar.siard2.postgres.ddl.PostgresDropTypeStatement;
import ch.admin.bar.siard2.postgres.expression.PostgresUnsignedLiteral;
import ch.admin.bar.siard2.postgres.expression.PostgresValueExpressionPrimary;
import ch.enterag.sqlparser.BaseSqlFactory;
import ch.enterag.sqlparser.SqlFactory;
import ch.enterag.sqlparser.datatype.IntervalQualifier;
import ch.enterag.sqlparser.datatype.PredefinedType;
import ch.enterag.sqlparser.ddl.CreateTypeStatement;
import ch.enterag.sqlparser.ddl.DropTypeStatement;
import ch.enterag.sqlparser.expression.UnsignedLiteral;
import ch.enterag.sqlparser.expression.ValueExpressionPrimary;

/* =============================================================================== */

/** PostgresSqlFactory implements a wrapped SqlFactory for Postgres.
 * @author Hartwig Thomas
 */
public class PostgresSqlFactory extends BaseSqlFactory implements SqlFactory {
    private PostgresConnection _pgConn = null;

    public void setConnection(PostgresConnection pgConn) {
        _pgConn = pgConn;
    }

    public PostgresConnection getConnection() {
        return _pgConn;
    }

    @Override
    public PredefinedType newPredefinedType() {
        return new PostgresPredefinedType(this);
    } /* newPredefinedType */

    @Override
    public IntervalQualifier newIntervalQualifier() {
        return new PostgresIntervalQualifier(this);
    } /* newIntervalQualifier */

    @Override
    public CreateTypeStatement newCreateTypeStatement() {
        return new PostgresCreateTypeStatement(this);
    } /* newCreateTypeStatement */

    @Override
    public DropTypeStatement newDropTypeStatement() {
        return new PostgresDropTypeStatement(this);
    } /* newDropTypeStatement */

    @Override
    public ValueExpressionPrimary newValueExpressionPrimary() {
        return new PostgresValueExpressionPrimary(this);
    } /* newValueExpressionPrimary */

    @Override
    public UnsignedLiteral newUnsignedLiteral() {
        return new PostgresUnsignedLiteral(this);
    } /* newUnsignedLiteral */

} /* class PostgresSqlFactory */
