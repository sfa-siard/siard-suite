package ch.enterag.sqlparser.expression;

import static org.junit.Assert.*;
import org.junit.*;
import ch.enterag.sqlparser.*;

public class ValueExpressionPrimaryTester
{
  private SqlFactory _sf = new BaseSqlFactory();
  private ValueExpressionPrimary _vep = null;
  
  @Before
  public void setUp()
  {
    _vep = _sf.newValueExpressionPrimary();
  }

  @Test
  public void testUnsignedLiteral()
  {
    _vep.parse("23.23E7");
    // System.out.println(_vep.format());
    assertEquals("Unsigned literal not recognized!","2.323E8",_vep.format());
  }

  @Test
  public void testBooleanLiteral()
  {
    _vep.parse("true");
    // System.out.println(_vep.format());
    assertEquals("Boolean literal not recognized!","TRUE",_vep.format());
  }

  @Test
  public void testDateLiteral()
  {
    _vep.parse("date'2016-4-28'");
    System.out.println(_vep.format());
    assertEquals("Date literal not recognized!","DATE'2016-04-28'",_vep.format());
  }

  @Test
  public void TestInterval()
  {
    Interval iv = new Interval(1,123,3);
    _vep.parse(SqlLiterals.formatIntervalLiteral(iv));
    // System.out.println(_ul.format());
    assertEquals("INTERVAL not recognized!","INTERVAL '123-3' YEAR(3) TO MONTH",_vep.format());
  }
  
  @Test
  public void testSimpleVariable()
  {
    _vep.parse(":a1");
    // System.out.println(_vep.format());
    assertEquals("Variable reference not recognized!",":A1",_vep.format());
  }

  @Test
  public void testComplexVariable()
  {
    _vep.parse(":a1 indicator :b1");
    // System.out.println(_vep.format());
    assertEquals("Complex variable reference not recognized!",":A1 INDICATOR :B1",_vep.format());
  }
  
  @Test
  public void testDynamicReference()
  {
    _vep.parse("?");
    // System.out.println(_vep.format());
    assertEquals("Dynamic reference not recognized!","?",_vep.format());
  }
  
  @Test
  public void testCurrentPath()
  {
    _vep.parse("CURRENT_PATH");
    // System.out.println(_vep.format());
    assertEquals("CURRENT_PATH not recognized!","CURRENT_PATH",_vep.format());
  }
  
  @Test
  public void testCurrentRole()
  {
    _vep.parse("current_role");
    // System.out.println(_vep.format());
    assertEquals("CURRENT_ROLE not recognized!","CURRENT_ROLE",_vep.format());
  }
  
  @Test
  public void testCurrentUser()
  {
    _vep.parse("CURRENT_user");
    // System.out.println(_vep.format());
    assertEquals("CURRENT_USER not recognized!","CURRENT_USER",_vep.format());
  }
  
  @Test
  public void testSessionUser()
  {
    _vep.parse("session_user");
    // System.out.println(_vep.format());
    assertEquals("SESSION_USER not recognized!","SESSION_USER",_vep.format());
  }
  
  @Test
  public void testSystemUser()
  {
    _vep.parse("system_user");
    // System.out.println(_vep.format());
    assertEquals("SYSTEM_USER not recognized!","SYSTEM_USER",_vep.format());
  }
  
  @Test
  public void testUser()
  {
    _vep.parse("user");
    // System.out.println(_vep.format());
    assertEquals("USER not recognized!","USER",_vep.format());
  }
  
  @Test
  public void testValue()
  {
    _vep.parse("value");
    // System.out.println(_vep.format());
    assertEquals("VALUE not recognized!","VALUE",_vep.format());
  }
  
  @Test
  public void testSimpleReference()
  {
    _vep.parse("a1");
    // System.out.println(_vep.format());
    assertEquals("Simple Reference not recognized!","A1",_vep.format());
  }

  @Test
  public void testComplexReference()
  {
    _vep.parse("aa.bb.ddd.\"am\".ccd");
    // System.out.println(_vep.format());
    assertEquals("Complex reference not recognized!","AA.BB.DDD.\"am\".CCD",_vep.format());
  }

  @Test
  public void testAggregateFunction()
  {
    _vep.parse("COUNT( * )");
    // System.out.println(_vep.format());
    assertEquals("Aggregate function not recognized!","COUNT(*)",_vep.format());
  }

