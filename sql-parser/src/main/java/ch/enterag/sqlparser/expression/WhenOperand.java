package ch.enterag.sqlparser.expression;

import java.text.*;
import java.util.*;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.datatype.DataType;
import ch.enterag.sqlparser.datatype.PredefinedType;
import ch.enterag.sqlparser.expression.enums.*;
import ch.enterag.sqlparser.generated.*;
import ch.enterag.sqlparser.identifier.*;
import ch.enterag.utils.logging.*;

public class WhenOperand
  extends SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(WhenOperand.class.getName());
  
  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  private class WoVisitor extends EnhancedSqlBaseVisitor<WhenOperand>
  {
    @Override
    public WhenOperand visitWhenOperand(SqlParser.WhenOperandContext ctx)
    {
      if (ctx.rowValuePredicand() != null)
      {
        setRowValuePredicand(getSqlFactory().newRowValuePredicand());
        getRowValuePredicand().parse(ctx.rowValuePredicand());
      }
      return visitChildren(ctx);
    }
    @Override
    public WhenOperand visitComparisonCondition(SqlParser.ComparisonConditionContext ctx)
    {
      setCompOp(getCompOp(ctx.compOp()));
      setSecondRowValuePredicand(getSqlFactory().newRowValuePredicand());
      getSecondRowValuePredicand().parse(ctx.rowValuePredicand());
      return WhenOperand.this;
    }
    @Override 
    public WhenOperand visitBetweenCondition(SqlParser.BetweenConditionContext ctx)
    {
      if (ctx.NOT() != null)
        setNot(true);
      setBooleanCondition(BooleanCondition.BETWEEN);
      if (ctx.symmetricOption() != null)
        setSymmetricOption(getSymmetricOption(ctx.symmetricOption()));
      setSecondRowValuePredicand(getSqlFactory().newRowValuePredicand());
      getSecondRowValuePredicand().parse(ctx.rowValuePredicand(0));
      setThirdRowValuePredicand(getSqlFactory().newRowValuePredicand());
      getThirdRowValuePredicand().parse(ctx.rowValuePredicand(1));
      return WhenOperand.this;
    }
    @Override
    public WhenOperand visitInCondition(SqlParser.InConditionContext ctx)
    {
      if (ctx.NOT() != null)
        setNot(true);
      setBooleanCondition(BooleanCondition.IN);
      if (ctx.queryExpression() != null)
      {
        setQueryExpression(getSqlFactory().newQueryExpression());
        getQueryExpression().parse(ctx.queryExpression());
      }
      else
      {
        for (int i = 0; i < ctx.rowValueExpression().size(); i++)
        {
          RowValueExpression rve = getSqlFactory().newRowValueExpression();
          rve.parse(ctx.rowValueExpression(i));
          getRowValueExpressions().add(rve);
        }
      }
      return WhenOperand.this;
    }
    @Override
    public WhenOperand visitLikeCondition(SqlParser.LikeConditionContext ctx)
    {
      if (ctx.NOT() != null)
        setNot(true);
      setBooleanCondition(BooleanCondition.LIKE);
      setStringValueExpression(getSqlFactory().newStringValueExpression());
      getStringValueExpression().parse(ctx.stringValueExpression());
      if (ctx.ESCAPE() != null)
      {
        try { setEscapeLetter(SqlLiterals.parseStringLiteral(ctx.CHARACTER_STRING_LITERAL().getText())); }
        catch (ParseException pe) { throw new IllegalArgumentException("Error visiting escape letter", pe); }
      }
      return WhenOperand.this;
    }
    @Override
    public WhenOperand visitSimilarCondition(SqlParser.SimilarConditionContext ctx)
    {
      if (ctx.NOT() != null)
        setNot(true);
      setBooleanCondition(BooleanCondition.SIMILAR);
      setStringValueExpression(getSqlFactory().newStringValueExpression());
      getStringValueExpression().parse(ctx.stringValueExpression());
      if (ctx.ESCAPE() != null)
      {
        try { setEscapeLetter(SqlLiterals.parseStringLiteral(ctx.CHARACTER_STRING_LITERAL().getText())); }
        catch (ParseException pe) { throw new IllegalArgumentException("Error visiting escape letter", pe); }
      }
      return WhenOperand.this;
    }
    @Override
    public WhenOperand visitNullCondition(SqlParser.NullConditionContext ctx)
    {
      setNullCondition(getNullCondition(ctx));
      return WhenOperand.this;
    }
    @Override
    public WhenOperand visitQuantifiedComparisonCondition(SqlParser.QuantifiedComparisonConditionContext ctx)
    {
      setCompOp(getCompOp(ctx.compOp()));
      setQuantifier(getQuantifier(ctx.quantifier()));
      setQueryExpression(getSqlFactory().newQueryExpression());
      getQueryExpression().parse(ctx.queryExpression());
      return WhenOperand.this;
    }
    @Override
    public WhenOperand visitMatchCondition(SqlParser.MatchConditionContext ctx)
    {
      setBooleanCondition(BooleanCondition.MATCH);
      if (ctx.UNIQUE() != null)
        setUnique(true);
      setMatchType(getMatchType(ctx.match()));
      setQueryExpression(getSqlFactory().newQueryExpression());
      getQueryExpression().parse(ctx.queryExpression());
      return WhenOperand.this;
    }
    @Override
    public WhenOperand visitOverlapsCondition(SqlParser.OverlapsConditionContext ctx)
    {
      setBooleanCondition(BooleanCondition.OVERLAPS);
      setSecondRowValuePredicand(getSqlFactory().newRowValuePredicand());
      getSecondRowValuePredicand().parse(ctx.rowValuePredicand());
      return WhenOperand.this;
    }
    @Override
    public WhenOperand visitDistinctCondition(SqlParser.DistinctConditionContext ctx)
    {
      setBooleanCondition(BooleanCondition.IS_DISTINCT_FROM);
      setSecondRowValuePredicand(getSqlFactory().newRowValuePredicand());
      getSecondRowValuePredicand().parse(ctx.rowValuePredicand());
      return WhenOperand.this;
    }
    @Override
    public WhenOperand visitMemberCondition(SqlParser.MemberConditionContext ctx)
    {
      if (ctx.NOT() != null)
        setNot(true);
      setBooleanCondition(BooleanCondition.MEMBER_OF);
      setMultisetValueExpression(getSqlFactory().newMultisetValueExpression());
      getMultisetValueExpression().parse(ctx.multisetValueExpression());
      return WhenOperand.this;
    }
    @Override
    public WhenOperand visitSubmultisetCondition(SqlParser.SubmultisetConditionContext ctx)
    {
      if (ctx.NOT() != null)
        setNot(true);
      setBooleanCondition(BooleanCondition.SUBMULTISET);
      setMultisetValueExpression(getSqlFactory().newMultisetValueExpression());
      getMultisetValueExpression().parse(ctx.multisetValueExpression());
      return WhenOperand.this;
    }
    @Override
    public WhenOperand visitSetCondition(SqlParser.SetConditionContext ctx)
    {
      setSetCondition(getSetCondition(ctx));
      return WhenOperand.this;
    }
    @Override
    public WhenOperand visitTypeCondition(SqlParser.TypeConditionContext ctx)
    {
      if (ctx.NOT() != null)
        setNot(true);
      setBooleanCondition(BooleanCondition.OF);
      for (int i = 0; i < ctx.udtSpecification().size(); i++)
      {
        QualifiedId qiUdtName = new QualifiedId();
        setUdtName(ctx.udtSpecification(i).udtName(),qiUdtName);
        getUdtNames().add(qiUdtName);
        Boolean bExclusive = Boolean.FALSE;
        if (ctx.udtSpecification(i).ONLY() != null)
          bExclusive = Boolean.TRUE;
        getExclusives().add(bExclusive);
      }
      return WhenOperand.this;
    }
  }
  /*==================================================================*/
  
  private WoVisitor _visitor = new WoVisitor();
  private WoVisitor getVisitor() { return _visitor; }
  
  private RowValuePredicand _rve = null;
  public RowValuePredicand getRowValuePredicand() { return _rve; }
  public void setRowValuePredicand(RowValuePredicand rve) { _rve = rve; }
  
  private CompOp _co = null;
  public CompOp getCompOp() { return _co; }
  public void setCompOp(CompOp co) { _co = co; }
  
  private Boolean _bNot = false;
  public boolean isNot() { return _bNot; }
  public void setNot(boolean bNot) { _bNot = bNot; }
  
  private BooleanCondition _bc = null;
  public BooleanCondition getBooleanCondition() { return _bc; }
  public void setBooleanCondition(BooleanCondition bc) { _bc = bc; }
  
  private RowValuePredicand _rve2 = null;
  public RowValuePredicand getSecondRowValuePredicand() { return _rve2; }
  public void setSecondRowValuePredicand(RowValuePredicand rve2) { _rve2 = rve2; }
  
  private RowValuePredicand _rve3 = null;
  public RowValuePredicand getThirdRowValuePredicand() { return _rve3; }
  public void setThirdRowValuePredicand(RowValuePredicand rve3) { _rve3 = rve3; }
  
  private SymmetricOption _so = null;
  public SymmetricOption getSymmetricOption() { return _so; }
  public void setSymmetricOption(SymmetricOption so) { _so = so; }
  
  private List<RowValueExpression> _listRve = new ArrayList<RowValueExpression>();
  public List<RowValueExpression> getRowValueExpressions() { return _listRve; }
  private void setRowValueExpressions(List<RowValueExpression> listRve) { _listRve = listRve; }
  
  private QueryExpression _qe = null;
  public QueryExpression getQueryExpression() { return _qe; }
  public void setQueryExpression(QueryExpression qe) { _qe = qe; }
  
  private StringValueExpression _sve = null;
  public StringValueExpression getStringValueExpression() { return _sve; }
  public void setStringValueExpression(StringValueExpression sve) { _sve = sve; }
  
  private String _sEscapeLetter = null;
  public String getEscapeLetter() { return _sEscapeLetter; }
  public void setEscapeLetter(String sEscapeLetter) { _sEscapeLetter = sEscapeLetter; }
  
  private NullCondition _nc = null;
  public NullCondition getNullCondition() { return _nc; }
  public void setNullCondition(NullCondition nc) { _nc = nc; }
  
  private Quantifier _q = null;
  public Quantifier getQuantifier() { return _q; }
  public void setQuantifier(Quantifier q) { _q = q; }
  
  private boolean _bUnique = false;
  public boolean isUnique() { return _bUnique; }
  public void setUnique(boolean bUnique) { _bUnique = bUnique; }
  
  private MatchType _mt = null;
  public MatchType getMatchType() { return _mt; }
  public void setMatchType(MatchType mt) { _mt = mt; }
  
  private MultisetValueExpression _mve = null;
  public MultisetValueExpression getMultisetValueExpression() { return _mve; }
  public void setMultisetValueExpression(MultisetValueExpression mve) { _mve = mve; }
  
  private SetCondition _sc = null;
  public SetCondition getSetCondition() { return _sc; }
  public void setSetCondition(SetCondition sc) { _sc = sc; }
  
  private List<QualifiedId> _listUdtNames = new ArrayList<QualifiedId>();
  public List<QualifiedId> getUdtNames() { return _listUdtNames; }
  private void setUdtNames(List<QualifiedId> listUdtNames) { _listUdtNames = listUdtNames; }
  
  private List<Boolean> _listExclusives = new ArrayList<Boolean>();
  public List<Boolean> getExclusives() { return _listExclusives; }
  private void setExclusives(List<Boolean> listExclusives) { _listExclusives = listExclusives; }
  
  /*------------------------------------------------------------------*/
  /** format the when operand.
   * @return the SQL string corresponding to the fields of the when operand.
   */
  @Override
  public String format()
  {
    String s = "";
    if (getBooleanCondition() != null)
    {
      switch (getBooleanCondition())
      {
        case BETWEEN:
          if (isNot())
            s = K.NOT.getKeyword() + sSP;
          s = s + getBooleanCondition().getKeywords();
          if (getSymmetricOption() != null)
            s = s + sSP + getSymmetricOption().getKeywords();
          s = s + sSP + getSecondRowValuePredicand().format() +
            sSP + K.AND.getKeyword() + sSP + getThirdRowValuePredicand().format();
          break;
        case IN:
          if (isNot())
            s = K.NOT.getKeyword() + sSP;
          s = s + getBooleanCondition().getKeywords();
          if (getQueryExpression() != null)
            s = s + sLEFT_PAREN + getQueryExpression().format() + sRIGHT_PAREN;
          else
          {
            for (int i = 0; i < getRowValueExpressions().size(); i++)
              s = s + sSP + getRowValueExpressions().get(i).format();
          }
          break;
        case LIKE:
          if (isNot())
            s = K.NOT.getKeyword() + sSP;
          s = s + getBooleanCondition().getKeywords() + sSP + 
            getStringValueExpression().format();
          if (getEscapeLetter() != null)
            s = s + sSP + K.ESCAPE.getKeyword() + sSP + SqlLiterals.formatStringLiteral(getEscapeLetter());
          break;
        case SIMILAR:
          if (isNot())
            s = K.NOT.getKeyword() + sSP;
          s = s + getBooleanCondition().getKeywords() + sSP + K.TO.getKeyword() + sSP + 
            getStringValueExpression().format();
          if (getEscapeLetter() != null)
            s = s + sSP + K.ESCAPE.getKeyword() + sSP + SqlLiterals.formatStringLiteral(getEscapeLetter());
          break;
        case MATCH:
          s = getBooleanCondition().getKeywords();
          if (isUnique())
            s = s + sSP + K.UNIQUE.getKeyword();
          s = s + sSP + getMatchType().getKeywords() + 
            sLEFT_PAREN + getQueryExpression().format() + sRIGHT_PAREN; 
          break;
        case OVERLAPS:
        case IS_DISTINCT_FROM:
          s = getBooleanCondition().getKeywords() + sSP +
          getSecondRowValuePredicand().format();
          break;
        case MEMBER_OF:
        case SUBMULTISET:
          if (isNot())
            s = K.NOT.getKeyword() + sSP;
          s = s + getBooleanCondition().getKeywords() + sSP +
            getMultisetValueExpression().format();
          break;
        case OF:
          s = K.IS.getKeyword() + sSP;
          if (isNot())
            s = s + K.NOT.getKeyword() + sSP;
          s = s + getBooleanCondition().getKeywords() + sLEFT_PAREN;
          for (int i = 0; i < getUdtNames().size(); i++)
          {
            if (i > 0)
              s = s + sCOMMA;
            if (getExclusives().get(i).booleanValue())
              s = s + sSP + K.ONLY.getKeyword();
            s = s + sSP + getUdtNames().get(i).format();
          }
          s = s + sRIGHT_PAREN;
          break;
        case EXISTS:
          s = K.EXISTS.getKeyword() + sLEFT_PAREN + 
            getQueryExpression().format()+sRIGHT_PAREN;
          break;
        default:
          throw new IllegalArgumentException("Boolean condition "+getBooleanCondition().getKeywords()+" not (yet) supported in WHEN operand!");
      }
    }
    else
    {
      if ((getQuantifier() != null) && (getCompOp() != null))
      {
        s = getCompOp().getKeywords() + sSP +
          getQuantifier().getKeywords() + sLEFT_PAREN + getQueryExpression().format() + sRIGHT_PAREN;
      }
      else if (getCompOp() != null)
        s = getCompOp().getKeywords() + sSP + getSecondRowValuePredicand().format();
      else if (getNullCondition() != null)
        s = getNullCondition().getKeywords();
      else if (getSetCondition() != null)
        s = getSetCondition().getKeywords();
      else if (getRowValuePredicand() != null)
        s = getRowValuePredicand().format();
    }
    return s;
  } /* format */
  
  /*------------------------------------------------------------------*/
  /** get data type of this when operand from the context 
   * of a query.
   * @param ss sql statement.
   * @return data type (is always Boolean for WhenOperand).
   */
  public DataType getDataType(SqlStatement ss)
  {
    
    DataType dt = getSqlFactory().newDataType();
    PredefinedType ptType = getSqlFactory().newPredefinedType();
    ptType.initBooleanType();
    dt.initPredefinedDataType(ptType);
    return dt;
  } /* getDataType */
  
  /*------------------------------------------------------------------*/
  /** evaluate this case expression against the context of a query.
   * @param ss sql statement.
   * @param bAggregated true, if the value occurs in the argument of an
   *        aggregating function.
   * @return value.
   */
  public Object evaluate(SqlStatement ss, boolean bAggregated)
  {
    throw new IllegalArgumentException("Evaluation of WhenOperand is not yet implemented!");
    //Object oValue = null;
    //return oValue;
  } /* evaluate */
  
  /*------------------------------------------------------------------*/
  /** parse the when operand from the parsing tree context.
   * @param ctx parsing context (tree).
   * @throws NullPointerException if no parsing tree is available. 
   */
  public void parse(SqlParser.WhenOperandContext ctx)
  {
    setContext(ctx);
    getVisitor().visit(getContext());
  } /* parse */
  
  /*------------------------------------------------------------------*/
  /** parse the when operand from SQL.
   * @param sSql SQL.
   */
  @Override
  public void parse(String sSql)
  {
    setParser(newSqlParser(sSql));
    parse(getParser().whenOperand());
  } /* parse */
  
  /*------------------------------------------------------------------*/
  /** initialize a when operand.
   * @param co comparison operator.
   * @param bNot true, if NOT.
   * @param bc boolean condition.
   * @param rve row value predicand.
   * @param rve2 second row value predicand.
   * @param rve3 third row value predicand.
   * @param so symmetry option.
   * @param listRve list of row value expressions for IN predicate.
   * @param qe query expression.
   * @param sve string value expression.
   * @param sEscapeLetter escape letter.
   * @param nc NULL condition.
   * @param q match quantifier.
   * @param bUnique true if UNIQUE match.
   * @param mt match type.
   * @param mve multiset value espression.
   * @param sc set condition.
   * @param listUdtNames list of type names for OF.
   * @param listExclusives list of exclusive option ONLY.
   */
  public void initialize(
      CompOp co,
      Boolean bNot,
      BooleanCondition bc,
      RowValuePredicand rve,
      RowValuePredicand rve2,
      RowValuePredicand rve3,
      SymmetricOption so,
      List<RowValueExpression> listRve,
      QueryExpression qe,
      StringValueExpression sve,
      String sEscapeLetter,
      NullCondition nc,
      Quantifier q,
      boolean bUnique,
      MatchType mt,
      MultisetValueExpression mve,
      SetCondition sc,
      List<QualifiedId> listUdtNames,
      List<Boolean> listExclusives
    )
  {
    _il.enter(co, String.valueOf(bNot), bc, rve, rve2, rve3, so, listRve, 
      qe, sve, sEscapeLetter, nc, q, bUnique, mt, mve, sc, 
      listUdtNames, listExclusives);
    setCompOp(co);
    setNot(bNot);
    setBooleanCondition(bc);
    setRowValuePredicand(rve);
    setSecondRowValuePredicand(rve2);
    setThirdRowValuePredicand(rve3);
    setSymmetricOption(so);
    setRowValueExpressions(listRve);
    setQueryExpression(qe);
    setStringValueExpression(sve);
    setEscapeLetter(sEscapeLetter);
    setNullCondition(nc);
    setQuantifier(q);
    setUnique(bUnique);
    setMatchType(mt);
    setMultisetValueExpression(mve);
    setSetCondition(sc);
    setUdtNames(listUdtNames);
    setExclusives(listExclusives);
    _il.exit();
  } /* initialize */
  
  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public WhenOperand(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class WhenOperand */
