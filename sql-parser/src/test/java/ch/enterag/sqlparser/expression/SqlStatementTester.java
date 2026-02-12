package ch.enterag.sqlparser.expression;


import java.math.*;
import java.util.*;
import static org.junit.Assert.*;
import org.junit.*;
import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.datatype.*;
import ch.enterag.sqlparser.expression.enums.*;
import ch.enterag.sqlparser.identifier.*;

public class SqlStatementTester
{
  private SqlFactory _sf = new BaseSqlFactory();
  private SqlStatement _ss = null;

  @Before
  public void setUp()
  {
    _ss = _sf.newSqlStatement();
  }

  @Test
  public void testSelectQuery()
  {
    String sSql = "SELECT\r\n"+
      "  COLA,\r\n" +
      "  COLB,\r\n" +
      "  COLC,\r\n" +
      "  COLD,\r\n" +
      "  COLE,\r\n" +
      "  COLF,\r\n" +
      "  COLG,\r\n" +
      "  COLH,\r\n" +
      "  COLI,\r\n" +
      "  COLJ,\r\n" +
      "  COLK,\r\n" +
      "  COLL,\r\n" +
      "  COLM,\r\n" +
      "  COLM,\r\n" +
      "  COLN,\r\n" +
      "  COLO,\r\n" +
      "  COLP,\r\n" +
      "  COLQ,\r\n" +
      "  COLR,\r\n" +
      "  COLS,\r\n" +
      "  COLT,\r\n" +
      "  COLU,\r\n" +
      "  COLV,\r\n" +
      "  COLW,\r\n" +
      "  COLX,\r\n" +
      "  COLY,\r\n" +
      "  COLZ\r\n" +
      "FROM TABA";
    SqlStatement ss = _sf.newSqlStatement();
    long lStart = System.currentTimeMillis();
    ss.parse(sSql);
    long lDuration = System.currentTimeMillis() - lStart;
    System.out.println(String.valueOf(lDuration)+" ms");
  } /* testSelectQuery */
  
