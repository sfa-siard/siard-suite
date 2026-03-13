package ch.admin.bar.siard2.oracle;

import ch.admin.bar.siard2.jdbc.OracleConnection;
import ch.admin.bar.siard2.oracle.datatype.OracleDataType;
import ch.admin.bar.siard2.oracle.datatype.OraclePredefinedType;
import ch.admin.bar.siard2.oracle.ddl.*;
import ch.admin.bar.siard2.oracle.dml.OracleInsertStatement;
import ch.admin.bar.siard2.oracle.expression.*;
import ch.enterag.sqlparser.BaseSqlFactory;
import ch.enterag.sqlparser.SqlFactory;
import ch.enterag.sqlparser.datatype.DataType;
import ch.enterag.sqlparser.datatype.PredefinedType;
import ch.enterag.sqlparser.ddl.*;
import ch.enterag.sqlparser.dml.InsertStatement;
import ch.enterag.sqlparser.expression.*;


/** OracleSqlFactory overrides the BaseSqlFactory for the Oracle-specific
 * SQL parser classes. 
 * @author Simon Jutz
 */
public class OracleSqlFactory
        extends BaseSqlFactory
        implements SqlFactory {
    private OracleConnection _oracleConn = null;

    public void setConnection(OracleConnection oracleConn) {
        _oracleConn = oracleConn;
    }

    public OracleConnection getConnection() {
        return _oracleConn;
    }

    @Override
    public DataType newDataType() {
        return new OracleDataType(this);
    }

    @Override
    public Literal newLiteral() {
        return new OracleLiteral(this);
    }

    @Override
    public UnsignedLiteral newUnsignedLiteral() {
        return new OracleUnsignedLiteral(this);
    }

    @Override
    public PredefinedType newPredefinedType() {
        return new OraclePredefinedType(this);
    }

    @Override
    public CreateSchemaStatement newCreateSchemaStatement() {
        return new OracleCreateSchemaStatement(this);
    }

    @Override
    public DropSchemaStatement newDropSchemaStatement() {
        return new OracleDropSchemaStatement(this);
    }

    @Override
    public DropTableStatement newDropTableStatement() {
        return new OracleDropTableStatement(this);
    }

    @Override
    public DropViewStatement newDropViewStatement() {
        return new OracleDropViewStatement(this);
    }

    @Override
    public CreateTypeStatement newCreateTypeStatement() {
        return new OracleCreateTypeStatement(this);
    }

    @Override
    public AttributeDefinition newAttributeDefinition() {
        return new OracleAttributeDefinition(this);
    }

    @Override
    public MethodSpecification newMethodSpecification() {
        return new OracleMethodSpecification(this);
    }

    @Override
    public PartialMethodSpecification newPartialMethodSpecification() {
        return new OraclePartialMethodSpecification(this);
    }

    @Override
    public CreateFunctionStatement newCreateFunctionStatement() {
        return new OracleCreateFunctionStatement(this);
    }

    @Override
    public CreateProcedureStatement newCreateProcedureStatement() {
        return new OracleCreateProcedureStatement(this);
    }

    @Override
    public SqlParameterDeclaration newSqlParameterDeclaration() {
        return new OracleSqlParameterDeclaration(this);
    }

    @Override
    public ReturnsClause newReturnsClause() {
        return new OracleReturnsClause(this);
    }

    @Override
    public DropTypeStatement newDropTypeStatement() {
        return new OracleDropTypeStatement(this);
    }

    @Override
    public InsertStatement newInsertStatement() {
        return new OracleInsertStatement(this);
    }

    @Override
    public QueryExpressionBody newQueryExpressionBody() {
        return new OracleQueryExpressionBody(this);
    }

    @Override
    public ValueExpressionPrimary newValueExpressionPrimary() {
        return new OracleValueExpressionPrimary(this);
    }

    @Override
    public NumericValueFunction newNumericValueFunction() {
        return new OracleNumericValueFunction(this);
    }

}
