package ch.admin.bar.siardsuite.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FailureTest {

    Failure failure = new Failure(new IllegalArgumentException("illegal argument"));

    @Test
    void shouldGetTheLocalizedMessage() {
        // given

        // when
        String result = failure.message();

        // then
        assertNotNull(result);
        assertEquals("illegal argument", result);
    }

    @Test
    void shouldGetStacktrace() {
        // given

        // when
        String result = failure.stacktrace();

        // then
        assertNotNull(result);
        assertTrue(result.startsWith(startOfExpectedStacktrace));
    }

    private final String startOfExpectedStacktrace = "java.lang.IllegalArgumentException: illegal argument";
}