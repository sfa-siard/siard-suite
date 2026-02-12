package ch.enterag.sqlparser.expression;

import java.util.*;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.datatype.*;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.generated.*;
import ch.enterag.sqlparser.identifier.*;
import ch.enterag.utils.logging.*;

public class ValueExpressionPrimary
  extends SqlBase
{
  /** logger */
  protected static IndentLogger _il = IndentLogger.getIndentLogger(ValueExpressionPrimary.class.getName());
  
  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  private class VepVisitor extends EnhancedSqlBaseVisitor<ValueExpressionPrimary>
  {
    private void getSqlArgumentList(SqlParser.SqlArgumentListContext ctx, List<SqlArgument> listSqlArguments)
    {
      setSqlArgumentList(true);
      for (int i = 0; i < ctx.sqlArgument().size(); i++)
      {
        SqlArgument sa = getSqlFactory().newSqlArgument();
        sa.parse(ctx.sqlArgument(i));
        listSqlArguments.add(sa);
      }
    }
    private void getCollectionValueComponentList(List<SqlParser.ValueExpressionContext> listCtx)
    {
      for (int i = 0; i < listCtx.size(); i++)
      {
        ValueExpression ve = getSqlFactory().newValueExpression();
        ve.parse(listCtx.get(i));
        getCollectionValueComponents().add(ve);
      }
    }
    @Override
    public ValueExpressionPrimary visitUnsignedLiteral(SqlParser.UnsignedLiteralContext ctx)
    {
      setUnsignedLit(getSqlFactory().newUnsignedLiteral());
      getUnsignedLit().parse(ctx);
      return ValueExpressionPrimary.this;
    }
    /**
    @Override
    public ValueExpressionPrimary visitColumnRef(SqlParser.ColumnRefContext ctx)
    {
      setIdChain(ctx.columnReference().identifierChain(), getColumnRef());
      return ValueExpressionPrimary.this;
    }
    **/
    @Override
    public ValueExpressionPrimary visitGeneralValueSpec(SqlParser.GeneralValueSpecContext ctx)
    {
      setGeneralValueSpecification(getSqlFactory().newGeneralValueSpecification());
      getGeneralValueSpecification().parse(ctx.generalValueSpecification());
      return ValueExpressionPrimary.this;
    }
    @Override
    public ValueExpressionPrimary visitAggregateFunc(SqlParser.AggregateFuncContext ctx)
    {
      setAggregateFunc(getSqlFactory().newAggregateFunction());
      getAggregateFunc().parse(ctx.aggregateFunction());
      return ValueExpressionPrimary.this;
    }
    @Override
    public ValueExpressionPrimary visitGroupingOp(SqlParser.GroupingOpContext ctx)
    {
      for (int i = 0; i < ctx.groupingOperation().columnReference().size(); i++)
      {
        IdChain idcColumnReference = new IdChain();
        setIdChain(ctx.groupingOperation().columnReference(i).identifierChain(),idcColumnReference);
        getGroupingOp().add(idcColumnReference);
      }
      return ValueExpressionPrimary.this;
    }
    @Override
    public ValueExpressionPrimary visitWindowFunc(SqlParser.WindowFuncContext ctx)
    {
      setWindowFunc(getSqlFactory().newWindowFunction());
      getWindowFunc().parse(ctx.windowFunction());
      return ValueExpressionPrimary.this;
    }
    @Override
    public ValueExpressionPrimary visitScalarSubq(SqlParser.ScalarSubqContext ctx)
    {
      setScalarSubq(getSqlFactory().newQueryExpression());
      getScalarSubq().parse(ctx.scalarSubquery().queryExpression());
      return ValueExpressionPrimary.this;
    }
    @Override
    public ValueExpressionPrimary visitCaseExp(SqlParser.CaseExpContext ctx)
    {
      setCaseExpression(getSqlFactory().newCaseExpression());
      getCaseExpression().parse(ctx.caseExpression());
      return ValueExpressionPrimary.this;
    }
    @Override
    public ValueExpressionPrimary visitCastSpec(SqlParser.CastSpecContext ctx)
    {
      setCastSpecification(getSqlFactory().newCastSpecification());
      getCastSpecification().parse(ctx.castSpecification());
      return ValueExpressionPrimary.this;
    }
    @Override
    public ValueExpressionPrimary visitSubtypeTreat(SqlParser.SubtypeTreatContext ctx)
    {
      setSubtypeTreatment(getSqlFactory().newSubtypeTreatment());
      getSubtypeTreatment().parse(ctx.subtypeTreatment());
      return ValueExpressionPrimary.this;
    }
    @Override
    public ValueExpressionPrimary visitMethodInvoc(SqlParser.MethodInvocContext ctx)
    {
      setMethodQualifier(getSqlFactory().newValueExpressionPrimary());
      getMethodQualifier().parse(ctx.valueExpressionPrimary());
      setIdentifier(ctx.methodName().IDENTIFIER(),getMethodName());
      if (ctx.sqlArgumentList() != null)
        getSqlArgumentList(ctx.sqlArgumentList(), getSqlArguments());
      return ValueExpressionPrimary.this;
    }
    @Override
    public ValueExpressionPrimary visitGeneralizedMethodInvoc(SqlParser.GeneralizedMethodInvocContext ctx)
    {
      setMethodQualifier(getSqlFactory().newValueExpressionPrimary());
      getMethodQualifier().parse(ctx.generalizedInvocation().valueExpressionPrimary());
      setDataType(getSqlFactory().newDataType());
      getDataType().parse(ctx.generalizedInvocation().dataType());
      setIdentifier(ctx.generalizedInvocation().methodName().IDENTIFIER(),getMethodName());
      if (ctx.generalizedInvocation().sqlArgumentList() != null)
        getSqlArgumentList(ctx.generalizedInvocation().sqlArgumentList(), getSqlArguments());
      return ValueExpressionPrimary.this;
    }
    @Override
    public ValueExpressionPrimary visitStaticMethodInvoc(SqlParser.StaticMethodInvocContext ctx)
    {
      setUdtName(ctx.staticMethodInvocation().udtName(),getUdtName());
      setIdentifier(ctx.staticMethodInvocation().methodName().IDENTIFIER(),getMethodName());
      if (ctx.staticMethodInvocation().sqlArgumentList() != null)
        getSqlArgumentList(ctx.staticMethodInvocation().sqlArgumentList(), getSqlArguments());
      return ValueExpressionPrimary.this;
    }
    @Override
    public ValueExpressionPrimary visitNewSpec(SqlParser.NewSpecContext ctx)
    {
      setNew(true);
      setRoutineName(ctx.newSpecification().routineInvocation().routineName(),getRoutineName());
      if (ctx.newSpecification().routineInvocation().sqlArgumentList() != null)
        getSqlArgumentList(ctx.newSpecification().routineInvocation().sqlArgumentList(), getSqlArguments());
      return ValueExpressionPrimary.this;
    }
    @Override
    public ValueExpressionPrimary visitAttributeOrMethodRef(SqlParser.AttributeOrMethodRefContext ctx)
    {
      setDeref(getSqlFactory().newValueExpressionPrimary());
      getDeref().parse(ctx.valueExpressionPrimary());
      setIdentifier(ctx.IDENTIFIER(),getAttributeOrMethodName());
      if (ctx.sqlArgumentList() != null)
        getSqlArgumentList(ctx.sqlArgumentList(), getSqlArguments());
      return ValueExpressionPrimary.this;
    }
    @Override
    public ValueExpressionPrimary visitReferenceRes(SqlParser.ReferenceResContext ctx)
    {
      setReferenceResolution(getSqlFactory().newValueExpressionPrimary());
      getReferenceResolution().parse(ctx.referenceResolution().valueExpressionPrimary());
      return ValueExpressionPrimary.this;
    }
    @Override
    public ValueExpressionPrimary visitArrayValueConstruct(SqlParser.ArrayValueConstructContext ctx)
    {
      setArrayValueConstructor(true);
      if (ctx.arrayValueConstructor().queryExpression() != null)
      {
        setQueryExpression(getSqlFactory().newQueryExpression());
        getQueryExpression().parse(ctx.arrayValueConstructor().queryExpression());
        if (ctx.arrayValueConstructor().sortSpecificationList() != null)
        {
          for (int i = 0; i < ctx.arrayValueConstructor().sortSpecificationList().sortSpecification().size(); i++)
          {
            SortSpecification ss = getSqlFactory().newSortSpecification();
            ss.parse(ctx.arrayValueConstructor().sortSpecificationList().sortSpecification(i));
            getSortSpecifications().add(ss);
          }
        }
      }
      else
        getCollectionValueComponentList(ctx.arrayValueConstructor().valueExpression());
      return ValueExpressionPrimary.this;
    }
    @Override
    public ValueExpressionPrimary visitMultisetValueConstruct(SqlParser.MultisetValueConstructContext ctx)
    {
      setMultisetValueConstructor(true);
      if (ctx.multisetValueConstructor().TABLE() != null)
        setTableMultisetValueConstructor(true);
      if (ctx.multisetValueConstructor().queryExpression() != null)
      {
        setQueryExpression(getSqlFactory().newQueryExpression());
        getQueryExpression().parse(ctx.multisetValueConstructor().queryExpression());
      }
      else
        getCollectionValueComponentList(ctx.multisetValueConstructor().valueExpression());
      return ValueExpressionPrimary.this;
    }
    @Override
    public ValueExpressionPrimary visitArrayElementRefConcat(SqlParser.ArrayElementRefConcatContext ctx)
    {
      setFirstArrayValueExpression(getSqlFactory().newArrayValueExpression());
      getFirstArrayValueExpression().parse(ctx.arrayValueExpression(0));
      setSecondArrayValueExpression(getSqlFactory().newArrayValueExpression());
      getSecondArrayValueExpression().parse(ctx.arrayValueExpression(1));
      setNumericValueExpression(getSqlFactory().newNumericValueExpression());
      getNumericValueExpression().parse(ctx.numericValueExpression());
      return ValueExpressionPrimary.this;
    }
    @Override
    public ValueExpressionPrimary visitArrayElementRef(SqlParser.ArrayElementRefContext ctx)
    {
      setArray(getSqlFactory().newValueExpressionPrimary());
      getArray().parse(ctx.valueExpressionPrimary());
      setNumericValueExpression(getSqlFactory().newNumericValueExpression());
      getNumericValueExpression().parse(ctx.numericValueExpression());
      return ValueExpressionPrimary.this;
    }
    @Override
    public ValueExpressionPrimary visitMultisetElementRef(SqlParser.MultisetElementRefContext ctx)
    {
      setMultisetValueExpression(getSqlFactory().newMultisetValueExpression());
      getMultisetValueExpression().parse(ctx.multisetElementReference().multisetValueExpression());
      return ValueExpressionPrimary.this;
    }
    @Override
    public ValueExpressionPrimary visitRoutineInvoc(SqlParser.RoutineInvocContext ctx)
    {
      setRoutineName(ctx.routineInvocation().routineName(),getRoutineName());
      if (ctx.routineInvocation().sqlArgumentList() != null)
        getSqlArgumentList(ctx.routineInvocation().sqlArgumentList(), getSqlArguments());
      return ValueExpressionPrimary.this;
    }
    @Override
    public ValueExpressionPrimary visitNextValueExp(SqlParser.NextValueExpContext ctx)
    {
      setSequenceName(ctx.nextValueExpression().sequenceName(),getSequenceName());
      return ValueExpressionPrimary.this;
    }
    @Override
    public ValueExpressionPrimary visitValueExpressionPrimaryParen(SqlParser.ValueExpressionPrimaryParenContext ctx)
    {
      setValueExpressionPrimary(getSqlFactory().newValueExpressionPrimary());
      getValueExpressionPrimary().parse(ctx.valueExpressionPrimary());
      return ValueExpressionPrimary.this;
    }
  }
  /*==================================================================*/

  private VepVisitor _visitor = new VepVisitor();
  private VepVisitor getVisitor() { return _visitor; }
  
  /* unsignedLit */
  private UnsignedLiteral _ul = null;
  public UnsignedLiteral getUnsignedLit() { return _ul; }
  public void setUnsignedLit(UnsignedLiteral ul) { _ul = ul; }
  
  /* generalValueSpec */
  private GeneralValueSpecification _gvs = null;
  public  GeneralValueSpecification getGeneralValueSpecification() { return _gvs; }
  public void setGeneralValueSpecification(GeneralValueSpecification gvs) { _gvs = gvs; }
  
  /* aggregateFunc */
  private AggregateFunction _af = null;
  public AggregateFunction getAggregateFunc() { return _af; }
  public void setAggregateFunc(AggregateFunction af) { _af = af; }
  
  /* groupingOp */
  private List<IdChain> _listGroupingOp = new ArrayList<IdChain>();
  public List<IdChain> getGroupingOp() { return _listGroupingOp; }
  private void setGroupingOp(List<IdChain> listGroupingOp) { _listGroupingOp = listGroupingOp; }
  
  /* windowFunc */
  private WindowFunction _wf = null;
  public WindowFunction getWindowFunc() { return _wf; }
  public void setWindowFunc(WindowFunction wf) { _wf = wf; }
  
  /* scalarSubq */
  private QueryExpression _qeScalarSubq = null;
  public QueryExpression getScalarSubq() { return _qeScalarSubq; }
  public void setScalarSubq(QueryExpression qeScalarSubq) { _qeScalarSubq = qeScalarSubq; }
  
  /* caseExp */
  private CaseExpression _ce = null;
  public CaseExpression getCaseExpression() { return _ce; }
  public void setCaseExpression(CaseExpression ce) { _ce = ce; }
  
  /* castSpec */
  private CastSpecification _cs = null;
  public CastSpecification getCastSpecification() { return _cs; }
  public void setCastSpecification(CastSpecification cs) { _cs = cs; }
  
  private Identifier _idFieldName = new Identifier();
  public Identifier getFieldName() { return _idFieldName; }
  private void setFieldName(Identifier idFieldName) { _idFieldName = idFieldName; }

  /* subtypeTreat */
  private SubtypeTreatment _st = null;
  public SubtypeTreatment getSubtypeTreatment() { return _st; }
  public void setSubtypeTreatment(SubtypeTreatment st) { _st = st; }
  
  /* methodInvoc and generalizedMethodInvoc */
  private ValueExpressionPrimary _vepMethodRef = null;
  public ValueExpressionPrimary getMethodQualifier() { return _vepMethodRef; }
  public void setMethodQualifier(ValueExpressionPrimary vepMethodRef) { _vepMethodRef = vepMethodRef; }
  
  /* methodInvoc and generalizedMethodInvoc and staticMethodInvoc */
  private Identifier _idMethodName = new Identifier();
  public Identifier getMethodName() { return _idMethodName; }
  private void setMethodName(Identifier idMethodName) { _idMethodName = idMethodName; }

  private boolean _bSqlArgumentList = false;
  public boolean isSqlArgumentList() { return _bSqlArgumentList; }
  public void setSqlArgumentList(boolean bSqlArgumentList) { _bSqlArgumentList = bSqlArgumentList;}
  
  /* methodInvoc and generalizedMethodInvoc and staticMethodInvoc and newSpec and attributeOrMethodRef and routineInvoc */
  private List<SqlArgument> _listSqlArguments = new ArrayList<SqlArgument>();
  public List<SqlArgument> getSqlArguments() { return _listSqlArguments; }
  private void setSqlArguments(List<SqlArgument> listSqlArguments) { _listSqlArguments = listSqlArguments; }
  
  /* generalizedMethodInvoc */
  private DataType _dt = null;
  public DataType getDataType() { return _dt; }
  public void setDataType(DataType dt) { _dt = dt; }
  
  /* staticMethodInvoc */
  private QualifiedId _qiUdtName = new QualifiedId();
  public QualifiedId getUdtName() { return _qiUdtName; }
  private void setUdtName(QualifiedId qiUdtName) { _qiUdtName = qiUdtName; }
  
  /* newSpec */
  private boolean _bNew = false;
  public boolean isNew() { return _bNew; }
  public void setNew(boolean bNew) { _bNew = bNew; }
  
  /* newSpec and routineInvoc */
  private QualifiedId _qiRoutineName = new QualifiedId();
  public QualifiedId getRoutineName() { return _qiRoutineName; }
  protected void setRoutineName(QualifiedId qiRoutineName) { _qiRoutineName = qiRoutineName; }

  /* attributeOrMethodRef */
  private ValueExpressionPrimary _vepDeref = null;
  public ValueExpressionPrimary getDeref() { return _vepDeref; }
  public void setDeref(ValueExpressionPrimary vepDeref) { _vepDeref = vepDeref; }
  
  private Identifier _idAttributeOrMethodName = new Identifier();
  public Identifier getAttributeOrMethodName() { return _idAttributeOrMethodName; }
  private void setAttributeOrMethodName(Identifier idAttributeOrMethodName) { _idAttributeOrMethodName = idAttributeOrMethodName; }
  
  /* referenceRes */
  private ValueExpressionPrimary _vepReferenceResolution = null;
  public ValueExpressionPrimary getReferenceResolution() { return _vepReferenceResolution; }
  public void setReferenceResolution(ValueExpressionPrimary vepReferenceResolution) { _vepReferenceResolution = vepReferenceResolution; }
  
  /* arrayValueConstruct */
  private boolean _bArrayValueConstructor = false;
  public boolean isArrayValueConstructor() { return _bArrayValueConstructor; }
  public void setArrayValueConstructor(boolean bArrayValueConstructor) { _bArrayValueConstructor = bArrayValueConstructor; }
  
  /* multisetValueConstruct */
  private boolean _bMultisetValueConstructor = false;
  public boolean isMultisetValueConstructor() { return _bMultisetValueConstructor; }
  public void setMultisetValueConstructor(boolean bMultisetValueConstructor) { _bMultisetValueConstructor = bMultisetValueConstructor; }
  
  private boolean _bTableMultisetValueConstructor = false;
  public boolean isTableMultisetValueConstructor() { return _bTableMultisetValueConstructor; }
  public void setTableMultisetValueConstructor(boolean bTableMultisetValueConstructor) { _bTableMultisetValueConstructor = bTableMultisetValueConstructor; }
  
  /* arrayValueConstruct or multisetValueConstruct */
  private List<ValueExpression> _listCollectionValueComponents = new ArrayList<ValueExpression>();
  public List<ValueExpression> getCollectionValueComponents() { return _listCollectionValueComponents; }
  private void setCollectionValueComponents(List<ValueExpression> listCollectionValueComponents) { _listCollectionValueComponents = listCollectionValueComponents; }
  
  private QueryExpression _qe = null;
  public QueryExpression getQueryExpression() { return _qe; }
  public void setQueryExpression(QueryExpression qe) { _qe = qe; }
  
  private List<SortSpecification> _listSortSpecifications = new ArrayList<SortSpecification>();
  public List<SortSpecification> getSortSpecifications() { return _listSortSpecifications; }
  private void setSortSpecifications(List<SortSpecification> listSortSpecifications) { _listSortSpecifications = listSortSpecifications; }

  /* arrayElementRefConcat */
  private ArrayValueExpression _ave1 = null;
  public ArrayValueExpression getFirstArrayValueExpression() { return _ave1; }
  public void setFirstArrayValueExpression(ArrayValueExpression ave1) { _ave1 = ave1; }
  
  private ArrayValueExpression _ave2 = null;
  public ArrayValueExpression getSecondArrayValueExpression() { return _ave2; }
  public void setSecondArrayValueExpression(ArrayValueExpression ave2) { _ave2 = ave2; }
  
  /* arrayElementRefConcat and arrayElementRef */
  private NumericValueExpression _nve = null;
  public NumericValueExpression getNumericValueExpression() { return _nve; }
  public void setNumericValueExpression(NumericValueExpression nve) { _nve = nve; }
  
  /* arrayElementRef */
  private ValueExpressionPrimary _vepArray = null;
  public ValueExpressionPrimary getArray() { return _vepArray; }
  public void setArray(ValueExpressionPrimary vepArray) { _vepArray = vepArray; }
  
  /* multisetElementRef */
  private MultisetValueExpression _mve = null;
  public MultisetValueExpression getMultisetValueExpression() { return _mve; }
  public void setMultisetValueExpression(MultisetValueExpression mve) { _mve = mve; }

  /* routineInvoc */
  
  /* nextValueExp */
  private QualifiedId _qiSequenceName = new QualifiedId();
  public QualifiedId getSequenceName() { return _qiSequenceName; }
  private void setSequenceName(QualifiedId qiSequenceName) { _qiSequenceName = qiSequenceName; }
  
  /* valueExpressionPrimaryParen */
  private ValueExpressionPrimary _vep = null;
  public ValueExpressionPrimary getValueExpressionPrimary() { return _vep; }
  public void setValueExpressionPrimary(ValueExpressionPrimary vep) { _vep = vep; }
  
  protected String formatSqlArguments()
  {
    String s = "";
    s = sLEFT_PAREN;
    for (int i = 0; i < getSqlArguments().size(); i++)
    {
      if (i > 0)
        s = s + sCOMMA + sSP;
      s = s + getSqlArguments().get(i).format();
    }
    s = s + sRIGHT_PAREN;
    return s;
  } /* formatSqlArguments */
  
  protected String formatCollectionValueComponents()
  {
    String s = "";
    if (getCollectionValueComponents().size() > 0)
    {
      s = sLEFT_BRACKET;
      for (int i = 0; i < getCollectionValueComponents().size(); i++)
      {
        if (i > 0)
          s = s + sCOMMA + sSP;
        s = s + getCollectionValueComponents().get(i).format();
      }
      s = s + sRIGHT_BRACKET;
    }
    return s;
  } /* formatCollectionValueComponents */
  
  /*------------------------------------------------------------------*/
  /** format the value expression primary.
   * @return the SQL string corresponding to the fields of the value 
   *   expression primary.
   */
  @Override
  public String format()
  {
    String sExpression = "";

    /* unsignedLit */
    if (getUnsignedLit() != null)
      sExpression = getUnsignedLit().format();
    /* generalValueSpec */
    else if (getGeneralValueSpecification() != null)
      sExpression = getGeneralValueSpecification().format();
    /* aggregateFunc */
    else if (getAggregateFunc() != null)
      sExpression = getAggregateFunc().format();
    /* groupingOp */
    else if (getGroupingOp().size() > 0)
    {
      sExpression = K.GROUPING.getKeyword() + sLEFT_PAREN;
      for (int i = 0; i < getGroupingOp().size(); i++)
      {
        if (i > 0)
          sExpression = sExpression + sCOMMA + sSP;
        sExpression = sExpression + getGroupingOp().get(i).format();
      }
      sExpression = sExpression + sRIGHT_PAREN;
    }
    /* windowFunc */
    else if (getWindowFunc() != null)
      sExpression = getWindowFunc().format();
    /* scalarSubq */
    else if (getScalarSubq() != null)
      sExpression = sLEFT_PAREN + getScalarSubq().format() + sRIGHT_PAREN;
    /* caseExp */
    else if (getCaseExpression() != null)
      sExpression = getCaseExpression().format();
    /* castSpec */
    else if (getCastSpecification() != null)
      sExpression = getCastSpecification().format();
    /* subtypeTreat */
    else if (getSubtypeTreatment() != null)
      sExpression = getSubtypeTreatment().format();
    /* generalizedMethodInvoc */
    else if ((getMethodQualifier() != null) && (getDataType() != null) && getMethodName().isSet())
    {
      sExpression = sLEFT_PAREN + getMethodQualifier().format() + sSP + K.AS.getKeyword() + sSP + 
        getDataType().format() + sRIGHT_PAREN + sPERIOD + getMethodName().format();
      if (isSqlArgumentList())
        sExpression = sExpression + formatSqlArguments();
    }
    /* methodInvoc */
    else if ((getMethodQualifier() != null) && getMethodName().isSet())
    {
      sExpression = getMethodQualifier().format() + sPERIOD + getMethodName().format();
      if (isSqlArgumentList())
        sExpression = sExpression + formatSqlArguments();
    }
    /* staticMethodInvoc */
    else if (getUdtName().isSet() && getMethodName().isSet())
    {
      sExpression = getUdtName().format() + sDOUBLE_COLON + getMethodName().format();
      if (isSqlArgumentList())
        sExpression = sExpression + formatSqlArguments();
    }
    /* newSpec */
    else if (isNew() && getRoutineName().isSet())
      sExpression = K.NEW.getKeyword() + sSP + getRoutineName().format() + formatSqlArguments();
    /* attributeOrMethodRef */
    else if ((getDeref() != null) && getAttributeOrMethodName().isSet())
    {
      sExpression = getDeref().format() + sDEREFERENCE_OPERATOR + getAttributeOrMethodName().format();
      if (isSqlArgumentList())
        sExpression = sExpression + formatSqlArguments();
    }
    /* referenceRes */
    else if (getReferenceResolution() != null)
      sExpression = K.DEREF.getKeyword() + sLEFT_PAREN + getReferenceResolution().format() + sRIGHT_PAREN; 
    /* arrayValueConstruct */
    else if (isArrayValueConstructor())
    {
      sExpression = K.ARRAY.getKeyword();
      if (getQueryExpression() != null)
      {
        sExpression = sExpression + sLEFT_PAREN + getQueryExpression().format();
        if (getSortSpecifications().size() > 0)
        {
          sExpression = sExpression + sNEW_LINE + K.ORDER.getKeyword() + sSP + K.BY.getKeyword() + sSP;
          for (int i = 0; i < getSortSpecifications().size(); i++)
          {
            if (i > 0)
              sExpression = sExpression + sCOMMA + sSP;
            sExpression = sExpression + getSortSpecifications().get(i).format();
          }
        }
        sExpression = sExpression + sRIGHT_PAREN;
      }
      else
        sExpression = sExpression + formatCollectionValueComponents();
    }
    else if (isMultisetValueConstructor())
    {
      if (isTableMultisetValueConstructor())
        sExpression = K.TABLE.getKeyword();
      else
        sExpression = K.MULTISET.getKeyword();
      if (getQueryExpression() != null)
        sExpression = sExpression + sLEFT_PAREN + getQueryExpression().format() + sRIGHT_PAREN;
      else
        sExpression = sExpression + formatCollectionValueComponents();
    }
    /* arrayElementRefConcat */
    else if ((getFirstArrayValueExpression() != null) && 
             (getSecondArrayValueExpression() != null) &&
             (getNumericValueExpression() != null))
      sExpression = sLEFT_PAREN + getFirstArrayValueExpression().format() + sSP + 
        sCONCATENATION_OPERATOR + sSP + getSecondArrayValueExpression().format() + sRIGHT_PAREN +
        sLEFT_BRACKET + getNumericValueExpression().format() + sRIGHT_BRACKET;
    /* arrayElementRef */
    else if ((getArray() != null) && (getNumericValueExpression() != null))
      sExpression = getArray().format() + sLEFT_BRACKET + getNumericValueExpression().format() + sRIGHT_BRACKET;
    /* multisetElementRef */
    else if (getMultisetValueExpression() != null)
      sExpression = K.ELEMENT.getKeyword() + sLEFT_PAREN + getMultisetValueExpression().format() + sRIGHT_PAREN; 
    /* routineInvoc */
    else if (getRoutineName().isSet())
      sExpression = getRoutineName().format() + formatSqlArguments();
    /* nextValueExp */
    else if (getSequenceName().isSet())
      sExpression = K.NEXT.getKeyword() + sSP + K.VALUE.getKeyword() + sSP + K.FOR.getKeyword() + sSP +
          getSequenceName().format();
    /* valueExpressionPrimaryParen */
    else if (getValueExpressionPrimary() != null)
      sExpression = sLEFT_PAREN + getValueExpressionPrimary().format() + sRIGHT_PAREN;
    return sExpression;
  } /* format */

  /*------------------------------------------------------------------*/
  /** get data type of this value expression primary from the context 
   * of a query.
   * @param ss sql statement.
   * @return data type.
   */
  public DataType getDataType(SqlStatement ss)
  {
    DataType dt = null;
    if (getUnsignedLit() != null)
      dt = getUnsignedLit().getDataType();
    else if (getGeneralValueSpecification() != null)
      dt = getGeneralValueSpecification().getDataType(ss);
    else if (getCaseExpression() != null)
      dt = getCaseExpression().getDataType(ss);
    else if (getAggregateFunc() != null)
      dt = getAggregateFunc().getDataType(ss);
    else
      throw new IllegalArgumentException("Type of ValueExpressionPrimary not supported for evaluation!");
    return dt;
  } /* getDataType */
  
  /*------------------------------------------------------------------*/
  /** evaluate this value expression primary against the context of a query.
   * @param ss sql statement.
   * @param bAggregated true, if the value occurs in the argument of an
   *        aggregating function.
   * @return value.
   */
  public Object evaluate(SqlStatement ss, boolean bAggregated)
  {
    Object oValue = null;
    if (getUnsignedLit() != null)
      oValue = getUnsignedLit().evaluate();
    else if (getGeneralValueSpecification() != null)
      oValue = getGeneralValueSpecification().evaluate(ss, bAggregated);
    else if (getAggregateFunc() != null)
    {
      if (!bAggregated)
        oValue = getAggregateFunc().evaluate(ss);
      else
        throw new IllegalArgumentException("Aggregate function must not be referenced within argument of an aggregate function!");
    }
    else if (getCollectionValueComponents() != null)
    {
      List<Object> listValues = new ArrayList<Object>();
      for (int iElement = 0; iElement < getCollectionValueComponents().size(); iElement++)
      {
        ValueExpression ve = getCollectionValueComponents().get(iElement);
        listValues.add(ve.evaluate(ss, bAggregated));
      }
      oValue = listValues;
    }
    else
      throw new IllegalArgumentException("Type of ValueExpressionPrimary not supported for evaluation!");
    return oValue;
  } /* evaluate */
  
  /*------------------------------------------------------------------*/
  /** reset the aggregate values in this value expression to their initial
   * value.
   * @param ss sql statement.
   * @return final aggregate value.
   */
  public Object resetAggregates(SqlStatement ss)
  {
    Object oValue = null;
    if (getAggregateFunc() != null)
      oValue = getAggregateFunc().reset();
    else
      oValue = evaluate(ss,true);
    return oValue;
  } /* reset */
  
  /*------------------------------------------------------------------*/
  /** parse the value expression primary from the parsing tree context.
   * @param ctx parsing context (tree).
   * @throws NullPointerException if no parsing tree is available. 
   */
  public void parse(SqlParser.ValueExpressionPrimaryContext ctx)
  {
    setContext(ctx);
    getVisitor().visit(getContext());
  } /* parse */
  
  /*------------------------------------------------------------------*/
  /** parse the value expression primary from SQL.
   * @param sSql SQL.
   */
  @Override
  public void parse(String sSql)
  {
    setParser(newSqlParser(sSql));
    SqlParser.ValueExpressionPrimaryContext ctx = null; 
    try { ctx = getParser().valueExpressionPrimary(); }
    catch(Exception e)
    {
      setParser(newSqlParser2(sSql));
      ctx = getParser().valueExpressionPrimary();
    }
    parse(ctx);
  } /* parse */
  
  /*------------------------------------------------------------------*/
  /* initialize a value expression primary.
   */
  public void initialize(
      UnsignedLiteral ul,
      GeneralValueSpecification gvs,
      AggregateFunction af,
      List<IdChain> listGroupingOp,
      WindowFunction wf,
      QueryExpression qeScalarSubq,
      CaseExpression ce,
      CastSpecification cs,
      Identifier idFieldName,
      SubtypeTreatment st,
      ValueExpressionPrimary vepMethodRef,
      Identifier idMethodName,
      List<SqlArgument> listSqlArguments,
      DataType dt,
      QualifiedId qiUdtName,
      boolean bNew,
      QualifiedId qiRoutineName,
      ValueExpressionPrimary vepDeref,
      Identifier idAttributeOrMethodName,
      ValueExpressionPrimary vepReferenceResolution,
      boolean bArrayValueConstructor,
      boolean bMultisetValueConstructor,
      boolean bTableMultisetValueConstructor,
      List<ValueExpression> listCollectionValueComponents,
      QueryExpression qe,
      List<SortSpecification> listSortSpecifications,
      ArrayValueExpression ave1,
      ArrayValueExpression ave2,
      NumericValueExpression nve,
      ValueExpressionPrimary vepArray,
      MultisetValueExpression mve,
      QualifiedId qiSequenceName,
      ValueExpressionPrimary vep
    )
  {
    _il.enter();
    setUnsignedLit(ul);
    setGeneralValueSpecification(gvs);
    setAggregateFunc(af);
    setGroupingOp(listGroupingOp);
    setWindowFunc(wf);
    setScalarSubq(qeScalarSubq);
    setCaseExpression(ce);
    setCastSpecification(cs);
    setFieldName(idFieldName);
    setSubtypeTreatment(st);
    setMethodQualifier(vepMethodRef);
    setMethodName(idMethodName);
    setSqlArguments(listSqlArguments);
    setDataType(dt);
    setUdtName(qiUdtName);
    setNew(bNew);
    setRoutineName(qiRoutineName);
    setDeref(vepDeref);
    setAttributeOrMethodName(idAttributeOrMethodName);
    setReferenceResolution(vepReferenceResolution);
    setArrayValueConstructor(bArrayValueConstructor);
    setMultisetValueConstructor(bMultisetValueConstructor);
    setTableMultisetValueConstructor(bTableMultisetValueConstructor);
    setCollectionValueComponents(listCollectionValueComponents);
    setQueryExpression(qe);
    setSortSpecifications(listSortSpecifications);
    setFirstArrayValueExpression(ave1);
    setSecondArrayValueExpression(ave2);
    setNumericValueExpression(nve);
    setArray(vepArray);
    setMultisetValueExpression(mve);
    setSequenceName(qiSequenceName);
    setValueExpressionPrimary(vep);
    _il.exit();
  } /* initialize */

  /*------------------------------------------------------------------*/
  /** initialize a value expression primary.
   * @param ul unsigned literal.
   */
  public void initUnsignedLiteral(
      UnsignedLiteral ul
    )
  {
    _il.enter();
    setUnsignedLit(ul);
    _il.exit();
  } /* initUnsignedLiteral */

  /*------------------------------------------------------------------*/
  /** initialize a value expression primary.
   * @param listArrayValueComponents list of array value components.
   */
  public void initArrayValueConstructor(
    List<ValueExpression> listArrayValueComponents
    )
  {
    _il.enter();
    setArrayValueConstructor(true);
    setCollectionValueComponents(listArrayValueComponents);
    _il.exit();
  } /* initArrayValueConstructor */

  /*------------------------------------------------------------------*/
  /** initialize a value expression primary.
   * @param qiUdt name of UDT type to be instantiated with NEW.
   * @param listAttributeValues list of attribute parameters.
   */
  public void initUdtValueConstructor(
    QualifiedId qiUdt,
    List<SqlArgument> listAttributeValues
    )
  {
    _il.enter();
    setNew(true);
    setRoutineName(qiUdt);
    setSqlArguments(listAttributeValues);
    _il.exit();
  } /* initUdtValueConstructor */

  /*------------------------------------------------------------------*/
  /** initialize a value expression primary.
   * @param gvs general value specification.
   */
  public void initGeneralValueSpecification(
      GeneralValueSpecification gvs
    )
  {
    _il.enter();
    setGeneralValueSpecification(gvs);
    _il.exit();
  } /* initialize */

  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public ValueExpressionPrimary(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class ValueExpressionPrimary */
