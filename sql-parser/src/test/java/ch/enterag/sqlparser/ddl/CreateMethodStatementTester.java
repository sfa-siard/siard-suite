package ch.enterag.sqlparser.ddl;

import ch.enterag.sqlparser.BaseSqlFactory;
import ch.enterag.sqlparser.SqlFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CreateMethodStatementTester {
    private SqlFactory _sf = new BaseSqlFactory();
    private CreateMethodStatement _cms = null;

    @BeforeEach
    public void setUp() {
        _cms = _sf.newCreateMethodStatement();
    }

    @Test
    public void test() {
        String sStatement = "create instance method length_interval ( )\r\n" +
                "returns INTERVAL HOUR(2) TO MINUTE\r\n" +
                "for movie return select count(id) from tab where id < idLimit; end";
        _cms.parse(sStatement);
        // System.out.println(_cms.format());
        String sExpected = "CREATE INSTANCE METHOD LENGTH_INTERVAL(\r\n)\r\nRETURNS INTERVAL HOUR(2) TO MINUTE FOR MOVIE\r\nRETURN SELECT COUNT ( ID ) FROM TAB WHERE ID < IDLIMIT ; END";
        assertEquals(sExpected, _cms.format(), "Create method statement not recognized!");
    }

}
