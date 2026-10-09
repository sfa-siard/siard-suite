package ch.admin.bar.siard2.access;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AccessLiteralsTester {

    @Test
    public void test() {
        String s = AccessLiterals.normalizeId("COL1.COLA[1]");
        assertEquals("COL1_COLA_1_", s, "Regex replacement failed!");
    }

}
