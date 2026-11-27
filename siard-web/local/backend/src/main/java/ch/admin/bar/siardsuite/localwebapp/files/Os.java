package ch.admin.bar.siardsuite.localwebapp.files;

import java.util.Locale;

/** The operating-system families with their own places and file-manager rules (design D4, D5). */
enum Os {
    WINDOWS, MAC, LINUX;

    static Os current() {
        String name = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        if (name.startsWith("windows")) {
            return WINDOWS;
        }
        if (name.startsWith("mac")) {
            return MAC;
        }
        return LINUX;
    }
}
