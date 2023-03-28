package ch.admin.bar.siardsuite.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class FailureTest {

    Failure failure = new Failure(new IllegalArgumentException("illegal argument"));

    @Test
    void shouldGetTheLocalizedMessage() {
        // given

        // when
        String result = failure.message();

        // then
        Assertions.assertNotNull(result);
        Assertions.assertEquals("illegal argument", result);
    }

    @Test
    void shouldGetStacktrace() {
        // given

        // when
        String result = failure.stacktrace();

        // then
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.startsWith(startOfExpectedStacktrace));
    }

    private final String startOfExpectedStacktrace = "java.lang.IllegalArgumentException: illegal argument";
}