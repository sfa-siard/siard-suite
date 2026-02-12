package ch.enterag.sqlparser.antlr4;

import java.text.*;
import org.antlr.v4.runtime.tree.*;
import ch.enterag.sqlparser.ddl.enums.*;
import ch.enterag.sqlparser.dml.enums.*;
import ch.enterag.sqlparser.expression.enums.*;
import ch.enterag.sqlparser.generated.*;
import ch.enterag.sqlparser.identifier.*;

public class EnhancedSqlBaseVisitor<T> extends SqlBaseVisitor<T>
{
  /* resolve data type labels */
  public T visitDataType(SqlParser.DataTypeContext ctx)
  {
    return visitChildren(ctx);
  }
  @Override
  public T visitPreType(SqlParser.PreTypeContext ctx)
  {
    return visitDataType(ctx);
  }
  @Override 
  public T visitStructType(SqlParser.StructTypeContext ctx)
  {
    return visitDataType(ctx);
  }
  @Override
  public T visitRowType(SqlParser.RowTypeContext ctx)
  {
    return visitDataType(ctx);
  }
  @Override 
  public T visitRefType(SqlParser.RefTypeContext ctx)
  {
    return visitDataType(ctx);
  }
  @Override
  public T visitArrayType(SqlParser.ArrayTypeContext ctx)
  {
    return visitDataType(ctx);
  }
  @Override
  public T visitMultisetType(SqlParser.MultisetTypeContext ctx)
  {
    return visitDataType(ctx);
  }
  /* resolve numeric value function labels */
  public T visitNumericValueFunction(SqlParser.NumericValueFunctionContext ctx)
  {
    return visitChildren(ctx);
  }
  @Override 
  public T visitPositionExpression(SqlParser.PositionExpressionContext ctx)
  {
    return visitNumericValueFunction(ctx);
  }
  @Override 
  public T visitExtractExpression(SqlParser.ExtractExpressionContext ctx)
  {
    return visitNumericValueFunction(ctx);
  }
  @Override 
  public T visitLengthExpression(SqlParser.LengthExpressionContext ctx)
  {
    return visitNumericValueFunction(ctx);
  }
  @Override 
  public T visitCardinalityExpression(SqlParser.CardinalityExpressionContext ctx)
  {
    return visitNumericValueFunction(ctx);
  }
  @Override 
  public T visitAbsoluteValueExpression(SqlParser.AbsoluteValueExpressionContext ctx)
  {
    return visitNumericValueFunction(ctx);
  }
  @Override 
  public T visitModulusExpression(SqlParser.ModulusExpressionContext ctx)
  {
    return visitNumericValueFunction(ctx);
  }
  @Override 
  public T visitNaturalLogarithm(SqlParser.NaturalLogarithmContext ctx)
  {
    return visitNumericValueFunction(ctx);
  }
  @Override 
  public T visitExponentialFunction(SqlParser.ExponentialFunctionContext ctx)
  {
    return visitNumericValueFunction(ctx);
  }
  @Override 
  public T visitPowerFunction(SqlParser.PowerFunctionContext ctx)
  {
    return visitNumericValueFunction(ctx);
  }
  @Override 
  public T visitSquareRoot(SqlParser.SquareRootContext ctx)
  {
    return visitNumericValueFunction(ctx);
  }
  @Override 
  public T visitFloorFunction(SqlParser.FloorFunctionContext ctx)
  {
    return visitNumericValueFunction(ctx);
  }
  @Override 
  public T visitCeilingFunction(SqlParser.CeilingFunctionContext ctx)
  {
    return visitNumericValueFunction(ctx);
  }
  @Override 
  public T visitWidthBucketFunction(SqlParser.WidthBucketFunctionContext ctx)
  {
    return visitNumericValueFunction(ctx);
  }
  /* resolve value expression primary labels */
  public T visitValueExpressionPrimary(SqlParser.ValueExpressionPrimaryContext ctx)
  {
    return visitChildren(ctx);
  }
  @Override
  public T visitUnsignedLit(SqlParser.UnsignedLitContext ctx)
  {
    return visitValueExpressionPrimary(ctx);
  }
  @Override
  public T visitGeneralValueSpec(SqlParser.GeneralValueSpecContext ctx)
  {
    return visitValueExpressionPrimary(ctx);
  }
  /**
  @Override
  public T visitColumnRef(SqlParser.ColumnRefContext ctx)
  {
    return visitValueExpressionPrimary(ctx);
  }
  **/
  @Override
  public T visitAggregateFunc(SqlParser.AggregateFuncContext ctx)
  {
    return visitValueExpressionPrimary(ctx);
  }
  @Override
  public T visitGroupingOp(SqlParser.GroupingOpContext ctx)
  {
    return visitValueExpressionPrimary(ctx);
  }
  @Override
  public T visitWindowFunc(SqlParser.WindowFuncContext ctx)
  {
    return visitValueExpressionPrimary(ctx);
  }
  @Override
  public T visitScalarSubq(SqlParser.ScalarSubqContext ctx)
  {
    return visitValueExpressionPrimary(ctx);
  }
  @Override
  public T visitCaseExp(SqlParser.CaseExpContext ctx)
  {
    return visitValueExpressionPrimary(ctx);
  }
  @Override
  public T visitCastSpec(SqlParser.CastSpecContext ctx)
  {
    return visitValueExpressionPrimary(ctx);
  }
  @Override
  public T visitSubtypeTreat(SqlParser.SubtypeTreatContext ctx)
  {
    return visitValueExpressionPrimary(ctx);
  }
  @Override
  public T visitMethodInvoc(SqlParser.MethodInvocContext ctx)
  {
    return visitValueExpressionPrimary(ctx);
  }
  @Override
  public T visitGeneralizedMethodInvoc(SqlParser.GeneralizedMethodInvocContext ctx)
  {
    return visitValueExpressionPrimary(ctx);
  }
  @Override
  public T visitStaticMethodInvoc(SqlParser.StaticMethodInvocContext ctx)
  {
    return visitValueExpressionPrimary(ctx);
  }
  @Override
  public T visitNewSpec(SqlParser.NewSpecContext ctx)
  {
    return visitValueExpressionPrimary(ctx);
  }
  @Override
  public T visitAttributeOrMethodRef(SqlParser.AttributeOrMethodRefContext ctx)
  {
    return visitValueExpressionPrimary(ctx);
  }
  @Override
  public T visitReferenceRes(SqlParser.ReferenceResContext ctx)
  {
    return visitValueExpressionPrimary(ctx);
  }
  @Override
  public T visitArrayValueConstruct(SqlParser.ArrayValueConstructContext ctx)
  {
    return visitValueExpressionPrimary(ctx);
  }
  @Override
  public T visitMultisetValueConstruct(SqlParser.MultisetValueConstructContext ctx)
  {
    return visitValueExpressionPrimary(ctx);
  }
  @Override
  public T visitArrayElementRefConcat(SqlParser.ArrayElementRefConcatContext ctx)
  {
    return visitValueExpressionPrimary(ctx);
  }
  @Override
  public T visitArrayElementRef(SqlParser.ArrayElementRefContext ctx)
  {
    return visitValueExpressionPrimary(ctx);
  }
  @Override
  public T visitMultisetElementRef(SqlParser.MultisetElementRefContext ctx)
  {
    return visitValueExpressionPrimary(ctx);
  }
  @Override
  public T visitRoutineInvoc(SqlParser.RoutineInvocContext ctx)
  {
    return visitValueExpressionPrimary(ctx);
  }
  @Override
  public T visitNextValueExp(SqlParser.NextValueExpContext ctx)
  {
    return visitValueExpressionPrimary(ctx);
  }
  @Override
  public T visitValueExpressionPrimaryParen(SqlParser.ValueExpressionPrimaryParenContext ctx)
  {
    return visitValueExpressionPrimary(ctx);
  }

