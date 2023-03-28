package ch.enterag.utils.lang;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


@RunWith(PowerMockRunner.class)
@PrepareForTest({System.class, Execute.class})
public class ExecuteTester {

    @Test
    public void testIsJavaVersionLessThan() {
        PowerMockito.mockStatic(System.class);

        PowerMockito.when(System.getProperty("java.version")).thenReturn("1.8.0");
        assertTrue(Execute.isJavaVersionLessThan("9"));
        assertTrue(Execute.isJavaVersionLessThan("1.9"));
        assertFalse(Execute.isJavaVersionLessThan("8"));
        assertFalse(Execute.isJavaVersionLessThan("1.8"));

        PowerMockito.when(System.getProperty("java.version")).thenReturn("9.0.1");
        assertTrue(Execute.isJavaVersionLessThan("10"));
        assertTrue(Execute.isJavaVersionLessThan("1.10"));
        assertFalse(Execute.isJavaVersionLessThan("9"));
        assertFalse(Execute.isJavaVersionLessThan("1.9"));
    }

}
