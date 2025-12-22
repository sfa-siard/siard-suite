package ch.admin.bar.siard2.api.convertableSiardArchive.Siard22;

import ch.admin.bar.siard2.api.generated.ColumnType;
import org.junit.Test;

import java.math.BigInteger;
import java.util.Collections;

import static org.junit.Assert.*;

public class ConvertableSiard22ViewTypeTest {

    @Test
    public void shouldInitializeAllProperties() {
        // given
        ColumnType columnType = new ColumnType();

        // when
        ConvertableSiard22ViewType result = new ConvertableSiard22ViewType("name",
                                                                           "description",
                                                                           BigInteger.TEN,
                                                                           "query",
                                                                           "queryOriginal",
                                                                           Collections.singletonList(columnType));
        // then
        assertEquals("name", result.getName());
        assertEquals("description", result.getDescription());
        assertEquals(BigInteger.TEN, result.getRows());
        assertEquals("query", result.getQuery());
        assertEquals("queryOriginal", result.getQueryOriginal());
        assertTrue(result.getColumns()
                         .getColumn()
                         .contains(columnType));
    }

    @Test
    public void shouldNotInitializeContainerTypes() {
        // given
        ColumnType columnType = new ColumnType();

        // when
        ConvertableSiard22ViewType result = new ConvertableSiard22ViewType("name",
                                                                           "description",
                                                                           BigInteger.TEN,
                                                                           "query",
                                                                           "queryOriginal",
                                                                           Collections.emptyList());
        // then
        assertEquals("name", result.getName());
        assertEquals("description", result.getDescription());
        assertEquals(BigInteger.TEN, result.getRows());
        assertEquals("query", result.getQuery());
        assertEquals("queryOriginal", result.getQueryOriginal());
        assertNull(result.getColumns());
    }

}