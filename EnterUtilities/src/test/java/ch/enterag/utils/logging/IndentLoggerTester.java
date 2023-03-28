/* add -Djava.util.logging.config.file=etc/debug.properties to the VM arguments
 * and check logs/siard*.log afterwards.
 */
package ch.enterag.utils.logging;

import org.junit.Test;

public class IndentLoggerTester {
    IndentLogger _il = IndentLogger.getIndentLogger(IndentLoggerTester.class.getPackage().getName());

    @Test
    public void test() {
        _il.enter();
        _il.exit();
    }

} /* IndentLoggerTester */
