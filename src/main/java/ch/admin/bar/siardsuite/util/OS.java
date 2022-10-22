package ch.admin.bar.siardsuite.util;

// understands the operating system it is running on...
public class OS {
        private static String OS = System.getProperty("os.name").toLowerCase();
        public static boolean IS_WINDOWS = (OS.indexOf("win") >= 0);
        public static boolean IS_MAC = (OS.indexOf("mac") >= 0);
        public static boolean IS_UNIX = (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0);
        public static boolean UNSUPPORTED = !IS_MAC && !IS_UNIX && !IS_WINDOWS;
}
