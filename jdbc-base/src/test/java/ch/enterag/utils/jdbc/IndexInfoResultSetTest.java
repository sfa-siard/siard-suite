package ch.enterag.utils.jdbc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IndexInfoResultSetTest {

    private static final int TYPE_COLUMN = 3;
    private static final int OTHER_COLUMN = 1;

    @Mock
    private ResultSet wrapped;

    private IndexInfoResultSet indexInfoResultSet;

    @BeforeEach
    void setUp() throws SQLException {
        when(wrapped.findColumn("TYPE")).thenReturn(TYPE_COLUMN);
        indexInfoResultSet = new IndexInfoResultSet(wrapped);
    }

    @Test
    void shouldMapTableIndexOtherToHashed() throws SQLException {
        when(wrapped.getInt(TYPE_COLUMN)).thenReturn((int) DatabaseMetaData.tableIndexOther);

        assertEquals(DatabaseMetaData.tableIndexHashed, indexInfoResultSet.getInt(TYPE_COLUMN));
    }

    @Test
    void shouldKeepOtherIndexTypes() throws SQLException {
        when(wrapped.getInt(TYPE_COLUMN)).thenReturn((int) DatabaseMetaData.tableIndexClustered);

        assertEquals(DatabaseMetaData.tableIndexClustered, indexInfoResultSet.getInt(TYPE_COLUMN));
    }

    @Test
    void shouldNotRemapOtherColumns() throws SQLException {
        when(wrapped.getInt(OTHER_COLUMN)).thenReturn((int) DatabaseMetaData.tableIndexOther);

        assertEquals(DatabaseMetaData.tableIndexOther, indexInfoResultSet.getInt(OTHER_COLUMN));
    }

    @Test
    void shouldRemapTypeColumnReadByLabel() throws SQLException {
        when(wrapped.getInt(TYPE_COLUMN)).thenReturn((int) DatabaseMetaData.tableIndexOther);

        assertEquals(DatabaseMetaData.tableIndexHashed, indexInfoResultSet.getInt("TYPE"));
    }

    @Test
    void shouldFailWhenTypeColumnIsMissing() throws SQLException {
        when(wrapped.findColumn("TYPE")).thenThrow(new SQLException("Column TYPE not found"));

        assertThrows(SQLException.class, () -> new IndexInfoResultSet(wrapped));
    }
}