  protected void setSchemaName(SqlParser.SchemaNameContext ctx, SchemaId sId)
  {
    try 
    {
      if (ctx.IDENTIFIER().size() == 1)
        sId.parseSchema(ctx.IDENTIFIER(0).getText());
      else if (ctx.IDENTIFIER().size() == 2)
      {
        sId.parseCatalog(ctx.IDENTIFIER(0).getText());
        sId.parseSchema(ctx.IDENTIFIER(1).getText());
      }
    }
    catch (ParseException pe) { throw new IllegalArgumentException("Error visiting SchemaName!", pe); }
  }
  protected void setQualifiedId(SqlParser.QualifiedIdContext ctx, QualifiedId qId)
  {
    try 
    {
      if (ctx.IDENTIFIER().size() == 1)
        qId.parseName(ctx.IDENTIFIER(0).getText());
      else if (ctx.IDENTIFIER().size() == 2)
      {
        qId.parseSchema(ctx.IDENTIFIER(0).getText());
        qId.parseName(ctx.IDENTIFIER(1).getText());
      }
      else if (ctx.IDENTIFIER().size() == 3)
      {
        qId.parseCatalog(ctx.IDENTIFIER(0).getText());
        qId.parseSchema(ctx.IDENTIFIER(1).getText());
        qId.parseName(ctx.IDENTIFIER(2).getText());
      }
    }
    catch (ParseException pe) { throw new IllegalArgumentException("Error visiting qualified identifier!", pe); }
  }
  protected void setTableName(SqlParser.TableNameContext ctx, QualifiedId qId)
  {
    setQualifiedId(ctx.qualifiedId(),qId);
  }
  protected void setConstraintName(SqlParser.ConstraintNameContext ctx, QualifiedId qId)
  {
    setQualifiedId(ctx.qualifiedId(),qId);
  }
  protected void setUdtName(SqlParser.UdtNameContext ctx, QualifiedId qId)
  {
    setQualifiedId(ctx.qualifiedId(),qId);
  }
  protected void setRoutineName(SqlParser.RoutineNameContext ctx, QualifiedId qId)
  {
    setQualifiedId(ctx.qualifiedId(),qId);
  }
  protected void setTriggerName(SqlParser.TriggerNameContext ctx, QualifiedId qId)
  {
    setQualifiedId(ctx.qualifiedId(),qId);
  }
  protected void setSpecificMethodName(SqlParser.SpecificMethodNameContext ctx, QualifiedId qId)
  {
    setQualifiedId(ctx.qualifiedId(),qId);
  }
  protected void setSequenceName(SqlParser.SequenceNameContext ctx, QualifiedId qId)
  {
    setQualifiedId(ctx.qualifiedId(),qId);
  }
  protected void setIdentifier(TerminalNode tn, Identifier id)
  {
    try { id.parse(tn.getText()); }
    catch (ParseException pe) { throw new IllegalArgumentException("Error visiting Identifier!", pe); }
  }
  protected void addIdentifier(TerminalNode tn, IdList il)
  {
    try { il.parseAdd(tn.getText()); }
    catch (ParseException pe) { throw new IllegalArgumentException("Error visiting Identifier!", pe); }
  }
  protected void setColumnName(SqlParser.ColumnNameContext ctx, Identifier id)
  {
    setIdentifier(ctx.IDENTIFIER(),id);
  }
  protected void setParameterName(SqlParser.ParameterNameContext ctx, Identifier id)
  {
    setIdentifier(ctx.IDENTIFIER(),id);
  }
  protected void setAttributeName(SqlParser.AttributeNameContext ctx, Identifier id)
  {
    setIdentifier(ctx.IDENTIFIER(),id);
  }
  protected void setAuthorizationName(SqlParser.AuthorizationNameContext ctx, Identifier id)
  {
    setIdentifier(ctx.IDENTIFIER(),id);
  }
  protected void setMethodName(SqlParser.MethodNameContext ctx, Identifier id)
  {
    setIdentifier(ctx.IDENTIFIER(),id);
  }
  protected void addColumnName(SqlParser.ColumnNameContext ctx, IdList il)
  {
    addIdentifier(ctx.IDENTIFIER(),il);
  }
  protected void addAttributeName(SqlParser.AttributeNameContext ctx, IdList il)
  {
    addIdentifier(ctx.IDENTIFIER(),il);
  }
  protected void setVariableName(SqlParser.VariableNameContext ctx, ColonId cid)
  {
    try { cid.parse(ctx.getText()); }
    catch (ParseException pe) { throw new IllegalArgumentException("Error visiting VariableName!", pe); }
  }
  protected void setIdChain(SqlParser.IdentifierChainContext ctx, IdChain idc)
  {
    try
    {
      for (int i = 0; i < ctx.IDENTIFIER().size(); i++)
        idc.parseAdd(ctx.IDENTIFIER(i).getText());
    }
    catch (ParseException pe) { throw new IllegalArgumentException("Error visiting IdentifierChain!", pe); }
  }

