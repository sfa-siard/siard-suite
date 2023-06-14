package ch.admin.bar.siardsuite.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class StringUtilsTest {

    @Test
    void shouldReplaceSiardAPINullStringWithEmptyString() {
        Assertions.assertEquals("", StringUtils.emptyApiNull("(...)"));
        Assertions.assertEquals("archived by", StringUtils.emptyApiNull("archived by"));
        Assertions.assertEquals("any", StringUtils.emptyApiNull("any"));
        Assertions.assertEquals("", StringUtils.emptyApiNull(""));
        Assertions.assertEquals(null, StringUtils.emptyApiNull(null));
    }
}