  @Test
  public void testGroupingOperation()
  {
    _vep.parse("GROUPING(s.t.c1,s.t.c2,s.t.c3)");
    // System.out.println(_vep.format());
    assertEquals("Grouping operation not recognized!","GROUPING(S.T.C1, S.T.C2, S.T.C3)",_vep.format());
  }

  @Test
  public void testWindowRank()
  {
    _vep.parse("RANK() OVER (wn ORDER BY t.c1, t.c2)");
    // System.out.println(_vep.format());
    assertEquals("Window rank function not recognized!","RANK() OVER (WN ORDER BY T.C1, T.C2)",_vep.format());
  }

  @Test
  public void testWindowRowNumber()
  {
    _vep.parse("ROW_NUMBER() OVER (WN PARTITION BY T.C1)");
    // System.out.println(_vep.format());
    assertEquals("Window ROW_NUMBER function not recognized!","ROW_NUMBER() OVER (WN PARTITION BY T.C1)",_vep.format());
  }

  @Test
  public void testWindowAggregate()
  {
    _vep.parse("AVG(A1) OVER WN");
    // System.out.println(_vep.format());
    assertEquals("Window aggregate function not recognized!","AVG(A1) OVER WN",_vep.format());
  }

  @Test
  public void testWindowAggregateWithFrame()
  {
    _vep.parse("AVG(A1) OVER (WN RANGE UNBOUNDED PRECEDING)");
    // System.out.println(_vep.format());
    assertEquals("Window aggregate function with frame not recognized!","AVG(A1) OVER (WN RANGE UNBOUNDED PRECEDING)",_vep.format());
  }

  @Test
  public void testWindowAggregateWithFrameBetween()
  {
    _vep.parse("AVG(A1) OVER (WN ROWS BETWEEN UNBOUNDED PRECEDING AND 5 FOLLOWING)");
    // System.out.println(_vep.format());
    assertEquals("Window aggregate function with frame not recognized!","AVG(A1) OVER (WN ROWS BETWEEN UNBOUNDED PRECEDING AND 5 FOLLOWING)",_vep.format());
  }

  @Test
  public void testScalarSubquery()
  {
    _vep.parse("(SELECT COUNT(*) FROM C1.S1.T1)");
    // System.out.println(_vep.format());
    assertEquals("Scalar subquery not recognized!","(SELECT\r\n  COUNT(*)\r\nFROM C1.S1.T1)",_vep.format());
  }

  @Test
  public void testCaseExpression()
  {
    _vep.parse("CASE WHEN A1 IS NULL THEN -1 WHEN B1 = -1 THEN 0 ELSE C1.S1.TEST.COL END");
    // System.out.println(_vep.format());
    assertEquals("CASE expression not recognized!","CASE\r\n  WHEN A1 IS NULL THEN -1\r\n  WHEN B1 = -1 THEN 0\r\n  ELSE C1.S1.TEST.COL\r\nEND",_vep.format());
  }

  @Test
  public void testCastExpression()
  {
    _vep.parse("CAST (Test as INTEGER)");
    // System.out.println(_vep.format());
    assertEquals("CAST expression not recognized!","CAST(TEST AS INT)",_vep.format());
  }

  @Test
  public void testSubtypeTreatment()
  {
    _vep.parse("TREAT (5 as ref (udt1) scope testtable)");
    // System.out.println(_vep.format());
    assertEquals("TREAT expression not recognized!","TREAT(5 AS(UDT1) SCOPE TESTTABLE)",_vep.format());
  }

  @Test
  public void testMethodInvocation()
  {
    _vep.parse("C1.S1.TY1.M1(A1)");
    // System.out.println(_vep.format());
    assertEquals("Method invocation not recognized!","C1.S1.TY1.M1(A1)",_vep.format());
  }
  
  @Test
  public void testGeneralizedInvocation()
  {
    _vep.parse("(5 as date).M1(A1,b1)");
    // System.out.println(_vep.format());
    assertEquals("Generalized invocation not recognized!","(5 AS DATE).M1(A1, B1)",_vep.format());
  }
  
  @Test
  public void testStaticMethodInvocation()
  {
    _vep.parse("udt2::m2(A1,b1,c2)");
    // System.out.println(_vep.format());
    assertEquals("Static method invocation not recognized!","UDT2::M2(A1, B1, C2)",_vep.format());
  }
  
