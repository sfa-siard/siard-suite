package ch.admin.bar.siardsuite.web;

import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;

/**
 * Opens the operating system's native file dialog on the local machine and
 * returns the selected absolute path. Runs inside the agent process, so the
 * dialog belongs to the desktop session of the user who launched the agent.
 */
final class FilePicker {

    private FilePicker() {
    }

    static String pick() {
        if (GraphicsEnvironment.isHeadless()) {
            return null;
        }
        FileDialog dialog = new FileDialog((Frame) null, "Open SIARD archive", FileDialog.LOAD);
        dialog.setFile("*.siard");
        dialog.setVisible(true);
        String dir = dialog.getDirectory();
        String file = dialog.getFile();
        return dir == null || file == null ? null : dir + file;
    }
}
