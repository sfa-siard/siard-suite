package ch.enterag.sqlparser.ddl;

import ch.enterag.sqlparser.BaseSqlFactory;
import ch.enterag.sqlparser.SqlFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MethodSpecificationTester {
    private SqlFactory _sf = new BaseSqlFactory();
    private MethodSpecification _ms = null;

    @BeforeEach
    public void setUp() {
        _ms = _sf.newMethodSpecification();
    }

    @Test
    public void test() {
        _ms.parse("METHOD length_interval () RETURNS INTERVAL HOUR(2) TO MINUTE");
        // System.out.println(_ms.format());
        assertEquals("METHOD LENGTH_INTERVAL() RETURNS INTERVAL HOUR(2) TO MINUTE", _ms.format(), "Method specification recognized!");
    }

}
