package ch.admin.bar.siard2.oracle.legacy;

import static org.junit.Assert.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

public class TypePatternTester {
    private static Pattern _patTypeText = Pattern.compile("^TYPE.*VARRAY\\s*\\((\\d+)\\)\\s+OF\\s+(.*?)\\s*;?\\s*$", Pattern.CASE_INSENSITIVE + Pattern.DOTALL);
    private static Pattern _patType = Pattern.compile("^(.*?)(\\(\\s*(\\d+)\\s*(,\\s*(\\d)+\\s*)?\\))?$");

    @Test
    public void testPatternCreateVarray() {
    /*
    String sText = "TYPE phone_list_typ\n                                        \n  AS VARRAY(5) OF VARCHAR2(25);";
    Matcher match = _patTypeText.matcher(sText);
    if (match.matches())
    {
      int iSize = match.groupCount();
      for (int i = 0; i <= iSize; i++)
        System.out.println(String.valueOf(i)+": "+match.group(i));
    }
    else
      fail("Does not match!");
    */
        String sText = "TYPE SDO_ELEM_INFO_ARRAY\n                                                                      \nAS VARRAY (1048576) of NUMBER ";
        Matcher match = _patTypeText.matcher(sText);
        if (match.matches()) {
            int iSize = match.groupCount();
            for (int i = 0; i <= iSize; i++)
                System.out.println(String.valueOf(i) + ": " + match.group(i));
        } else fail("Does not match!");
    }

    @Test
    public void testPatternType() {
        Matcher matchType = _patType.matcher("VARCHAR2(25)");
        if (matchType.matches()) {
            int iSize = matchType.groupCount();
            for (int i = 0; i <= iSize; i++)
                System.out.println(String.valueOf(i) + ": " + matchType.group(i));
        } else fail("Does not match!");
    }

    @Test
    public void testXmlPattern() {
        String s = "aaa\n  bbb\n ccc\nddd\n";
        s = s.replaceAll("\\n\\s*", "");
        System.out.println("\"" + s + "\"");
    }
}
