package ch.admin.bar.siard2.api.convertableSiardArchive.Siard22;

import ch.admin.bar.siard2.api.generated.FieldType;
import org.junit.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

public class ConvertableSiard22ColumnTypeTest {


    @Test
    public void shouldInitializeAllProperties() {
        // given
        FieldType fieldType = new FieldType();

        // when
        ConvertableSiard22ColumnType result = new ConvertableSiard22ColumnType("name",
                                                                               "description",
                                                                               "defaultValue",
                                                                               "lobFolder",
                                                                               "mimeType",
                                                                               "type",
                                                                               "typeName",
                                                                               "typeSchema",
                                                                               "typrOriginal",
                                                                               BigInteger.ZERO,
                                                                               true,
                                                                               Collections.singletonList(fieldType));

        // then
        assertEquals("name", result.getName());
        assertEquals("description", result.getDescription());
        assertEquals("defaultValue", result.getDefaultValue());
        assertEquals("lobFolder", result.getLobFolder());
        assertEquals("mimeType", result.getMimeType());
        assertEquals("type", result.getType());
        assertEquals("typeName", result.getTypeName());
        assertEquals("typeSchema", result.getTypeSchema());
        assertEquals("typrOriginal", result.getTypeOriginal());
        assertEquals(BigInteger.ZERO, result.getCardinality());
        assertEquals(true, result.isNullable());
        assertNotNull(result.getFields());
        assertTrue(result.getFields()
                         .getField()
                         .contains(fieldType));
    }

    @Test
    public void shouldNotInitializeFields() {
        // given

        // when
        ConvertableSiard22ColumnType convertableSiard22ColumnType = new ConvertableSiard22ColumnType("name",
                                                                                                     "descripion",
                                                                                                     "defaultValue",
                                                                                                     "lobFolder",
                                                                                                     "mimeType",
                                                                                                     "type",
                                                                                                     "typeName",
                                                                                                     "typeSchema",
                                                                                                     "typrOriginal",
                                                                                                     BigInteger.ZERO,
                                                                                                     true,
                                                                                                     new ArrayList<>());

        // then
        assertNull(convertableSiard22ColumnType.getFields());
    }
}