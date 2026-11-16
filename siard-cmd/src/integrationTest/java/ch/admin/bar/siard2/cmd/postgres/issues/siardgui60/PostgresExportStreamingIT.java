package ch.admin.bar.siard2.cmd.postgres.issues.siardgui60;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siard2.api.Cell;
import ch.admin.bar.siard2.api.Schema;
import ch.admin.bar.siard2.api.Table;
import ch.admin.bar.siard2.api.TableRecord;
import ch.admin.bar.siard2.api.TableRecordDispenser;
import ch.admin.bar.siard2.api.primary.ArchiveImpl;
import ch.admin.bar.siard2.cmd.MetaDataFromDb;
import ch.admin.bar.siard2.cmd.PrimaryDataFromDb;
import ch.admin.bar.siard2.cmd.SiardConnection;
import ch.enterag.utils.ProgramInfo;
import ch.enterag.utils.background.Progress;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.postgresql.jdbc.PgConnection;
import org.postgresql.largeobject.LargeObject;
import org.postgresql.largeobject.LargeObjectManager;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.File;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Covers the spec scenarios of {@code postgres-archive-export} that need a
 * real database:
 *
 * <ul>
 *   <li>"Export survives interleaved per-cell queries": a &gt;fetchSize table
 *   whose blob/clob oid-domain cells issue large-object calls and whose
 *   interval/array cells issue catalog lookups is exported through the real
 *   {@link PrimaryDataFromDb} path while the result set is streamed; the
 *   archive must contain every row with intact values.</li>
 *   <li>"No large-object descriptors remain open": during the same export a
 *   {@link Progress} callback opens a probe large object on the exporting
 *   connection and records its fd. PostgreSQL assigns the lowest free
 *   descriptor index (verified by {@link #largeObjectFdUsesLowestFreeIndex}),
 *   so leaked detection/value streams would push the observed fd up with the
 *   row count.</li>
 * </ul>
 */
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PostgresExportStreamingIT {

    private static final int ROWS = 350; // fetchSize default is 100 -> ~3 refetches while per-cell calls interleave
    private static final String TABLE = "stream_export_test";
    private static final long FD_LEAK_THRESHOLD = 10;
    private static final int MIN_PROBES = 20;

    @Container
    private static final PostgreSQLContainer<?> DB = new PostgreSQLContainer<>(
            DockerImageName.parse("postgres:16-alpine"))
            .withDatabaseName("postgres")
            .withUsername("postgres")
            .withPassword("postgres");

    private static Connection newNativeConnection() throws SQLException {
        Properties props = new Properties();
        props.setProperty("user", DB.getUsername());
        props.setProperty("password", DB.getPassword());
        return new org.postgresql.Driver().connect(DB.getJdbcUrl(), props);
    }

    private Connection newWrappedConnection() throws SQLException {
        // returns the wrapped PostgresConnection and creates the public.BLOB/CLOB oid domains
        return SiardConnection.getSiardConnection()
                              .createValidConnection(DB.getJdbcUrl(), DB.getUsername(), DB.getPassword());
    }

    private static byte[] blobContent(int id) {
        byte[] bytes = new byte[1024 + (id % 257)];
        Arrays.fill(bytes, (byte) (id % 251));
        return bytes;
    }

    private static String clobContent(int id) {
        return "row-" + id + "-" + "x".repeat(1000);
    }

    private static String intervalText(int id) {
        // archived via the VARCHAR path as Postgres' HH:MM:SS interval text
        return "%02d:00:00".formatted(id % 23);
    }

    private static long probeOid;

    @BeforeAll
    void setUp() throws Exception {
        // MetaDataFromDb writes producer info via the ProgramInfo singleton
        try {
            ProgramInfo.getProgramInfo();
        } catch (RuntimeException notInitialized) {
            ProgramInfo.getProgramInfo("SIARD Suite", "test", "siard", "test",
                                       "test", "test",
                                       List.of(), List.of(), List.of(), List.of());
        }

        // the wrapped connection creates the public.BLOB/CLOB oid domains in its constructor
        try (Connection ignored = newWrappedConnection()) {
        }

        try (Connection conn = newNativeConnection()) {
            conn.setAutoCommit(false);
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("DROP TABLE IF EXISTS " + TABLE);
                stmt.executeUpdate("CREATE TABLE " + TABLE + " (" +
                                           "id INT PRIMARY KEY, " +
                                           "b public.blob, " +
                                           "c public.clob, " +
                                           "i interval, " +
                                           "a int[])");
            }
            LargeObjectManager lom = conn.unwrap(PgConnection.class)
                                       .getLargeObjectAPI();
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO " + TABLE + " VALUES (?,?,?,?::interval,?::int[])")) {
                for (int i = 0; i < ROWS; i++) {
                    long blobOid = createLargeObject(lom, blobContent(i));
                    long clobOid = createLargeObject(lom, clobContent(i).getBytes(StandardCharsets.UTF_8));
                    if (i == 0) probeOid = blobOid;
                    ps.setInt(1, i);
                    ps.setLong(2, blobOid);
                    ps.setLong(3, clobOid);
                    ps.setString(4, (i % 23) + " hours");
                    ps.setString(5, "{" + i + "," + (i * 2) + "}");
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            conn.commit();
        }
    }

    /** pgjdbc exposes no fd getter; the server assigns the lowest free
     * descriptor index, so the private fd field is read via reflection. */
    private static int fdOf(LargeObject lo) {
        try {
            java.lang.reflect.Field fd = LargeObject.class.getDeclaredField("fd");
            fd.setAccessible(true);
            return fd.getInt(lo);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }

    private static long createLargeObject(LargeObjectManager lom, byte[] content) throws SQLException {
        long oid = lom.createLO();
        LargeObject lo = lom.open(oid, LargeObjectManager.WRITE);
        lo.write(content);
        lo.close();
        return oid;
    }

    @AfterAll
    void tearDown() throws SQLException {
        try (Connection conn = newNativeConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DROP TABLE IF EXISTS " + TABLE);
        }
    }

    @Test
    void largeObjectFdUsesLowestFreeIndex() throws SQLException {
        // control: documents that PostgreSQL hands out the lowest free LO
        // descriptor index, which is what makes the export probe meaningful
        try (Connection conn = newNativeConnection()) {
            conn.setAutoCommit(false);
            LargeObjectManager lom = conn.unwrap(PgConnection.class)
                                       .getLargeObjectAPI();
            List<LargeObject> open = new ArrayList<>();
            for (int i = 0; i < 20; i++) {
                open.add(lom.open(probeOid, LargeObjectManager.READ));
            }
            LargeObject probe = lom.open(probeOid, LargeObjectManager.READ);
            assertTrue(fdOf(probe) >= 20, "expected fd >= 20 with 20 descriptors open, got " + fdOf(probe));
            probe.close();
            for (LargeObject lo : open) {
                lo.close();
            }
            assertTrue(fdOf(lom.open(probeOid, LargeObjectManager.READ)) < 20,
                       "fd must be reused after close");
        }
    }

    @Test
    void exportStreamsRowsAndDoesNotLeakLargeObjectFds() throws Exception {
        File siard = File.createTempFile("export-streaming", ".siard");
        siard.delete();

        List<Integer> probeFds = new ArrayList<>();
        int holdabilityDuringExport;

        try (Connection conn = newWrappedConnection()) {
            conn.setAutoCommit(false);

            Archive archive = ArchiveImpl.newInstance();
            archive.create(siard);
            MetaDataFromDb.newInstance(conn.getMetaData(), archive.getMetaData())
                          .download(false, false, "public", null);

            LargeObjectManager lom = ((PgConnection) conn.unwrap(Connection.class)).getLargeObjectAPI();
            Progress probe = new Progress() {
                @Override
                public boolean cancelRequested() {
                    return false;
                }

                @Override
                public void notifyProgress(int percent) {
                    try {
                        LargeObject lo = lom.open(probeOid, LargeObjectManager.READ);
                        probeFds.add(fdOf(lo));
                        lo.close();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            };

            PrimaryDataFromDb pdfd = PrimaryDataFromDb.newInstance(conn, archive);
            pdfd.setOut(new PrintWriter(Writer.nullWriter()));
            pdfd.download(probe);
            archive.close();

            // PrimaryDataTransfer.openTable requests HOLD_CURSORS_OVER_COMMIT;
            // the PostgresConnection wrapper must have downgraded it
            holdabilityDuringExport = conn.getHoldability();
        }

        // scenario (b): no large-object descriptors accumulate across cells
        assertTrue(probeFds.size() >= MIN_PROBES,
                   "expected at least " + MIN_PROBES + " progress probes, got " + probeFds.size());
        int maxFd = probeFds.stream()
                            .mapToInt(Integer::intValue)
                            .max()
                            .orElseThrow();
        assertTrue(maxFd < FD_LEAK_THRESHOLD,
                   "large-object descriptors leaked during export: fds observed " + probeFds);

        assertEquals(ResultSet.CLOSE_CURSORS_AT_COMMIT, holdabilityDuringExport);

        // scenario (a): all rows exported with intact values, read back from the archive
        Archive read = ArchiveImpl.newInstance();
        read.open(siard);
        Schema schema = read.getSchema("public");
        Table table = schema.getTable(TABLE);
        int[] spotRows = {0, 100, 101, 200, ROWS - 1};
        List<TableRecord> spots = new ArrayList<>();
        TableRecordDispenser dispenser = table.openTableRecords();
        int count = 0;
        for (TableRecord record = dispenser.get(); record != null; record = dispenser.get()) {
            int row = count;
            if (Arrays.stream(spotRows)
                      .anyMatch(r -> r == row)) {
                spots.add(record);
            }
            count++;
        }
        dispenser.close();
        assertEquals(ROWS, count);

        for (int i = 0; i < spotRows.length; i++) {
            int id = spotRows[i];
            TableRecord record = spots.get(i);
            assertEquals(id, record.getCell(0)
                                   .getInt());
            assertTrue(Arrays.equals(blobContent(id), record.getCell(1)
                                                            .getBytes()), "blob mismatch at row " + id);
            assertEquals(clobContent(id), record.getCell(2)
                                               .getString(), "clob mismatch at row " + id);
            // interval columns are archived as VARCHAR holding Postgres' HH:MM:SS text
            assertEquals(intervalText(id), record.getCell(3)
                                               .getString(), "interval mismatch at row " + id);
            Cell arrayCell = record.getCell(4);
            assertEquals(2, arrayCell.getElements());
            assertEquals(id, arrayCell.getElement(0)
                                      .getInt());
            assertEquals(id * 2, arrayCell.getElement(1)
                                          .getInt());
        }
        read.close();
    }
}
