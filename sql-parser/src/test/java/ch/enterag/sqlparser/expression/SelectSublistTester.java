package ch.enterag.sqlparser.expression;

import ch.enterag.sqlparser.BaseSqlFactory;
import ch.enterag.sqlparser.SqlFactory;
import ch.enterag.sqlparser.SqlStatement;
import ch.enterag.sqlparser.datatype.DataType;
import ch.enterag.sqlparser.datatype.PredefinedType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SelectSublistTester {
    private SqlFactory _sf = new BaseSqlFactory();
    private SelectSublist _ss = null;

    @BeforeEach
    public void setUp() {
        _ss = _sf.newSelectSublist();
    }

    @Test
    public void testPlainColumnName() {
        // ErrorListener.getInstance().suppressException();
        _ss.parse("a1");
        // System.out.println(_ss.format());
        assertEquals("A1", _ss.format(), "Plain column name not recognized!");
    }

    @Test
    public void testPlainAlias() {
        _ss.parse("a1 as b");
        // System.out.println(_ss.format());
        assertEquals("A1 AS B", _ss.format(), "Alias column not recognized!");
    }

    @Test
    public void testPlainQualifiedAsterisk() {
        _ss.parse("t1.*");
        // System.out.println(_ss.format());
        assertEquals("T1.*", _ss.format(), "Qualified asterisk not recognized!");
    }

    @Test
    public void testPlainQualifiedAsteriskWithAliases() {
        _ss.parse("t1.* as (a1,a2,a3)");
        System.out.println(_ss.format());
        assertEquals("T1.* AS (A1, A2, A3)", _ss.format(), "Qualified asterisk not recognized!");
    }

    @Test
    public void testResetAggregates() {
        SqlStatement ss = _sf.newSqlStatement();
        DataType dt = _sf.newDataType();
        PredefinedType pt = _sf.newPredefinedType();
        dt.initPredefinedDataType(pt);
        pt.initIntegerType();
        String sColumnName = "COLMEMO";
        ss.parse("SELECT COUNT(*) AS RECORDS, SUM(OCTET_LENGTH(\"" + sColumnName + "\")) AS \"COLMEMO_SIZE\" FROM \"Schem\".\"TABLETEST\"");
        ss.setEvaluationContext("User", "Cat", "Schem");
        QuerySpecification qs = ss.getQuerySpecification();
        TablePrimary tp = qs.getTableReferences()
                            .get(0)
                            .getTablePrimary();
        tp.setColumnType(sColumnName, dt);
        List<String> listColumnNames = new ArrayList<String>();
        listColumnNames.add(sColumnName);
        tp.setColumnNames(listColumnNames);
        tp.setColumnValue(sColumnName, new String("SIARD2"));
        List<SelectSublist> listSelectSublists = ss.getQuerySpecification()
                                                   .getSelectSublists();
        for (int i = 0; i < listSelectSublists.size(); i++) {
            _ss = listSelectSublists.get(i);
            Object o = _ss.evaluate(ss);
            System.out.println(String.valueOf(o));
        }
        for (int i = 0; i < listSelectSublists.size(); i++) {
            _ss = listSelectSublists.get(i);
            Object o = _ss.resetAggregates(ss);
            System.out.println(String.valueOf(o));
        }
    }

}
