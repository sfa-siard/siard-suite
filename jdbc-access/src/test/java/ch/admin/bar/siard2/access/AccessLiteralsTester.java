package ch.admin.bar.siard2.access;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AccessLiteralsTester {

    @Test
    public void test() {
        String s = AccessLiterals.normalizeId("COL1.COLA[1]");
        assertEquals("Regex replacement failed!", "COL1_COLA_1_", s);
    }

    @Test
    public void testNormalizeQueryText_missingSpaceBeforeFrom() {
        String input = "SELECT DonDate AS DateVersement, donValeur AS MontantFROM AGORA_Dons";
        String result = AccessLiterals.normalizeQueryText(input);
        assertEquals("SELECT DonDate AS DateVersement, donValeur AS Montant FROM AGORA_Dons", result);
    }

    @Test
    public void testNormalizeQueryText_missingSpaceBeforeWhere() {
        String input = "SELECT aFROM tWHERE a = 1";
        String result = AccessLiterals.normalizeQueryText(input);
        assertEquals("SELECT a FROM t WHERE a = 1", result);
    }

    @Test
    public void testNormalizeQueryText_missingSpaceBeforeOrderBy() {
        String input = "SELECT a FROM tORDER BY a";
        String result = AccessLiterals.normalizeQueryText(input);
        assertEquals("SELECT a FROM t ORDER BY a", result);
    }

    @Test
    public void testNormalizeQueryText_alreadyCorrectlySpaced() {
        String input = "SELECT a FROM t WHERE a = 1 ORDER BY a";
        String result = AccessLiterals.normalizeQueryText(input);
        assertEquals(input, result);
    }

    @Test
    public void testNormalizeQueryText_caseInsensitive() {
        String input = "SELECT afrom bwhere c = 1";
        String result = AccessLiterals.normalizeQueryText(input);
        assertEquals("SELECT a from b where c = 1", result);
    }

}
