package ch.admin.bar.siard2.access;

public class AccessLiterals {
    // special characters not allowed in identifiers in MS Access
    // \.[]{}()*+-?^$| must be escaped in regex
    private static final String sREGEX_SPECIAL_CHARS = "[ \"'@`#%><!\\.\\[\\]\\*\\$;:\\?\\^\\{\\}\\+\\-=~\\\\]";

    public static String normalizeId(String sIdentifier) {
        if (sIdentifier != null)
            sIdentifier = sIdentifier.replaceAll(sREGEX_SPECIAL_CHARS, "_");
        return sIdentifier;
    }

}