  protected WithOrWithoutTimeZone getWithOrWithoutTimeZone(SqlParser.WithOrWithoutTimeZoneContext ctx)
  {
    WithOrWithoutTimeZone wowtz = null;
    if (ctx.WITHOUT() != null)
      wowtz = WithOrWithoutTimeZone.WITHOUT_TIME_ZONE;
    else if (ctx.WITH() != null)
      wowtz = WithOrWithoutTimeZone.WITH_TIME_ZONE;
    return wowtz;
  }
  protected Multiplier getMultiplier(SqlParser.MultiplierContext ctx)
  {
    Multiplier m = null;
    if (ctx.K() != null)
      m = Multiplier.K;
    else if (ctx.M() != null)
      m = Multiplier.M;
    else if (ctx.G() != null)
      m = Multiplier.G;
    return m;
  }
  protected TableConstraintType getTableConstraint(SqlParser.TableConstraintContext ctx)
  {
    TableConstraintType tct = null;
    if (ctx.UNIQUE() != null)
      tct = TableConstraintType.UNIQUE;
    else if (ctx.PRIMARY() != null)
      tct = TableConstraintType.PRIMARY_KEY;
    else if (ctx.FOREIGN() != null)
      tct = TableConstraintType.FOREIGN_KEY;
    else if (ctx.CHECK() != null)
      tct = TableConstraintType.CHECK;
    return tct;
  }
  protected ColumnConstraintType getColumnConstraint(SqlParser.ColumnConstraintContext ctx)
  {
    ColumnConstraintType cct = null;
    if (ctx.NULL() != null)
      cct = ColumnConstraintType.NOT_NULL;
    else if (ctx.UNIQUE() != null)
      cct = ColumnConstraintType.UNIQUE;
    else if (ctx.PRIMARY() != null)
      cct = ColumnConstraintType.PRIMARY_KEY;
    else if (ctx.REFERENCES() != null)
      cct = ColumnConstraintType.REFERENCES;
    else if (ctx.CHECK() != null)
      cct = ColumnConstraintType.CHECK;
    return cct;
  }
  protected ReferenceScopeCheck getReferenceScopeCheck(SqlParser.ReferenceScopeCheckContext ctx)
  {
    ReferenceScopeCheck rc = null;
    if (ctx.NOT() != null)
      rc = ReferenceScopeCheck.NOT_CHECKED;
    else
      rc = ReferenceScopeCheck.CHECKED;
    return rc;
  }
  protected ReferentialAction getReferentialAction(SqlParser.ReferentialActionContext ctx)
  {
    ReferentialAction ra = null;
    if (ctx.CASCADE() != null)
      ra = ReferentialAction.CASCADE;
    else if (ctx.NULL() != null)
      ra = ReferentialAction.SET_NULL;
    else if (ctx.DEFAULT() != null)
      ra = ReferentialAction.SET_DEFAULT;
    else if (ctx.RESTRICT() != null)
      ra = ReferentialAction.RESTRICT;
    else if (ctx.ACTION() != null)
      ra = ReferentialAction.NO_ACTION;
    return ra;
  }
  protected Match getMatch(SqlParser.MatchContext ctx)
  {
    Match match = null;
    if (ctx.FULL() != null)
      match = Match.FULL;
    else if (ctx.PARTIAL() != null)
      match = Match.PARTIAL;
    else if (ctx.SIMPLE() != null)
      match = Match.SIMPLE;
    return match;
  }
  protected IdentityOption getIdentityOption(SqlParser.IdentityOptionContext ctx)
  {
    IdentityOption io = null;
    if (ctx.INCLUDING() != null)
      io = IdentityOption.INCLUDING_IDENTITY;
    else if (ctx.EXCLUDING() != null)
      io = IdentityOption.EXCLUDING_IDENTITY;
    return io;
  }
  protected DefaultsOption getDefaultsOption(SqlParser.DefaultsOptionContext ctx)
  {
    DefaultsOption dop = null;
    if (ctx.INCLUDING() != null)
      dop = DefaultsOption.INCLUDING_DEFAULTS;
    else if (ctx.EXCLUDING() != null)
      dop = DefaultsOption.EXCLUDING_DEFAULTS;
    return dop;
  }
  protected ReferenceGeneration getReferenceGeneration(SqlParser.ReferenceGenerationContext ctx)
  {
    ReferenceGeneration rg = null;
    if (ctx.SYSTEM() != null)
      rg = ReferenceGeneration.SYSTEM_GENERATED;
    else if (ctx.USER() != null)
      rg = ReferenceGeneration.USER_GENERATED;
    else if (ctx.DERIVED() != null)
      rg = ReferenceGeneration.DERIVED;
    return rg;
  }
  protected TableScope getTableScope(SqlParser.TableScopeContext ctx)
  {
    TableScope ts = null;
    if (ctx.GLOBAL() != null)
      ts = TableScope.GLOBAL;
    else if (ctx.LOCAL() != null)
      ts = TableScope.LOCAL;
    return ts;
  }
  protected CommitAction getCommitAction(SqlParser.CommitActionContext ctx)
  {
    CommitAction ca = null;
    if (ctx.DELETE() != null)
      ca = CommitAction.DELETE;
    else if (ctx.PRESERVE() != null)
      ca = CommitAction.PRESERVE;
    return ca;
  }
  protected ParameterMode getParameterMode(SqlParser.ParameterModeContext ctx)
  {
    ParameterMode pm = null;
    if (ctx.IN() != null)
      pm = ParameterMode.IN;
    else if (ctx.OUT() != null)
      pm = ParameterMode.OUT;
    else if (ctx.INOUT() != null)
      pm = ParameterMode.INOUT;
    return pm;
  }
  protected MethodType getMethodType(SqlParser.MethodTypeContext ctx)
  {
    MethodType mt = null;
    if (ctx.INSTANCE() != null)
      mt = MethodType.INSTANCE;
    else if (ctx.STATIC() != null)
      mt = MethodType.STATIC;
    else if (ctx.CONSTRUCTOR() != null)
      mt = MethodType.CONSTRUCTOR;
    return mt;
  }
  protected LanguageName getLanguageName(SqlParser.LanguageNameContext ctx)
  {
    LanguageName ln = null;
    if (ctx.ADA() != null)
      ln = LanguageName.ADA;
    else if (ctx.C() != null)
      ln = LanguageName.C;
    else if (ctx.COBOL() != null)
      ln = LanguageName.COBOL;
    else if (ctx.FORTRAN() != null)
      ln = LanguageName.FORTRAN;
    else if (ctx.MUMPS() != null)
      ln = LanguageName.MUMPS;
    else if (ctx.PASCAL() != null)
      ln = LanguageName.PASCAL;
    else if (ctx.PLI() != null)
      ln = LanguageName.PLI;
    else if (ctx.SQL() != null)
      ln = LanguageName.SQL;
    return ln;
  }
  protected Instantiability getInstantiability(SqlParser.InstantiabilityContext ctx)
  {
    Instantiability instantiability = null;
    if (ctx.NOT() != null)
      instantiability = Instantiability.NOT_INSTANTIABLE;
    else
      instantiability = Instantiability.INSTANTIABLE;
    return instantiability;
  }
  protected Finality getFinality(SqlParser.FinalityContext ctx)
  {
    Finality finality = null;
    if (ctx.NOT() != null)
      finality = Finality.NOT_FINAL;
    else
      finality = Finality.FINAL;
    return finality;
  }
  protected ParameterStyle getParameterStyle(SqlParser.ParameterStyleContext ctx)
  {
    ParameterStyle ps = null;
    if (ctx.SQL() != null)
      ps = ParameterStyle.SQL;
    else if (ctx.GENERAL() != null)
      ps = ParameterStyle.GENERAL;
    return ps;
  }
  protected Deterministic getDeterministic(SqlParser.DeterministicContext ctx)
  {
    Deterministic deterministic = null;
    if (ctx.NOT() != null)
      deterministic = Deterministic.NOT_DETERMINISTIC;
    else
      deterministic = Deterministic.DETERMINISTIC;
    return deterministic;
  }
  protected DataAccess getDataAccess(SqlParser.DataAccessContext ctx)
  {
    DataAccess da = null;
    if (ctx.NO() != null)
      da = DataAccess.NO_SQL;
    else if (ctx.CONTAINS() != null)
      da = DataAccess.CONTAINS_SQL;
    else if (ctx.READS() != null)
      da = DataAccess.READS_SQL_DATA;
    else if (ctx.MODIFIES() != null)
      da = DataAccess.MODIFIES_SQL_DATA;
    return da;
  }
  protected NullCallClause getNullCallClause(SqlParser.NullCallClauseContext ctx)
  {
    NullCallClause ncc = null;
    if (ctx.CALLED() != null)
      ncc = NullCallClause.CALLED;
    else if (ctx.RETURNS() != null)
      ncc = NullCallClause.RETURNS_NULL;
    return ncc;
  }
  protected DropBehavior getDropBehavior(SqlParser.DropBehaviorContext ctx)
  {
    DropBehavior db = null;
    if (ctx.CASCADE() != null)
      db = DropBehavior.CASCADE;
    else if (ctx.RESTRICT() != null)
      db = DropBehavior.RESTRICT;
    return db;
  }
  protected GeneralValue getGeneralValue(SqlParser.GeneralValueSpecificationContext ctx)
  {
    GeneralValue gv = null;
    if (ctx.QUESTION_MARK() != null)
      gv = GeneralValue.QUESTION_MARK;
    else if (ctx.CURRENT_PATH() != null)
      gv = GeneralValue.CURRENT_PATH;
    else if (ctx.CURRENT_ROLE() != null)
      gv = GeneralValue.CURRENT_ROLE;
    else if (ctx.CURRENT_USER() != null)
      gv = GeneralValue.CURRENT_USER;
    else if (ctx.SESSION_USER() != null)
      gv = GeneralValue.SESSION_USER;
    else if (ctx.SYSTEM_USER() != null)
      gv = GeneralValue.SYSTEM_USER;
    else if (ctx.USER() != null)
      gv = GeneralValue.USER;
    else if (ctx.VALUE() != null)
      gv = GeneralValue.VALUE;
    return gv;
  }
  protected SetQuantifier getSetQuantifier(SqlParser.SetQuantifierContext ctx)
  {
    SetQuantifier sq = null;
    if (ctx.DISTINCT() != null)
      sq = SetQuantifier.DISTINCT;
    else if (ctx.ALL() != null)
      sq = SetQuantifier.ALL;
    return sq;
  }
  protected SetFunction getSetFunction(SqlParser.SetFunctionContext ctx)
  {
    SetFunction sf = null;
    if (ctx.AVG() != null)
      sf = SetFunction.AVG;
    else if (ctx.MAX() != null)
      sf = SetFunction.MAX;
    else if (ctx.MIN() != null)
      sf = SetFunction.MIN;
    else if (ctx.SUM() != null)
      sf = SetFunction.SUM;
    else if (ctx.EVERY() != null)
      sf = SetFunction.EVERY;
    else if (ctx.ANY() != null)
      sf = SetFunction.ANY;
    else if (ctx.SOME() != null)
      sf = SetFunction.SOME;
    else if (ctx.COUNT() != null)
      sf = SetFunction.COUNT;
    else if (ctx.STDDEV_POP() != null)
      sf = SetFunction.STDDEV_POP;
    else if (ctx.STDDEV_SAMP() != null)
      sf = SetFunction.STDDEV_SAMP;
    else if (ctx.VAR_POP() != null)
      sf = SetFunction.VAR_POP;
    else if (ctx.VAR_SAMP() != null)
      sf = SetFunction.VAR_SAMP;
    else if (ctx.COLLECT() != null)
      sf = SetFunction.COLLECT;
    else if (ctx.FUSION() != null)
      sf = SetFunction.FUSION;
    else if (ctx.INTERSECTION() != null)
      sf = SetFunction.INTERSECTION;
    return sf;
  }
  protected BinarySetFunction getBinarySetFunction(SqlParser.BinarySetFunctionContext ctx)
  {
    BinarySetFunction bsf = null;
    if (ctx.COVAR_POP() != null)
      bsf = BinarySetFunction.COVAR_POP;
    else if (ctx.COVAR_SAMP() != null)
      bsf = BinarySetFunction.COVAR_SAMP;
    else if (ctx.CORR() != null)
      bsf = BinarySetFunction.CORR;
    else if (ctx.REGR_SLOPE() != null)
      bsf = BinarySetFunction.REGR_SLOPE;
    else if (ctx.REGR_INTERCEPT() != null)
      bsf = BinarySetFunction.REGR_INTERCEPT;
    else if (ctx.REGR_COUNT() != null)
      bsf = BinarySetFunction.REGR_COUNT;
    else if (ctx.REGR_RSQUARED() != null)
      bsf = BinarySetFunction.REGR_RSQUARED;
    else if (ctx.REGR_AVGX() != null)
      bsf = BinarySetFunction.REGR_AVGX;
    else if (ctx.REGR_AVGY() != null)
      bsf = BinarySetFunction.REGR_AVGY;
    else if (ctx.REGR_SXX() != null)
      bsf = BinarySetFunction.REGR_SXX;
    else if (ctx.REGR_SYY() != null)
      bsf = BinarySetFunction.REGR_SYY;
    else if (ctx.REGR_SXY() != null)
      bsf = BinarySetFunction.REGR_SXY;
    return bsf;
  }
  protected RankFunction getRankFunction(SqlParser.RankFunctionContext ctx)
  {
    RankFunction rf = null;
    if (ctx.RANK() != null)
      rf = RankFunction.RANK;
    else if (ctx.DENSE_RANK() != null)
      rf = RankFunction.DENSE_RANK;
    else if (ctx.PERCENT_RANK() != null)
      rf = RankFunction.PERCENT_RANK;
    else if (ctx.CUME_DIST() != null)
      rf = RankFunction.CUME_DIST;
    return rf;
  }
  protected InverseDistributionFunction getInverseDistributionFunction(SqlParser.InverseDistributionFunctionContext ctx)
  {
    InverseDistributionFunction idf = null;
    if (ctx.PERCENTILE_CONT() != null)
      idf = InverseDistributionFunction.PERCENTILE_CONT;
    else if (ctx.PERCENTILE_DISC() != null)
      idf = InverseDistributionFunction.PERCENTILE_DISC;
    return idf;
  }
  protected OrderingSpecification getOrderingSpecification(SqlParser.OrderingSpecificationContext ctx)
  {
    OrderingSpecification os = null;
    if (ctx.ASC() != null)
      os = OrderingSpecification.ASC;
    else if (ctx.DESC() != null)
      os = OrderingSpecification.DESC;
    return os;
  }
  protected NullOrdering getNullOrdering(SqlParser.NullOrderingContext ctx)
  {
    NullOrdering no = null;
    if (ctx.FIRST() != null)
      no = NullOrdering.NULLS_FIRST;
    else if (ctx.LAST() != null)
      no = NullOrdering.NULL_LAST;
    return no;
  }
  protected AdditiveOperator getAdditiveOperator(SqlParser.AdditiveOperatorContext ctx)
  {
    AdditiveOperator ao = null;
    if (ctx.PLUS_SIGN() != null)
      ao = AdditiveOperator.PLUS_SIGN;
    else if (ctx.MINUS_SIGN() != null)
      ao = AdditiveOperator.MINUS_SIGN;
    return ao;
  }
  protected MultiplicativeOperator getMultiplicativeOperator(SqlParser.MultiplicativeOperatorContext ctx)
  {
    MultiplicativeOperator mo = null;
    if (ctx.ASTERISK() != null)
      mo = MultiplicativeOperator.ASTERISK;
    else if (ctx.SOLIDUS() != null)
      mo = MultiplicativeOperator.SOLIDUS;
    return mo;
  }
  /***
  protected NumericFunction getNumericFunction(SqlParser.NumericValueFunctionContext ctx)
  {
    NumericFunction nf = null;
    if (ctx.POSITION() != null)
      nf = NumericFunction.POSITION;
    else if (ctx.EXTRACT() != null)
      nf = NumericFunction.EXTRACT;
    else if ((ctx.CHAR_LENGTH() != null) || (ctx.CHARACTER_LENGTH() != null))
      nf = NumericFunction.CHARACTER_LENGTH;
    else if (ctx.OCTET_LENGTH() != null)
      nf = NumericFunction.OCTET_LENGTH;
    else if (ctx.CARDINALITY() != null)
      nf = NumericFunction.CARDINALITY;
    else if (ctx.ABS() != null)
      nf = NumericFunction.ABS;
    else if (ctx.MOD() != null)
      nf = NumericFunction.MOD;
    else if (ctx.LN() != null)
      nf = NumericFunction.LN;
    else if (ctx.EXP() != null)
      nf = NumericFunction.EXP;
    else if (ctx.POWER() != null)
      nf = NumericFunction.POWER;
    else if (ctx.SQRT() != null)
      nf = NumericFunction.SQRT;
    else if (ctx.FLOOR() != null)
      nf = NumericFunction.FLOOR;
    else if ((ctx.CEIL() != null) || (ctx.CEILING() != null))
      nf = NumericFunction.CEIL;
    else if (ctx.WIDTH_BUCKET() != null)
      nf = NumericFunction.WIDTH_BUCKET;
    return nf;
  }
  ***/
  protected StringFunction getStringFunction(SqlParser.StringValueFunctionContext ctx)
  {
    StringFunction sf = null;
    if (ctx.SUBSTRING() != null)
      sf = StringFunction.SUBSTRING;
    else if (ctx.UPPER() != null)
      sf = StringFunction.UPPER;
    else if (ctx.LOWER() != null)
      sf = StringFunction.LOWER;
    else if (ctx.TRIM() != null)
      sf = StringFunction.TRIM;
    else if (ctx.NORMALIZE() != null)
      sf = StringFunction.NORMALIZE;
    return sf;
  }
  protected DatetimeFunction getDatetimeFunction(SqlParser.DatetimeValueFunctionContext ctx)
  {
    DatetimeFunction df = null;
    if (ctx.CURRENT_DATE() != null)
      df = DatetimeFunction.CURRENT_DATE;
    else if (ctx.CURRENT_TIME() != null)
      df = DatetimeFunction.CURRENT_TIME;
    else if (ctx.CURRENT_TIMESTAMP() != null)
      df = DatetimeFunction.CURRENT_TIMESTAMP;
    else if (ctx.LOCALTIME() != null)
      df = DatetimeFunction.LOCALTIME;
    else if (ctx.LOCALTIMESTAMP() != null)
      df = DatetimeFunction.LOCALTIMESTAMP;
    return df;
  }
  protected Sign getSign(SqlParser.SignContext ctx)
  {
    Sign sign = null;
    if (ctx.PLUS_SIGN() != null)
      sign = Sign.PLUS_SIGN;
    else if (ctx.MINUS_SIGN() != null)
      sign = Sign.MINUS_SIGN;
    return sign;
  }
  protected MultisetOperator getMultisetOperator(SqlParser.MultisetOperatorContext ctx)
  {
    MultisetOperator mo = null;
    if (ctx.MULTISET() != null)
    {
      if (ctx.INTERSECT() != null)
        mo = MultisetOperator.MULTISET_INTERSECT;
      else if (ctx.UNION() != null)
        mo = MultisetOperator.MULTISET_UNION;
      else if (ctx.EXCEPT() != null)
        mo = MultisetOperator.MULTISET_EXCEPT;
    }
    return mo;
  }
  protected WindowFrameUnits getWindowFrameUnits(SqlParser.WindowFrameUnitsContext ctx)
  {
    WindowFrameUnits wfo = null;
    if (ctx.ROWS() != null)
      wfo = WindowFrameUnits.ROWS;
    else if (ctx.RANGE() != null)
      wfo = WindowFrameUnits.RANGE;
    return wfo;
  }
  protected WindowFrameExclusion getWindowFrameExclusion(SqlParser.WindowFrameExclusionContext ctx)
  {
    WindowFrameExclusion wfe = null;
    if (ctx.CURRENT() != null)
      wfe = WindowFrameExclusion.EXCLUDE_CURRENT_ROW;
    else if (ctx.GROUP() != null)
      wfe = WindowFrameExclusion.EXCLUDE_GROUP;
    else if (ctx.TIES() != null)
      wfe = WindowFrameExclusion.EXCLUDE_TIES;
    else if (ctx.OTHERS() != null)
      wfe = WindowFrameExclusion.EXCLUDE_NO_OTHERS;
    return wfe;
  }
  protected DatetimeField getDatetimeField(SqlParser.PrimaryDatetimeFieldContext ctx)
  {
    DatetimeField df = null;
    if (ctx.YEAR() != null)
      df = DatetimeField.YEAR;
    else if (ctx.MONTH() != null)
      df = DatetimeField.MONTH;
    else if (ctx.DAY() != null)
      df = DatetimeField.DAY;
    else if (ctx.HOUR() != null)
      df = DatetimeField.HOUR;
    else if (ctx.MINUTE() != null)
      df = DatetimeField.MINUTE;
    else if (ctx.SECOND() != null)
      df = DatetimeField.SECOND;
    return df;
  }
  protected TimeZoneField getTimeZoneField(SqlParser.TimeZoneFieldContext ctx)
  {
    TimeZoneField tzf = null;
    if (ctx.TIMEZONE_HOUR() != null)
      tzf = TimeZoneField.TIMEZONE_HOUR;
    else if (ctx.TIMEZONE_MINUTE() != null)
      tzf = TimeZoneField.TIMEZONE_MINUTE;
    return tzf;
  }
  protected CompOp getCompOp(SqlParser.CompOpContext ctx)
  {
    CompOp co = null;
    if (ctx.EQUALS_OPERATOR() != null)
      co = CompOp.EQUALS_OPERATOR;
    else if (ctx.NOT_EQUALS_OPERATOR() != null)
      co = CompOp.NOT_EQUALS_OPERATOR;
    else if (ctx.LESS_THAN_OPERATOR() != null)
      co = CompOp.LESS_THAN_OPERATOR;
    else if (ctx.GREATER_THAN_OPERATOR() != null)
      co = CompOp.GREATER_THAN_OPERATOR;
    else if (ctx.LESS_THAN_OR_EQUALS_OPERATOR() != null)
      co = CompOp.LESS_THAN_OR_EQUALS_OPERATOR;
    else if (ctx.GREATER_THAN_OR_EQUALS_OPERATOR() != null)
      co = CompOp.GREATER_THAN_OR_EQUALS_OPERATOR;
    return co;
  }
  protected SymmetricOption getSymmetricOption(SqlParser.SymmetricOptionContext ctx)
  {
    SymmetricOption so = null;
    if (ctx.ASYMMETRIC() != null)
      so = SymmetricOption.ASYMMETRIC;
    else if (ctx.SYMMETRIC() != null)
      so = SymmetricOption.SYMMETRIC;
    return so;
  }
  protected NullCondition getNullCondition(SqlParser.NullConditionContext ctx)
  {
    NullCondition nc = null;
    if (ctx.NOT() != null)
      nc = NullCondition.IS_NOT_NULL;
    else
      nc = NullCondition.IS_NULL;
    return nc;
  }
  protected Quantifier getQuantifier(SqlParser.QuantifierContext ctx)
  {
    Quantifier q = null;
    if (ctx.ALL() != null)
      q = Quantifier.ALL;
    else if (ctx.SOME() != null)
      q = Quantifier.SOME;
    else if (ctx.ANY() != null)
      q = Quantifier.ANY;
    return q;
  }
  protected SetCondition getSetCondition(SqlParser.SetConditionContext ctx)
  {
    SetCondition sc = null;
    if (ctx.NOT() != null)
      sc = SetCondition.IS_NOT_A_SET;
    else
      sc = SetCondition.IS_A_SET;
    return sc;
  }
  protected BooleanOperator getBooleanOperator(SqlParser.BooleanOperatorContext ctx)
  {
    BooleanOperator bo = null;
    if (ctx.AND() != null)
      bo = BooleanOperator.AND;
    else if (ctx.OR() != null)
      bo = BooleanOperator.OR;
    return bo;
  }
  protected BooleanLiteral getBooleanLiteral(TerminalNode tn)
  {
    BooleanLiteral bl = null;
    if (tn.getSymbol().getType() == SqlParser.BOOLEAN_LITERAL)
    {
      if (BooleanLiteral.TRUE.getKeywords().equals(tn.getText().toUpperCase()))
        bl = BooleanLiteral.TRUE; 
      else if (BooleanLiteral.FALSE.getKeywords().equals(tn.getText().toUpperCase()))
        bl = BooleanLiteral.FALSE;
      else if (BooleanLiteral.UNKNOWN.getKeywords().equals(tn.getText().toUpperCase()))
        bl = BooleanLiteral.UNKNOWN;
    }
    return bl;
  }
  protected QueryOperator getQueryOperator(SqlParser.QueryOperatorContext ctx)
  {
    QueryOperator qo = null;
    if (ctx.INTERSECT() != null)
      qo = QueryOperator.INTERSECT;
    else if (ctx.UNION() != null)
      qo = QueryOperator.UNION;
    else if (ctx.EXCEPT() != null)
      qo = QueryOperator.EXCEPT;
    return qo;
  }
  protected JoinType getJoinType(SqlParser.JoinTypeContext ctx)
  {
    JoinType jt = null;
    if (ctx.INNER() != null)
      jt = JoinType.INNER;
    else if (ctx.LEFT() != null)
      jt = JoinType.LEFT_OUTER;
    else if (ctx.RIGHT() != null)
      jt = JoinType.RIGHT_OUTER;
    else if (ctx.FULL() != null)
      jt = JoinType.FULL_OUTER;
    return jt;
  }
  protected SampleMethod getSampleMethod(SqlParser.SampleMethodContext ctx)
  {
    SampleMethod sm = null;
    if (ctx.BERNOULLI() != null)
      sm = SampleMethod.BERNOULLI;
    else if (ctx.SYSTEM() != null)
      sm = SampleMethod.SYSTEM;
    return sm;
  }
  protected MatchType getMatchType(SqlParser.MatchContext ctx)
  {
    MatchType mt = null;
    if (ctx.FULL() != null)
      mt = MatchType.FULL;
    else if (ctx.PARTIAL() != null)
      mt = MatchType.PARTIAL;
    else if (ctx.SIMPLE() != null)
      mt = MatchType.SIMPLE;
    return mt;
  }
  protected WithOrWithoutData getWithOrWithoutData(SqlParser.WithOrWithoutDataContext ctx)
  {
    WithOrWithoutData wowd = null;
    if (ctx.NO() != null)
      wowd = WithOrWithoutData.WITH_NO_DATA;
    else
      wowd = WithOrWithoutData.WITH_DATA;
    return wowd;
  }
  protected Deferrability getDeferrability(SqlParser.DeferrabilityContext ctx)
  {
    Deferrability def = null;
    if (ctx.NOT() != null)
      def = Deferrability.NOT_DEFERRABLE;
    else
      def = Deferrability.DEFERRABLE;
    return def;
  }
  protected ConstraintCheckTime getConstraintCheckTime(SqlParser.ConstraintCheckTimeContext ctx)
  {
    ConstraintCheckTime cct = null;
    if (ctx.DEFERRED()!= null)
      cct = ConstraintCheckTime.INITIALLY_DEFERRED;
    else if (ctx.IMMEDIATE() != null)
      cct = ConstraintCheckTime.INITIALLY_IMMEDIATE;
    return cct;
  }
  protected Levels getLevels(SqlParser.LevelsContext ctx)
  {
    Levels levels = null;
    if (ctx.CASCADED() != null)
      levels = Levels.CASCADED;
    else if (ctx.LOCAL() != null)
      levels = Levels.LOCAL;
    return levels;
  }
  protected TriggerActionTime getTriggerActionTime(SqlParser.TriggerActionTimeContext ctx)
  {
    TriggerActionTime tat = null;
    if (ctx.BEFORE()!= null)
      tat = TriggerActionTime.BEFORE;
    else if (ctx.AFTER() != null)
      tat = TriggerActionTime.AFTER;
    return tat;
  }
  protected TriggerEvent getTriggerEvent(SqlParser.TriggerEventContext ctx)
  {
    TriggerEvent te = null;
    if (ctx.INSERT() != null)
      te = TriggerEvent.INSERT;
    else if (ctx.DELETE() != null)
      te = TriggerEvent.DELETE;
    else if (ctx.UPDATE() != null)
      te = TriggerEvent.UPDATE;
    return te;
  }
  protected OldOrNew getOldOrNew(SqlParser.OldOrNewValueContext ctx)
  {
    OldOrNew oon = null;
    if (ctx.TABLE() != null)
    {
      if (ctx.OLD() != null)
        oon = OldOrNew.OLD_TABLE_AS;
      else if (ctx.NEW() != null)
        oon = OldOrNew.NEW_TABLE_AS; 
    }
    else
    {
      if (ctx.OLD() != null)
        oon = OldOrNew.OLD_ROW_AS;
      else if (ctx.NEW() != null)
        oon = OldOrNew.NEW_ROW_AS;
    }
    return oon;
  }
  protected OverrideClause getOverrideClause(SqlParser.OverrideClauseContext ctx)
  {
    OverrideClause oc = null;
    if (ctx.USER() != null)
      oc = OverrideClause.OVERRIDING_USER_VALUE;
    else if (ctx.SYSTEM() != null)
      oc = OverrideClause.OVERRIDING_SYSTEM_VALUE;
    return oc;
  }
  protected SpecialValue getSpecialValue(SqlParser.SpecialValueContext ctx)
  {
    SpecialValue sv = null;
    if (ctx.NULL() != null)
      sv = SpecialValue.NULL;
    else if (ctx.ARRAY() != null)
      sv = SpecialValue.EMPTY_ARRAY;
    else if (ctx.MULTISET() != null)
      sv = SpecialValue.EMPTY_MULTISET;
    else if (ctx.DEFAULT() != null)
      sv = SpecialValue.DEFAULT;
    return sv;
  }
}
