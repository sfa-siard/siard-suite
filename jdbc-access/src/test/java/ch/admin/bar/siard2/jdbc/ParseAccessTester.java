package ch.admin.bar.siard2.jdbc;

import org.junit.Test;

public class ParseAccessTester {
    @Test
    public void testModParser() {
        String s = "a mod b, (expr) MOD 4 as something, fun(arg) mod 3 AS Month, (Month(\"Order Date\") Mod 3) AS MonthOfQuarter";
        //String sExp = "(\\w+|\\([^\\)]+\\)|\\w+\\([^\\)]+\\))";
        String sExp = "(\\w+|\\w+\\([^\\)]+\\))";
        s = s.replaceAll("(?i)" + sExp + "\\s+Mod\\s+" + sExp, "MOD($1,$2)");
        System.out.println(s);
    }

    @Test
    public void testAsIdParser() {
        String s = "gaga AS Month, guguseli as Claudia FROM Pool";
        s = s.replaceAll("(?i)AS\\s+(\\w+)", "AS \"$1\"");
        System.out.println(s);
    }
}
