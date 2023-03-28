package ch.enterag.utils.io;

import ch.enterag.utils.lang.Execute;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.fail;

public class SpecialFolderTester {

    @Test
    public void testFindInPath() {
        String sFile = "edit";
        if (Execute.isOsLinux())
            sFile = "nano";
        else if (Execute.isOsWindows())
            sFile = "Notepad.exe";
        else
            sFile = "chown";
        File file = SpecialFolder.findInPath(sFile);
        if (file != null)
            System.out.println(file.getAbsolutePath());
        else
            fail(sFile + " not found!");

    }

}
