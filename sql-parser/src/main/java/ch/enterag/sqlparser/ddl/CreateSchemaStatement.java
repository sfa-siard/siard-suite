package ch.enterag.sqlparser.ddl;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.generated.*;
import ch.enterag.sqlparser.identifier.*;
import ch.enterag.utils.logging.*;

public class CreateSchemaStatement
  extends SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(CreateSchemaStatement.class.getName());
  
  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  private class CssVisitor extends EnhancedSqlBaseVisitor<CreateSchemaStatement>
  {
    @Override
    public CreateSchemaStatement visitSchemaName(SqlParser.SchemaNameContext ctx)
    {
      setSchemaName(ctx,getSchemaName());
      return CreateSchemaStatement.this;
    }
    @Override
    public CreateSchemaStatement visitAuthorizationName(SqlParser.AuthorizationNameContext ctx)
    {
      setAuthorizationName(ctx,getAuthorizationName());
      return CreateSchemaStatement.this;
    }
  }

  private CssVisitor _visitor = new CssVisitor();
  private CssVisitor getVisitor() { return _visitor; }
  
  private SchemaId _sidSchemaName = new SchemaId();
  public SchemaId getSchemaName() { return _sidSchemaName; }
  private void setSchemaName(SchemaId sidSchemaName) { _sidSchemaName = sidSchemaName; }
  
  private Identifier _idAuthorizationName = new Identifier();
  public Identifier getAuthorizationName() { return _idAuthorizationName; }
  private void setAuthorizationName(Identifier idAuthorizationName) { _idAuthorizationName = idAuthorizationName; }
  
  /*------------------------------------------------------------------*/
  /** format the create schema statement.
   * @return the SQL string corresponding to the fields of the create schema statement.
   */
  @Override
  public String format()
  {
    String sStatement = K.CREATE.getKeyword() + sSP + K.SCHEMA.getKeyword() + sSP + getSchemaName().format(); 
    if (getAuthorizationName().isSet())
      sStatement = sStatement + sSP + K.AUTHORIZATION.getKeyword() + sSP + 
        getAuthorizationName().format();
    return sStatement;
  } /* format */
  
  /*------------------------------------------------------------------*/
  /** parse the create schema statement from the parsing tree context.
  * @param ctx parsing context (tree).
  * @throws NullPointerException if no parsing tree is available. 
  */
 public void parse(SqlParser.CreateSchemaStatementContext ctx)
 {
   setContext(ctx);
   getVisitor().visit(getContext());
 } /* parse */
 
 /*------------------------------------------------------------------*/
 /** parse the create schema statement from SQL.
  * @param sSql SQL.
  */
 @Override
 public void parse(String sSql)
 {
   setParser(newSqlParser(sSql));
   parse(getParser().createSchemaStatement());
 } /* parse */
 
  /*------------------------------------------------------------------*/
  /** initialize a create schema statement.
   * @param sidSchemaName name of schema to be created.
   * @param idAuthorizationName user or role name.
   */
  public void initialize(SchemaId sidSchemaName, Identifier idAuthorizationName)
  {
    _il.enter(sidSchemaName, idAuthorizationName);
    setSchemaName(sidSchemaName);
    setAuthorizationName(idAuthorizationName);
    _il.exit();
  } /* initialize */
  
  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public CreateSchemaStatement(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class CreateSchemaStatement */
