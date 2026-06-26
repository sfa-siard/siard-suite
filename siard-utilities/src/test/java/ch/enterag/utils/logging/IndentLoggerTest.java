package ch.enterag.utils.logging;


import org.junit.jupiter.api.Test;

public class IndentLoggerTest {
    IndentLogger _il = IndentLogger.getIndentLogger(IndentLoggerTest.class.getPackage()
                                                                          .getName());

    @Test
    public void test() {
        _il.enter();
        _il.exit();
    }

}
