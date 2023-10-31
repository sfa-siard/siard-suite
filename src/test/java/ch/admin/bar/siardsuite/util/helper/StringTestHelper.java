package ch.admin.bar.siardsuite.util.helper;

import lombok.val;

import java.util.regex.Pattern;

public class StringTestHelper {

    private static final Pattern FIND_PLACEHOLDERS_REGEX = Pattern
            .compile("%(\\d+\\$)?([-#+ 0,(\\<]*)?(\\d+)?(\\.\\d+)?([tT])?([a-zA-Z%])");

    public static int getNumberOfPlaceholders(String text) {
        val matcher = FIND_PLACEHOLDERS_REGEX.matcher(text);

        // Count the total amount of matches in the String
        int counter = 0;
        while (matcher.find()) {
            counter++;
        }

        return counter;
    }
}
