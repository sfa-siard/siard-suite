package ch.admin.bar.siardsuite.util;

public class StringUtils {


    public static String emptyApiNull(String s) {
        if (s == null) return null;
        if (s.equals("(...)")) return "";
        return s;
    }
}
