package ch.enterag.sqlparser.ddl;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.generated.*;
import ch.enterag.sqlparser.identifier.*;
import ch.enterag.utils.logging.*;

public class AlterTypeStatement
  extends SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(AlterTypeStatement.class.getName());
  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  private class AtsVisitor extends EnhancedSqlBaseVisitor<AlterTypeStatement>
  {
    @Override
    public AlterTypeStatement visitUdtName(SqlParser.UdtNameContext ctx)
    {
      setUdtName(ctx,getTypeName());
      return AlterTypeStatement.this;
    }
    @Override
    public AlterTypeStatement visitAddAttributeDefinition(SqlParser.AddAttributeDefinitionContext ctx)
    {
      setAttributeDefinition(getSqlFactory().newAttributeDefinition());
      getAttributeDefinition().parse(ctx.attributeDefinition());
      return AlterTypeStatement.this;
    }
    @Override
    public AlterTypeStatement visitDropAttributeDefinition(SqlParser.DropAttributeDefinitionContext ctx)
    {
      setAttributeName(ctx.attributeName(),getAttributeName());
      return AlterTypeStatement.this;
    }
    @Override
    public AlterTypeStatement visitAddMethodSpecification(SqlParser.AddMethodSpecificationContext ctx)
    {
      setMethodSpecification(getSqlFactory().newMethodSpecification());
      getMethodSpecification().parse(ctx.methodSpecification());
      return AlterTypeStatement.this;
    }
    @Override
    public AlterTypeStatement visitDropMethodSpecification(SqlParser.DropMethodSpecificationContext ctx)
    {
      setMethodDesignator(getSqlFactory().newMethodDesignator());
      getMethodDesignator().parse(ctx.methodDesignator());
      return AlterTypeStatement.this;
    }
  }
  /*==================================================================*/
  
  private AtsVisitor _visitor = new AtsVisitor();
  private AtsVisitor getVisitor() { return _visitor; }
  
  /* type name */
  private QualifiedId _qTypeName = new QualifiedId();
  public QualifiedId getTypeName() { return _qTypeName; }
  private void setTypeName(QualifiedId qTypeName) { _qTypeName = qTypeName; }
  
  /* add attribute definition */
  private AttributeDefinition _ad = null;
  public AttributeDefinition getAttributeDefinition() { return _ad; }
  public void setAttributeDefinition(AttributeDefinition ad) { _ad = ad; }
  
  /* drop attribute definition */
  private Identifier _idAttributeName = null;
  public Identifier getAttributeName() { return _idAttributeName; }
  private void setAttributeName(Identifier idAttributeName) { _idAttributeName = idAttributeName; }
  
  /* add method specification */
  private MethodSpecification _ms = null;
  public MethodSpecification getMethodSpecification() { return _ms; }
  public void setMethodSpecification(MethodSpecification ms) { _ms = ms; }
  
  /* drop method specification */
  private MethodDesignator _md = null;
  public MethodDesignator getMethodDesignator() { return _md; }
  public void setMethodDesignator(MethodDesignator md) { _md = md; }
  
  /*------------------------------------------------------------------*/
  /** format the alter type statement.
   * @return the SQL string corresponding to the fields of the alter 
   *   type statement.
   */
  @Override
  public String format()
  {
    String sStatement = K.ALTER.getKeyword() + sSP + K.TYPE.getKeyword() + sSP + getTypeName().format();
    if (getAttributeDefinition() != null)
      sStatement = sStatement + sSP + K.ADD.getKeyword() + sSP + K.ATTRIBUTE.getKeyword() + sSP +
        getAttributeDefinition().format();
    else if (getAttributeName() != null)
      sStatement = sStatement + sSP + K.DROP.getKeyword() + sSP + K.ATTRIBUTE.getKeyword() + sSP +
        getAttributeName().format() + sSP + K.RESTRICT.getKeyword();
    else if (getMethodSpecification() != null)
      sStatement = sStatement + sSP + K.ADD.getKeyword() + getMethodSpecification().format();
    else if (getMethodDesignator() != null)
      sStatement = sStatement + sSP + K.DROP.getKeyword() + getMethodDesignator().format() + sSP + K.RESTRICT;
    return sStatement;
  } /* format */
  
  /*------------------------------------------------------------------*/
  /** parse the alter type statement  from the parsing tree context.
   * @param ctx parsing context (tree).
   * @throws NullPointerException if no parsing tree is available. 
   */
  public void parse(SqlParser.AlterTypeStatementContext ctx)
  {
    setContext(ctx);
    getVisitor().visit(getContext());
  } /* parse */
  
  /*------------------------------------------------------------------*/
  /** parse the alter type statement from SQL.
   * @param sSql SQL.
   */
  @Override
  public void parse(String sSql)
  {
    setParser(newSqlParser(sSql));
    parse(getParser().alterTypeStatement());
  } /* parse */
  
  /*------------------------------------------------------------------*/
  /** initialize an alter type statement.
   * @param qTypeName name of type to be altered (not null!).
   * @param ad attribute definition to be added (or null).
   * @param idAttributeName name of attribute to be dropped (or null).
   * @param ms method specification to be added (or null).
   * @param md designator of method to be dropped.
   */
  public void initialize(
    QualifiedId qTypeName,
    AttributeDefinition ad,
    Identifier idAttributeName,
    MethodSpecification ms,
    MethodDesignator md)    
  {
    _il.enter(qTypeName,ad,idAttributeName,ms,md);
    setTypeName(qTypeName);
    setAttributeDefinition(ad);
    setAttributeName(idAttributeName);
    setMethodSpecification(ms);
    setMethodDesignator(md);
    _il.exit();
  } /* initialize */
  
  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public AlterTypeStatement(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class AlterTypeStatement */
