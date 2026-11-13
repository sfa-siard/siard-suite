package ch.admin.bar.siard2.jdbc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.Types;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Verifies that {@link PostgresResultSetMetaData} resolves each column's
 * type and type name at most once per result set: the underlying
 * {@link PostgresDatabaseMetaData#getColumns} catalog query is issued on
 * first access only and repeated reads are served from the cache.
 */
class PostgresResultSetMetaDataCacheTest {

    private PostgresDatabaseMetaData pdmd;
    private PostgresResultSetMetaData rsmd;

    @BeforeEach
    void setUp() throws Exception {
        ResultSetMetaData wrapped = mock(ResultSetMetaData.class);
        when(wrapped.getColumnCount()).thenReturn(2);
        // column 1: int4, column 2: bytea (both resolve via PostgresType keywords)
        when(wrapped.getColumnTypeName(1)).thenReturn("int4");
        when(wrapped.getColumnTypeName(2)).thenReturn("bytea");
        when(wrapped.getColumnType(1)).thenReturn(Types.INTEGER);
        when(wrapped.getColumnType(2)).thenReturn(Types.BINARY);
        for (int column = 1; column <= 2; column++) {
            when(wrapped.getTableName(column)).thenReturn("t");
            when(wrapped.getSchemaName(column)).thenReturn("public");
            when(wrapped.getColumnName(column)).thenReturn("c" + column);
        }

        ResultSet columnsC1 = columnsResultSet("int4");
        ResultSet columnsC2 = columnsResultSet("bytea");
        pdmd = mock(PostgresDatabaseMetaData.class);
        when(pdmd.toPattern(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(pdmd.getColumns(isNull(), eq("public"), eq("t"), eq("c1"))).thenReturn(columnsC1);
        when(pdmd.getColumns(isNull(), eq("public"), eq("t"), eq("c2"))).thenReturn(columnsC2);

        Connection conn = mock(Connection.class);
        when(conn.getMetaData()).thenReturn(pdmd);
        Statement stmt = mock(Statement.class);
        when(stmt.getConnection()).thenReturn(conn);

        rsmd = new PostgresResultSetMetaData(wrapped, stmt);
    }

    private static ResultSet columnsResultSet(String typeName) throws Exception {
        ResultSet rs = mock(ResultSet.class);
        when(rs.next()).thenReturn(true, false);
        when(rs.getString("TYPE_NAME")).thenReturn(typeName);
        return rs;
    }

    @Test
    void repeatedAccessOnOneColumnQueriesCatalogOnce() throws Exception {
        // multiple reads of type and type name on the same column
        for (int i = 0; i < 3; i++) {
            assertEquals("int4", rsmd.getColumnTypeName(1));
            assertEquals(Types.INTEGER, rsmd.getColumnType(1));
        }
        // exactly one catalog query for column 1, none for column 2
        verify(pdmd, times(1)).getColumns(isNull(), eq("public"), eq("t"), eq("c1"));
        verify(pdmd, times(0)).getColumns(isNull(), eq("public"), eq("t"), eq("c2"));
    }

    @Test
    void differentColumnsResolveIndependently() throws Exception {
        assertEquals("int4", rsmd.getColumnTypeName(1));
        assertEquals("bytea", rsmd.getColumnTypeName(2));
        assertEquals(Types.INTEGER, rsmd.getColumnType(1));
        assertEquals(Types.VARBINARY, rsmd.getColumnType(2));

        verify(pdmd, times(1)).getColumns(isNull(), eq("public"), eq("t"), eq("c1"));
        verify(pdmd, times(1)).getColumns(isNull(), eq("public"), eq("t"), eq("c2"));

        // repeated reads stay cached and stable
        assertEquals("int4", rsmd.getColumnTypeName(1));
        assertEquals(Types.VARBINARY, rsmd.getColumnType(2));
        verify(pdmd, times(1)).getColumns(isNull(), eq("public"), eq("t"), eq("c1"));
        verify(pdmd, times(1)).getColumns(isNull(), eq("public"), eq("t"), eq("c2"));
    }
}
