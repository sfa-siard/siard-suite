package ch.enterag.utils;

import org.junit.jupiter.api.Test;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SUTest {

    @Test
    void shouldIsNotEmpty() {
        assertAll(
                () -> assertTrue(SU.isNotEmpty("Test")),
                () -> assertFalse(SU.isNotEmpty(null)),
                () -> assertFalse(SU.isNotEmpty(""))
        );
    }

    @Test
    void shouldRepeat() {
        assertAll(
                () -> assertEquals("111", SU.repeat(3, "1")),
                () -> assertEquals("AAAAA", SU.repeat(5, 'A')),
                () -> assertEquals("", SU.repeat(0, "x")),
                () -> assertEquals("!£$!£$", SU.repeat(2, "!£$"))
        );
    }

    @Test
    void shouldPutStringMethods() {
        String sampleText = "Hello, world! *\u00A0\uD83D\uDE00\u00A0*";
        assertAll(
                () -> assertArrayEquals("world!".getBytes(StandardCharsets.UTF_8), SU.putUtf8CharArray(sampleText.toCharArray(), 7, 6)),
                () -> assertArrayEquals(sampleText.getBytes(StandardCharsets.US_ASCII), SU.putEncodedString(sampleText, "ASCII")),
                () -> assertArrayEquals(sampleText.getBytes(StandardCharsets.UTF_8), SU.putUtf8String(sampleText)),
                () -> assertArrayEquals(sampleText.getBytes(Charset.forName("Cp437")), SU.putCp437String(sampleText))
        );
    }

    @Test
    void shouldGetStringMethods() {
        String sampleText = "Hello, world! *\u00A0\uD83D\uDE00\u00A0*";
        byte[] utf8Bytes = sampleText.getBytes(Charset.forName("UTF-8"));
        byte[] cp437Bytes = sampleText.getBytes(Charset.forName("Cp437"));
        byte[] iso88591Bytes = sampleText.getBytes(Charset.forName("ISO-8859-1"));
        byte[] windows1252Bytes = sampleText.getBytes(Charset.forName("Windows-1252"));
        assertAll(
                () -> assertEquals((new String(utf8Bytes, (Charset.forName("UTF-8")))), SU.getUtf8String(utf8Bytes)),
                () -> assertEquals((new String(cp437Bytes, (Charset.forName("Cp437")))), SU.getCp437String(cp437Bytes)),
                () -> assertEquals((new String(iso88591Bytes, (Charset.forName("ISO-8859-1")))), SU.getIsoLatin1String(iso88591Bytes)),
                () -> assertEquals((new String(windows1252Bytes, (Charset.forName("Windows-1252")))), SU.getWindows1252String(windows1252Bytes))
        );
    }

    @Test
    void shouldToHtml() {
        assertEquals("He said &quot;Hello, 5 &gt; 3 &amp; 3 &lt; 5!&quot;", SU.toHtml("He said \"Hello, 5 > 3 & 3 < 5!\""));
    }
}