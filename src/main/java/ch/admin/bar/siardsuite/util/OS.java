package ch.admin.bar.siardsuite.util;

// understands the operating system it is running on...
public class OS {
        private static final String OS = System.getProperty("os.name").toLowerCase();
        public static final boolean IS_WINDOWS = (OS.contains("win"));
        public static final boolean IS_MAC = (OS.contains("mac"));
        public static final boolean IS_UNIX = (OS.contains("nix") || OS.contains("nux") || OS.indexOf("aix") > 0);
        public static final boolean UNSUPPORTED = !IS_MAC && !IS_UNIX && !IS_WINDOWS;
}
