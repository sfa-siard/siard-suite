package ch.admin.bar.siardsuite.util;

import java.io.IOException;


// understands the operating system it is running on...
public class OS {
    private static final String OS = System.getProperty("os.name").toLowerCase();
    public static final boolean IS_WINDOWS = (OS.contains("win"));
    public static final boolean IS_MAC = (OS.contains("mac"));
    public static final boolean IS_UNIX = (OS.contains("nix") || OS.contains("nux") || OS.indexOf("aix") > 0);
    public static final boolean UNSUPPORTED = !IS_MAC && !IS_UNIX && !IS_WINDOWS;


    public static void openFile(String uri) throws IOException {
        Runtime.getRuntime().exec(getCommand(uri));
    }

    private static String getCommand(String uri) {
        String path = uri.replaceAll("file:", "");
        if (IS_UNIX) return "xdg-open " + path;
        if (IS_WINDOWS) return "cmd.exe /C start " + path.replaceFirst("/", "");
        if (IS_MAC) return "open " + path;
        return "";
    }

}
