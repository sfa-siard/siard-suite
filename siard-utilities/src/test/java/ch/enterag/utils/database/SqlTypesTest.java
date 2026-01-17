package ch.enterag.utils.database;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SqlTypesTest {

    @Test
    void shouldGetTypeName() {
        assertAll(
                () -> assertEquals("INTEGER", SqlTypes.getTypeName(4)),
                () -> assertEquals("VARCHAR", SqlTypes.getTypeName(12)),
                () -> assertEquals("UNKNOWN", SqlTypes.getTypeName(9999))
        );
    }

    @Test
    void shouldGetAllTypes() {
        List<Integer> allTypes = SqlTypes.getAllTypes();
        assertAll(
                () -> assertTrue(allTypes.contains(4)),
                () -> assertTrue(allTypes.contains(12)),
                () -> assertFalse(allTypes.contains(9999))
        );
    }
}