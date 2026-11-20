package ch.admin.bar.siardsuite.localwebapp.connections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;
import java.time.Clock;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * CRUD of {@link SqliteConnectionStore} against a temp-dir database: ordering by name, the
 * UNIQUE name constraint and the nullable {@code file} column.
 */
class SqliteConnectionStoreTest {

    @TempDir
    Path tempDir;

    private SqliteConnectionStore store;
    private Path databaseFile;

    @BeforeEach
    void setUp() {
        databaseFile = tempDir.resolve("siard-suite.db");
        store = new SqliteConnectionStore(databaseFile, Clock.systemUTC());
    }

    @Test
    void listIsEmptyOnFreshDatabase() {
        assertTrue(store.list().isEmpty());
        assertTrue(Files.isRegularFile(databaseFile), "the database file is created on first use");
    }

    @Test
    void createAndListRoundTrips() {
        SavedConnection saved = store.create(newConnection("Archive A"));

        assertTrue(saved.id() > 0);
        assertNotNull(saved.createdAt());
        assertEquals(saved.createdAt(), saved.updatedAt());

        List<SavedConnection> listed = store.list();
        assertEquals(1, listed.size());
        SavedConnection found = listed.get(0);
        assertEquals(saved.id(), found.id());
        assertEquals("Archive A", found.name());
        assertEquals("postgresql", found.dbmsId());
        assertEquals("db.example.org", found.host());
        assertEquals("5432", found.port());
        assertEquals("archive", found.dbName());
        assertEquals("archivist", found.user());
        assertEquals("ssl=true", found.options());
        assertNull(found.file());
    }

    @Test
    void listIsOrderedByName() {
        store.create(newConnection("Archive B"));
        store.create(newConnection("Archive A"));
        store.create(newConnection("Archive C"));

        assertEquals(List.of("Archive A", "Archive B", "Archive C"),
                store.list().stream().map(SavedConnection::name).toList());
    }

    @Test
    void duplicateNameIsRejected() {
        store.create(newConnection("Archive A"));

        assertThrows(DuplicateConnectionNameException.class,
                () -> store.create(newConnection("Archive A")));
        assertEquals(1, store.list().size());
    }

    @Test
    void updateReplacesFieldsAndKeepsIdAndCreationTime() {
        SavedConnection saved = store.create(newConnection("Archive A"));

        SavedConnection updated = store.update(saved.id(), new NewConnection("Archive Renamed", "mysql",
                "other.example.org", "3306", "other", "someone", null, null)).orElseThrow();

        assertEquals(saved.id(), updated.id());
        assertEquals(saved.createdAt(), updated.createdAt());
        assertEquals("Archive Renamed", updated.name());
        assertEquals("mysql", updated.dbmsId());
        assertNull(updated.options());
        assertEquals(List.of(updated), store.list());
    }

    @Test
    void updateKeepingTheNameIsAllowed() {
        SavedConnection saved = store.create(newConnection("Archive A"));

        assertTrue(store.update(saved.id(), newConnection("Archive A")).isPresent());
        assertEquals(1, store.list().size());
    }

    @Test
    void updateToAnotherProfilesNameIsRejected() {
        store.create(newConnection("Archive A"));
        SavedConnection other = store.create(newConnection("Archive B"));

        assertThrows(DuplicateConnectionNameException.class,
                () -> store.update(other.id(), newConnection("Archive A")));
        assertEquals(List.of("Archive A", "Archive B"),
                store.list().stream().map(SavedConnection::name).toList());
    }

    @Test
    void updateOfUnknownIdIsEmpty() {
        assertTrue(store.update(999, newConnection("Archive A")).isEmpty());
        assertTrue(store.list().isEmpty());
    }

    @Test
    void failedOpenIsRetried() throws Exception {
        // a directory where the database file should be makes the open fail
        Path blocked = tempDir.resolve("blocked.db");
        Files.createDirectory(blocked);
        SqliteConnectionStore blockedStore = new SqliteConnectionStore(blocked, Clock.systemUTC());

        assertThrows(ConnectionStoreException.class, blockedStore::list);

        Files.delete(blocked);
        assertTrue(blockedStore.list().isEmpty(), "the schema is created on the next attempt");
    }

    @Test
    void deleteRemovesTheProfile() {
        SavedConnection saved = store.create(newConnection("Archive A"));

        assertTrue(store.delete(saved.id()));
        assertFalse(store.delete(saved.id()));
        assertTrue(store.list().isEmpty());
    }

    @Test
    void nullOptionalFieldsStayNull() {
        store.create(new NewConnection("Plain", "mysql", "host", "3306", "db", "user", null, null));

        SavedConnection found = store.list().get(0);
        assertNull(found.options());
        assertNull(found.file());
    }

    @Test
    void databaseFileIsReadableOnlyByTheOwner() throws Exception {
        store.list();
        if (FileSystems.getDefault().supportedFileAttributeViews().contains("posix")) {
            assertEquals(PosixFilePermissions.fromString("rw-------"),
                    Files.getPosixFilePermissions(databaseFile));
        }
    }

    @Test
    void timestampsComeFromTheClock() {
        Instant fixed = Instant.parse("2026-01-02T03:04:05Z");
        SqliteConnectionStore fixedClockStore =
                new SqliteConnectionStore(tempDir.resolve("fixed.db"), Clock.fixed(fixed, java.time.ZoneOffset.UTC));

        SavedConnection saved = fixedClockStore.create(newConnection("Timed"));
        assertEquals(fixed, saved.createdAt());
        assertEquals(fixed, saved.updatedAt());
    }

    private static NewConnection newConnection(String name) {
        return new NewConnection(name, "postgresql", "db.example.org", "5432", "archive",
                "archivist", "ssl=true", null);
    }
}
