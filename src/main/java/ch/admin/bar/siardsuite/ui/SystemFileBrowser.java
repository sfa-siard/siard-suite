package ch.admin.bar.siardsuite.ui;

import ch.admin.bar.siardsuite.util.OS;

import java.io.File;
import java.io.IOException;

// understands how to open the systems file browser at a files location
public class SystemFileBrowser {

    private final File file;

    public SystemFileBrowser(File file) {
        this.file = file;
    }

    public void show() throws IOException {
        if (OS.UNSUPPORTED) throw new UnsupportedOperationException("Open file browser is not supported on your OS");
        if (OS.IS_WINDOWS) Runtime.getRuntime().exec("explorer /select, "+ file.getAbsolutePath());
        if (OS.IS_UNIX) Runtime.getRuntime().exec("xdg-open "+ file.getParentFile().getAbsolutePath()); // for linux: pass a directory to show, not the file itself
        if (OS.IS_MAC) Runtime.getRuntime().exec("open -R " + file.getAbsolutePath());
    }
}