  @Test
  public void testQueryEvaluation()
  {
    _ss.parse("SELECT -4+ATAB.ACOL AS COL1, 'abc' || BCOL, BALIAS.ACOL FROM ATAB, BTAB BALIAS WHERE ATAB.ID = 5");
    System.out.println(_ss.format());
    // analyze the parse tree
    QuerySpecification qs = _ss.getQuerySpecification();
    System.out.println("Tables: "+String.valueOf(qs.getTableReferences().size()));
    System.out.println("Select sublists: "+String.valueOf(qs.getSelectSublists().size()));
    
    TablePrimary tp1 = qs.getTableReferences().get(0).getTablePrimary();
    System.out.println("Table 0: " + tp1.getTableName().format());
    TablePrimary tp2 = qs.getTableReferences().get(1).getTablePrimary();
    System.out.println("Table 1: " + tp2.getTableName().format()+" AS "+tp2.getCorrelationName().format());
    
    SelectSublist ss1 = qs.getSelectSublists().get(0);
    NumericValueExpression nve = ss1.getValueExpression().getCommonValueExpression().getNumericValueExpression();
    System.out.println("Select 0 numeric value, operator: "+nve.getAdditiveOperator().getKeywords());
    NumericValueExpression nve1 = nve.getFirstOperand();
    ValueExpressionPrimary vep_0_0 = nve1.getValueExpressionPrimary();
    BigDecimal bd = vep_0_0.getUnsignedLit().getExact();
    Sign sign = nve1.getSign();
    System.out.println("Select 0, first operand: "+(sign==null?"":sign.getKeywords())+bd.toPlainString());
    NumericValueExpression nve2 = nve.getSecondOperand();
    ValueExpressionPrimary vep_0_1 = nve2.getValueExpressionPrimary();
    IdChain idc0 = vep_0_1.getGeneralValueSpecification().getColumnOrParameter();
    System.out.println("Select 0, second operand: "+idc0.format());
    
    SelectSublist ss2 = qs.getSelectSublists().get(1);
    StringValueExpression sve = ss2.getValueExpression().getCommonValueExpression().getStringValueExpression();
    System.out.println("Select 1 string value, concatenation");
    StringValueExpression sve1 = sve.getFirstOperand();
    ValueExpressionPrimary vep_1_0 = sve1.getValueExpressionPrimary();
    String s = vep_1_0.getUnsignedLit().getCharacterString();
    System.out.println("Select 1, first operand: "+s);
    StringValueExpression sve2 = sve.getSecondOperand();
    ValueExpressionPrimary vep_1_1 = sve2.getValueExpressionPrimary();
    IdChain idc1 = vep_1_1.getGeneralValueSpecification().getColumnOrParameter();
    System.out.println("Select 1, second operand: "+idc1.format());
    
    SelectSublist ss3 = qs.getSelectSublists().get(2);
    ValueExpressionPrimary vep_2 = ss3.getValueExpression().getCommonValueExpression().getValueExpressionPrimary();
    IdChain idc2 = vep_2.getGeneralValueSpecification().getColumnOrParameter();
    System.out.println("Select 2: "+idc2.format());
    
    BooleanPrimary bp = qs.getWhereCondition().getBooleanPrimary();
    System.out.println("Where condition: "+bp.getCompOp().getKeywords());
    RowValuePredicand rvp1 = bp.getRowValuePredicand();
    ValueExpressionPrimary vep_w_0 = rvp1.getCommonValueExpression().getValueExpressionPrimary();
    IdChain idcw = vep_w_0.getGeneralValueSpecification().getColumnOrParameter();
    System.out.println("Where condition first operand: "+idcw.format());
    RowValuePredicand rvp2 = bp.getSecondRowValuePredicand();
    ValueExpressionPrimary vep_w_1 = rvp2.getCommonValueExpression().getValueExpressionPrimary();
    BigDecimal bdw = vep_w_1.getUnsignedLit().getExact();
    System.out.println("Where condition, second operand: "+bdw.toPlainString());

    DataType dtInteger = _sf.newDataType();
    dtInteger.parse("INTEGER");
    DataType dtSmallInt = _sf.newDataType();
    dtSmallInt.parse("SMALLINT");
    DataType dtVarchar = _sf.newDataType();
    dtVarchar.parse("VARCHAR(256)");
    DataType dtBoolean = _sf.newDataType();
    dtBoolean.parse("BOOLEAN");
    
    _ss.setEvaluationContext("Hartwig", null, "TestSchema");
    List<String> listColumnNames = Arrays.asList(new String[]{"ID","ACOL","CCOL"});
    tp1.setColumnNames(listColumnNames);
    tp1.setColumnValue("ID", BigDecimal.valueOf(5));
    tp1.setColumnValue("ACOL", BigDecimal.valueOf(38));
    tp1.setColumnValue("BCOL","AText");
    tp1.setColumnType("ID", dtInteger);
    tp1.setColumnType("ACOL", dtSmallInt);
    tp1.setColumnType("BCOL", dtVarchar);
    listColumnNames = Arrays.asList(new String[]{"ID","ACOL","BCOL"});
    tp2.setColumnNames(listColumnNames);
    tp2.setColumnValue("ID",BigDecimal.valueOf(1));
    tp2.setColumnValue("ACOL",BigDecimal.valueOf(-18));
    tp2.setColumnValue("CCOL","BText");
    tp2.setColumnType("ID", dtInteger);
    tp2.setColumnType("ACOL", dtSmallInt);
    tp2.setColumnType("CCOL", dtVarchar);
    
    assertEquals("Wrong first SELECT type!","SMALLINT",ss1.getDataType(_ss).format());
    assertEquals("Wrong second SELECT type!","VARCHAR(259)",ss2.getDataType(_ss).format());
    assertEquals("Wrong third SELECT type!","SMALLINT",ss3.getDataType(_ss).format());
    assertEquals("Wrong WHERE condition!","BOOLEAN",qs.getWhereCondition().getDataType(_ss).format());
    
    assertEquals("Wrong first SELECT column!",BigDecimal.valueOf(34),ss1.evaluate(_ss));
    assertEquals("Wrong second SELECT column!","abcAText",ss2.evaluate(_ss));
    assertEquals("Wrong third SELECT column!",BigDecimal.valueOf(-18),ss3.evaluate(_ss));
    assertEquals("Wrong WHERE condition!",Boolean.TRUE,qs.getWhereCondition().evaluate(_ss,true));
    
    System.out.println("1: "+String.valueOf(ss1.evaluate(_ss)+" "+ss1.getDataType(_ss).format())); // 34
    System.out.println("2: "+String.valueOf(ss2.evaluate(_ss)+" "+ss2.getDataType(_ss).format())); // abcAText
    System.out.println("3: "+String.valueOf(ss3.evaluate(_ss)+" "+ss3.getDataType(_ss).format())); // -18
    System.out.println("W: "+String.valueOf(qs.getWhereCondition().evaluate(_ss,true)+" "+qs.getWhereCondition().getDataType(_ss).format())); // false
  } /* testQueryEvaluation */

} /* SqlStatementTester */
