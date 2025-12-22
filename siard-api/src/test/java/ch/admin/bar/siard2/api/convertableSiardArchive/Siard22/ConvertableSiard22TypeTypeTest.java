package ch.admin.bar.siard2.api.convertableSiardArchive.Siard22;

import ch.admin.bar.siard2.api.generated.AttributeType;
import ch.admin.bar.siard2.api.generated.CategoryType;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.*;

public class ConvertableSiard22TypeTypeTest {

    @Test
    public void shouldInitializeAllProperties() {
        // given

        // when
        AttributeType attributeType = new AttributeType();
        ConvertableSiard22TypeType result = new ConvertableSiard22TypeType("name",
                                                                           "description",
                                                                           "base",
                                                                           "underType",
                                                                           "underSchema",
                                                                           false,
                                                                           true,
                                                                           CategoryType.DISTINCT,
                                                                           Collections.singletonList(attributeType));

        // then
        assertEquals("name", result.getName());
        assertEquals("description", result.getDescription());
        assertEquals("base", result.getBase());
        assertEquals("underType", result.getUnderType());
        assertEquals("underSchema", result.getUnderSchema());
        assertFalse(result.isFinal());
        assertTrue(result.isInstantiable());
        assertEquals(CategoryType.DISTINCT, result.getCategory());
        assertTrue(result.getAttributes()
                         .getAttribute()
                         .contains(attributeType));
    }

    @Test
    public void shouldNotInitializeContainerTypes() {
        // given
        AttributeType attributeType = new AttributeType();

        // when
        ConvertableSiard22TypeType result = new ConvertableSiard22TypeType("name",
                                                                           "description",
                                                                           "base",
                                                                           "underType",
                                                                           "underSchema",
                                                                           false,
                                                                           true,
                                                                           CategoryType.DISTINCT,
                                                                           Collections.emptyList());

        // then
        assertEquals("name", result.getName());
        assertEquals("description", result.getDescription());
        assertEquals("base", result.getBase());
        assertEquals("underType", result.getUnderType());
        assertEquals("underSchema", result.getUnderSchema());
        assertFalse(result.isFinal());
        assertTrue(result.isInstantiable());
        assertEquals(CategoryType.DISTINCT, result.getCategory());
        assertNull(result.getAttributes());
    }
}