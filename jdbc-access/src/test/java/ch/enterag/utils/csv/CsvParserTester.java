package ch.enterag.utils.csv;

import ch.enterag.utils.EU;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.fail;

public class CsvParserTester {

    @Test
    public void test() {
        CsvParser cp = new CsvParserImpl(';');
        try {
            String[] as = cp.parseLine("\"D\"\"E\";\"FR\";\"IT\";\"EN\"");
            for (int i = 0; i < as.length; i++)
                System.out.println(as[i]);
            as = cp.parseLine("1;2;3;4");
            for (int i = 0; i < as.length; i++)
                System.out.println(as[i]);
            as = cp.parseLine("\"DE\";2;\"IT\";4");
            for (int i = 0; i < as.length; i++)
                System.out.println(as[i]);
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

}
