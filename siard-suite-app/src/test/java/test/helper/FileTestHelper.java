package test.helper;

import lombok.val;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileTestHelper {

    public static File removeDriveLetterIfNecessary(final File orig) {
        val path = orig.getAbsolutePath();
        return new File(removeDriveLetterIfNecessary(path));
    }

    public static String removeDriveLetterIfNecessary(final String orig) {
        val driveLetterPattern = Pattern.compile( "\\b[a-zA-Z]:\\\\");
        val matcher = driveLetterPattern.matcher(orig);

        return matcher.replaceAll(Matcher.quoteReplacement(File.separator));
    }
}
