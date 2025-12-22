package ch.admin.bar.siard2.api.convertableSiardArchive.Siard22;

import ch.admin.bar.siard2.api.generated.ParameterType;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

public class ConvertableSiard22RoutineTypeTest {

    @Test
    public void shouldInitializeAllProperties() {
        // given

        // when
        ParameterType parameterType = new ParameterType();
        ConvertableSiard22RoutineType result = new ConvertableSiard22RoutineType("name",
                                                                                 "description",
                                                                                 "body",
                                                                                 "characteristic",
                                                                                 "returnType",
                                                                                 "specificName",
                                                                                 "source",
                                                                                 Collections.singletonList(parameterType));

        // then
        assertEquals("name", result.getName());
        assertEquals("description", result.getDescription());
        assertEquals("body", result.getBody());
        assertEquals("characteristic", result.getCharacteristic());
        assertEquals("returnType", result.getReturnType());
        assertEquals("specificName", result.getSpecificName());
        assertEquals("source", result.getSource());
        assertNotNull(result.getParameters());
        assertTrue(result.getParameters()
                         .getParameter()
                         .contains(parameterType));
    }

    @Test
    public void shouldInitializeParameters() {
        // given

        // when
        ConvertableSiard22RoutineType result = new ConvertableSiard22RoutineType("name",
                                                                                 "description",
                                                                                 "body",
                                                                                 "characteristic",
                                                                                 "returnType",
                                                                                 "specificName",
                                                                                 "source",
                                                                                 Collections.emptyList());

        // then
        assertNull(result.getParameters());
    }
}