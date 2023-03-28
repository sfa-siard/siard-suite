package ch.enterag.utils;

import ch.enterag.utils.configuration.ManifestAttributesTester;
import ch.enterag.utils.io.SpecialFolderTester;
import ch.enterag.utils.lang.ExecuteTester;
import ch.enterag.utils.logging.IndentLoggerTester;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses(
        {
                ManifestAttributesTester.class,
                SpecialFolderTester.class,
                ExecuteTester.class,
                IndentLoggerTester.class,
                DuTester.class
        })
public class _EnterUtilsTestSuite {
}
