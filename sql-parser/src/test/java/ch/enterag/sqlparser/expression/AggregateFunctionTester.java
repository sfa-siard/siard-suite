package ch.enterag.sqlparser.expression;

import ch.enterag.sqlparser.BaseSqlFactory;
import ch.enterag.sqlparser.SqlFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AggregateFunctionTester {
    private SqlFactory _sf = new BaseSqlFactory();
    private AggregateFunction _af = null;

    @BeforeEach
    public void setUp() {
        _af = _sf.newAggregateFunction();
    }

    @Test
    public void testCount() {
        _af.parse("COUNT( * )");
        // System.out.println(_af.format());
        assertEquals("COUNT(*)", _af.format(), "COUNT function not recognized!");
    }

    @Test
    public void testCountWithFilter() {
        _af.parse("COUNT( * ) FILTER(WHERE TRUE)");
        // System.out.println(_af.format());
        assertEquals("COUNT(*) FILTER(WHERE TRUE)", _af.format(), "COUNT function with filter not recognized!");
    }

    @Test
    public void testAvg() {
        _af.parse("AVG(emp.\"Income\")");
        // System.out.println(_af.format());
        assertEquals("AVG(EMP.\"Income\")", _af.format(), "AVG function not recognized!");
    }

    @Test
    public void testAvgWithFilter() {
        _af.parse("AVG(emp.\"Income\") FILTER(WHERE \"Income\" > 3000)");
        // System.out.println(_af.format());
        assertEquals("AVG(EMP.\"Income\") FILTER(WHERE \"Income\" > 3000)", _af.format(), "AVG function with filter not recognized!");
    }

    @Test
    public void testMax() {
        _af.parse("Max(2+emp.\"Income\"*emp.\"Age\")");
        // System.out.println(_af.format());
        assertEquals("MAX(2 + EMP.\"Income\"*EMP.\"Age\")", _af.format(), "MAX function not recognized!");
    }

    @Test
    public void testMin() {
        _af.parse("Min(a1)");
        // System.out.println(_af.format());
        assertEquals("MIN(A1)", _af.format(), "MIN function not recognized!");
    }

    @Test
    public void testSum() {
        _af.parse("Sum(a1)");
        // System.out.println(_af.format());
        assertEquals("SUM(A1)", _af.format(), "SUM function not recognized!");
    }

    @Test
    public void testEvery() {
        _af.parse("Every(distinct a1)");
        // System.out.println(_af.format());
        assertEquals("EVERY(DISTINCT A1)", _af.format(), "EVERY function not recognized!");
    }

    @Test
    public void testAny() {
        _af.parse("Any(a1)");
        // System.out.println(_af.format());
        assertEquals("ANY(A1)", _af.format(), "ANY function not recognized!");
    }

    @Test
    public void testSome() {
        _af.parse("Some(a1)");
        // System.out.println(_af.format());
        assertEquals("SOME(A1)", _af.format(), "SOME function not recognized!");
    }

    @Test
    public void testCountWithArg() {
        _af.parse("count(a1)");
        // System.out.println(_af.format());
        assertEquals("COUNT(A1)", _af.format(), "COUNT function with argument not recognized!");
    }

    @Test
    public void testStddevPop() {
        _af.parse("stddev_pop(a1)");
        // System.out.println(_af.format());
        assertEquals("STDDEV_POP(A1)", _af.format(), "STDDEV_POP function not recognized!");
    }

    @Test
    public void testStddevSamp() {
        _af.parse("stddev_samp(a1)");
        // System.out.println(_af.format());
        assertEquals("STDDEV_SAMP(A1)", _af.format(), "STDDEV_SAMP function not recognized!");
    }

    @Test
    public void testVarPop() {
        _af.parse("var_pop(a1)");
        // System.out.println(_af.format());
        assertEquals("VAR_POP(A1)", _af.format(), "VAR_POP function not recognized!");
    }

    @Test
    public void testVarSamp() {
        _af.parse("var_samp(a1)");
        // System.out.println(_af.format());
        assertEquals("VAR_SAMP(A1)", _af.format(), "VAR_SAMP function not recognized!");
    }

    @Test
    public void testVarCollect() {
        _af.parse("Collect(a1)");
        // System.out.println(_af.format());
        assertEquals("COLLECT(A1)", _af.format(), "COLLECT function not recognized!");
    }

    @Test
    public void testFusion() {
        _af.parse("fusion(a1)");
        // System.out.println(_af.format());
        assertEquals("FUSION(A1)", _af.format(), "FUSION function not recognized!");
    }

    @Test
    public void testIntersection() {
        _af.parse("intersection(a1)");
        // System.out.println(_af.format());
        assertEquals("INTERSECTION(A1)", _af.format(), "INTERSECTION function not recognized!");
    }

    @Test
    public void testCovarPop() {
        _af.parse("covar_pop(a1,5) filter(where a1 = 0)");
        // System.out.println(_af.format());
        assertEquals("COVAR_POP(A1, 5) FILTER(WHERE A1 = 0)", _af.format(), "COVAR_POP function not recognized!");
    }

    @Test
    public void testCovarSamp() {
        _af.parse("covar_samp(5,a1) filter(where a1 = 0)");
        // System.out.println(_af.format());
        assertEquals("COVAR_SAMP(5, A1) FILTER(WHERE A1 = 0)", _af.format(), "COVAR_SAMP function not recognized!");
    }

    @Test
    public void testRegrSlope() {
        _af.parse("regr_slope(a1,b1)");
        // System.out.println(_af.format());
        assertEquals("REGR_SLOPE(A1, B1)", _af.format(), "REGR_SLOPE function not recognized!");
    }

    @Test
    public void testRegrIntercept() {
        _af.parse("regr_intercept(a1,b1)");
        // System.out.println(_af.format());
        assertEquals("REGR_INTERCEPT(A1, B1)", _af.format(), "REGR_INTERCEPT function not recognized!");
    }

    @Test
    public void testRegrCount() {
        _af.parse("regr_count(a1,b1)");
        // System.out.println(_af.format());
        assertEquals("REGR_COUNT(A1, B1)", _af.format(), "REGR_COUNT function not recognized!");
    }

    @Test
    public void testRegrRSquared() {
        _af.parse("regr_r2(a1,b1)");
        // System.out.println(_af.format());
        assertEquals("REGR_R2(A1, B1)", _af.format(), "REGR_R2 function not recognized!");
    }

    @Test
    public void testRegrAvgx() {
        _af.parse("regr_avgx(a1,b1)");
        // System.out.println(_af.format());
        assertEquals("REGR_AVGX(A1, B1)", _af.format(), "REGR_AVGX function not recognized!");
    }

    @Test
    public void testRegrAvgy() {
        _af.parse("regr_avgy(a1,b1)");
        // System.out.println(_af.format());
        assertEquals("REGR_AVGY(A1, B1)", _af.format(), "REGR_AVGY function not recognized!");
    }

    @Test
    public void testRegrSxx() {
        _af.parse("regr_sxx(a1,b1)");
        // System.out.println(_af.format());
        assertEquals("REGR_SXX(A1, B1)", _af.format(), "REGR_SXX function not recognized!");
    }

    @Test
    public void testRegrSyy() {
        _af.parse("regr_syy(a1,b1)");
        // System.out.println(_af.format());
        assertEquals("REGR_SYY(A1, B1)", _af.format(), "REGR_SYY function not recognized!");
    }

    @Test
    public void testRegrSxy() {
        _af.parse("regr_sxy(a1,b1)");
        // System.out.println(_af.format());
        assertEquals("REGR_SXY(A1, B1)", _af.format(), "REGR_SXY function not recognized!");
    }

    @Test
    public void testRank() {
        _af.parse("rank(a1,b1,c1,d1) within group (order by a1 ASC, b1 DESC)");
        // System.out.println(_af.format());
        assertEquals("RANK(A1, B1, C1, D1) WITHIN GROUP(ORDER BY A1 ASC, B1 DESC)", _af.format(), "RANK function not recognized!");
    }

    @Test
    public void testDenseRank() {
        _af.parse("dense_rank(a1,b1,c1) within group (order by a1 ASC, b1 DESC)");
        // System.out.println(_af.format());
        assertEquals("DENSE_RANK(A1, B1, C1) WITHIN GROUP(ORDER BY A1 ASC, B1 DESC)", _af.format(), "DENSE_RANK function not recognized!");
    }

    @Test
    public void testPercentRank() {
        _af.parse("percent_rank(a1,b1,c1,d1,e1) within group (order by a1)");
        // System.out.println(_af.format());
        assertEquals("PERCENT_RANK(A1, B1, C1, D1, E1) WITHIN GROUP(ORDER BY A1)", _af.format(), "PERCENT_RANK function not recognized!");
    }

    @Test
    public void testCumeDist() {
        _af.parse("cume_dist(a1,b1,c1,d1,e1) within group (order by a1)");
        // System.out.println(_af.format());
        assertEquals("CUME_DIST(A1, B1, C1, D1, E1) WITHIN GROUP(ORDER BY A1)", _af.format(), "CUME_DIST function not recognized!");
    }

}
