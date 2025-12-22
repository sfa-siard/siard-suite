package ch.enterag.sqlparser.expression;

import java.text.*;
import java.util.*;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.K;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.expression.enums.*;
import ch.enterag.sqlparser.generated.*;
import ch.enterag.sqlparser.identifier.*;
import ch.enterag.utils.logging.*;

public class BooleanPrimary
  extends SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(BooleanPrimary.class.getName());
  
  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  private class BpVisitor extends EnhancedSqlBaseVisitor<BooleanPrimary>
  {
    @Override
    public BooleanPrimary visitBooleanPrimary(SqlParser.BooleanPrimaryContext ctx)
    {
      if (ctx.rowValuePredicand() != null)
      {
        setRowValuePredicand(getSqlFactory().newRowValuePredicand());
        getRowValuePredicand().parse(ctx.rowValuePredicand());
      }
      if (ctx.EXISTS() != null)
        setBooleanCondition(BooleanCondition.EXISTS);
      else if (ctx.UNIQUE() != null)
        setBooleanCondition(BooleanCondition.UNIQUE);
      else if (ctx.NORMALIZED() != null)
        setBooleanCondition(BooleanCondition.NORMALIZED);
      if (ctx.NOT() != null)
        setNot(true);
      if (ctx.stringValueExpression() != null)
      {
        setStringValueExpression(getSqlFactory().newStringValueExpression());
        getStringValueExpression().parse(ctx.stringValueExpression());
      }
      if (ctx.queryExpression() != null)
      {
        setQueryExpression(getSqlFactory().newQueryExpression());
        getQueryExpression().parse(ctx.queryExpression());
      }
      return visitChildren(ctx);
    }
    @Override
    public BooleanPrimary visitComparisonCondition(SqlParser.ComparisonConditionContext ctx)
    {
      setCompOp(getCompOp(ctx.compOp()));
      setSecondRowValuePredicand(getSqlFactory().newRowValuePredicand());
      getSecondRowValuePredicand().parse(ctx.rowValuePredicand());
      return BooleanPrimary.this;
    }
    @Override 
    public BooleanPrimary visitBetweenCondition(SqlParser.BetweenConditionContext ctx)
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
      return BooleanPrimary.this;
    }
    @Override
    public BooleanPrimary visitInCondition(SqlParser.InConditionContext ctx)
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
      return BooleanPrimary.this;
    }
    @Override
    public BooleanPrimary visitLikeCondition(SqlParser.LikeConditionContext ctx)
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
      return BooleanPrimary.this;
    }
    @Override
    public BooleanPrimary visitSimilarCondition(SqlParser.SimilarConditionContext ctx)
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
      return BooleanPrimary.this;
    }
    @Override
    public BooleanPrimary visitNullCondition(SqlParser.NullConditionContext ctx)
    {
      setNullCondition(getNullCondition(ctx));
      return BooleanPrimary.this;
    }
    @Override
    public BooleanPrimary visitQuantifiedComparisonCondition(SqlParser.QuantifiedComparisonConditionContext ctx)
    {
      setCompOp(getCompOp(ctx.compOp()));
      setQuantifier(getQuantifier(ctx.quantifier()));
      setQueryExpression(getSqlFactory().newQueryExpression());
      getQueryExpression().parse(ctx.queryExpression());
      return BooleanPrimary.this;
    }
    @Override
    public BooleanPrimary visitMatchCondition(SqlParser.MatchConditionContext ctx)
    {
      setBooleanCondition(BooleanCondition.MATCH);
      if (ctx.UNIQUE() != null)
        setUnique(true);
      setMatchType(getMatchType(ctx.match()));
      setQueryExpression(getSqlFactory().newQueryExpression());
      getQueryExpression().parse(ctx.queryExpression());
      return BooleanPrimary.this;
    }
    @Override
    public BooleanPrimary visitOverlapsCondition(SqlParser.OverlapsConditionContext ctx)
    {
      setBooleanCondition(BooleanCondition.OVERLAPS);
      setSecondRowValuePredicand(getSqlFactory().newRowValuePredicand());
      getSecondRowValuePredicand().parse(ctx.rowValuePredicand());
      return BooleanPrimary.this;
    }
    @Override
    public BooleanPrimary visitDistinctCondition(SqlParser.DistinctConditionContext ctx)
    {
      setBooleanCondition(BooleanCondition.IS_DISTINCT_FROM);
      setSecondRowValuePredicand(getSqlFactory().newRowValuePredicand());
      getSecondRowValuePredicand().parse(ctx.rowValuePredicand());
      return BooleanPrimary.this;
    }
    @Override
    public BooleanPrimary visitMemberCondition(SqlParser.MemberConditionContext ctx)
    {
      if (ctx.NOT() != null)
        setNot(true);
      setBooleanCondition(BooleanCondition.MEMBER_OF);
      setMultisetValueExpression(getSqlFactory().newMultisetValueExpression());
      getMultisetValueExpression().parse(ctx.multisetValueExpression());
      return BooleanPrimary.this;
    }
    @Override
    public BooleanPrimary visitSubmultisetCondition(SqlParser.SubmultisetConditionContext ctx)
    {
      if (ctx.NOT() != null)
        setNot(true);
      setBooleanCondition(BooleanCondition.SUBMULTISET);
      setMultisetValueExpression(getSqlFactory().newMultisetValueExpression());
      getMultisetValueExpression().parse(ctx.multisetValueExpression());
      return BooleanPrimary.this;
    }
    @Override
    public BooleanPrimary visitSetCondition(SqlParser.SetConditionContext ctx)
    {
      setSetCondition(getSetCondition(ctx));
      return BooleanPrimary.this;
    }
    @Override
    public BooleanPrimary visitTypeCondition(SqlParser.TypeConditionContext ctx)
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
      return BooleanPrimary.this;
    }
    @Override
    public BooleanPrimary visitBooleanValueExpression(SqlParser.BooleanValueExpressionContext ctx)
    {
      setBooleanValueExpression(getSqlFactory().newBooleanValueExpression());
      getBooleanValueExpression().parse(ctx);
      return BooleanPrimary.this;
    }
    @Override
    public BooleanPrimary visitValueExpressionPrimary(SqlParser.ValueExpressionPrimaryContext ctx)
    {
      setValueExpressionPrimary(getSqlFactory().newValueExpressionPrimary());
      getValueExpressionPrimary().parse(ctx);
      return BooleanPrimary.this;
    }
  }
  /*==================================================================*/
  
  private BpVisitor _visitor = new BpVisitor();
  private BpVisitor getVisitor() { return _visitor; }
  
  private CompOp _co = null;
  public CompOp getCompOp() { return _co; }
  public void setCompOp(CompOp co) { _co = co; }
  
  private Boolean _bNot = false;
  public boolean isNot() { return _bNot; }
  public void setNot(boolean bNot) { _bNot = bNot; }
  
  private BooleanCondition _bc = null;
  public BooleanCondition getBooleanCondition() { return _bc; }
  public void setBooleanCondition(BooleanCondition bc) { _bc = bc; }
  
  private RowValuePredicand _rve = null;
  public RowValuePredicand getRowValuePredicand() { return _rve; }
  public void setRowValuePredicand(RowValuePredicand rve) { _rve = rve; }
  
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
  
  private BooleanValueExpression _bve = null;
  public BooleanValueExpression getBooleanValueExpression() { return _bve; }
  public void setBooleanValueExpression(BooleanValueExpression bve) { _bve = bve; } 
  
  private ValueExpressionPrimary _vep = null;
  public ValueExpressionPrimary getValueExpressionPrimary() { return _vep; }
  public void setValueExpressionPrimary(ValueExpressionPrimary vep) { _vep = vep; }
  
  /*------------------------------------------------------------------*/
  /** format the boolean primary.
   * @return the SQL string corresponding to the fields of the boolean primary.
   */
  @Override
  public String format()
  {
    String s = "";
    if (getRowValuePredicand() != null)
      s = getRowValuePredicand().format();
    if (getBooleanCondition() != null)
    {
      switch (getBooleanCondition())
      {
        case BETWEEN:
          if (isNot())
            s = s + sSP + K.NOT.getKeyword();
          s = s + sSP + getBooleanCondition().getKeywords();
          if (getSymmetricOption() != null)
            s = s + sSP + getSymmetricOption().getKeywords();
          s = s + sSP + getSecondRowValuePredicand().format() +
            sSP + K.AND.getKeyword() + sSP + getThirdRowValuePredicand().format();
          break;
        case IN:
          if (isNot())
            s = s + sSP + K.NOT.getKeyword();
          s = s + sSP + getBooleanCondition().getKeywords();
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
            s = s + sSP + K.NOT.getKeyword();
          s = s + sSP + getBooleanCondition().getKeywords() + sSP + 
            getStringValueExpression().format();
          if (getEscapeLetter() != null)
            s = s + sSP + K.ESCAPE.getKeyword() + sSP + SqlLiterals.formatStringLiteral(getEscapeLetter());
          break;
        case SIMILAR:
          if (isNot())
            s = s + sSP + K.NOT.getKeyword();
          s = s + sSP + getBooleanCondition().getKeywords() + sSP + K.TO.getKeyword() + sSP + 
            getStringValueExpression().format();
          if (getEscapeLetter() != null)
            s = s + sSP + K.ESCAPE.getKeyword() + sSP + SqlLiterals.formatStringLiteral(getEscapeLetter());
          break;
        case EXISTS:
        case UNIQUE:
          s = getBooleanCondition().getKeywords() + 
            sLEFT_PAREN + getQueryExpression().format() + sRIGHT_PAREN;
          break;
        case NORMALIZED:
          s = getStringValueExpression().format() + sSP + K.IS.getKeyword();
          if (isNot())
            s = s + sSP + K.NOT.getKeyword();
          s = s + sSP + getBooleanCondition().getKeywords();
          break;
        case MATCH:
          s = s + sSP + getBooleanCondition().getKeywords();
          if (isUnique())
            s = s + sSP + K.UNIQUE.getKeyword();
          s = s + sSP + getMatchType().getKeywords() + 
            sLEFT_PAREN + getQueryExpression().format() + sRIGHT_PAREN; 
          break;
        case OVERLAPS:
        case IS_DISTINCT_FROM:
          s = s + sSP + getBooleanCondition().getKeywords() + sSP +
          getSecondRowValuePredicand().format();
          break;
        case MEMBER_OF:
        case SUBMULTISET:
          if (isNot())
            s = s + sSP + K.NOT.getKeyword();
          s = s + sSP + getBooleanCondition().getKeywords() + sSP +
            getMultisetValueExpression().format();
          break;
        case OF:
          s = s + sSP + K.IS.getKeyword();
          if (isNot())
            s = s + sSP + K.NOT.getKeyword();
          s = s + sSP + getBooleanCondition().getKeywords() + sLEFT_PAREN;
          for (int i = 0; i < getUdtNames().size(); i++)
          {
            if (i > 0)
              s = s + sCOMMA+sSP;
            if (getExclusives().get(i).booleanValue())
              s = s + K.ONLY.getKeyword()+sSP;
            s = s + getUdtNames().get(i).format();
          }
          s = s + sRIGHT_PAREN;
          break;
      }
    }
    else
    {
      if ((getQuantifier() != null) && (getCompOp() != null))
      {
        s = s + sSP + getCompOp().getKeywords() + sSP +
          getQuantifier().getKeywords() + sLEFT_PAREN + getQueryExpression().format() + sRIGHT_PAREN;
      }
      else if (getCompOp() != null)
        s = s + sSP + getCompOp().getKeywords() + sSP + getSecondRowValuePredicand().format();
      else if (getNullCondition() != null)
        s = s + sSP + getNullCondition().getKeywords();
      else if (getSetCondition() != null)
        s = s + sSP + getSetCondition().getKeywords();
      else if (getBooleanValueExpression() != null)
        s = sLEFT_PAREN + getBooleanValueExpression().format() + sRIGHT_PAREN;
      else if (getValueExpressionPrimary() != null)
        s = getValueExpressionPrimary().format();
    }
    return s;
  } /* format */
  
  /*------------------------------------------------------------------*/
  /** evaluate a LIKE condition
   * @param sValue value.
   * @param sPattern pattern to be matched.
   * @param sEscape escape letter.
   * @return Boolean.TRUE if LIKE is true.
   */
  private Boolean evaluateLike(String sValue, String sPattern, String sEscape)
  {
    Boolean bValue = null;
    if (sValue != null)
    {
      /* wild cards % and _ can be escaped */
      if ((sValue != null) && (sPattern != null))
      {
        bValue = Boolean.TRUE;
        int iPattern = 0;
        boolean bEscaped = false;
        for (int iPosition = 0;
             (Boolean.TRUE.equals(bValue)) && (iPosition < sValue.length()); 
             iPosition++)
        {
          char c = sValue.charAt(iPosition);
          char cPattern = sValue.charAt(iPattern);
          if ((sEscape == null) || (c != sEscape.charAt(0)))
          {
            if (!bEscaped)
            {
              if ((cPattern != '_') && (cPattern != '%'))
              {
                if (c != cPattern)
                  bValue = Boolean.FALSE;
              }
              else if (cPattern == '%')
              {
                if (iPattern < sPattern.length())
                {
                  iPosition++;
                  if (iPosition < sValue.length())
                  {
                    iPattern++;
                    cPattern = sPattern.charAt(iPattern);
                    for (c = sValue.charAt(iPosition); 
                        (c != cPattern) && (iPosition < sValue.length()); 
                         c = sValue.charAt(iPosition))
                      iPosition++;
                    iPattern--;
                  }
                  iPosition--;
                }
              }
            }
            else // escaped
            {
              if ((c == '_') || (c == '%'))
              {
                if (c != cPattern)
                  bValue = Boolean.FALSE;
              }
              else
                throw new IllegalArgumentException("Only '_' and '%' can be escaped with a LIKE escape!");
            }
            iPattern++;
            bEscaped = false;
          }
          else
            bEscaped = true;
        }
      }
    }
    return bValue;
  } /* evaluateLike */

  /*------------------------------------------------------------------*/
  /** evaluate the comparison of two values.
   * @param co comparison operator.
   * @param oValue first value to be compared.
   * @param oValue2 second value to be compared.
   * @return evaluation result.
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  private Boolean evaluateComparison(CompOp co, Object oValue, Object oValue2)
  {
    Boolean bValue = null;
    if (oValue != null)
    {
      Comparable cmpValue = null;
      if (oValue instanceof Comparable)
        cmpValue = (Comparable)oValue;
      switch(co)
      {
        case EQUALS_OPERATOR:
          bValue = Boolean.valueOf(oValue.equals(oValue2));
          break;
        case NOT_EQUALS_OPERATOR:
          bValue = Boolean.valueOf(!oValue.equals(oValue2));
          break;
        case LESS_THAN_OPERATOR:
          bValue = Boolean.valueOf(cmpValue.compareTo(oValue2) < 0);
          break;
        case LESS_THAN_OR_EQUALS_OPERATOR:
          bValue = Boolean.valueOf(cmpValue.compareTo(oValue2) <= 0);
          break;
        case GREATER_THAN_OPERATOR:
          bValue = Boolean.valueOf(cmpValue.compareTo(oValue2) > 0);
          break;
        case GREATER_THAN_OR_EQUALS_OPERATOR:
          bValue = Boolean.valueOf(cmpValue.compareTo(oValue2) >= 0);
          break;
      }
    }
    return bValue;
  } /* evaluateComparison */
  
  /*------------------------------------------------------------------*/
  /** evaluate the given NULL condition for the given value.
   * @param nc NULL condition.
   * @param oValue value.
   * @return true, if value fulfills the NULL condition.
   */
  private Boolean evaluateNullCondition(NullCondition nc, Object oValue)
  {
    Boolean bValue = null;
    if (oValue != null)
    {
      switch(nc)
      {
        case IS_NULL: bValue = Boolean.valueOf(oValue == null); break;
        case IS_NOT_NULL: bValue = Boolean.valueOf(oValue != null); break;
      }
    }
    return bValue;
  } /* evaluateNullCondition */

  /*------------------------------------------------------------------*/
  /** evaluate a boolean primary from its components.
   * @return Boolean.TRUE, Boolean.FALSE oder null for the value.
   */
  private Boolean evaluate(
    Object oRowValuePredicand,
    Object oSecondRowValuePredicand,
    Object oStringValue,
    Boolean bBooleanValue,
    Object oValuePrimary)
  {
    Boolean bValue = null;
    BooleanCondition bc = getBooleanCondition();
    if (bc != null)
    {
      if (bc == BooleanCondition.LIKE)
        bValue = evaluateLike((String)oRowValuePredicand,(String)oStringValue,getEscapeLetter());
      else
        throw new IllegalArgumentException("Boolean condition "+bc.getKeywords()+" cannot be evaluated!");
    }
    else
    {
      if (getQuantifier() != null) /* Quantifier: ANY, ALL, SOME */
        throw new IllegalArgumentException("Quantifier "+getQuantifier().getKeywords()+" cannot be evaluated!");
      else if (getCompOp() != null)
        bValue = evaluateComparison(getCompOp(),oRowValuePredicand,oSecondRowValuePredicand);
      else if (getNullCondition() != null)
        bValue = evaluateNullCondition(getNullCondition(),oRowValuePredicand);
      else if (getSetCondition() != null)
        throw new IllegalArgumentException("SET condition cannot be evaluated!");
      else if (getBooleanValueExpression() != null)
        bValue = bBooleanValue;
      else if (getValueExpressionPrimary() != null)
        bValue = (Boolean)oValuePrimary;
    }
    return bValue;
  } /* evaluate */
  
  /*------------------------------------------------------------------*/
  /** evaluate the boolean primary against the context of a query.
   * @param ss sql statement.
   * @param bAggregated true, if the value occurs in the argument of an
   *        aggregating function.
   * @return Boolean.TRUE, Boolean.FALSE oder null for the value.
   */
  public Boolean evaluate(SqlStatement ss, boolean bAggregated)
  {
    Object oRowValuePredicand = null;
    if (getRowValuePredicand() != null)
      oRowValuePredicand = getRowValuePredicand().evaluate(ss, bAggregated);
    Object oSecondRowValuePredicand = null;
    if (getSecondRowValuePredicand() != null)
      oSecondRowValuePredicand = getSecondRowValuePredicand().evaluate(ss, bAggregated);
    Object oStringValue = null;
    if (getStringValueExpression() != null)
      oStringValue = getStringValueExpression().evaluate(ss,bAggregated);
    Boolean bBooleanValue = null;
    if (getBooleanValueExpression() != null)
      bBooleanValue = getBooleanValueExpression().evaluate(ss, bAggregated);
    Object oValuePrimary = null;
    if (getValueExpressionPrimary() != null)
      oValuePrimary = getValueExpressionPrimary().evaluate(ss, bAggregated);
    Boolean bValue = evaluate(oRowValuePredicand, oSecondRowValuePredicand,
      oStringValue, bBooleanValue, oValuePrimary);
    return bValue;
  } /* evaluate */
  
  /*------------------------------------------------------------------*/
  /** reset the aggregate values in a boolean primary expression to 
   * their initial value.
   * @param ss sql statement.
   * @return final value before reset.
   */
  public Boolean resetAggregates(SqlStatement ss)
  {
    Object oRowValuePredicand = null;
    if (getRowValuePredicand() != null)
      oRowValuePredicand = getRowValuePredicand().resetAggregates(ss);
    Object oSecondRowValuePredicand = null;
    if (getSecondRowValuePredicand() != null)
      oSecondRowValuePredicand = getSecondRowValuePredicand().resetAggregates(ss);
    Object oStringValue = null;
    if (getStringValueExpression() != null)
      oStringValue = getStringValueExpression().resetAggregates(ss);
    Boolean bBooleanValue = null;
    if (getBooleanValueExpression() != null)
      bBooleanValue = getBooleanValueExpression().resetAggregates(ss);
    Object oValuePrimary = null;
    if (getValueExpressionPrimary() != null)
      oValuePrimary = getValueExpressionPrimary().resetAggregates(ss);
    Boolean bValue = evaluate(oRowValuePredicand, oSecondRowValuePredicand,
      oStringValue, bBooleanValue, oValuePrimary);
    return bValue;
  } /* resetAggregates */
  
  /*------------------------------------------------------------------*/
  /** parse the boolean primary from the parsing tree context.
   * @param ctx parsing context (tree).
   * @throws NullPointerException if no parsing tree is available. 
   */
  public void parse(SqlParser.BooleanPrimaryContext ctx)
  {
    setContext(ctx);
    getVisitor().visit(getContext());
  } /* parse */
  
  /*------------------------------------------------------------------*/
  /** parse the boolean primary from SQL.
   * @param sSql SQL.
   */
  @Override
  public void parse(String sSql)
  {
    setParser(newSqlParser(sSql));
    parse(getParser().booleanPrimary());
  } /* parse */

  /*------------------------------------------------------------------*/
  /** initialize a boolean primary.
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
   * @param bve boolean value expression.
   * @param vep value expression primary.
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
      List<Boolean> listExclusives,
      BooleanValueExpression bve,
      ValueExpressionPrimary vep
    )
  {
    _il.enter(co, String.valueOf(bNot), bc, rve, rve2, rve3, so, listRve, 
        qe, sve, sEscapeLetter, nc, q, bUnique, mt, mve, sc, 
        listUdtNames, listExclusives, bve, vep);
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
    setBooleanValueExpression(bve);
    setValueExpressionPrimary(vep);
    _il.exit();
  } /* initialize */
  
  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public BooleanPrimary(SqlFactory sf)
  {
    super(sf);
  } /* constructor */

} /* class BooleanPrimary */
