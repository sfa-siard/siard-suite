package ch.enterag.sqlparser.ddl;

import java.util.*;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.datatype.*;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.ddl.enums.*;
import ch.enterag.sqlparser.generated.*;
import ch.enterag.sqlparser.identifier.*;
import ch.enterag.utils.logging.*;

public class CreateTypeStatement
  extends SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(CreateTypeStatement.class.getName());
  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  private class CtsVisitor extends EnhancedSqlBaseVisitor<CreateTypeStatement>
  {
    @Override
    public CreateTypeStatement visitUdtName(SqlParser.UdtNameContext ctx)
    {
      setUdtName(ctx,getTypeName());
      return CreateTypeStatement.this;
    }
    @Override
    public CreateTypeStatement visitSubTypeClause(SqlParser.SubTypeClauseContext ctx)
    {
      setUdtName(ctx.udtName(),getSuperType());
      return CreateTypeStatement.this;
    }
    @Override
    public CreateTypeStatement visitPredefinedType(SqlParser.PredefinedTypeContext ctx)
    {
      setDistinctBaseType(getSqlFactory().newPredefinedType());
      getDistinctBaseType().parse(ctx);
      return CreateTypeStatement.this;
    }
    @Override
    public CreateTypeStatement visitAttributeDefinition(SqlParser.AttributeDefinitionContext ctx)
    {
      AttributeDefinition ad = getSqlFactory().newAttributeDefinition();
      ad.parse(ctx);
      getAttributeDefinitions().add(ad);
      return CreateTypeStatement.this;
    }
    @Override
    public CreateTypeStatement visitInstantiability(SqlParser.InstantiabilityContext ctx)
    {
      setInstantiability(getInstantiability(ctx));
      return CreateTypeStatement.this;
    }
    @Override
    public CreateTypeStatement visitFinality(SqlParser.FinalityContext ctx)
    {
      setFinality(getFinality(ctx));
      return CreateTypeStatement.this;
    }
    @Override
    public CreateTypeStatement visitUserDefinedRepresentation(SqlParser.UserDefinedRepresentationContext ctx)
    {
      setRefUsing(getSqlFactory().newPredefinedType());
      getRefUsing().parse(ctx.predefinedType());
      return CreateTypeStatement.this;
    }
    @Override
    public CreateTypeStatement visitAttributeName(SqlParser.AttributeNameContext ctx)
    {
      addAttributeName(ctx,getRefFromAttributes());
      return CreateTypeStatement.this;
    }
    @Override 
    public CreateTypeStatement visitSystemGeneratedRepresentation(SqlParser.SystemGeneratedRepresentationContext ctx)
    {
      setRefSystemGenerated(true);
      return CreateTypeStatement.this;
    }
    @Override
    public CreateTypeStatement visitCastToRef(SqlParser.CastToRefContext ctx)
    {
      setIdentifier(ctx.castIdentifier().IDENTIFIER(), getCastToRef());
      return CreateTypeStatement.this;
    }
    @Override
    public CreateTypeStatement visitCastToType(SqlParser.CastToTypeContext ctx)
    {
      setIdentifier(ctx.castIdentifier().IDENTIFIER(), getCastToType());
      return CreateTypeStatement.this;
    }
    @Override
    public CreateTypeStatement visitCastToDistinct(SqlParser.CastToDistinctContext ctx)
    {
      setIdentifier(ctx.castIdentifier().IDENTIFIER(), getCastToDistinct());
      return CreateTypeStatement.this;
    }
    @Override
    public CreateTypeStatement visitCastToSource(SqlParser.CastToSourceContext ctx)
    {
      setIdentifier(ctx.castIdentifier().IDENTIFIER(),getCastToSource());
      return CreateTypeStatement.this;
    }
    @Override
    public CreateTypeStatement visitMethodSpecification(SqlParser.MethodSpecificationContext ctx)
    {
      MethodSpecification ms = getSqlFactory().newMethodSpecification();
      ms.parse(ctx);
      getMethodSpecifications().add(ms);
      return CreateTypeStatement.this;
    }
  }
  /*==================================================================*/

  private CtsVisitor _visitor = new CtsVisitor();
  private CtsVisitor getVisitor() { return _visitor; }
  
  private QualifiedId _qTypeName = new QualifiedId();
  public QualifiedId getTypeName() { return _qTypeName; }
  private void setTypeName(QualifiedId qTypeName) { _qTypeName = qTypeName; }
  
  private QualifiedId _qSuperType = new QualifiedId();
  public QualifiedId getSuperType() { return _qSuperType; }
  private void setSuperType(QualifiedId qSuperType) { _qSuperType = qSuperType; }
  
  private PredefinedType _pdtDistinctBaseType = null;
  public PredefinedType getDistinctBaseType() { return _pdtDistinctBaseType; }
  public void setDistinctBaseType(PredefinedType pdtDistinctBaseType) { _pdtDistinctBaseType = pdtDistinctBaseType; }
  
  private List<AttributeDefinition> _listAttributeDefinitions = new ArrayList<AttributeDefinition>();
  public List<AttributeDefinition> getAttributeDefinitions() { return _listAttributeDefinitions; }
  private void setAttributeDefinitions(List<AttributeDefinition> listAttributeDefinitions) { _listAttributeDefinitions = listAttributeDefinitions; }
  
  private Instantiability _instantiability = null;
  public Instantiability getInstantiability() { return _instantiability; }
  public void setInstantiability(Instantiability instantiability) { _instantiability = instantiability; }
  
  private Finality _finality = null;
  public Finality getFinality() { return _finality; }
  public void setFinality(Finality finality) { _finality = finality; }
  
  private PredefinedType _pdtRefUsing = null;
  public PredefinedType getRefUsing() { return _pdtRefUsing; }
  public void setRefUsing(PredefinedType pdtRefUsing) { _pdtRefUsing = pdtRefUsing; }
  
  private IdList _ilRefFromAttributes = new IdList();
  public IdList getRefFromAttributes() { return _ilRefFromAttributes; }
  private void setRefFromAttributes(IdList ilRefFromAttributes) { _ilRefFromAttributes = ilRefFromAttributes; }
  
  private boolean _bRefSystemGenerated = false;
  public boolean getRefSystemGenerated() { return _bRefSystemGenerated; }
  public void setRefSystemGenerated(boolean bRefSystemGenerated) { _bRefSystemGenerated = bRefSystemGenerated; }
  
  private Identifier _idCastToRef = new Identifier();
  public Identifier getCastToRef() { return _idCastToRef; }
  private void setCastToRef(Identifier idCastToRef) { _idCastToRef = idCastToRef; }

  private Identifier _idCastToType = new Identifier();
  public Identifier getCastToType() { return _idCastToType; }
  private void setCastToType(Identifier idCastToType) { _idCastToType = idCastToType; }
  
  private Identifier _idCastToDistinct = new Identifier();
  public Identifier getCastToDistinct() { return _idCastToDistinct; }
  private void setCastToDistinct(Identifier idCastToDistinct) { _idCastToDistinct = idCastToDistinct; }
  
  private Identifier _idCastToSource = new Identifier();
  public Identifier getCastToSource() { return _idCastToSource; }
  private void setCastToSource(Identifier idCastToSource) { _idCastToSource = idCastToSource; }
  
  private List<MethodSpecification> _listMethodSpecifications = new ArrayList<MethodSpecification>();
  public List<MethodSpecification> getMethodSpecifications() { return _listMethodSpecifications; }
  private void setMethodSpecifications(List<MethodSpecification> listMethodSpecifications) { _listMethodSpecifications = listMethodSpecifications; }

  /*------------------------------------------------------------------*/
  /** format the attribute definitions list.
   * @return formatted list.
   */
  protected String formatAttributeDefinitions()
  {
    String sDefinitions = sLEFT_PAREN;
    for (int iDefinition = 0; iDefinition < getAttributeDefinitions().size(); iDefinition++)
    {
      if (iDefinition > 0)
        sDefinitions = sDefinitions + sCOMMA;
      sDefinitions = sDefinitions + sNEW_LINE + sINDENT + getAttributeDefinitions().get(iDefinition).format();
    }
    sDefinitions = sDefinitions + sNEW_LINE + sRIGHT_PAREN;
    return sDefinitions;
  } /* formatAttributeDefinitions */
  
  /*------------------------------------------------------------------*/
  /** format the method specifications list.
   * @return formatted list.
   */
  protected String formatMethodSpecifications()
  {
    String sSpecification = "";
    for (int iSpecification = 0; iSpecification < getMethodSpecifications().size(); iSpecification++)
    {
      if (iSpecification > 0)
        sSpecification = sSpecification + sCOMMA;
      sSpecification = sSpecification + sNEW_LINE + getMethodSpecifications().get(iSpecification).format(); 
    }
    return sSpecification;
  } /* formatMethodSpecifications **
  
  /*------------------------------------------------------------------*/
  /** format the create type statement.
   * @return the SQL string corresponding to the fields of the create 
   *   type statement.
   */
  @Override
  public String format()
  {
    String sStatement = K.CREATE.getKeyword() + sSP + K.TYPE.getKeyword() + sSP + 
      getTypeName().format();
    if (getSuperType().isSet())
      sStatement = sStatement + sSP + K.UNDER.getKeyword() + sSP + getSuperType().format();
    if (getDistinctBaseType() != null)
      sStatement = sStatement + sSP + K.AS.getKeyword() + sSP + getDistinctBaseType().format();
    if (getAttributeDefinitions().size() > 0)
      sStatement = sStatement + sSP + K.AS.getKeyword() + formatAttributeDefinitions();
    if (getInstantiability() != null)
      sStatement = sStatement + sSP + getInstantiability().getKeywords();
    if (getFinality() != null)
      sStatement = sStatement + sSP + getFinality().getKeywords();
    if (getRefUsing() != null)
      sStatement = sStatement + sSP + K.REF.getKeyword() + sSP + K.USING.getKeyword() + sSP + 
        getRefUsing().format();
    else if (getRefFromAttributes().isSet())
      sStatement = sStatement + sSP + K.REF.getKeyword() + sSP + K.FROM.getKeyword() + 
        sLEFT_PAREN + getRefFromAttributes().format() + sRIGHT_PAREN;
    else if (getRefSystemGenerated())
      sStatement = sStatement + sSP + K.REF.getKeyword() + sSP + K.IS.getKeyword() + sSP + 
        K.SYSTEM.getKeyword() + sSP + K.GENERATED.getKeyword();
    if (getCastToRef().isSet())
      sStatement = sStatement + sSP + K.CAST.getKeyword() + sLEFT_PAREN +
      K.SOURCE.getKeyword() + sSP + K.AS.getKeyword() + sSP + K.REF.getKeyword() +
      sRIGHT_PAREN + sSP + K.WITH.getKeyword() + sSP + getCastToRef().format();
    if (getCastToType().isSet())
      sStatement = sStatement + sSP + K.CAST.getKeyword() + sLEFT_PAREN +
      K.REF.getKeyword() + sSP + K.AS.getKeyword() + sSP + K.SOURCE.getKeyword() +
      sRIGHT_PAREN + sSP + K.WITH.getKeyword() + sSP + getCastToRef().format();
    if (getCastToDistinct().isSet())
      sStatement = sStatement + sSP + K.CAST.getKeyword() + sLEFT_PAREN +
      K.SOURCE.getKeyword() + sSP + K.AS.getKeyword() + sSP + K.DISTINCT.getKeyword() +
      sRIGHT_PAREN + sSP + K.WITH.getKeyword() + sSP + getCastToRef().format();
    if (getCastToSource().isSet())
      sStatement = sStatement + sSP + K.CAST.getKeyword() + sLEFT_PAREN +
      K.DISTINCT.getKeyword() + sSP + K.AS.getKeyword() + sSP + K.SOURCE.getKeyword() +
      sRIGHT_PAREN + sSP + K.WITH.getKeyword() + sSP + getCastToRef().format();
    if (getMethodSpecifications().size() > 0)
      sStatement = sStatement + formatMethodSpecifications(); 
    return sStatement;
  } /* format */
  
  /*------------------------------------------------------------------*/
  /** parse the create type statement from the parsing tree context.
   * @param ctx parsing context (tree).
   * @throws NullPointerException if no parsing tree is available. 
   */
  public void parse(SqlParser.CreateTypeStatementContext ctx)
  {
    setContext(ctx);
    getVisitor().visit(getContext());
  } /* parse */
   
  /*------------------------------------------------------------------*/
  /** parse the create type statement from SQL.
   * @param sSql SQL.
   */
  @Override
  public void parse(String sSql)
  {
    setParser(newSqlParser(sSql));
    SqlParser.CreateTypeStatementContext ctx = null; 
    try { ctx = getParser().createTypeStatement(); }
    catch(Exception e)
    {
      setParser(newSqlParser2(sSql));
      ctx = getParser().createTypeStatement();
    }
    parse(ctx);
  } /* parse */
   
  /*------------------------------------------------------------------*/
  /** initialize a create type statement.
   * @param qTypeName name of type to be created (not null!).
   * @param qSuperType name of super type (not null).
   * @param pdtDistinctBaseType base type of a DISTINCT type (or null).
   * @param listAttributeDefinitions list of attribute definitions of UDT (not null).
   * @param instantiability INSTANTIABILITY option (or null).
   * @param finality FINAL option (or null).
   * @param pdtRefUsing REF USING option type (or null).
   * @param ilRefFromAttributes REF FROM attribute names (not null!)
   * @param bRefSystemGenerated true, for REF IS SYSTEM GENERATED.
   * @param idCastToRef CAST(SOURCE AS REF) WITH identifier (not null!)
   * @param idCastToType CAST(REF AS SOURCE) WITH identifier (not null!)
   * @param idCastToDistinct CAST(SOURCE AS DISTINCT) WITH identifier (not null!)
   * @param idCastToSource CAST(DISTINCT AS SOURCE) WITH identifier (not null!)
   * @param listMethodSpecifications list of method specifications (not null) associated with type.
   */
  public void initialize(
    QualifiedId qTypeName,
    QualifiedId qSuperType,
    PredefinedType pdtDistinctBaseType,
    List<AttributeDefinition> listAttributeDefinitions,
    Instantiability instantiability,
    Finality finality,
    PredefinedType pdtRefUsing,
    IdList ilRefFromAttributes,
    boolean bRefSystemGenerated,
    Identifier idCastToRef,
    Identifier idCastToType,
    Identifier idCastToDistinct,
    Identifier idCastToSource,
    List<MethodSpecification> listMethodSpecifications
  )
  {
    _il.enter(qTypeName,qSuperType,pdtDistinctBaseType,listAttributeDefinitions,
      instantiability, finality, 
      pdtRefUsing, ilRefFromAttributes, String.valueOf(bRefSystemGenerated),
      idCastToRef, idCastToType, idCastToDistinct, idCastToSource,
      listMethodSpecifications);
    setTypeName(qTypeName);
    setSuperType(qSuperType);
    setDistinctBaseType(pdtDistinctBaseType);
    setAttributeDefinitions(listAttributeDefinitions);
    setInstantiability(instantiability);
    setFinality(finality);
    setRefUsing(pdtRefUsing);
    setRefFromAttributes(ilRefFromAttributes);
    setCastToRef(idCastToRef);
    setCastToType(idCastToType);
    setCastToDistinct(idCastToDistinct);
    setCastToSource(idCastToSource);
    setMethodSpecifications(listMethodSpecifications);
    _il.exit();
  } /* initialize */
  
  /*------------------------------------------------------------------*/
  /** initialize a create type statement for distinct types.
   * @param qTypeName name of type to be created (not null!).
   * @param pdtDistinctBaseType base type of a DISTINCT type (or null).
   */
  public void initDistinct(
    QualifiedId qTypeName,
    PredefinedType pdtDistinctBaseType
    )
  {
    _il.enter(qTypeName,pdtDistinctBaseType);
    setTypeName(qTypeName);
    setDistinctBaseType(pdtDistinctBaseType);
    _il.exit();
  } /* initDistinct */
  
  /*------------------------------------------------------------------*/
  /** initialize a create type statement from attribute definitions.
   * @param qTypeName name of type to be created (not null!).
   * @param listAttributeDefinitions list of attribute definitions (not null!).
   */
  public void initAttributes(
    QualifiedId qTypeName,
    List<AttributeDefinition> listAttributeDefinitions)
  {
    _il.enter(qTypeName,listAttributeDefinitions);
    setTypeName(qTypeName);
    setAttributeDefinitions(listAttributeDefinitions);
    _il.exit();
  } /* initAttributes */

  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public CreateTypeStatement(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class CreateTypeStatement */
