package ch.admin.bar.siard2.jdbc;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.mockito.Mockito.*;

class PostgresPreparedSqlTest {
    @Test
    void jdbcPlaceholdersReachDriverUnchangedForEveryPrepareOverload() throws Exception {
        String sql = "INSERT INTO t (a, b) VALUES (?, ?)";
        Connection raw = mock(Connection.class);
        when(raw.createStatement()).thenReturn(mock(Statement.class));
        when(raw.nativeSQL(sql)).thenReturn("INSERT INTO t (a, b) VALUES ($1, $2)");
        PreparedStatement prepared = mock(PreparedStatement.class);
        int type = ResultSet.TYPE_FORWARD_ONLY;
        int concurrency = ResultSet.CONCUR_READ_ONLY;
        int holdability = ResultSet.CLOSE_CURSORS_AT_COMMIT;
        int[] indexes = {1};
        String[] names = {"a"};
        when(raw.prepareStatement(anyString())).thenReturn(prepared);
        when(raw.prepareStatement(anyString(), anyInt(), anyInt())).thenReturn(prepared);
        when(raw.prepareStatement(anyString(), anyInt(), anyInt(), anyInt())).thenReturn(prepared);
        when(raw.prepareStatement(anyString(), anyInt())).thenReturn(prepared);
        when(raw.prepareStatement(anyString(), same(indexes))).thenReturn(prepared);
        when(raw.prepareStatement(anyString(), same(names))).thenReturn(prepared);
        PostgresConnection connection = new PostgresConnection(raw);
        connection.prepareStatement(sql);
        connection.prepareStatement(sql, type, concurrency);
        connection.prepareStatement(sql, type, concurrency, holdability);
        connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        connection.prepareStatement(sql, indexes);
        connection.prepareStatement(sql, names);
        verify(raw).prepareStatement(sql);
        verify(raw).prepareStatement(sql, type, concurrency);
        verify(raw).prepareStatement(sql, type, concurrency, holdability);
        verify(raw).prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        verify(raw).prepareStatement(sql, indexes);
        verify(raw).prepareStatement(sql, names);
        verify(raw, never()).nativeSQL(anyString());
    }
}
