package ch.enterag.sqlparser.expression;

import ch.enterag.sqlparser.BaseSqlFactory;
import ch.enterag.sqlparser.SqlFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class QuerySpecificationTester {
    private SqlFactory _sf = new BaseSqlFactory();
    private QuerySpecification _qs = null;

    @BeforeEach
    public void setUp() {
        _qs = _sf.newQuerySpecification();
    }

    @Test
    public void testMinimum() {
        _qs.parse("select * from t");
        // System.out.println(_qs.format());
        assertEquals("SELECT *\r\nFROM T", _qs.format(), "Minimum query not recognized!");
    }

    @Test
    public void testSimpleSelect() {
        _qs.parse("select a1, b, c from t");
        // System.out.println(_qs.format());
        assertEquals("SELECT\r\n  A1,\r\n  B,\r\n  C\r\nFROM T", _qs.format(), "Simple select not recognized!");
    }

    @Test
    public void testComplexSelect() {
        _qs.parse("select a1 as first, t1.*, t2.* as (c1, b3) from t");
        // System.out.println(_qs.format());
        assertEquals("SELECT\r\n  A1 AS FIRST,\r\n  T1.*,\r\n  T2.* AS (C1, B3)\r\nFROM T", _qs.format(), "Complex select not recognized!");
    }

    @Test
    public void testFrom() {
        _qs.parse("select a1 as first, t1.*, t2.* as (c1, b3) from emp t1, (select * from t) as t2");
        // System.out.println(_qs.format());
        assertEquals("SELECT\r\n  A1 AS FIRST,\r\n  T1.*,\r\n  T2.* AS (C1, B3)\r\nFROM EMP AS T1, (SELECT *\r\nFROM T) AS T2", _qs.format(), "FROM expression not recognized!");
    }

    @Test
    public void testWhere() {
        _qs.parse("select a1 as first, t1.*, t2.* as (c1, b3) from emp t1, (select * from t) as t2 where T1.\"column\" > 'aa' and t2.id = t1.id");
        // System.out.println(_qs.format());
        assertEquals("SELECT\r\n  A1 AS FIRST,\r\n  T1.*,\r\n  T2.* AS (C1, B3)\r\nFROM EMP AS T1, (SELECT *\r\nFROM T) AS T2\r\nWHERE T1.\"column\" > 'aa' AND T2.ID = T1.ID", _qs.format(), "WHERE expression not recognized!");
    }

    @Test
    public void testGroup() {
        _qs.parse("select a1 as first, t1.*, t2.* as (c1, b3) from emp t1, (select * from t) as t2 where T1.\"column\" > 'aa' and t2.id = t1.id group by a1, a2, a3");
        // System.out.println(_qs.format());
        assertEquals("SELECT\r\n  A1 AS FIRST,\r\n  T1.*,\r\n  T2.* AS (C1, B3)\r\nFROM EMP AS T1, (SELECT *\r\nFROM T) AS T2\r\nWHERE T1.\"column\" > 'aa' AND T2.ID = T1.ID\r\nGROUP BY A1, A2, A3", _qs.format(), "GROUP expression not recognized!");
    }

    @Test
    public void testHaving() {
        _qs.parse("select a1 as first, t1.*, t2.* as (c1, b3) from emp t1, (select * from t) as t2 where T1.\"column\" > 'aa' and t2.id = t1.id group by a1, a2, a3 having t1.\"column\" is null");
        // System.out.println(_qs.format());
        assertEquals("SELECT\r\n  A1 AS FIRST,\r\n  T1.*,\r\n  T2.* AS (C1, B3)\r\nFROM EMP AS T1, (SELECT *\r\nFROM T) AS T2\r\nWHERE T1.\"column\" > 'aa' AND T2.ID = T1.ID\r\nGROUP BY A1, A2, A3\r\nHAVING T1.\"column\" IS NULL", _qs.format(), "HAVING expression not recognized!");
    }

    @Test
    public void testCount() {
        _qs.parse("SELECT COUNT(*) AS RECORDS FROM Cat1.Schem1.tab1");
        assertTrue(_qs.isCount(), "COUNT(*) not detected!");
        _qs.parse("SELECT COL1 AS RECORDS FROM Cat1.Schem1.tab1");
        assertFalse(_qs.isCount(), "COUNT(*) reported!");
    }

    @Test
    public void testCountAndSumQuery() {
        long lStart = System.currentTimeMillis();
        _qs.parse("SELECT COUNT(*) AS RECORDS, SUM(CHAR_LENGTH(COL1)) AS COL1_SIZE FROM CAT1.SCHEM1.TAB1");
        long lDuration = System.currentTimeMillis() - lStart;
        System.out.println(String.valueOf(lDuration) + " ms: " + _qs.format());
        // select list and from clause
    }

    @Test
    public void testCountAndSumEvaluation() {
        _qs.parse("SELECT COUNT(*) AS RECORDS, SUM(CHAR_LENGTH(COL1)) AS COL1_SIZE FROM CAT1.SCHEM1.TAB1");
        System.out.println(_qs.format());
        // analyze the parse tree
        System.out.println("Tables: " + String.valueOf(_qs.getTableReferences()
                                                          .size()));
        System.out.println("Select sublists: " + String.valueOf(_qs.getSelectSublists()
                                                                   .size()));
        SelectSublist ss1 = _qs.getSelectSublists()
                               .get(0);
        ValueExpression ve1 = ss1.getValueExpression();
        CommonValueExpression cve1 = ve1.getCommonValueExpression();
        ValueExpressionPrimary vep1 = cve1.getValueExpressionPrimary();
        AggregateFunction af1 = vep1.getAggregateFunc();
        System.out.println(af1.format());
        SelectSublist ss2 = _qs.getSelectSublists()
                               .get(1);
        ValueExpression ve2 = ss2.getValueExpression();
        CommonValueExpression cve2 = ve2.getCommonValueExpression();
        ValueExpressionPrimary vep2 = cve2.getValueExpressionPrimary();
        AggregateFunction af2 = vep2.getAggregateFunc();
        System.out.println(af2.format());
        ValueExpression ve2sub = af2.getValueExpression();
        CommonValueExpression cve2sub = ve2sub.getCommonValueExpression();
        NumericValueExpression nve2sub = cve2sub.getNumericValueExpression();
        NumericValueFunction nvf2sub = nve2sub.getNumericValueFunction();
        System.out.println(nvf2sub.getNumericFunction()
                                  .getKeywords());
        StringValueExpression sve2sub = nvf2sub.getStringValueExpression();
        ValueExpressionPrimary vep2sub = sve2sub.getValueExpressionPrimary();
        GeneralValueSpecification gvs2sub = vep2sub.getGeneralValueSpecification();
        System.out.println(gvs2sub.format());
    }

}
