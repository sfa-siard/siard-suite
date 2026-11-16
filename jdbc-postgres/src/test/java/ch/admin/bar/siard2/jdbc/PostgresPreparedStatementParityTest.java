package ch.admin.bar.siard2.jdbc;

import ch.admin.bar.siard2.postgres.PostgresObject;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.postgresql.util.PGobject;

import javax.xml.datatype.DatatypeFactory;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PostgresPreparedStatementParityTest {
    private void assertParity(int type, Object value) throws Exception {
        List<Object> updated = new ArrayList<>();
        List<Object> bound = new ArrayList<>();
        ResultSet rawResult = mock(ResultSet.class, invocation -> {
            if (invocation.getMethod().getName().startsWith("update")) updated.add(invocation.getArgument(1));
            return RETURNS_DEFAULTS.answer(invocation);
        });
        PreparedStatement rawStatement = mock(PreparedStatement.class, invocation -> {
            if (invocation.getMethod().getName().startsWith("setObject")
                    || invocation.getMethod().getName().matches("set(Long|Int|Short|BigDecimal|Time|String|Bytes|Array)")) {
                bound.add(invocation.getArgument(1));
            }
            return RETURNS_DEFAULTS.answer(invocation);
        });
        ResultSetMetaData metadata = mock(ResultSetMetaData.class);
        when(metadata.getColumnType(1)).thenReturn(type);
        when(metadata.getColumnTypeName(1)).thenReturn("public.custom_type");
        ParameterMetaData parameters = mock(ParameterMetaData.class);
        when(parameters.getParameterTypeName(1)).thenReturn("public.custom_type");
        when(rawStatement.getParameterMetaData()).thenReturn(parameters);
        PostgresConnection connection = mock(PostgresConnection.class);
        Statement statement = mock(Statement.class);
        when(statement.getConnection()).thenReturn(connection);
        PostgresResultSet result = new PostgresResultSet(rawResult, statement) {
            @Override
            public ResultSetMetaData getMetaData() { return metadata; }
        };
        result.updateObject(1, value);
        new PostgresPreparedStatement(rawStatement, connection).setObject(1, value, type);
        assertEquals(updated.size(), bound.size());
        assertEquals(1, bound.size());
        if (updated.getFirst() instanceof byte[] bytes) assertArrayEquals(bytes, (byte[]) bound.getFirst());
        else assertEquals(updated.getFirst(), bound.getFirst());
    }

    @Test
    void scalarBranchesMatchResultSet() throws Exception {
        for (int type : new int[]{Types.SMALLINT, Types.INTEGER, Types.BIGINT}) {
            for (Object value : new Object[]{(short) 2, 3, 4L, BigInteger.TEN, BigDecimal.TEN}) assertParity(type, value);
        }
        for (int type : new int[]{Types.DECIMAL, Types.NUMERIC}) assertParity(type, new BigDecimal("12.34"));
        for (int type : new int[]{Types.BINARY, Types.VARBINARY}) assertParity(type, new byte[]{0, -1, 2});
        assertParity(Types.TIME, Time.valueOf("12:34:56"));
        assertParity(Types.VARCHAR, "Grüezi 世界");
        assertParity(Types.DATE, Date.valueOf("2026-11-13"));
        assertParity(Types.TIMESTAMP, Timestamp.valueOf("2026-11-13 12:34:56.123456"));
        assertParity(Types.BOOLEAN, true);
    }

    @Test
    void lobAndArrayBranchesMatchResultSet() throws Exception {
        PostgresBlob blob = mock(PostgresBlob.class);
        when(blob.getOid()).thenReturn(123L);
        assertParity(Types.BLOB, blob);
        assertParity(Types.DATALINK, blob);
        PostgresClob clob = mock(PostgresClob.class);
        when(clob.getOid()).thenReturn(456L);
        assertParity(Types.CLOB, clob);
        PostgresArray array = mock(PostgresArray.class);
        org.postgresql.jdbc.PgArray raw = mock(org.postgresql.jdbc.PgArray.class);
        when(array.unwrap(Array.class)).thenReturn(raw);
        assertParity(Types.ARRAY, array);
    }

    @Test
    void intervalBranchesMatchResultSet() throws Exception {
        for (String duration : new String[]{"P2Y3M", "P4DT5H6M7.123S", "-P4DT5H6M7.123S"}) {
            assertParity(Types.OTHER, DatatypeFactory.newInstance().newDuration(duration));
        }
    }

    @Test
    void structuredBranchesMatchResultSet() throws Exception {
        try (var construction = mockConstruction(PostgresObject.class,
                (object, context) -> when(object.getValue()).thenReturn("serialized"))) {
            Struct struct = mock(Struct.class);
            when(struct.getSQLTypeName()).thenReturn("public.custom_type");
            assertParity(Types.STRUCT, struct);
            assertParity(Types.DISTINCT, "domain value");
            assertEquals(4, construction.constructed().size());
        }
    }

    @Test
    void typedBinaryStreamBypassesInsertParameterMetadata() throws Exception {
        PreparedStatement raw = mock(PreparedStatement.class);
        ParameterMetaData parameters = mock(ParameterMetaData.class);
        when(parameters.getParameterTypeName(anyInt())).thenThrow(new AssertionError("no insert parameter type"));
        when(raw.getParameterMetaData()).thenReturn(parameters);
        InputStream stream = mock(InputStream.class);
        new PostgresPreparedStatement(raw, mock(Connection.class))
                .setBinaryStream(1, stream, 10, Types.VARBINARY);
        verify(raw).setBinaryStream(1, stream, 10L);
        verify(parameters, never()).getParameterTypeName(anyInt());
    }

    @Test
    void distinctUsesCallerResolvedTypeName() throws Exception {
        PreparedStatement raw = mock(PreparedStatement.class);
        ParameterMetaData parameters = mock(ParameterMetaData.class);
        when(parameters.getParameterTypeName(anyInt())).thenThrow(new AssertionError("no insert parameter type"));
        when(raw.getParameterMetaData()).thenReturn(parameters);
        try (var construction = mockConstruction(PostgresObject.class,
                (object, context) -> when(object.getValue()).thenReturn("serialized"))) {
            new PostgresPreparedStatement(raw, mock(PostgresConnection.class))
                    .setObject(1, "domain value", Types.DISTINCT, "public.domain");
            ArgumentCaptor<Object> bound = ArgumentCaptor.forClass(Object.class);
            verify(raw).setObject(eq(1), bound.capture());
            PGobject object = (PGobject) bound.getValue();
            assertEquals("public.domain", object.getType());
            assertEquals("serialized", object.getValue());
            verify(parameters, never()).getParameterTypeName(anyInt());
        }
    }

    @Test
    void nullsBindSqlNullForEveryType() throws Exception {
        PreparedStatement raw = mock(PreparedStatement.class);
        PostgresPreparedStatement statement = new PostgresPreparedStatement(raw, mock(Connection.class));
        int[] types = {Types.CLOB, Types.BLOB, Types.DATALINK, Types.ARRAY, Types.STRUCT,
                Types.DISTINCT, Types.OTHER, Types.INTEGER, Types.VARCHAR, Types.BINARY};
        for (int type : types) statement.setObject(1, null, type);
        verify(raw, times(types.length)).setNull(1, Types.NULL);
    }
}
