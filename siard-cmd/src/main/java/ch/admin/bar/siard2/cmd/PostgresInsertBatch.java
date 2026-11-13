package ch.admin.bar.siard2.cmd;

import java.io.Closeable;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

final class PostgresInsertBatch implements AutoCloseable {
    static final int ROW_LIMIT = 100;
    static final long BYTE_LIMIT = 16L * 1024 * 1024;

    private final PreparedStatement statement;
    private final List<Closeable> streams = new ArrayList<>();
    private int rows;
    private long bytes;

    PostgresInsertBatch(PreparedStatement statement) {
        this.statement = statement;
    }

    void beforeRow(long estimatedBytes) throws SQLException, IOException {
        if (rows > 0 && estimatedBytes > BYTE_LIMIT - bytes) flush();
    }

    void retain(Closeable stream) {
        streams.add(stream);
    }

    void addRow(long estimatedBytes) throws SQLException, IOException {
        statement.addBatch();
        rows++;
        bytes = Math.min(BYTE_LIMIT, bytes + Math.min(BYTE_LIMIT, estimatedBytes));
        if (rows >= ROW_LIMIT || bytes >= BYTE_LIMIT) flush();
    }

    void flush() throws SQLException, IOException {
        if (rows == 0) return;
        try {
            statement.executeBatch();
        } finally {
            statement.clearBatch();
            statement.clearParameters();
            rows = 0;
            bytes = 0;
            closeStreams();
        }
    }

    private void closeStreams() throws IOException {
        IOException failure = null;
        for (Closeable stream : streams) {
            try {
                stream.close();
            } catch (IOException exception) {
                if (failure == null) failure = exception;
                else failure.addSuppressed(exception);
            }
        }
        streams.clear();
        if (failure != null) throw failure;
    }

    @Override
    public void close() throws IOException {
        closeStreams();
    }
}
