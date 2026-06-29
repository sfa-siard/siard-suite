package ch.admin.bar.siard2.access;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AccessLiteralsTester {

    @Test
    public void test() {
        String s = AccessLiterals.normalizeId("COL1.COLA[1]");
        assertEquals("Regex replacement failed!", "COL1_COLA_1_", s);
    }

}
