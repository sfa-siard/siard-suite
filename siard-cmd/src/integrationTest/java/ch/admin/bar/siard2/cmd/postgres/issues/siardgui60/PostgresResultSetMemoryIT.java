package ch.admin.bar.siard2.cmd.postgres.issues.siardgui60;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siard2.api.MetaData;
import ch.admin.bar.siard2.api.primary.ArchiveImpl;
import ch.admin.bar.siard2.cmd.MetaDataFromDb;
import ch.admin.bar.siard2.cmd.MetaDataToDb;
import ch.admin.bar.siard2.cmd.PrimaryDataFromDb;
import ch.admin.bar.siard2.cmd.PrimaryDataToDb;
import ch.admin.bar.siard2.jdbcx.PostgresDataSource;
import ch.enterag.utils.ProgramInfo;
import ch.enterag.utils.background.Progress;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.File;
import java.io.PrintWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Reproducer for https://github.com/sfa-siard/SiardGui/issues/60 and
 * https://github.com/sfa-siard/SiardGui/issues/61.
 *
 * PgJDBC only uses a server-side cursor (portal) when fetchSize &gt; 0, the
 * result set is forward-only, autocommit is off and the result set is not
 * holdable (HOLD_CURSORS_OVER_COMMIT disables it). PrimaryDataTransfer.openTable()
 * creates statements with HOLD_CURSORS_OVER_COMMIT and never sets a fetch size,
 * so the driver buffers the whole result set in heap. For tables dominated by
 * bytea payloads this requires heap sizes far beyond the data size.
 */
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PostgresResultSetMemoryIT {

    private static final int ROWS = 4000;
    private static final int PAYLOAD_SIZE = 25 * 1024;
    private static final long EXPECTED_TOTAL_BYTES = (long) ROWS * PAYLOAD_SIZE;
    private static final String QUERY = "SELECT id, payload FROM memory_test ORDER BY id";

    // payload ~= 97.7 MiB; a fully buffered result set retains ~= payload size in heap
    private static final long BUFFERED_THRESHOLD = 60L * 1024 * 1024;
    // streaming keeps at most fetchSize rows in flight; generous bound for any sane fetch size
    private static final long STREAMING_THRESHOLD = 40L * 1024 * 1024;

    @Container
    private static final PostgreSQLContainer<?> DB = new PostgreSQLContainer<>(
            DockerImageName.parse("postgres:16-alpine"))
            .withDatabaseName("postgres")
            .withUsername("postgres")
            .withPassword("postgres");

    // must use the native driver explicitly: once the SIARD PostgresDriver is
    // registered, DriverManager.getConnection returns the wrapped connection
    // whose SQL parser does not support "DROP TABLE IF EXISTS"
    private static Connection newNativeConnection() throws SQLException {
        java.util.Properties props = new java.util.Properties();
        props.setProperty("user", DB.getUsername());
        props.setProperty("password", DB.getPassword());
        return new org.postgresql.Driver().connect(DB.getJdbcUrl(), props);
    }

    @BeforeAll
    void setUp() throws SQLException {
        try {
            ProgramInfo.getProgramInfo();
        } catch (RuntimeException notInitialized) {
            ProgramInfo.getProgramInfo("SIARD Suite", "test", "siard", "test",
                                       "test", "test",
                                       List.of(), List.of(), List.of(), List.of());
        }
        try (Connection conn = newNativeConnection()) {
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("DROP TABLE IF EXISTS memory_test");
                stmt.executeUpdate("CREATE TABLE memory_test (id INT PRIMARY KEY, payload BYTEA)");
            }
            byte[] payload = new byte[PAYLOAD_SIZE];
            new Random(42).nextBytes(payload);
            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO memory_test (id, payload) VALUES (?, ?)")) {
                for (int i = 0; i < ROWS; i++) {
                    ps.setInt(1, i);
                    ps.setBytes(2, payload);
                    ps.addBatch();
                }
                ps.executeBatch();
            }
        }
    }

    @AfterAll
    void tearDown() throws SQLException {
        try (Connection conn = newNativeConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DROP TABLE IF EXISTS memory_test");
        }
    }

    private static long usedMemory() {
        System.gc();
        System.gc();
        return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
    }

    private Connection newWrappedConnection() throws SQLException {
        PostgresDataSource ds = new PostgresDataSource(DB.getJdbcUrl(), DB.getUsername(), DB.getPassword());
        return ds.getConnection();
    }

    private record Measurement(ResultSet resultSet, long memoryDelta) {
    }

    /**
     * Executes the query and measures the heap growth caused by executeQuery()
     * before the first row is consumed. The returned result set must be kept
     * referenced while interpreting the delta, then consumed and closed.
     */
    private static Measurement measureExecuteMemory(Statement stmt) throws SQLException {
        long before = usedMemory();
        ResultSet rs = stmt.executeQuery(QUERY);
        long delta = usedMemory() - before;
        return new Measurement(rs, delta);
    }

    private static void assertAllRowsReadable(ResultSet rs) throws SQLException {
        long total = 0;
        int count = 0;
        while (rs.next()) {
            total += rs.getBytes(2).length;
            count++;
        }
        assertEquals(ROWS, count);
        assertEquals(EXPECTED_TOTAL_BYTES, total);
    }

    private void exportMemoryTestArchive(File siard) throws Exception {
        try (Connection conn = newWrappedConnection()) {
            conn.setAutoCommit(false);
            Archive archive = ArchiveImpl.newInstance();
            archive.create(siard);
            try {
                MetaDataFromDb.newInstance(conn.getMetaData(), archive.getMetaData())
                              .download(false, false, "public", null);
                PrimaryDataFromDb pdfd = PrimaryDataFromDb.newInstance(conn, archive);
                pdfd.setOut(new PrintWriter(Writer.nullWriter()));
                pdfd.download(null);
            } catch (Throwable failure) {
                try {
                    archive.close();
                } catch (Throwable closeFailure) {
                    failure.addSuppressed(closeFailure);
                }
                throw failure;
            }
            archive.close();
        }
    }

    private long restoreMemoryTestArchive(File siard) throws Exception {
        Archive archive = ArchiveImpl.newInstance();
        archive.open(siard);
        try (Connection conn = newWrappedConnection()) {
            conn.setAutoCommit(false);
            MetaData md = archive.getMetaData();
            MetaDataToDb mdtd = MetaDataToDb.newInstance(conn.getMetaData(), md,
                    Map.of("public", "restore_memory"));
            mdtd.setOut(new PrintWriter(Writer.nullWriter()));
            mdtd.upload(null);

            PrimaryDataToDb pdtd = PrimaryDataToDb.newInstance(conn, archive,
                    mdtd.getArchiveMapping(), mdtd.supportsArrays(),
                    mdtd.supportsDistincts(), mdtd.supportsUdts());
            pdtd.setOut(new PrintWriter(Writer.nullWriter()));

            long baseline = usedMemory();
            AtomicLong peak = new AtomicLong();
            Progress progress = new Progress() {
                @Override
                public boolean cancelRequested() {
                    return false;
                }

                @Override
                public void notifyProgress(int percent) {
                    if (percent % 10 == 0) {
                        long delta = usedMemory() - baseline;
                        peak.set(Math.max(peak.get(), delta));
                    }
                }
            };
            pdtd.upload(progress);
            return peak.get();
        } finally {
            archive.close();
        }
    }

    @Test
    void restorePathDoesNotRetainInsertedRows() throws Exception {
        // The previous CONCUR_UPDATABLE path retained every inserted row in the
        // pgjdbc result set until the table finished, so this peak grows with the
        // ~98 MiB table on the old path and stays bounded on the prepared path.
        File siard = File.createTempFile("restore-memory", ".siard");
        siard.delete();
        try {
            exportMemoryTestArchive(siard);
            long peak = restoreMemoryTestArchive(siard);
            assertTrue(peak < STREAMING_THRESHOLD,
                       "restore retained " + peak + " bytes of heap for a "
                               + EXPECTED_TOTAL_BYTES + "-byte table");
            try (Connection conn = newNativeConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(
                         "SELECT COUNT(*), COALESCE(SUM(OCTET_LENGTH(payload)), 0)"
                                 + " FROM restore_memory.memory_test")) {
                assertTrue(rs.next());
                assertEquals(ROWS, rs.getLong(1));
                assertEquals(EXPECTED_TOTAL_BYTES, rs.getLong(2));
            }
        } finally {
            siard.delete();
        }
    }

    @Test
    void holdableResultSetWithoutFetchSize_buffersEntireResultSet() throws SQLException {
        // documents raw pgjdbc driver behavior (unwrapped connection):
        // HOLD_CURSORS_OVER_COMMIT + fetchSize=0 buffers all rows during executeQuery
        try (Connection conn = newNativeConnection()) {
            conn.setAutoCommit(false);
            try (Statement stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY,
                                                        ResultSet.CONCUR_READ_ONLY,
                                                        ResultSet.HOLD_CURSORS_OVER_COMMIT)) {
                Measurement m = measureExecuteMemory(stmt);
                assertTrue(m.memoryDelta() > BUFFERED_THRESHOLD,
                           "expected entire result set (~97 MiB) to be buffered, delta was " + m.memoryDelta());
                assertAllRowsReadable(m.resultSet());
            }
        }
    }

    @Test
    void fetchSizeAloneDoesNotEnableCursorWhenResultSetIsHoldable() throws SQLException {
        // proves holdability must be fixed as well: fetchSize>0 is ignored for
        // holdable result sets (raw pgjdbc driver behavior, unwrapped connection)
        try (Connection conn = newNativeConnection()) {
            conn.setAutoCommit(false);
            try (Statement stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY,
                                                        ResultSet.CONCUR_READ_ONLY,
                                                        ResultSet.HOLD_CURSORS_OVER_COMMIT)) {
                stmt.setFetchSize(10);
                Measurement m = measureExecuteMemory(stmt);
                assertTrue(m.memoryDelta() > BUFFERED_THRESHOLD,
                           "holdable result set must ignore fetchSize and buffer everything, delta was " + m.memoryDelta());
                assertAllRowsReadable(m.resultSet());
            }
        }
    }

    @Test
    void closeAtCommitResultSetWithFetchSize_streamsResultSet() throws SQLException {
        // target configuration: CLOSE_CURSORS_AT_COMMIT + fetchSize -> server-side
        // cursor, bounded memory, all rows still readable
        try (Connection conn = newWrappedConnection()) {
            conn.setAutoCommit(false);
            try (Statement stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY,
                                                        ResultSet.CONCUR_READ_ONLY,
                                                        ResultSet.CLOSE_CURSORS_AT_COMMIT)) {
                stmt.setFetchSize(10);
                Measurement m = measureExecuteMemory(stmt);
                assertTrue(m.memoryDelta() < STREAMING_THRESHOLD,
                           "expected bounded memory with server-side cursor, delta was " + m.memoryDelta());
                assertAllRowsReadable(m.resultSet());
            }
        }
    }

    @Test
    void exportPathConfiguration_doesNotBufferEntireResultSet() throws SQLException {
        // regression test for the actual export path: PrimaryDataTransfer.openTable()
        // calls createStatement(TYPE_FORWARD_ONLY, CONCUR_READ_ONLY, HOLD_CURSORS_OVER_COMMIT)
        // and never sets a fetch size. Once fixed (wrapper-side or caller-side), the same
        // call sequence must stream instead of buffering the whole table in heap.
        // NOTE: fails until the fix is implemented.
        try (Connection conn = newWrappedConnection()) {
            conn.setAutoCommit(false);
            try (Statement stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY,
                                                        ResultSet.CONCUR_READ_ONLY,
                                                        ResultSet.HOLD_CURSORS_OVER_COMMIT)) {
                Measurement m = measureExecuteMemory(stmt);
                assertTrue(m.memoryDelta() < STREAMING_THRESHOLD,
                           "export path must stream the result set, but " + m.memoryDelta() + " bytes were buffered");
                assertAllRowsReadable(m.resultSet());
            }
        }
    }
}
