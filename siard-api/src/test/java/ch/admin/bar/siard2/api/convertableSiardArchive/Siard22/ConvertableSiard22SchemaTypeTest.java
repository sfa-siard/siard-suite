package ch.admin.bar.siard2.api.convertableSiardArchive.Siard22;

import ch.admin.bar.siard2.api.generated.RoutineType;
import ch.admin.bar.siard2.api.generated.TableType;
import ch.admin.bar.siard2.api.generated.TypeType;
import ch.admin.bar.siard2.api.generated.ViewType;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

public class ConvertableSiard22SchemaTypeTest {

    @Test
    public void shouldInitializeAllProperties() {
        // given

        // when
        TypeType typeType = new TypeType();
        RoutineType routineType = new RoutineType();
        TableType tableType = new TableType();
        ViewType viewType = new ViewType();
        ConvertableSiard22SchemaType result = new ConvertableSiard22SchemaType("name",
                                                                               "description",
                                                                               "folder",
                                                                               Collections.singletonList(typeType),
                                                                               Collections.singletonList(routineType),
                                                                               Collections.singletonList(tableType),
                                                                               Collections.singletonList(viewType));

        // then
        assertEquals("name", result.getName());
        assertEquals("description", result.getDescription());
        assertEquals("folder", result.getFolder());
        assertNotNull(result.getTypes());
        assertNotNull(result.getRoutines());
        assertNotNull(result.getTables());
        assertNotNull(result.getViews());
        assertTrue(result.getTypes()
                         .getType()
                         .contains(typeType));
        assertTrue(result.getRoutines()
                         .getRoutine()
                         .contains(routineType));
        assertTrue(result.getTables()
                         .getTable()
                         .contains(tableType));
        assertTrue(result.getViews()
                         .getView()
                         .contains(viewType));
    }

    @Test
    public void shouldNotInitializeContainerTypes() {
        // given

        // when
        ConvertableSiard22SchemaType result = new ConvertableSiard22SchemaType("name",
                                                                               "description",
                                                                               "folder",
                                                                               Collections.emptyList(),
                                                                               Collections.emptyList(),
                                                                               Collections.emptyList(),
                                                                               Collections.emptyList());

        // then
        assertNull(result.getTypes());
        assertNull(result.getRoutines());
        assertNull(result.getTables());
        assertNull(result.getViews());
    }
}