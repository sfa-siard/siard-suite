package ch.admin.bar.siard2.api.convertableSiardArchive.Siard22;

import ch.admin.bar.siard2.api.generated.*;
import org.junit.Test;

import java.math.BigInteger;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.*;

public class ConvertableSiard22TableTypeTest {

    @Test
    public void shouldInitializeAllProperties() {
        // given
        ColumnType columnType = new ColumnType();
        UniqueKeyType uniqueKeyType = new UniqueKeyType();
        CheckConstraintType checkConstraintType = new CheckConstraintType();
        ForeignKeyType foreignKeyType = new ForeignKeyType();
        UniqueKeyType primaryKey = new UniqueKeyType();
        TriggerType trigger = new TriggerType();

        // when

        ConvertableSiard22TableType result = new ConvertableSiard22TableType("name",
                                                                             "description",
                                                                             "folder",
                                                                             BigInteger.ONE,
                                                                             primaryKey, singletonList(columnType),
                                                                             singletonList(uniqueKeyType),
                                                                             singletonList(checkConstraintType),
                                                                             singletonList(foreignKeyType), singletonList(trigger));

        // then
        assertEquals("name", result.getName());
        assertEquals("description", result.getDescription());
        assertEquals("folder", result.getFolder());
        assertEquals(primaryKey, result.getPrimaryKey());
        assertEquals(BigInteger.ONE, result.getRows());
        assertTrue(result.getColumns()
                         .getColumn()
                         .contains(columnType));
        assertTrue(result.getCandidateKeys()
                         .getCandidateKey()
                         .contains(uniqueKeyType));
        assertTrue(result.getCheckConstraints()
                         .getCheckConstraint()
                         .contains(checkConstraintType));
        assertTrue(result.getForeignKeys()
                         .getForeignKey()
                         .contains(foreignKeyType));
        assertTrue(result.getTriggers()
                         .getTrigger()
                         .contains(trigger));
    }

    @Test
    public void shouldNotInitializeContainerTypes() {
        // given

        // when

        ConvertableSiard22TableType result = new ConvertableSiard22TableType("name",
                                                                             "description",
                                                                             "folder",
                                                                             BigInteger.ONE,
                                                                             new UniqueKeyType(), emptyList(),
                                                                             emptyList(),
                                                                             emptyList(),
                                                                             emptyList(), emptyList());

        // then
        assertNull(result.getColumns());
        assertNull(result.getCandidateKeys());
        assertNull(result.getCheckConstraints());
        assertNull(result.getForeignKeys());
        assertNull(result.getTriggers());
    }

}