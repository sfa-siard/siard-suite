package ch.enterag.utils.logging;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class IndentLoggerTest {
    IndentLogger _il = IndentLogger.getIndentLogger(IndentLoggerTest.class.getPackage()
                                                                          .getName());

    @Test
    public void test() {
        _il.enter();
        _il.exit();
    }

    @Test
    public void getParent_returnsParentLogger() {
        assertNotNull(_il.getParent());
    }

}