  @Test
  public void testNewSpecification()
  {
    _vep.parse("new c1.s1.r1(null)");
    System.out.println(_vep.format());
    assertEquals("NEW specification not recognized!","NEW C1.S1.R1(NULL)",_vep.format());
  }
  
  @Test
  public void testDereference()
  {
    _vep.parse("c1.s1.ty1->a1(b1,c2)");
    // System.out.println(_vep.format());
    assertEquals("Dereference not recognized!","C1.S1.TY1->A1(B1, C2)",_vep.format());
  }
  
  @Test
  public void testReferenceResolution()
  {
    _vep.parse("deref (a1)");
    // System.out.println(_vep.format());
    assertEquals("Reference resolution not recognized!","DEREF(A1)",_vep.format());
  }
  
  @Test
  public void testArrayValueConstructor()
  {
    _vep.parse("ARRAY[5,'test',DATE'1986-04-28', t.c]");
    System.out.println(_vep.format());
    assertEquals("Array value constructor not recognized!","ARRAY[5, 'test', DATE'1986-04-28', T.C]",_vep.format());
  }

  @Test
  public void testSearchedArrayValueConstructor()
  {
    _vep.parse("ARRAY(SELECT A1, B1, C1 FROM TEST ORDER BY A1 ASC, B1 DESC)");
    // System.out.println(_vep.format());
    assertEquals("Searched array value constructor not recognized!","ARRAY(SELECT\r\n  A1,\r\n  B1,\r\n  C1\r\nFROM TEST\r\nORDER BY A1 ASC, B1 DESC)",_vep.format());
  }
  
  @Test
  public void testMultisetValueConstructor()
  {
    _vep.parse("Multiset[5,'test',DATE'1986-04-28', t.c]");
    // System.out.println(_vep.format());
    assertEquals("Multiset value constructor not recognized!","MULTISET[5, 'test', DATE'1986-04-28', T.C]",_vep.format());
  }

  @Test
  public void testSearchedMultisetValueConstructor()
  {
    _vep.parse("Multiset(SELECT A1, B1, C1 FROM TEST)");
    // System.out.println(_vep.format());
    assertEquals("Searched multiset value constructor not recognized!","MULTISET(SELECT\r\n  A1,\r\n  B1,\r\n  C1\r\nFROM TEST)",_vep.format());
  }

  @Test
  public void testTableMultisetValueConstructor()
  {
    _vep.parse("table(SELECT A1, B1, C1 FROM TEST)");
    // System.out.println(_vep.format());
    assertEquals("Table multiset value constructor not recognized!","TABLE(SELECT\r\n  A1,\r\n  B1,\r\n  C1\r\nFROM TEST)",_vep.format());
  }

  @Test
  public void testConcatenatedArraysElement()
  {
    _vep.parse("(A1 || B)[4]");
    // System.out.println(_vep.format());
    assertEquals("Concatenated array not recognized!","(A1 || B)[4]",_vep.format());
  }

  @Test
  public void testArrayElementReference()
  {
    _vep.parse("ArrayName[4]");
    // System.out.println(_vep.format());
    assertEquals("Array element reference not recognized!","ARRAYNAME[4]",_vep.format());
  }

  @Test
  public void testMultisetElementReference()
  {
    _vep.parse("element(3)");
    // System.out.println(_vep.format());
    assertEquals("Multiset element reference not recognized!","ELEMENT(3)",_vep.format());
  }

  @Test
  public void testRoutineInvocation()
  {
    _vep.parse("s1.r1(3,col)");
    // System.out.println(_vep.format());
    assertEquals("Routine invocation not recognized!","S1.R1(3, COL)",_vep.format());
  }

  @Test
  public void testNextValueExpression()
  {
    _vep.parse("next value for c1.s1.seq1");
    // System.out.println(_vep.format());
    assertEquals("Next value expression not recognized!","NEXT VALUE FOR C1.S1.SEQ1",_vep.format());
  }

  @Test
  public void testGeneralValueExpression()
  {
    _vep.parse("aa.bb.ddd.\"am\".ccd");
    // System.out.println(_vep.format());
    assertEquals("General value expression not recognized!","AA.BB.DDD.\"am\".CCD",_vep.format());
  }

  @Test
  public void testParenthesizedValueExpressionPrimary()
  {
    _vep.parse("(aa.bb.ddd.\"am\".ccd)");
    // System.out.println(_vep.format());
    assertEquals("Parenthesized value expression primary not recognized!","(AA.BB.DDD.\"am\".CCD)",_vep.format());
  }

}
