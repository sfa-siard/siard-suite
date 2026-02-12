package ch.admin.bar.siard2.oracle;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.datatype.*;
import ch.enterag.sqlparser.expression.*;
import ch.enterag.sqlparser.ddl.*;
import ch.enterag.sqlparser.dml.*;
import ch.admin.bar.siard2.jdbc.*;
import ch.admin.bar.siard2.oracle.datatype.*;
import ch.admin.bar.siard2.oracle.expression.*;
import ch.admin.bar.siard2.oracle.ddl.*;
import ch.admin.bar.siard2.oracle.dml.*;

/*====================================================================*/
/** OracleSqlFactory overrides the BaseSqlFactory for the Oracle-specific
 * SQL parser classes. 
 * @author Simon Jutz
 */
public class OracleSqlFactory
	extends BaseSqlFactory
	implements SqlFactory
{
  private  OracleConnection _oracleConn = null;
  public void setConnection(OracleConnection oracleConn) { _oracleConn = oracleConn; }
  public OracleConnection getConnection() { return _oracleConn; }
  
	@Override 
	public DataType newDataType()
	{
		return new OracleDataType(this);
	} /* newDataType */  
 
	@Override 
	public Literal newLiteral()
	{
		return new OracleLiteral(this);
	} /* newLiteral */
	
	@Override 
	public UnsignedLiteral newUnsignedLiteral()
	{
		return new OracleUnsignedLiteral(this);
	} /* newUnsignedLiteral */
	  
	@Override
	public PredefinedType newPredefinedType()
	{
		return new OraclePredefinedType(this);
	} /* newPredefinedType */
	
	@Override
	public CreateSchemaStatement newCreateSchemaStatement()
	{
		return new OracleCreateSchemaStatement(this);
	} /* newCreateSchemaStatement */
	  
	@Override
	public DropSchemaStatement newDropSchemaStatement()
	{
	    return new OracleDropSchemaStatement(this);
	} /* newDropSchemaStatement */
	 
	@Override
	public DropTableStatement newDropTableStatement()
	{
		return new OracleDropTableStatement(this);
	} /* newDropTableStatement */

  @Override
  public DropViewStatement newDropViewStatement()
  {
    return new OracleDropViewStatement(this);
  } /* newDropViewStatement */
  
	@Override
	public CreateTypeStatement newCreateTypeStatement()
	{
		return new OracleCreateTypeStatement(this);
	} /* newCreateTypeStatement */
	
	@Override
	public AttributeDefinition newAttributeDefinition()
	{
		return new OracleAttributeDefinition(this);
	} /* newAttributeDefinition */
	
	@Override
	public MethodSpecification newMethodSpecification()
	{
		return new OracleMethodSpecification(this);
	} /* newMethodSpecification */

	@Override
	public PartialMethodSpecification newPartialMethodSpecification()
	{
		return new OraclePartialMethodSpecification(this);
	} /* newPartialMethodSpecification */
	
	@Override
	public CreateFunctionStatement newCreateFunctionStatement()
	{
		return new OracleCreateFunctionStatement(this);
	} /* newCreateFunctionStatement */
	
	@Override
	public CreateProcedureStatement newCreateProcedureStatement()
	{
		return new OracleCreateProcedureStatement(this);
	} /* newCreateProcedureStatement */
	
	@Override
	public SqlParameterDeclaration newSqlParameterDeclaration() 
	{
		return new OracleSqlParameterDeclaration(this);
	} /* newSqlParameterDeclaration */
	
	@Override
	public ReturnsClause newReturnsClause()
	{
		return new OracleReturnsClause(this);
	} /* newReturnsClause */
	
	@Override
	public DropTypeStatement newDropTypeStatement()
	{
		return new OracleDropTypeStatement(this);
	} /* newDropTypeStatement */
	
  @Override
  public InsertStatement newInsertStatement()
  {
    return new OracleInsertStatement(this);
  } /* newQueryExpressionBody */
      
	@Override
	public QueryExpressionBody newQueryExpressionBody()
	{
	  return new OracleQueryExpressionBody(this);
	} /* newQueryExpressionBody */
	    
  @Override
  public ValueExpressionPrimary newValueExpressionPrimary()
  {
    return new OracleValueExpressionPrimary(this);
  } /* newValueExpressionPrimary */
  
  @Override
  public NumericValueFunction newNumericValueFunction()
  {
    return new OracleNumericValueFunction(this);
  } /* newNumericValueFunction */
      
} /* class OracleSqlFactory */
