package ch.enterag.utils.mime;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MimeTypesTest {

    @Test
    void shouldGetExtension() {
        assertAll(
                () -> assertEquals("json", MimeTypes.getExtension("application/json")),
                () -> assertEquals("png", MimeTypes.getExtension("image/png")),
                () -> assertEquals("bin", MimeTypes.getExtension("invalid/mime-type")),
                () -> assertEquals("bin", MimeTypes.getExtension("foobar"))
        );
    }
}