package ch.admin.bar.siard2.cmd;

import ch.admin.bar.siard2.api.MetaValue;
import ch.admin.bar.siard2.api.Value;
import org.junit.jupiter.api.Test;

import java.io.Closeable;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PostgresInsertBatchTest {
    private static long binaryRow(long length) throws Exception {
        Value value = mock(Value.class);
        MetaValue metadata = mock(MetaValue.class);
        when(value.getMetaValue()).thenReturn(metadata);
        when(metadata.getPreType()).thenReturn(Types.VARBINARY);
        when(metadata.getCardinality()).thenReturn(-1);
        when(value.getByteLength()).thenReturn(length);
        long estimate = PrimaryDataToDb.estimatePostgresRow(List.of(value));
        verify(value, never()).getBytes();
        return estimate;
    }

    @Test
    void flushesAtRowCapAndFlushesRemainder() throws Exception {
        PreparedStatement statement = mock(PreparedStatement.class);
        try (PostgresInsertBatch batch = new PostgresInsertBatch(statement)) {
            for (int row = 0; row < 101; row++) {
                batch.beforeRow(64);
                batch.addRow(64);
            }
            verify(statement).executeBatch();
            batch.flush();
            verify(statement, times(2)).executeBatch();
            verify(statement, times(101)).addBatch();
        }
    }

    @Test
    void flushesBeforeNextRowWouldExceedByteBudget() throws Exception {
        PreparedStatement statement = mock(PreparedStatement.class);
        long bytes = binaryRow(10L * 1024 * 1024);
        try (PostgresInsertBatch batch = new PostgresInsertBatch(statement)) {
            batch.beforeRow(bytes);
            batch.addRow(bytes);
            verify(statement, never()).executeBatch();
            batch.beforeRow(bytes);
            verify(statement).executeBatch();
            batch.addRow(bytes);
            batch.flush();
            verify(statement, times(2)).executeBatch();
        }
    }

    @Test
    void oversizedRowFlushesAloneAndEmptyFlushDoesNothing() throws Exception {
        PreparedStatement statement = mock(PreparedStatement.class);
        try (PostgresInsertBatch batch = new PostgresInsertBatch(statement)) {
            batch.flush();
            verify(statement, never()).executeBatch();
            long bytes = binaryRow(Long.MAX_VALUE);
            batch.beforeRow(bytes);
            batch.addRow(bytes);
            verify(statement).executeBatch();
            batch.flush();
            verify(statement).executeBatch();
        }
    }

    @Test
    void binaryCellLargerThanBatchBudgetIsBoundAsStreamWithoutGetBytes() throws Exception {
        var connection = mock(ch.admin.bar.siard2.jdbc.PostgresConnection.class);
        var transfer = PrimaryDataToDb.newInstance(connection, null, null, true, true, true);
        var record = mock(ch.admin.bar.siard2.api.TableRecord.class);
        Value value = mock(Value.class);
        MetaValue metadata = mock(MetaValue.class);
        when(value.getMetaValue()).thenReturn(metadata);
        when(metadata.getPreType()).thenReturn(Types.VARBINARY);
        when(metadata.getCardinality()).thenReturn(-1);
        long length = 512L * 1024 * 1024;
        when(value.getByteLength()).thenReturn(length);
        var stream = mock(java.io.InputStream.class);
        when(value.getInputStream()).thenReturn(stream);
        when(record.getValues(true, true)).thenReturn(List.of(value));
        PreparedStatement statement = mock(PreparedStatement.class);
        try (PostgresInsertBatch batch = new PostgresInsertBatch(statement)) {
            transfer.putPreparedRecord(record, statement, new int[]{Types.BINARY}, new String[]{"bytea"}, new java.util.HashSet<>(), batch);
            verify(statement).setBinaryStream(1, stream, length);
            verify(statement).executeBatch();
            verify(value, never()).getBytes();
            verify(stream).close();
        }
    }

    @Test
    void retainsStreamsUntilExecutionAndClosesThemOnFailure() throws Exception {
        PreparedStatement statement = mock(PreparedStatement.class);
        Closeable stream = mock(Closeable.class);
        try (PostgresInsertBatch batch = new PostgresInsertBatch(statement)) {
            batch.retain(stream);
            batch.addRow(64);
            verify(stream, never()).close();
            when(statement.executeBatch()).thenThrow(new SQLException("failed"));
            assertThrows(SQLException.class, batch::flush);
            verify(stream).close();
            verify(statement).clearBatch();
            verify(statement).clearParameters();
        }
    }
}
